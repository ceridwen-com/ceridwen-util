package com.ceridwen.util.logging;

import java.util.logging.*;
import java.util.Properties;
import java.util.Iterator;
import java.util.Enumeration;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.*;

public class JabberLogHandler
    extends Handler {

  private String host;
  private int port;
  private String username;
  private String password;
  private String recipient;
  private boolean chatroom = false;
  private String nickname;
  private String app;
  private boolean SSL = false;

  protected XMPPConnection con;
  protected Chat chat;
  protected GroupChat groupchat;

  public JabberLogHandler(String host, int port, String username, String password, String app) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.app = app;
    recipient = "logs@" + host;
    try {
      nickname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
    } catch (Exception ex) {
      nickname = "unknown";
    }
    open();
  }

  public void open() {
      try {
          // Create a connection to the XMPP server
          if (SSL) {
              con = new SSLXMPPConnection(host, port);
          } else {
              con = new XMPPConnection(host, port);
          }

          // Most servers require you to login before performing other tasks
          con.login(username, password, app);

          // Start a conversation with IMAddress
          if (chatroom) {
              groupchat = con.createGroupChat(recipient);
              groupchat.join(nickname != null ? nickname : username);
          } else {
              chat = con.createChat(recipient);
          }

      } catch (Exception ex) {
        ex.printStackTrace();
      }
  }


  /**
   * Close the <tt>Handler</tt> and free all associated resources.
   *
   * @throws SecurityException if a security manager exists and if the caller
   *   does not have <tt>LoggingPermission("control")</tt>.
   * @todo Implement this java.util.logging.Handler method
   */
  public void close() throws SecurityException {
    // Closes the connection by setting presence to unavailable
    // then closing the stream to the XMPP server.
    if (con != null)
        con.close();

    // Help GC
    con = null;
    chat = null;
    groupchat = null;
  }

  /**
   * Flush any buffered output.
   *
   * @todo Implement this java.util.logging.Handler method
   */
  public void flush() {
  }

  /**
   * Publish a <tt>LogRecord</tt>.
   *
   * @param record description of the log event
   * @todo Implement this java.util.logging.Handler method
   */
  public void publish(LogRecord record) {
    if (!this.isLoggable(record)) {
       return;
     }

     Formatter format = this.getFormatter();
     if (format == null)
       format = new java.util.logging.XMLFormatter();

     String message = format.format(record);
     try {
       StringBuffer components = new StringBuffer("\r\nRegistered Components:");
       Iterator iter = com.ceridwen.util.versioning.ComponentRegistry.listRegisteredComponents();
       while (iter.hasNext()) {
         Class component = (Class)iter.next();
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
       Enumeration enumerate = props.keys();
       while (enumerate.hasMoreElements()) {
         String key = (String)enumerate.nextElement();
         environment.append("\r\n" + key + "=" + props.getProperty(key));
       }
       message = message + "\r\n" + components.toString() + "\r\n" +
           environment;
       if (chatroom) {
         groupchat.sendMessage(message);
       }
       else {
         chat.sendMessage(message);
       }

     } catch (Exception ex) {
       ex.printStackTrace();
    }
  }
}
