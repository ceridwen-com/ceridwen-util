package com.ceridwen.util.logging;

public interface JMXLogHandlerMBean {
  void setLoggingLevel(String level);
  String getLoggingLevel();
}
