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
  private static Hashtable components = new Hashtable();

  public static void registerComponent(Class component) {
    InputStream in = null;
    try {
      String[] bits = component.getName().split("\\.");
      in = component.getResourceAsStream(bits[bits.length-1] + ".num");
      Properties props = new Properties();
      props.load(in);
      in.close();
      components.put(component, props);
    } catch (Exception ex) {
    }
  }

  public static Iterator listRegisteredComponents() {
    ComponentRegistry.registerComponent(ComponentRegistry.class);
    return components.keySet().iterator();
  }
  public static String getVersionString(Class component) {
    return "Version " + getVersion(component) + " (Built: " + getBuildDate(component) + ")";
  }
  public static String getVersion(Class component) {
    Properties props = (Properties)components.get(component);
    if (props == null) {
      return null;
    }
    return props.getProperty("version","") + "." + Long.toString(getBuild(component));
  }
  public static long getBuild(Class component) {
    Properties props = (Properties)components.get(component);
    if (props == null) {
      return 0;
    }
    return Long.parseLong(props.getProperty("build","0"));
  }
  public static String getBuildDate(Class component) {
    Properties props = (Properties)components.get(component);
    if (props == null) {
      return null;
    }
    return props.getProperty("date","");
  }
  public static long getEpoch(Class component) {
    Properties props = (Properties)components.get(component);
    if (props == null) {
      return 0;
    }
    return Long.parseLong(props.getProperty("epoch","0"));
  }
  public static String getAuthor(Class component) {
    Properties props = (Properties)components.get(component);
    if (props == null) {
      return null;
    }
    return props.getProperty("author","");
  }
  public static String getName(Class component) {
    Properties props = (Properties)components.get(component);
    if (props == null) {
      return null;
    }
    return props.getProperty("name","");
  }


}
