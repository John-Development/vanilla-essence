package net.vanillaEssence.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
 
class SortedProperties extends Properties {
  private static final long serialVersionUID = 1L;

  public Set<Object> keySet() {
    return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
  }

  public Set<Map.Entry<Object, Object>> entrySet() {
    Set<Map.Entry<Object, Object>> set1 = super.entrySet();
    Set<Map.Entry<Object, Object>> set2 = new LinkedHashSet<Map.Entry<Object, Object>>(set1.size());

    Iterator<Map.Entry<Object, Object>> iterator = set1.stream().sorted(new Comparator<Map.Entry<Object, Object>>() {
      @Override
      public int compare(java.util.Map.Entry<Object, Object> o1, java.util.Map.Entry<Object, Object> o2) {
        return o1.getKey().toString().compareTo(o2.getKey().toString());
      }
    }).iterator();

    while (iterator.hasNext())
      set2.add(iterator.next());

    return set2;
  }
  public Enumeration<Object> keys() {
    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
  }
}

public class PropertiesCache {
  private final SortedProperties configProp = new SortedProperties();
  final public static String CONFIG_FILE = "config/essence.properties";

  private PropertiesCache() {
    try {
      // If config file does not exists creates a new one with the default values
      (new File(CONFIG_FILE)).createNewFile();

      InputStream in = new FileInputStream(CONFIG_FILE);
      configProp.load(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class LazyHolder {
    private static final PropertiesCache INSTANCE = new PropertiesCache();
  }

  public static PropertiesCache getInstance() {
    return LazyHolder.INSTANCE;
  }
  
  public <T> T getProperty(String key, Class<T> type) {
    Object property = configProp.getProperty(key);
    return type.cast(property);
  }

  public String getProperty(String key) {
    return configProp.getProperty(key);
  }
  
  public Set<String> getAllPropertyNames() {
    return configProp.stringPropertyNames();
  }
  
  public boolean containsKey(String key) {
    return configProp.containsKey(key);
  }

  public void setProperty(String key, String i) {
    configProp.setProperty(key, i);
  }
   
  public void flush() throws FileNotFoundException, IOException {
    try (final OutputStream outputstream = new FileOutputStream(CONFIG_FILE);) {
      configProp.store(outputstream, null);
      outputstream.close();
    }
  }
}