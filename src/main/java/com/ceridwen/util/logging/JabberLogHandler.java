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
