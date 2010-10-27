/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
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
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
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
  private java.util.Hashtable<NotificationListener, NotificationListenerStuff> listeners = new java.util.Hashtable<NotificationListener, NotificationListenerStuff>();


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

  public BroadcasterThread(java.util.Hashtable<NotificationListener, NotificationListenerStuff> listeners, Notification notification) {
    Enumeration<NotificationListener> enumeration = listeners.keys();

    while (enumeration.hasMoreElements()) {
      NotificationListener listener = enumeration.nextElement();
      NotificationListenerStuff stuff = listeners.get(listener);
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
