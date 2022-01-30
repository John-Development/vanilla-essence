package net.vanillaEssence.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class SortedProperties extends Properties {
  private static final long serialVersionUID = 1L;

  public Set<Object> keySet() {
    return Collections.unmodifiableSet(new TreeSet<>(super.keySet()));
  }

  public Set<Map.Entry<Object, Object>> entrySet() {
    Set<Map.Entry<Object, Object>> set1 = super.entrySet();
    Set<Map.Entry<Object, Object>> set2 = new LinkedHashSet<>(set1.size());

    Iterator<Map.Entry<Object, Object>> iterator = set1.stream().sorted(Comparator.comparing(o -> o.getKey().toString())).iterator();

    while (iterator.hasNext())
      set2.add(iterator.next());

    return set2;
  }

  public Enumeration<Object> keys() {
    return Collections.enumeration(new TreeSet<>(super.keySet()));
  }
}

public class PropertiesCache {
  private final SortedProperties configProp = new SortedProperties();

  private static String levelName = "";

  private final String configRoute;

  Logger logger = Logger.getLogger(PropertiesCache.class.getName());

  /**
   * Default config initialiser
   */
  private PropertiesCache() {
    configRoute = Constants.DEFAULT_CONFIG_FILE;

    try {
      // If config file does not exists creates a new one with the default values
      if (!((new File(configRoute)).createNewFile())) {
        logger.log(Level.INFO, "File creation failed.");
      }

      InputStream in = new FileInputStream(configRoute);
      configProp.load(in);

      initMissing();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private PropertiesCache(String name) {
    levelName = name;
    configRoute = Constants.CONFIG_DIRECTORY.concat("/").concat(String.format(Constants.CONFIG_FILE, name));

    try {
      File configDirectory = new File(Constants.CONFIG_DIRECTORY);
      if (!configDirectory.isDirectory() && !configDirectory.mkdir()) {
        logger.log(Level.INFO, "Directory creation failed.");
      }

      File defaultConfigFile = new File(Constants.DEFAULT_CONFIG_FILE);
      File configFile = new File(configRoute);

      if (defaultConfigFile.isFile() && !configFile.isFile()) {
        // Creates specific world config file from the old default one
        Path copied = Paths.get(configRoute);
        Path originalPath = defaultConfigFile.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
      }

      InputStream in = new FileInputStream(configRoute);
      configProp.load(in);

      initMissing();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void init() {

  }

  private void initMissing() throws IOException {
    boolean hasChanged = false;

    if (this.getProperty("beacons-enabled") == null) {
      this.setProperty("beacons-enabled", "false");
      hasChanged = true;
    }
    if (this.getProperty("crystal-enabled") == null) {
      this.setProperty("crystal-enabled", "false");
      hasChanged = true;
    }
    if (this.getProperty("crystal-radius") == null) {
      this.setProperty("crystal-radius", Constants.DEF_CRYSTAL_RAD);
      hasChanged = true;
    }
    if (this.getProperty("crystal-lower-limit-distance") == null) {
      this.setProperty("crystal-lower-limit-distance", Constants.DEF_CRYSTAL_LIM_DISTANCE);
      hasChanged = true;
    }
    if (this.getProperty("crystal-name") == null) {
      this.setProperty("crystal-name", Constants.DEF_CRYSTAL_NAME);
      hasChanged = true;
    }
    if (this.getProperty("sand-enabled") == null) {
      this.setProperty("sand-enabled", "false");
      hasChanged = true;
    }
    if (this.getProperty("vill-enabled") == null){
      this.setProperty("vill-enabled", "false");
      hasChanged = true;
    }
    if (this.getProperty("vill-daily-restocks") == null){
      this.setProperty("vill-daily-restocks", Constants.DEF_VILL_RESTOCK);
      hasChanged = true;
    }
    if (this.getProperty("vill-time-between-restocks") == null){
      this.setProperty("vill-time-between-restocks", Constants.DEF_VILL_RESTOCK_COOLDOWN);
      hasChanged = true;
    }
    if (this.getProperty("magnetic-lure-enabled") == null){
      this.setProperty("magnetic-lure-enabled", "false");
      hasChanged = true;
    }
    if (this.getProperty("redstoned-jukeboxes-enabled") == null){
      this.setProperty("redstoned-jukeboxes-enabled", "false");
      hasChanged = true;
    }
    if (this.getProperty("riptide-fix-enabled") == null){
      this.setProperty("riptide-fix-enabled", "false");
      hasChanged = true;
    }
    if (this.getProperty("riptide-fix-multiplier") == null){
      this.setProperty("riptide-fix-multiplier", Constants.DEF_RIPTIDE_MULTIPLIER);
      hasChanged = true;
    }

    if (hasChanged) {
      //Write to the file
      this.flush();
    }
  }

  public static void setLevelName(String name) {
    if (!Objects.equals(name, levelName)) {
      LazyHolder.resetInstance(name);
    }
  }

  private static class LazyHolder {
    private static final PropertiesCache DEFAULT_INSTANCE = new PropertiesCache();
    private static PropertiesCache INSTANCE;

    protected static void resetInstance(String name) {
      INSTANCE = new PropertiesCache(name);
    }
  }

  public static PropertiesCache getInstance() {
    PropertiesCache cache = LazyHolder.INSTANCE;
    if (cache == null) {
      return PropertiesCache.getDefaultInstance();
    }
    return cache;
  }

  public static PropertiesCache getDefaultInstance() {
    return LazyHolder.DEFAULT_INSTANCE;
  }

  public String getProperty(String key) {
    return configProp.getProperty(key);
  }

  public boolean getBoolProperty(String key) {
    return Boolean.parseBoolean(configProp.getProperty(key));
  }

  public int getIntProperty(String key) {
    return Integer.parseInt(configProp.getProperty(key));
  }

  public long getLongProperty(String key) {
    return Long.parseLong(configProp.getProperty(key));
  }

  public double getDoubleProperty(String key) {
    return Double.parseDouble(configProp.getProperty(key));
  }

  public void setProperty(String key, String i) {
    configProp.setProperty(key, i);
  }

  public void flush() throws IOException {
    try (final OutputStream outputstream = new FileOutputStream(configRoute)) {
      configProp.store(outputstream, null);
    }
  }
}