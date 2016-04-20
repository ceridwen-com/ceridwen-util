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
// Derived from
//
// Syslog - Unix-compatible syslog routines
//
// Original version by Tim Endres, <time@ice.com>.
//
// Re-written and fixed up by Jef Poskanzer <jef@acme.com>.
//
// Copyright (C)1996 by Jef Poskanzer <jef@acme.com>. All rights reserved.
//
// http://www.acme.com/java/
//

package com.ceridwen.util.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Syslog {
    // Priorities.
    public static final int LOG_EMERG = 0; // system is unusable
    public static final int LOG_ALERT = 1; // action must be taken immediately
    public static final int LOG_CRIT = 2; // critical conditions
    public static final int LOG_ERR = 3; // error conditions
    public static final int LOG_WARNING = 4; // warning conditions
    public static final int LOG_NOTICE = 5; // normal but significant condition
    public static final int LOG_INFO = 6; // informational
    public static final int LOG_DEBUG = 7; // debug-level messages
    public static final int LOG_PRIMASK = 0x0007; // mask to extract priority

    // Facilities.
    public static final int LOG_KERN = (0 << 3); // kernel messages
    public static final int LOG_USER = (1 << 3); // random user-level messages
    public static final int LOG_MAIL = (2 << 3); // mail system
    public static final int LOG_DAEMON = (3 << 3); // system daemons
    public static final int LOG_AUTH = (4 << 3); // security/authorization
    public static final int LOG_SYSLOG = (5 << 3); // internal syslogd use
    public static final int LOG_LPR = (6 << 3); // line printer subsystem
    public static final int LOG_NEWS = (7 << 3); // network news subsystem
    public static final int LOG_UUCP = (8 << 3); // UUCP subsystem
    public static final int LOG_CRON = (15 << 3); // clock daemon
    // Other codes through 15 reserved for system use.
    public static final int LOG_LOCAL0 = (16 << 3); // reserved for local use
    public static final int LOG_LOCAL1 = (17 << 3); // reserved for local use
    public static final int LOG_LOCAL2 = (18 << 3); // reserved for local use
    public static final int LOG_LOCAL3 = (19 << 3); // reserved for local use
    public static final int LOG_LOCAL4 = (20 << 3); // reserved for local use
    public static final int LOG_LOCAL5 = (21 << 3); // reserved for local use
    public static final int LOG_LOCAL6 = (22 << 3); // reserved for local use
    public static final int LOG_LOCAL7 = (23 << 3); // reserved for local use

    public static final int LOG_FACMASK = 0x03F8; // mask to extract facility

    // Option flags.
    public static final int LOG_PID = 0x01; // log the pid with each message
    public static final int LOG_CONS = 0x02; // log on the console if errors
    public static final int LOG_NDELAY = 0x08; // don't delay open
    public static final int LOG_NOWAIT = 0x10; // don't wait for console forks

    // private final int logopt = LOG_CONS | LOG_NDELAY | LOG_NOWAIT;
    private static final int facility = Syslog.LOG_LOCAL0;

    private static void getBytes(String src, int srcBegin,
            int srcEnd,
            byte[] dst,
            int dstBegin) {
        try {
            byte[] buffer = src.getBytes("ISO-8859-1");

            for (int i = 0; (i < (srcEnd - srcBegin)) && (i < (dst.length - dstBegin)); i++) {
                dst[dstBegin + i] = buffer[srcBegin + i];
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void sendSyslog(String host, int port, String ident, int priority, String msg) throws IOException {
        int pricode;
        int length;
        int idx;
        byte[] data;
        String strObj;

        DatagramPacket packet;
        DatagramSocket socket;
        java.net.InetAddress address;
        address = InetAddress.getByName(host);
        socket = new DatagramSocket();

        pricode = Syslog.MakePriorityCode(Syslog.facility, priority);
        Integer priObj = new Integer(pricode);

        length = 4 + ident.length() + msg.length() + 1;
        length += (pricode > 99) ? 3 : ((pricode > 9) ? 2 : 1);

        data = new byte[length];

        idx = 0;
        data[idx++] = (byte) '<';

        strObj = Integer.toString(priObj.intValue());
        Syslog.getBytes(strObj, 0, strObj.length(), data, idx);
        idx += strObj.length();

        data[idx++] = (byte) '>';

        Syslog.getBytes(ident, 0, ident.length(), data, idx);
        idx += ident.length();

        data[idx++] = (byte) ':';
        data[idx++] = (byte) ' ';

        Syslog.getBytes(msg, 0, msg.length(), data, idx);
        idx += msg.length();

        data[idx] = 0;

        packet = new DatagramPacket(data, length, address, port);

        socket.send(packet);
        
        packet = null;
        socket.close();
        socket = null;
        address = null;
    }

    private static int MakePriorityCode(int facility, int priority) {
        return ((facility & Syslog.LOG_FACMASK) | priority);
    }

}
