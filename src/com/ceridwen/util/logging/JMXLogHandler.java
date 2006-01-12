package com.ceridwen.util.logging;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import javax.management.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.ceridwen.util.management.notification.ThreadedNotificationBroadcasterSupport;

public class JMXLogHandler extends AbstractLogHandler implements JMXLogHandlerMBean,
    NotificationEmitter {
  private static Log log = LogFactory.getLog(JMXLogHandler.class);

  private ThreadedNotificationBroadcasterSupport broadcaster = new
      ThreadedNotificationBroadcasterSupport();
  private long seq = 0;

  private void initializeManagement()
  {
    try {
      MBeanServer mbs;
      mbs =
          ManagementFactory.getPlatformMBeanServer();
      StandardMBean mbean = new StandardMBean(this,
                                              JMXLogHandlerMBean.class);
      mbs.registerMBean(mbean,
                        new ObjectName(
          "com.ceridwen.util.logging:type=LogHandler"));
    } catch (NullPointerException ex1) {
      log.fatal("MBean error", ex1);
    } catch (MalformedObjectNameException ex1) {
      log.fatal("MBean error", ex1);
    } catch (MBeanRegistrationException ex1) {
      log.fatal("MBean error", ex1);
    } catch (InstanceAlreadyExistsException ex1) {
      log.fatal("MBean error", ex1);
    } catch (NotCompliantMBeanException ex1) {
      log.fatal("MBean error", ex1);
    } catch (NoClassDefFoundError ex1) {
      log.fatal("MBean error", ex1);
    }
  }

  public JMXLogHandler()
  {
    initializeManagement();
  }

  public void close() throws SecurityException
  {
  }

  public void flush()
  {
  }

  protected void sendMessage(String logger, String level, String message)
  {
    Notification notification = new Notification(level, this, seq++, message);
    notification.setUserData(logger);
    broadcaster.sendNotification(notification);
  }

  public void setLoggingLevel(String level) {
    this.setLevel(Level.parse(level));
  }

  public String getLoggingLevel() {
    return this.getLevel().getName();
  }

  public void removeNotificationListener(NotificationListener listener,
                                         NotificationFilter filter,
                                         Object handback) throws
      ListenerNotFoundException {
    broadcaster.removeNotificationListener(listener, filter, handback);
  }

  public void addNotificationListener(NotificationListener listener,
                                      NotificationFilter filter,
                                      Object handback) throws
      IllegalArgumentException {
    broadcaster.addNotificationListener(listener, filter, handback);
  }

  public void removeNotificationListener(NotificationListener listener) throws
      ListenerNotFoundException {
    broadcaster.removeNotificationListener(listener);
  }

  public MBeanNotificationInfo[] getNotificationInfo() {
    return new MBeanNotificationInfo[] {
        new MBeanNotificationInfo(new String[] {
                                  Level.FINEST.getName(),
                                  Level.FINER.getName(),
                                  Level.FINE.getName(),
                                  Level.CONFIG.getName(),
                                  Level.INFO.getName(),
                                  Level.WARNING.getName(),
                                  Level.SEVERE.getName()
      }, "Logging alert", "Logging alert")
    };
  }

  static {
    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(JMXLogHandler.class);
  }

}
