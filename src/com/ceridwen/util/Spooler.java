package com.ceridwen.util;

import org.apache.commons.logging.*;
import java.util.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class Spooler extends TimerTask implements Queue {
  private static Log log = LogFactory.getLog(Spooler.class);
  private Queue queue;
  private Timer scheduler;
  private SpoolerProcessor processor;

  public Spooler(Queue queue, SpoolerProcessor processor, long period) {
    this.queue = queue;
    this.processor = (SpoolerProcessor)processor;
    scheduler = new Timer();
    if (period < 1)
      period = 600000;
    scheduler.schedule(this, period, period);
  }

  public void cancelScheduler() {
    scheduler.cancel();
  }

  public void run() {
    int loop = queue.size();
    for (int n=0; n<loop; n++) {
      try {
        Object o = queue.remove();
        if (!processor.process(o)) {
          queue.add(o);
        }
      } catch (Exception ex) {
        log.error ("Exception in spooler thread", ex);
      }
    }
  }
  protected void finalize() throws java.lang.Throwable {
    this.cancelScheduler();
    super.finalize();
  }
  public void add(Object o) {
    queue.add(o);
  }
  public Object remove() {
    return queue.remove();
  }
  public boolean isEmpty() {
    return queue.isEmpty();
  }
  public int size() {
    return queue.size();
  }

  static {
    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(Spooler.class);
  }
}
