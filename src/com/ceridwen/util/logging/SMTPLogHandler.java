package com.ceridwen.util.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.apache.commons.net.smtp.SMTPClient;
import java.net.UnknownHostException;
import java.net.InetAddress;

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

public class SMTPLogHandler extends AbstractLogHandler {
  private MailerDaemon daemon;
  private String recipient;

  public SMTPLogHandler(String host, String recipient) {
    this.recipient = recipient;
    daemon = new MailerDaemon(host, "logger@ceridwen.com");
    daemon.start();
  }

  public void flush() {
  }

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
        address += Integer.toString((int)(ip[i] & 0x00ff));
        if (i < (ip.length-1)) {
          address += ".";
        }
      }
    } catch (UnknownHostException ex1) {
    }


    daemon.addToQueue(new String[]{recipient}, "Java Log Report for " + hostname + " (" + address + ") - " + logger, message);
  }

  public void close() throws java.lang.SecurityException {
    daemon.stopMailer();
  }

  static {
    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(SMTPLogHandler.class);
  }

}

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
  private Stack queue = new Stack();

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
          if (mail.recipients.length > 0) {
            expandTo.append(mail.recipients[0]);
          }
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
    while (count < 50 & !failed.isEmpty()) {
      queue.push(failed.pop());
      count++;
    }
  }

  private boolean active;

  public void stopMailer() {
    active = false;
  }

  private int timer;

  public void run() {
    active = true;
    while (active) {
      if (!queue.isEmpty()) {
        this.sendMail();
      }
      timer = 5;
      while (timer > 0) {
        if (!active) {
          return;
        }
        try {
          sleep(1000);
          timer--;
        }
        catch (Exception ex) {
          timer = 0;
        }
      }
    }
  }
}
