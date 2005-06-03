package com.ceridwen.util.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;
import java.util.logging.XMLFormatter;
import java.util.Iterator;
import java.util.Properties;
import java.util.Enumeration;

abstract public class AbstractLogHandler
    extends Handler {
  protected AbstractLogHandler() {
    super();
  }

  abstract void sendMessage(String logger, int level, String message);

  /**
   * Publish a <tt>LogRecord</tt>.
   *
   * @param record description of the log event
   * @todo Implement this java.util.logging.Handler method
   */
  public void publish(LogRecord record) {
    if (!this.isLoggable(record)) {
      return;
    }

    Formatter format = this.getFormatter();
    if (format == null)
      format = new XMLFormatter();

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
      sendMessage(record.getLoggerName(), 0, message);

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
