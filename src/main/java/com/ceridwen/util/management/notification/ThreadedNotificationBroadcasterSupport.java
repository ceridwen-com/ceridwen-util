/*******************************************************************************
 * Copyright 2016 Matthew J. Dovey (www.ceridwen.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    @Override
    public void removeNotificationListener(NotificationListener listener,
                                         NotificationFilter filter,
                                         Object handback) throws
            ListenerNotFoundException {
        this.listeners.remove(listener);
    }

    @Override
    public void addNotificationListener(NotificationListener listener,
                                      NotificationFilter filter,
                                      Object handback) throws
            IllegalArgumentException {
        NotificationListenerStuff stuff = new NotificationListenerStuff();
        stuff.filter = filter;
        stuff.handback = handback;
        this.listeners.put(listener, stuff);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws
            ListenerNotFoundException {
        this.listeners.remove(listener);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        throw new java.lang.UnsupportedOperationException(
                "getNotificationInfo must be overriden");
    }

    public void sendNotification(Notification notification) {
        BroadcasterThread thread = new BroadcasterThread(this.listeners, notification);
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
            this.broadcaster.addNotificationListener(listener, stuff.filter, stuff.handback);
        }

        this.notification = notification;
    }

    @Override
    public void run() {
        this.broadcaster.sendNotification(this.notification);
    }
}

class NotificationListenerStuff {
    public NotificationFilter filter;
    public Object handback;
}
