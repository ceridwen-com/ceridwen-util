package com.ceridwen.util.logging;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;
import org.apache.commons.net.smtp.*;
import java.util.Date;
import java.util.Iterator;
import java.lang.StringBuffer;
import java.util.logging.*;
import java.text.SimpleDateFormat;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

/**@todo: adjust synchronous connection
 *
 */


class QueuedMail {
  public QueuedMail(String[] r, String s, String m) {
    recipients = r;
    subject=s;
    message = m;
    date = new Date();
  }

  public Date date;
  public String[] recipients;
  public String subject;
  public String message;
}

class MailerDaemon extends Thread {

  private String mailfrom;
  private String mailrelay;
  Stack queue = new Stack();

  public MailerDaemon(String relay, String from) {
    mailfrom = from;
    mailrelay = relay;
  }

  public synchronized void addToQueue(String[] recipients, String subject, String message) {
    queue.push(new QueuedMail(recipients, subject, message));
    timer = 0;
  }

  private synchronized void sendMail() {
    Stack failed = new Stack();
    try {
      SMTPClient smtp = new SMTPClient();
      smtp.connect(mailrelay);
      smtp.setSoTimeout(60000);
      if (smtp.login()) {
        while (!queue.isEmpty()) {
          QueuedMail mail = (QueuedMail) queue.pop();
          StringBuffer expandTo = new StringBuffer();
          if (mail.recipients.length > 0)
            expandTo.append(mail.recipients[0]);
          for (int n=1; n<mail.recipients.length; n++) {
            expandTo.append(",");
            expandTo.append(mail.recipients[n]);
          }
          if (!smtp.sendSimpleMessage(mailfrom,
                                      mail.recipients,
                                      "From: " + mailfrom + "\r\n" +
                                      "To: " + expandTo + "\r\n" +
                                      "Date: " + new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z").format(mail.date) + "\r\n" +
                                      "Subject: " + mail.subject + "\r\n\r\n" +
                                      mail.message))
            failed.push(mail);
        }
      }
      smtp.logout();
      smtp.disconnect();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    int count = 0;
    while (count < 50 & !failed.isEmpty()) {
      queue.push(failed.pop());
    }
  }

  private boolean active;

  public void stopMailer() {
    active = false;
  }

  int timer;

  public void run() {
    active = true;
    while (active) {
      if (!queue.isEmpty()) {
        this.sendMail();
      }
      timer = 5;
      while (timer > 0) {
        if (!active)
          return;
        try {
          this.sleep(1000);
          timer--;
        }
        catch (Exception ex) {
          timer = 0;
        }
      }
    }
  }
}

public class SMTPLogHandler extends java.util.logging.Handler {
  MailerDaemon daemon;
  String recipient;

  public SMTPLogHandler(String host, String recipient) {
    this.recipient = recipient;
    daemon = new MailerDaemon(host, "logger@ceridwen.com");
    daemon.start();
  }

  public void flush() {
  }

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
      Enumeration enum = props.keys();
      while (enum.hasMoreElements()) {
        String key = (String)enum.nextElement();
        environment.append("\r\n" + key + "=" + props.getProperty(key));
      }
      message = message + "\r\n" + components.toString() + "\r\n" + environment;
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    String hostname = "unknown";
    try {
      hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
    } catch (Exception ex) {

    }
    daemon.addToQueue(new String[]{recipient}, "Java Log Report for " + hostname + " - " + record.getLoggerName(), message);
  }

  public void close() throws java.lang.SecurityException {
    daemon.stopMailer();
  }

  static {
    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(SMTPLogHandler.class);
  }

}
