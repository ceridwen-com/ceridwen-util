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

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

import java.io.Serializable;
import java.util.Vector;

public class TransientQueue<E extends Serializable> implements Queue<E> {
    private Vector<E> items;

    public TransientQueue() {
        this.items = new Vector<E>();
    }

    @Override
    public void add(E o) {
        this.items.add(o);
    }

    @Override
    public E remove() {
        if (this.items.isEmpty()) {
            return null;
        } else {
            return this.items.remove(0);
        }
    }

    @Override
    public E peek() {
        return this.items.get(0);
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public int size() {
        return this.items.size();
    }
}
