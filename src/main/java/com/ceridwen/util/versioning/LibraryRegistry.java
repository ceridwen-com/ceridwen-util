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
package com.ceridwen.util.versioning;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class LibraryRegistry {
    private Hashtable<LibraryIdentifier, Attributes> libraries = new Hashtable<LibraryIdentifier, Attributes>();
    
    public LibraryRegistry() {
    	loadLibraries();
    }
    
    private String getLibraryData(Attributes attr, String key) {
    	if (attr != null) {
    		String value = attr.getValue("Implementation-" + key);
    		if (value != null) {
    			return value;
    		}
    		value = attr.getValue("Specification-" + key);
    		if (value != null) {
    			return value;
    		}
    	}
    	return null;    	
    }
    
    private String getLibraryData(LibraryIdentifier id, String key) {
    	String value = getLibraryData(libraries.get(id), key);
    	if (value == null) {
    		return "(unknown)"; 
    	} else {
    		return value;
    	}
    }

    public Set<LibraryIdentifier> listLibraries() {
    	return libraries.keySet();
    }

    public String getLibraryName(LibraryIdentifier id) {
    	return getLibraryData(id, "Title");
    }
    
    public String getLibraryVersion(LibraryIdentifier id) {
    	return getLibraryData(id, "Version");
    }

    public String getLibraryBuildDate(LibraryIdentifier id) {
    	return getLibraryData(id, "Build-Date");
    }


    public String getLibraryVendor(LibraryIdentifier id) {
    	return getLibraryData(id, "Vendor");
    }
    
    public String getLibraryVendorId(LibraryIdentifier id) {
    	return getLibraryData(id, "Vendor-Id");
    }
    
    private void loadLibraries() {
        Enumeration<URL> resEnum;
        try {
        	libraries.clear();
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        Attributes mainAttribs = manifest.getMainAttributes();
                        String libraryName = getLibraryData(mainAttribs, "Title");
                        String vendorId = getLibraryData(mainAttribs, "Vendor-Id");
                        if (libraryName != null) {
                        	LibraryIdentifier id = new LibraryIdentifier(vendorId, libraryName);
	                    	if (libraries.containsKey(id)) {
	                    		//TODO: check for different versions of same library
	                    	} else {
	                    		libraries.put(id, mainAttribs);
	                    	}
                        }
                    }
                }
                catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
        } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
        }
    }
    
    public static void main(String[] args) {
    	LibraryRegistry reg = new LibraryRegistry();
    	for (LibraryIdentifier id: reg.listLibraries()) {
    		System.out.println(reg.getLibraryName(id) + " " +
    						   reg.getLibraryVersion(id) + " " +
    						   reg.getLibraryBuildDate(id) + " " +    							
    						   reg.getLibraryVendor(id) + " " +    							
    						   reg.getLibraryVendorId(id));
    	}
    	
//    	AboutDialog dlg = new AboutDialog(null, true, new LibraryIdentifier("org.apache", "Commons Lang"));
//    	dlg.setVisible(true);
    }

}
