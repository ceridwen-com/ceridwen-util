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

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.GroupChat;
import org.jivesoftware.smack.SSLXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class JabberLogHandler
    extends AbstractLogHandler {

  private String host;
  private int port;
  private String username;
  private String password;
  private String recipient;
  private final boolean chatroom = false;
  private String nickname;
  private String app;
  private final boolean SSL = false;

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
              groupchat.join((nickname != null)?nickname:username);
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
   */
  public void close() throws SecurityException {
    // Closes the connection by setting presence to unavailable
    // then closing the stream to the XMPP server.
    if (con != null) {
      con.close();
    }
    // Help GC
    con = null;
    chat = null;
    groupchat = null;
  }

  /**
   * Flush any buffered output.
   *
   */
  public void flush() {
  }

  protected void sendMessage(String logger, String level, String message) {
    try {
      if (chatroom) {
        groupchat.sendMessage(message);
      }
      else {
        chat.sendMessage(message);
      }
    }
    catch (XMPPException ex) {
    }
  }
}
