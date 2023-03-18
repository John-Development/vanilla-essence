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

public class Tweaks {
  private final SortedProperties configProp = new SortedProperties();

  private static String levelName = "";

  private final String configRoute;

  Logger logger = Logger.getLogger(Tweaks.class.getName());

  /**
   * Default config initialiser
   */
  private Tweaks() {
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

  private Tweaks(String name) {
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

    if (this.getProperty(TweaksEnum.BETTER_BEACONS) == null) {
      this.setProperty(TweaksEnum.BETTER_BEACONS, "false");
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.DO_END_CRYSTALS_LIMIT_SPAWN) == null) {
      this.setProperty(TweaksEnum.DO_END_CRYSTALS_LIMIT_SPAWN, "false");
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.END_CRYSTAL_RADIUS) == null) {
      this.setProperty(TweaksEnum.END_CRYSTAL_RADIUS, Constants.DEF_CRYSTAL_RAD);
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.END_CRYSTAL_LOWER_LIMIT_DISTANCE) == null) {
      this.setProperty(TweaksEnum.END_CRYSTAL_LOWER_LIMIT_DISTANCE, Constants.DEF_CRYSTAL_LIM_DISTANCE);
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.END_CRYSTAL_NAME) == null) {
      this.setProperty(TweaksEnum.END_CRYSTAL_NAME, Constants.DEF_CRYSTAL_NAME);
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.HUSK_DROP_SAND) == null) {
      this.setProperty(TweaksEnum.HUSK_DROP_SAND, "false");
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.MODIFY_VILLAGERS) == null) {
      this.setProperty(TweaksEnum.MODIFY_VILLAGERS, "false");
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.DAILY_VILLAGER_RESTOCKS) == null) {
      this.setProperty(TweaksEnum.DAILY_VILLAGER_RESTOCKS, Constants.DEF_VILL_RESTOCK);
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.TIME_BETWEEN_VILLAGER_RESTOCKS) == null) {
      this.setProperty(TweaksEnum.TIME_BETWEEN_VILLAGER_RESTOCKS, Constants.DEF_VILL_RESTOCK_COOLDOWN);
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.MAGNETIC_LURE) == null) {
      this.setProperty(TweaksEnum.MAGNETIC_LURE, "false");
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.SPLASH_OXIDIZE) == null) {
      this.setProperty(TweaksEnum.SPLASH_OXIDIZE, "false");
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.ONE_LVL_RENAMING) == null) {
      this.setProperty(TweaksEnum.ONE_LVL_RENAMING, "false");
      hasChanged = true;
    }
    if (this.getProperty(TweaksEnum.INFINITE_ENCHANTING) == null) {
      this.setProperty(TweaksEnum.INFINITE_ENCHANTING, "false");
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
    private static final Tweaks DEFAULT_INSTANCE = new Tweaks();
    private static Tweaks INSTANCE;

    protected static void resetInstance(String name) {
      INSTANCE = new Tweaks(name);
    }
  }

  public static Tweaks getInstance() {
    Tweaks tweaks = LazyHolder.INSTANCE;
    if (tweaks == null) {
      return Tweaks.getDefaultInstance();
    }
    return tweaks;
  }

  public static Tweaks getDefaultInstance() {
    return LazyHolder.DEFAULT_INSTANCE;
  }

  public String getProperty(TweaksEnum key) {
    return configProp.getProperty(key.getName());
  }

  public boolean getBoolProperty(TweaksEnum key) {
    return Boolean.parseBoolean(configProp.getProperty(key.getName()));
  }

  public int getIntProperty(TweaksEnum key) {
    return Integer.parseInt(configProp.getProperty(key.getName()));
  }

  public double getDoubleProperty(TweaksEnum key) {
    return Double.parseDouble(configProp.getProperty(key.getName()));
  }

  public long getLongProperty(TweaksEnum key) {
    return Long.parseLong(configProp.getProperty(key.getName()));
  }

  public void setProperty(TweaksEnum key, String i) {
    configProp.setProperty(key.getName(), i);
  }

  public void flush() throws IOException {
    try (final OutputStream outputstream = new FileOutputStream(configRoute)) {
      configProp.store(outputstream, null);
    }
  }
}