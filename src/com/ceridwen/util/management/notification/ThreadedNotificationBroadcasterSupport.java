package com.ceridwen.util.management.notification;

import java.util.Enumeration;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

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

public class ThreadedNotificationBroadcasterSupport implements NotificationEmitter {
  private java.util.Hashtable listeners = new java.util.Hashtable();


  public ThreadedNotificationBroadcasterSupport() {
  }

  public void removeNotificationListener(NotificationListener listener,
                                         NotificationFilter filter,
                                         Object handback) throws
      ListenerNotFoundException {
    listeners.remove(listener);
  }

  public void addNotificationListener(NotificationListener listener,
                                      NotificationFilter filter,
                                      Object handback) throws
      IllegalArgumentException {
    NotificationListenerStuff stuff = new NotificationListenerStuff();
    stuff.filter = filter;
    stuff.handback = handback;
    listeners.put(listener, stuff);
  }

  public void removeNotificationListener(NotificationListener listener) throws
      ListenerNotFoundException {
    listeners.remove(listener);
  }

  public MBeanNotificationInfo[] getNotificationInfo() {
    throw new java.lang.UnsupportedOperationException(
        "getNotificationInfo must be overriden");
  }

  public void sendNotification(Notification notification) {
    BroadcasterThread thread = new BroadcasterThread(listeners, notification);
    thread.start();
  }
}

class BroadcasterThread extends Thread {
  private NotificationBroadcasterSupport broadcaster = new NotificationBroadcasterSupport();
  private Notification notification;

  public BroadcasterThread(java.util.Hashtable listeners, Notification notification) {
    Enumeration enumeration = listeners.keys();

    while (enumeration.hasMoreElements()) {
      NotificationListener listener = (NotificationListener)enumeration.nextElement();
      NotificationListenerStuff stuff = (NotificationListenerStuff)listeners.get(listener);
      broadcaster.addNotificationListener(listener, stuff.filter, stuff.handback);
    }

    this.notification = notification;
  }

  public void run() {
    broadcaster.sendNotification(notification);
  }
}

class NotificationListenerStuff {
  public NotificationFilter filter;
  public Object handback;
}
