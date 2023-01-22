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
  @Serial
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

    if (this.getProperty(Tweaks.BETTER_BEACONS.getName()) == null) {
      this.setProperty(Tweaks.BETTER_BEACONS.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.DO_END_CRYSTALS_LIMIT_SPAWN.getName()) == null) {
      this.setProperty(Tweaks.DO_END_CRYSTALS_LIMIT_SPAWN.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.END_CRYSTAL_RADIUS.getName()) == null) {
      this.setProperty(Tweaks.END_CRYSTAL_RADIUS.getName(), Constants.DEF_CRYSTAL_RAD);
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.END_CRYSTAL_LOWER_LIMIT_DISTANCE.getName()) == null) {
      this.setProperty(Tweaks.END_CRYSTAL_LOWER_LIMIT_DISTANCE.getName(), Constants.DEF_CRYSTAL_LIM_DISTANCE);
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.END_CRYSTAL_NAME.getName()) == null) {
      this.setProperty(Tweaks.END_CRYSTAL_NAME.getName(), Constants.DEF_CRYSTAL_NAME);
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.HUSK_DROP_SAND.getName()) == null) {
      this.setProperty(Tweaks.HUSK_DROP_SAND.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.MODIFY_VILLAGERS.getName()) == null) {
      this.setProperty(Tweaks.MODIFY_VILLAGERS.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.DAILY_VILLAGER_RESTOCKS.getName()) == null) {
      this.setProperty(Tweaks.DAILY_VILLAGER_RESTOCKS.getName(), Constants.DEF_VILL_RESTOCK);
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.TIME_BETWEEN_VILLAGER_RESTOCKS.getName()) == null) {
      this.setProperty(Tweaks.TIME_BETWEEN_VILLAGER_RESTOCKS.getName(), Constants.DEF_VILL_RESTOCK_COOLDOWN);
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.MAGNETIC_LURE.getName()) == null) {
      this.setProperty(Tweaks.MAGNETIC_LURE.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.REDSTONED_JUKEBOXES.getName()) == null) {
      this.setProperty(Tweaks.REDSTONED_JUKEBOXES.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.SPLASH_OXIDIZE.getName()) == null) {
      this.setProperty(Tweaks.SPLASH_OXIDIZE.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.ONE_LVL_RENAMING.getName()) == null) {
      this.setProperty(Tweaks.ONE_LVL_RENAMING.getName(), "false");
      hasChanged = true;
    }
    if (this.getProperty(Tweaks.INFINITE_ENCHANTING.getName()) == null) {
      this.setProperty(Tweaks.INFINITE_ENCHANTING.getName(), "false");
      hasChanged = true;
    }
//    if (this.getProperty("riptide-fix-enabled") == null) {
//      this.setProperty("riptide-fix-enabled", "false");
//      hasChanged = true;
//    }
//    if (this.getProperty("riptide-fix-multiplier") == null) {
//      this.setProperty("riptide-fix-multiplier", Constants.DEF_RIPTIDE_MULTIPLIER);
//      hasChanged = true;
//    }

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

  public double getDoubleProperty(String key) {
    return Double.parseDouble(configProp.getProperty(key));
  }

  public long getLongProperty(String key) {
    return Long.parseLong(configProp.getProperty(key));
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