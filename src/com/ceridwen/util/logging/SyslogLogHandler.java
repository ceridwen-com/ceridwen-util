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

import java.io.IOException;

import com.ceridwen.util.net.Syslog;

public class SyslogLogHandler
        extends AbstractLogHandler {

    private int port = 514;
    private String host;

    public SyslogLogHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void sendMessage(String logger, String level, String message) {
        try {
            Syslog.sendSyslog(host, port, logger, Syslog.LOG_ALERT, message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws SecurityException {
    }

    /**
     * Flush any buffered output.
     * 
     */
    @Override
    public void flush() {
    }
}
