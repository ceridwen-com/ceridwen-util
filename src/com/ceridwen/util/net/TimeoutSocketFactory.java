package com.ceridwen.util.net;

import org.apache.commons.net.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;

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
  int connectionTimeout = 0;

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
   * @todo Implement this org.apache.commons.net.SocketFactory method
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
   * @todo Implement this org.apache.commons.net.SocketFactory method
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
   * @todo Implement this org.apache.commons.net.SocketFactory method
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
   * @todo Implement this org.apache.commons.net.SocketFactory method
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
