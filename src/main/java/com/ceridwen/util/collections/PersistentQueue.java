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
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class PersistentQueue<E extends Serializable> implements Queue<E> {
    private static Log log = LogFactory.getLog(PersistentQueue.class);

    private File store;

    private synchronized E load(int n, boolean delete) {
        try {
            File[] files = this.store.listFiles();
            if (files.length > 0) {
                PersistentQueue.log.trace("Loading queued object");
                XMLDecoder xml = new XMLDecoder(new FileInputStream(files[n]));
                E o = null;
                try {
                    o = (E)xml.readObject();
                } catch (Exception ex) {
                    PersistentQueue.log.error("Problem loading object", ex);
                }
                xml.close();
                if (delete) {
                    PersistentQueue.log.trace("Deleting queued object");
                    if (!files[0].delete()) {
                        throw new java.lang.NullPointerException(
                                "Loaded object was not deleted");
                    }
                    if (files[0].exists()) {
                        throw new java.lang.NullPointerException(
                                "Loaded object was still present");
                    }
                }
                PersistentQueue.log.trace("Returning queued object");
                return o;
            } else {
                throw new java.lang.ArrayIndexOutOfBoundsException("Queue is empty");
            }
        } catch (Exception ex) {
            PersistentQueue.log.error("Problem reading queue directory", ex);
            return null;
        }
    }

    private java.util.Random rand = new java.util.Random();

    private String UID(E o) {
        String id = Long.toHexString(System.currentTimeMillis()) + "-" + Integer.toHexString(o.hashCode()) + "-" + Integer.toHexString(this.rand.nextInt());
        return id;
    }

    private synchronized void save(E o) {
        try {
            XMLEncoder xml = new XMLEncoder(new FileOutputStream(new File(this.store, this.UID(o))));
            xml.writeObject(o);
            xml.flush();
            xml.close();
        } catch (Exception ex) {
            PersistentQueue.log.error("Could not save (" + o + ") to persistent queue: " + this.store.getAbsolutePath());
        }
    }

    public PersistentQueue(String file) throws IOException {
    	this(new File(file));    	
    }
    
    public PersistentQueue(File file) throws IOException {
        try {
            this.store = file;

            if (!this.store.exists()) {
                if (!this.store.mkdirs()) {
                    throw new java.io.FileNotFoundException();
                }
            }

            if (!this.store.isDirectory()) {
                throw new java.io.FileNotFoundException();
            }
        } catch (Exception ex) {
            PersistentQueue.log.fatal("Could not create queue store: " + ((this.store != null) ? this.store.getAbsolutePath() : null));
            throw new IOException(ex);
        }
    }

    @Override
	public void add(E obj) throws IOException {
        this.save(obj);
    }

    @Override
    public E remove() throws IOException {
        return this.load(0, true);
    }

	@Override
	public E peek() {
        return this.load(0, false);
    }

    @Override
    public int size() {
        try {
            return this.store.listFiles().length;
        } catch (Exception ex) {
            PersistentQueue.log.fatal("Could not determine queue size", ex);
            return 0;
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            return (this.store.listFiles().length == 0);
        } catch (Exception ex) {
            PersistentQueue.log.fatal("Could not determine queue size", ex);
            return true;
        }
    }

    public static void main(String[] args) {
        try {
	        PersistentQueue<String> q = new PersistentQueue<String>(new File("c:/temp/queue"));
	        new Spooler<String>(q, new SpoolerProcessor<String>() {
	            @Override
	            public boolean process(String o) {
	                System.out.println("Item: " + o);
	                return true;
	            }
	        }, 10000, 10000);

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
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}
