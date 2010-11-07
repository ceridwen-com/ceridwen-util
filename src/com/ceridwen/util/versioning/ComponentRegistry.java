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

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

public class ComponentRegistry {
    private static Hashtable<Class<?>, Properties> components = new Hashtable<Class<?>, Properties>();

    public static void registerComponent(Class<?> component) {
        InputStream in = null;
        try {
            String[] bits = component.getName().split("\\.");
            in = component.getResourceAsStream(bits[bits.length - 1] + ".num");
            Properties props = new Properties();
            props.load(in);
            in.close();
            ComponentRegistry.components.put(component, props);
        } catch (Exception ex) {
        }
    }

    public static Iterator<Class<?>> listRegisteredComponents() {
        ComponentRegistry.registerComponent(ComponentRegistry.class);
        return ComponentRegistry.components.keySet().iterator();
    }

    public static String getVersionString(Class<?> component) {
        return "Version " + ComponentRegistry.getVersion(component) + " (Built: " + ComponentRegistry.getBuildDate(component) + ")";
    }

    public static String getVersion(Class<?> component) {
        Properties props = ComponentRegistry.components.get(component);
        if (props == null) {
            return null;
        }
        return props.getProperty("version", "") + "." + Long.toString(ComponentRegistry.getBuild(component));
    }

    public static long getBuild(Class<?> component) {
        Properties props = ComponentRegistry.components.get(component);
        if (props == null) {
            return 0;
        }
        return Long.parseLong(props.getProperty("build", "0"));
    }

    public static String getBuildDate(Class<?> component) {
        Properties props = ComponentRegistry.components.get(component);
        if (props == null) {
            return null;
        }
        return props.getProperty("date", "");
    }

    public static long getEpoch(Class<?> component) {
        Properties props = ComponentRegistry.components.get(component);
        if (props == null) {
            return 0;
        }
        return Long.parseLong(props.getProperty("epoch", "0"));
    }

    public static String getAuthor(Class<?> component) {
        Properties props = ComponentRegistry.components.get(component);
        if (props == null) {
            return null;
        }
        return props.getProperty("author", "");
    }

    public static String getName(Class<?> component) {
        Properties props = ComponentRegistry.components.get(component);
        if (props == null) {
            return null;
        }
        return props.getProperty("name", "");
    }

}
