package com.ceridwen.util.logging;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

abstract public class AbstractLogHandler
    extends Handler {
  protected AbstractLogHandler() {
    super();
  }

  abstract protected void sendMessage(String logger, String level, String message);

  /**
   * Publish a <tt>LogRecord</tt>.
   *
   * @param record description of the log event
   */
  public void publish(LogRecord record) {
    if (!this.isLoggable(record)) {
      return;
    }

    Formatter format = this.getFormatter();
    if (format == null) {
      format = new XMLFormatter();
    }
    String message = format.format(record);
    try {
      StringBuffer components = new StringBuffer("\r\nRegistered Components:");
      Iterator iter = com.ceridwen.util.versioning.ComponentRegistry.
          listRegisteredComponents();
      while (iter.hasNext()) {
        Class component = (Class) iter.next();
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
      Enumeration enumerate = props.keys();
      while (enumerate.hasMoreElements()) {
        String key = (String) enumerate.nextElement();
        environment.append("\r\n" + key + "=" + props.getProperty(key));
      }
      message = message + "\r\n" + components.toString() + "\r\n" +
          environment;
      sendMessage(record.getLoggerName(), record.getLevel().getName(), message);

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
