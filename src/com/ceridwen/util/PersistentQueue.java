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
package com.ceridwen.util;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

/**@todo: push exception handling up?
 *
 */


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PersistentQueue implements Queue {
  private static Log log = LogFactory.getLog(PersistentQueue.class);

  private File store;

  private synchronized Object load(int n, boolean delete) {
      try {
        File [] files = store.listFiles();
        if (files.length > 0) {
          log.trace("Loading queued object");
          XMLDecoder xml = new XMLDecoder(new FileInputStream(files[n]));
          Object o = null;
          try {
            o = xml.readObject();
          } catch (Exception ex) {
            log.error("Problem loading object", ex);
          }
          xml.close();
          if (delete) {
            log.trace("Deleting queued object");
            if (!files[0].delete()) {
              throw new java.lang.NullPointerException(
                  "Loaded object was not deleted");
            }
            if (files[0].exists()) {
              throw new java.lang.NullPointerException(
                  "Loaded object was still present");
            }
          }
          log.trace("Returning queued object");
          return o;
        } else {
          throw new java.lang.ArrayIndexOutOfBoundsException("Queue is empty");
        }
      } catch (Exception ex) {
        log.error("Problem reading queue directory", ex);
        return null;
      }
  }

  private java.util.Random rand = new java.util.Random();

  private String UID(Object o) {
    String id = Long.toHexString(System.currentTimeMillis()) + "-" + Integer.toHexString(o.hashCode()) + "-" + Integer.toHexString(rand.nextInt());
    return id;
  }

  private synchronized void save(Object o) {
    try {
      XMLEncoder xml = new XMLEncoder(new FileOutputStream(new File(store, UID(o))));
      xml.writeObject(o);
      xml.flush();
      xml.close();
    } catch (Exception ex) {
      log.error("Could not save (" + o + ") to persistent queue: " + store.getAbsolutePath());
    }
  }

  public PersistentQueue(File file) {
    try {
      store = file;

      if (!store.exists()) {
        if (!store.mkdirs()) {
          throw new java.io.FileNotFoundException();
        }
      }

      if (!store.isDirectory()) {
        throw new java.io.FileNotFoundException();
      }
    } catch (Exception ex) {
      log.fatal("Could not create queue store: " + ((store != null)?store.getAbsolutePath():null));
    }
  }

  public void add(Object obj) {
    this.save(obj);
  }

  public Object remove() {
    return this.load(0, true);
  }

  public Object peek(int n) {
    return this.load(n, false);
  }

  public int size() {
    try {
      return store.listFiles().length;
    } catch (Exception ex) {
      log.fatal("Could not determine queue size", ex);
      return 0;
    }
  }

  public boolean isEmpty() {
    try {
      return (store.listFiles().length == 0);
    } catch (Exception ex) {
      log.fatal("Could not determine queue size", ex);
      return true;
    }
  }



  public static void main(String[] args) {
    PersistentQueue q = new PersistentQueue(new File("c:/temp/queue"));
    new Spooler(q, new SpoolerProcessor() {
      public boolean process(Object o) {
        System.out.println("Item: " + o);
        return true;
      }
    }

    , 10000);

    q.add("20");
    q.add("19");
    q.add("18");
    q.add("17");
    q.add("16");
    q.add("15");
    q.add("14");
    q.add("13");
    q.add("12");
    q.add("11");
    q.add("10");
    q.add("9");
    q.add("7");
    q.add("8");
    q.add("6");
    q.add("5");
    q.add("4");
    q.add("3");
    q.add("2");
    q.add("1");

    int size = -1;
    while (true) {
      if (q.size() != size) {
        System.out.println("Size: " + q.size());
        size = q.size();
      }
    }
  }

}
