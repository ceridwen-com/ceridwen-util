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

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    this.processor = processor;
    scheduler = new Timer();
    if (period < 1) {
      period = 600000;
    }
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
  public Object peek(int n) {
    return queue.peek(n);
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
