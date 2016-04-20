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
package com.ceridwen.util.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.net.DefaultSocketFactory;

public class TimeoutSocketFactory
        extends DefaultSocketFactory {
    private int connectionTimeout = 0;

    public TimeoutSocketFactory(int _int) {
        this.connectionTimeout = _int;
    }

    /**
     * createSocket
     * 
     * @param string
     *            String
     * @param _int
     *            int
     * @return Socket
     * @throws UnknownHostException
     *            Unknown host
     * @throws IOException
     *            I\O Error
     */
    @Override
    public Socket createSocket(String string, int _int) throws
            UnknownHostException, IOException {
        InetSocketAddress sAdd = new InetSocketAddress(string, _int);
        Socket socket = new Socket();
        socket.connect(sAdd, this.connectionTimeout);
        return socket;
    }

    /**
     * createSocket
     * 
     * @param inetAddress
     *            InetAddress
     * @param _int
     *            int
     * @return Socket
     * @throws IOException
     *            I\O Error
     */
    @Override
    public Socket createSocket(InetAddress inetAddress, int _int) throws
            IOException {
        InetSocketAddress sAdd = new InetSocketAddress(inetAddress, _int);
        Socket socket = new Socket();
        System.out.println("Timeout: " + this.connectionTimeout);
        socket.connect(sAdd, this.connectionTimeout);
        return socket;
    }

    /**
     * createSocket
     * 
     * @param inetAddress
     *            InetAddress
     * @param _int
     *            int
     * @param inetAddress2
     *            InetAddress
     * @param _int3
     *            int
     * @return Socket
     * @throws IOException
     *            I\O Error
     */
    @Override
    public Socket createSocket(InetAddress inetAddress, int _int,
                             InetAddress inetAddress2, int _int3) throws
            IOException {
        InetSocketAddress sAdd = new InetSocketAddress(inetAddress, _int);
        InetSocketAddress sLocal = new InetSocketAddress(inetAddress2, _int3);
        Socket socket = new Socket();
        socket.bind(sLocal);
        System.out.println("Timeout: " + this.connectionTimeout);
        socket.connect(sAdd, this.connectionTimeout);
        return socket;
    }

    /**
     * createSocket
     * 
     * @param string
     *            String
     * @param _int
     *            int
     * @param inetAddress
     *            InetAddress
     * @param _int3
     *            int
     * @return Socket
     * @throws UnknownHostException
     *            Unknown host
     * @throws IOException
     *            I\O Error
     */
    @Override
    public Socket createSocket(String string, int _int, InetAddress inetAddress,
                             int _int3) throws UnknownHostException,
            IOException {
        InetSocketAddress sAdd = new InetSocketAddress(string, _int);
        InetSocketAddress sLocal = new InetSocketAddress(inetAddress, _int3);
        Socket socket = new Socket();
        socket.bind(sLocal);
        System.out.println("Timeout: " + this.connectionTimeout);
        socket.connect(sAdd, this.connectionTimeout);
        return socket;
    }
}
