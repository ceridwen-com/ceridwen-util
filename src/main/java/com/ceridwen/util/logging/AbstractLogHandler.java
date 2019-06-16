/*******************************************************************************
 * Copyright 2016 Matthew J. Dovey (www.ceridwen.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ceridwen.util.logging;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

import com.ceridwen.util.versioning.LibraryIdentifier;
import com.ceridwen.util.versioning.LibraryRegistry;

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
            LibraryRegistry registry = new LibraryRegistry();
            for (LibraryIdentifier id: registry.listLibraries()) {
                components.append("\r\n" +
                          registry.getLibraryName(id) +
                          ". " +
                          registry.getLibraryVersion(id) +
                          " - " +
                          registry.getLibraryVendor(id) +
                		  ".");
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
