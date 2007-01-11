package com.ceridwen.util;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

import java.util.Vector;

public class TransientQueue implements Queue {
  private Vector items;
  public TransientQueue() {
    items = new Vector();
  }
  public void add(Object o) {
    items.add(o);
  }
  public Object remove() {
    if (items.isEmpty()) {
      return null;
    } else {
      return items.remove(0);
    }
  }
  public Object peek(int n) {
    return items.get(n);
  }
  public boolean isEmpty() {
    return items.isEmpty();
  }
  public int size() {
    return items.size();
  }
}
