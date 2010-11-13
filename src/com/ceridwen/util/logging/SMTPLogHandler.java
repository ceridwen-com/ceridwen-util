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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.apache.commons.net.smtp.SMTPClient;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

/**
 * @todo: adjust synchronous connection
 * 
 */

public class SMTPLogHandler extends AbstractLogHandler {
    private MailerDaemon daemon;
    private String recipient;

    public SMTPLogHandler(String host, String recipient, String sender) {
        this.recipient = recipient;
        this.daemon = new MailerDaemon(host, sender);
        this.daemon.start();
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

    static {
        com.ceridwen.util.versioning.ComponentRegistry.registerComponent(SMTPLogHandler.class);
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

    private String mailfrom;
    private String mailrelay;
    private Stack<QueuedMail> queue = new Stack<QueuedMail>();

    public MailerDaemon(String relay, String from) {
        this.mailfrom = from;
        this.mailrelay = relay;
    }

    public synchronized void addToQueue(String[] recipients, String subject, String message) {
        this.queue.push(new QueuedMail(recipients, subject, message));
        this.timer = 0;
    }

    private synchronized void sendMail() {
        Stack<QueuedMail> failed = new Stack<QueuedMail>();
        try {
            SMTPClient smtp = new SMTPClient();
            smtp.connect(this.mailrelay);
            smtp.setSoTimeout(60000);
            if (smtp.login()) {
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
            }
            smtp.logout();
            smtp.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int count = 0;
        while ((count < 50) & !failed.isEmpty()) {
            this.queue.push(failed.pop());
            count++;
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
                } catch (Exception ex) {
                    this.timer = 0;
                }
            }
        }
    }
}
