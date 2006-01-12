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
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
// OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
// LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
// OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
//
// Visit the ACME Labs Java page for up-to-date versions of this and other
// fine Java utilities: http://www.acme.com/java/


package com.ceridwen.util.logging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SyslogLogHandler
    extends AbstractLogHandler {
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
  public static final int LOG_KERN = (0<<3); // kernel messages
  public static final int LOG_USER = (1<<3); // random user-level messages
  public static final int LOG_MAIL = (2<<3); // mail system
  public static final int LOG_DAEMON = (3<<3); // system daemons
  public static final int LOG_AUTH = (4<<3); // security/authorization
  public static final int LOG_SYSLOG = (5<<3); // internal syslogd use
  public static final int LOG_LPR = (6<<3); // line printer subsystem
  public static final int LOG_NEWS = (7<<3); // network news subsystem
  public static final int LOG_UUCP = (8<<3); // UUCP subsystem
  public static final int LOG_CRON = (15<<3); // clock daemon
  // Other codes through 15 reserved for system use.
  public static final int LOG_LOCAL0 = (16<<3); // reserved for local use
  public static final int LOG_LOCAL1 = (17<<3); // reserved for local use
  public static final int LOG_LOCAL2 = (18<<3); // reserved for local use
  public static final int LOG_LOCAL3 = (19<<3); // reserved for local use
  public static final int LOG_LOCAL4 = (20<<3); // reserved for local use
  public static final int LOG_LOCAL5 = (21<<3); // reserved for local use
  public static final int LOG_LOCAL6 = (22<<3); // reserved for local use
  public static final int LOG_LOCAL7 = (23<<3); // reserved for local use

  public static final int LOG_FACMASK = 0x03F8; // mask to extract facility

  // Option flags.
  public static final int LOG_PID = 0x01; // log the pid with each message
  public static final int LOG_CONS = 0x02; // log on the console if errors
  public static final int LOG_NDELAY = 0x08; // don't delay open
  public static final int LOG_NOWAIT = 0x10; // don't wait for console forks

//  private final int logopt = LOG_CONS | LOG_NDELAY | LOG_NOWAIT;
  private final int facility = LOG_LOCAL0;

  private int port = 514;
  private String host;


  /// Creating a Syslog instance is equivalent of the Unix openlog() call.
  // @exception SyslogException if there was a problem
  public SyslogLogHandler(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /// Use this method to log your syslog messages. The facility and
  // level are the same as their Unix counterparts, and the Syslog
  // class provides constants for these fields. The msg is what is
  // actually logged.
  // @exception SyslogException if there was a problem
  public void sendMessage(String logger, String level, String message) {
    sendMessage(logger, LOG_ALERT, message);
  }

  public void getBytes(String src, int srcBegin,
                       int srcEnd,
                       byte[] dst,
                       int dstBegin) {
    try {
      byte[] buffer = src.getBytes("ISO-8859-1");

      for (int i = 0; (i < (srcEnd - srcBegin) + 1) && (i < (buffer.length - dstBegin) + 1); i++) {
        dst[dstBegin + i] = buffer[srcBegin + i];
      }
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }


  public void sendMessage(String ident, int priority, String msg) {
    int pricode;
    int length;
    int idx;
    byte[] data;
    String strObj;

    DatagramPacket packet;
    DatagramSocket socket;
    java.net.InetAddress address;
    try {
      address = InetAddress.getByName(this.host);
    }
    catch (UnknownHostException e) {
      System.err.println(
          "error locating localhost: " + e.getMessage());
      return;
    }
    try {
      socket = new DatagramSocket();
    }
    catch (SocketException e) {
      System.err.println(
          "error creating syslog udp socket: " + e.getMessage());
      return;
    }
    catch (IOException e) {
      System.err.println(
          "error creating syslog udp socket: " + e.getMessage());
      return;
    }

    pricode = MakePriorityCode(facility, priority);
    Integer priObj = new Integer(pricode);

    length = 4 + ident.length() + msg.length() + 1;
    length += (pricode > 99) ? 3 : ( (pricode > 9) ? 2 : 1);

    data = new byte[length];

    idx = 0;
    data[idx++] = (byte) '<';

    strObj = priObj.toString(priObj.intValue());
    getBytes(strObj, 0, strObj.length(), data, idx);
    idx += strObj.length();

    data[idx++] = (byte) '>';

    getBytes(ident, 0, ident.length(), data, idx);
    idx += ident.length();

    data[idx++] = (byte) ':';
    data[idx++] = (byte) ' ';

    getBytes(msg, 0, msg.length(), data, idx);
    idx += msg.length();

    data[idx] = 0;

    packet = new DatagramPacket(data, length, address, this.port);

    try {
      socket.send(packet);
    }
    catch (IOException e) {
      System.err.println(
          "error sending message: '" + e.getMessage() + "'");
    }

    packet = null;
    socket.close();
    socket = null;
    address = null;
  }

  private int MakePriorityCode(int facility, int priority) {
    return ( (facility & LOG_FACMASK) | priority);
  }

  /**
   * Close the <tt>Handler</tt> and free all associated resources.
   *
   * @throws SecurityException if a security manager exists and if the caller
   *   does not have <tt>LoggingPermission("control")</tt>.
   */
  public void close() throws SecurityException {
  }

  /**
   * Flush any buffered output.
   *
   */
  public void flush() {
  }

  /**
   * sendMessage
   *
   * @param logger String
   * @param level int
   * @param message String
   */

  public static void main(String args[]) {
    SyslogLogHandler test = new SyslogLogHandler("host.ceridwen.com", 514);
    test.sendMessage("test", 0, "test");

  }

}
