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
