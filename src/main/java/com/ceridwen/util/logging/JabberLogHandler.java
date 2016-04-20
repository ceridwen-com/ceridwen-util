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

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class JabberLogHandler
    extends AbstractLogHandler {

  private String host;
  private String username;
  private String password;
  private String recipient;

  protected AbstractXMPPConnection con;

  public JabberLogHandler(String host, String username, String password, String recipient) {
    this.host = host;
    this.username = username;
    this.password = password;
    this.recipient = recipient;
  }

  public void open() {
      try {
    	  con = new XMPPTCPConnection(username, password, host);
    	  con.connect();
    	  con.login();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
  }


  /**
   * Close the <tt>Handler</tt> and free all associated resources.
   *
   * @throws SecurityException if a security manager exists and if the caller
   *   does not have <tt>LoggingPermission("control")</tt>.
   */
  public void close() throws SecurityException {
    // Closes the connection by setting presence to unavailable
    // then closing the stream to the XMPP server.
    if (con != null) {
      con.disconnect();
    }
    // Help GC
    con = null;
  }

  /**
   * Flush any buffered output.
   *
   */
  public void flush() {
  }

  protected void sendMessage(String logger, String level, String message) {
    try {
      open();
      
      ChatManager chatmanager = ChatManager.getInstanceFor(con);  
      Chat chat = chatmanager.createChat(recipient);
     
      chat.sendMessage(message);
      
      chat.close();
      
      close();
    }
    catch (NotConnectedException ex) {
    }
  }
}
