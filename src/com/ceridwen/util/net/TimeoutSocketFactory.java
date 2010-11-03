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
package com.ceridwen.util.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.net.DefaultSocketFactory;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TimeoutSocketFactory
    extends DefaultSocketFactory {
  private int connectionTimeout = 0;

  public TimeoutSocketFactory(int _int) {
    connectionTimeout = _int;
  }

  /**
   * createSocket
   *
   * @param string String
   * @param _int int
   * @return Socket
   * @throws UnknownHostException
   * @throws IOException
   */
  public Socket createSocket(String string, int _int) throws
      UnknownHostException, IOException {
    InetSocketAddress sAdd = new InetSocketAddress(string, _int);
    Socket socket = new Socket();
    socket.connect(sAdd, connectionTimeout);
    return socket;
  }

  /**
   * createSocket
   *
   * @param inetAddress InetAddress
   * @param _int int
   * @return Socket
   * @throws IOException
   */
  public Socket createSocket(InetAddress inetAddress, int _int) throws
      IOException {
    InetSocketAddress sAdd = new InetSocketAddress(inetAddress, _int);
    Socket socket = new Socket();
    System.out.println("Timeout: " + connectionTimeout);
    socket.connect(sAdd, connectionTimeout);
    return socket;
  }

  /**
   * createSocket
   *
   * @param inetAddress InetAddress
   * @param _int int
   * @param inetAddress2 InetAddress
   * @param _int3 int
   * @return Socket
   * @throws IOException
   */
  public Socket createSocket(InetAddress inetAddress, int _int,
                             InetAddress inetAddress2, int _int3) throws
      IOException {
    InetSocketAddress sAdd = new InetSocketAddress(inetAddress, _int);
    InetSocketAddress sLocal = new InetSocketAddress(inetAddress2, _int3);
    Socket socket = new Socket();
    socket.bind(sLocal);
    System.out.println("Timeout: " + connectionTimeout);
    socket.connect(sAdd, connectionTimeout);
    return socket;
  }

  /**
   * createSocket
   *
   * @param string String
   * @param _int int
   * @param inetAddress InetAddress
   * @param _int3 int
   * @return Socket
   * @throws UnknownHostException
   * @throws IOException
   */
  public Socket createSocket(String string, int _int, InetAddress inetAddress,
                             int _int3) throws UnknownHostException,
      IOException {
    InetSocketAddress sAdd = new InetSocketAddress(string, _int);
    InetSocketAddress sLocal = new InetSocketAddress(inetAddress, _int3);
    Socket socket = new Socket();
    socket.bind(sLocal);
    System.out.println("Timeout: " + connectionTimeout);
    socket.connect(sAdd, connectionTimeout);
    return socket;
  }
}
