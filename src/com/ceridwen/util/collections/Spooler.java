/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey (www.ceridwen.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at 
 * <http://www.gnu.org/licenses/>
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
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 ******************************************************************************/
package com.ceridwen.util.collections;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title: RTSI</p> <p>Description: Real Time Self Issue</p> <p>Copyright:
 * </p> <p>Company: </p>
 * 
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class Spooler extends TimerTask implements Queue {
    private static Log log = LogFactory.getLog(Spooler.class);
    private Queue queue;
    private Timer scheduler;
    private SpoolerProcessor processor;

    public Spooler(Queue queue, SpoolerProcessor processor, long delay, long period) {
        this.queue = queue;
        this.processor = processor;
        this.scheduler = new Timer();
        if (period < 1) {
            period = 600000;
        }
        this.scheduler.schedule(this, delay, period);
    }

    public void cancelScheduler() {
        this.scheduler.cancel();
    }

    @Override
    public void run() {
        int loop = this.queue.size();
        for (int n = 0; n < loop; n++) {
            try {
                Object o = this.queue.remove();
                if (!this.processor.process(o)) {
                    this.queue.add(o);
                }
            } catch (Exception ex) {
                Spooler.log.error("Exception in spooler thread", ex);
            }
        }
    }

    @Override
    protected void finalize() throws java.lang.Throwable {
        this.cancelScheduler();
        super.finalize();
    }

    @Override
    public void add(Object o) {
        this.queue.add(o);
    }

    @Override
    public Object remove() {
        return this.queue.remove();
    }

    @Override
    public Object peek(int n) {
        return this.queue.peek(n);
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    static {
        com.ceridwen.util.versioning.ComponentRegistry.registerComponent(Spooler.class);
    }
}
