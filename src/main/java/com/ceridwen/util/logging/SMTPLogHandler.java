/* 
 * Copyright 2019 Ceridwen Limited.
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

/**
 * TODO adjust synchronous connection
 * 
 */

public class SMTPLogHandler extends AbstractLogHandler {
    private final MailerDaemon daemon;
    private final String recipient;

    public SMTPLogHandler(String host, int port, String recipient, String sender, boolean ssl, String username, String password) {
        this.recipient = recipient;
        this.daemon = new MailerDaemon(host, sender, port, ssl, username, password);
        this.daemon.start();
    }
    
    public SMTPLogHandler(String host, String recipient, String sender, boolean ssl) {
        this(host, 25, recipient, sender, ssl, null, null);
    }
    
    public SMTPLogHandler(String host, String recipient, String sender) {
        this(host, 25, recipient, sender, false, null, null);
    }

    @Override
    public void flush() {
    }

    @Override
    protected void sendMessage(String logger, String level, String message) {
        String hostname = "unknown";
        try {
            hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
        } catch (Exception ex) {
        }

        String address = "";
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            byte[] ip = addr.getAddress();
            for (int i = 0; i < ip.length; i++) {
                address += Integer.toString((ip[i] & 0x00ff));
                if (i < (ip.length - 1)) {
                    address += ".";
                }
            }
        } catch (UnknownHostException ex1) {
        }

        this.daemon.addToQueue(new String[] { this.recipient }, "Java Log Report for " + hostname + " (" + address + ") - " + logger, message);
    }

    @Override
    public void close() throws java.lang.SecurityException {
        this.daemon.stopMailer();
    }
}

class QueuedMail {
    public QueuedMail(String[] r, String s, String m) {
        this.recipients = r;
        this.subject = s;
        this.message = m;
        this.date = new Date();
    }

    public Date date;
    public String[] recipients;
    public String subject;
    public String message;
}

class MailerDaemon extends Thread {

    private final String mailfrom;
    private final String mailrelay;
    private final Stack<QueuedMail> queue = new Stack<>();
    private final boolean ssl;
    private final String username;
    private final String password;
    private final int port;

    public MailerDaemon(String relay, String from, int port, boolean ssl, String username, String password) {
        this.mailfrom = from;
        this.mailrelay = relay;
        this.port = port;
        this.ssl = ssl;
        this.username = username;
        this.password = password;
    }

    public synchronized void addToQueue(String[] recipients, String subject, String message) {
        this.queue.push(new QueuedMail(recipients, subject, message));
        this.timer = 0;
    }

    private synchronized void sendMail() {
        try {
            Stack<QueuedMail> failed = new Stack<>();
            
            AuthenticatingSMTPClient smtp = new AuthenticatingSMTPClient("TLS", false);
            smtp.connect(this.mailrelay, this.port);
            smtp.setSoTimeout(60000);

            if (ssl) {
                if (!smtp.elogin()) {
                    throw new IOException("EHLO failed on host " + this.mailrelay);
                }
                if (!smtp.execTLS()) {
                    throw new IOException("StartTLS failed on host " + this.mailrelay);
                }
            } else {
                if (!smtp.login()) {
                    throw new IOException("HELO failed on host " + this.mailrelay);
                }
            }

            if (username != null && !username.isBlank()) {
                if (!smtp.elogin()) {
                    throw new IOException("EHLO failed on host " + this.mailrelay);
                }                
                if (!smtp.auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, username, password)) {
                    throw new IOException("Authentication failed on host " + this.mailrelay);
                }
            }                 

            while (!this.queue.isEmpty()) {
                QueuedMail mail = this.queue.pop();
                StringBuffer expandTo = new StringBuffer();
                if (mail.recipients.length > 0) {
                    expandTo.append(mail.recipients[0]);
                }
                for (int n = 1; n < mail.recipients.length; n++) {
                    expandTo.append(",");
                    expandTo.append(mail.recipients[n]);
                }
                if (!smtp.sendSimpleMessage(this.mailfrom,
                        mail.recipients,
                        "From: " + this.mailfrom + "\r\n" +
                                "To: " + expandTo + "\r\n" +
                                        "Date: " + new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z").format(mail.date) + "\r\n" +
                                                "Subject: " + mail.subject + "\r\n\r\n" +
                                mail.message)) {
                    failed.push(mail);
                }
            }

            smtp.logout();
            smtp.disconnect();
            
            
            int count = 0;
            while ((count < 50) & !failed.isEmpty()) {
                this.queue.push(failed.pop());
                count++;
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException ex) {
            Logger.getLogger(MailerDaemon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean active;

    public void stopMailer() {
        this.active = false;
    }

    private int timer;

    @Override
    public void run() {
        this.active = true;
        while (this.active) {
            if (!this.queue.isEmpty()) {
                this.sendMail();
            }
            this.timer = 5;
            while (this.timer > 0) {
                if (!this.active) {
                    return;
                }
                try {
                    Thread.sleep(1000);
                    this.timer--;
                } catch (InterruptedException ex) {
                    this.timer = 0;
                }
            }
        }
    }
}
