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
package com.ceridwen.util.collections;

import java.io.IOException;
import java.io.Serializable;
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

public class Spooler<E extends Serializable> extends TimerTask implements Queue<E> {
    private static Log log = LogFactory.getLog(Spooler.class);
    private Queue<E> queue;
    private Timer scheduler;
    private SpoolerProcessor<E> processor;

    public Spooler(Queue<E> queue, SpoolerProcessor<E> processor, long delay, long period) {
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
                E o = this.queue.remove();
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
    public void add(E o) throws IOException {
        this.queue.add(o);
    }

    @Override
    public E remove() throws IOException {
        return this.queue.remove();
    }

    @Override
    public E peek() {
        return this.queue.peek();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public int size() {
        return this.queue.size();
    }
}
