/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey (www.ceridwen.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at 
 * <http://www.gnu.org/licenses/>
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 ******************************************************************************/
package com.ceridwen.util.logging;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

abstract public class AbstractLogHandler
        extends Handler {
    
    int throttle;
    int duration;
    Stack<Date> timeStack = new Stack<Date>();
    
    protected AbstractLogHandler() {
        super();
    }
    
    public void setThrottle(int throttle, int duration) {
        this.throttle = throttle;
        this.duration = duration;
    }

    private boolean isThrottled()
    {
        if (this.throttle == 0 || this.duration == 0) {
            return false;
        }
        Stack<Date> newStack = new Stack<Date>();
        Date now = new Date();
        long count = 0;
        while (!timeStack.empty()) {
            Date dt = timeStack.pop();
            if (now.getTime() - dt.getTime() <= duration * 1000) {
                newStack.push(dt);
                count++;
            }
        }        
        timeStack = newStack;
        if (count > this.throttle) {
            return true;
        } else {
            timeStack.push(new Date());
            return false;
        }
    }
    

    abstract protected void sendMessage(String logger, String level, String message);

    /**
     * Publish a <tt>LogRecord</tt>.
     * 
     * @param record
     *            description of the log event
     */
    @Override
    public void publish(LogRecord record) {
        if (!this.isLoggable(record)) {
            return;
        }
        if (this.isThrottled()) {
            return;
        }

        Formatter format = this.getFormatter();
        if (format == null) {
            format = new XMLFormatter();
        }
        String message = format.format(record);
        try {
            StringBuffer components = new StringBuffer("\r\nRegistered Components:");
            Iterator<?> iter = com.ceridwen.util.versioning.ComponentRegistry.
                    listRegisteredComponents();
            while (iter.hasNext()) {
                Class<?> component = (Class<?>) iter.next();
                components.append("\r\n" +
                          com.ceridwen.util.versioning.ComponentRegistry.
                                  getName(component) +
                          ". " +
                          com.ceridwen.util.versioning.ComponentRegistry.
                                  getVersionString(component) +
                          " - " +
                          com.ceridwen.util.versioning.ComponentRegistry.
                                  getAuthor(component) + ".");
            }

            StringBuffer environment = new StringBuffer("\r\nEnvironment:");
            Properties props = System.getProperties();
            Enumeration<?> enumerate = props.keys();
            while (enumerate.hasMoreElements()) {
                String key = (String) enumerate.nextElement();
                environment.append("\r\n" + key + "=" + props.getProperty(key));
            }
            message = message + "\r\n" + components.toString() + "\r\n" +
                    environment;
            this.sendMessage(record.getLoggerName(), record.getLevel().getName(), message);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
