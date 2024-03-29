/*
 * Copyright 2024 Ceridwen Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ceridwen.util.logging;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import java.util.logging.Level;

/**
 *
 * @author Matthew
 */
public class WinEventLogHandler extends AbstractLogHandler {
	private static final String DEFAULT_SOURCE = "ceridwen.com";
	/**
	 * 
	 */
	private String _source = null;
	private String _server = null;

	private HANDLE _handle = null;

	/**
	 * @param server The server for remote logging
	 * @param source The Event View Source
	 */
	public WinEventLogHandler(String server, String source) {
		if (source == null || source.length() == 0) {
			source = DEFAULT_SOURCE;
		}

		this._server = server;
		setSource(source);
	}

	/**
	 * The <b>Source</b> option which names the source of the event. The current
	 * value of this constant is <b>Source</b>.
     * @param source
	 */
	public final void setSource(String source) {

		if (source == null || source.length() == 0) {
			source = DEFAULT_SOURCE;
		}

		_source = source.trim();
	}

	/**
	 * @return
	 */
	public String getSource() {
		return _source;
	}

	/**
	 * 
	 */
	private void registerEventSource() {
		close();

		try {
			_handle = registerEventSource(_server, _source);
		} catch (Exception e) {
			close();
//			throw new RuntimeException("Could not register event source.", e);
		}
	}

	/**
	 * 
	 */
	public void activateOptions() {
		registerEventSource();
	}

	
        
        
        

	/**
	 * @param server The server for remote logging
	 * @param source The Event View Source
	 * @param application The Event View application (location)
	 * @param eventMessageFile The message file location in the file system
	 * @param categoryMessageFile The message file location in the file system
	 * @return
	 */
	private HANDLE registerEventSource(String server, String source) {
		HANDLE h = Advapi32.INSTANCE.RegisterEventSource(server, source);
		if (h == null) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}

		return h;
	}

	/**
	 * Convert log4j Priority to an EventLog type. The log4j package supports 8
	 * defined priorities, but the NT EventLog only knows 3 event types of
	 * interest to us: ERROR, WARNING, and INFO.
	 * 
	 * @param level
	 *            Log4j priority.
	 * @return EventLog type.
	 */
	private static int getEventLogType(String level) {
            Level _level = Level.parse(level);
            if (_level == Level.SEVERE) 
                return WinNT.EVENTLOG_ERROR_TYPE;
            else if (_level == Level.WARNING)
                return WinNT.EVENTLOG_WARNING_TYPE;
            else
		return WinNT.EVENTLOG_INFORMATION_TYPE;
	}

	/**
	 * Convert log4j Priority to an EventLog category. Each category is backed
	 * by a message resource so that proper category names will be displayed in
	 * the NT Event Viewer.
	 * 
	 * @param level  Log4J priority.
	 * @return EventLog category.
	 */
	private static int getEventLogCategory(String level) {
            return Level.parse(level).intValue();
 	}

    @Override
    protected void sendMessage(String logger, String level, String message) {
        if (_handle == null) {
            registerEventSource();
        }

        final int messageID = logger.hashCode();

        String[] buffer = { message };

        if (Advapi32.INSTANCE.ReportEvent(_handle, getEventLogType(level),
            getEventLogCategory(level), messageID, null, buffer.length, 0, buffer, null) == false) {
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
        if (_handle != null) {
            if (!Advapi32.INSTANCE.DeregisterEventSource(_handle)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            _handle = null;
        }
    }

}
