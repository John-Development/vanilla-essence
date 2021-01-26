package net.vanillaEssence;

import java.io.File;
import java.io.IOException;

import net.fabricmc.api.ModInitializer;
import net.vanillaEssence.util.PropertiesCache;

public class vanillaEssence implements ModInitializer {

  final public static String DEF_CRYSTAL_RAD = "32";
  final public static String DEF_CRYSTAL_LIM_DISTANCE = "1";
  final public static String DEF_CRYSTAL_NAME = "";
  final public static String DEF_SCAFF_LIMIT = "";
  final public static String CONFIG_FILE = "essence.properties";

  @Override
  public void onInitialize() {
    try {
      File configFile = new File(CONFIG_FILE);
      // If config file does not exists creates a new one with the default values
      configFile.createNewFile();

      PropertiesCache cache = PropertiesCache.getInstance();
      if (cache.getProperty("crystal-enabled") == null) {
        cache.setProperty("crystal-enabled", "true");
      }
      if (cache.getProperty("crystal-radius") == null) {
        cache.setProperty("crystal-radius", DEF_CRYSTAL_RAD);
      }
      if (cache.getProperty("crystal-lower-limit-distance") == null){
        cache.setProperty("crystal-lower-limit-distance", DEF_CRYSTAL_LIM_DISTANCE);
      }
      if (cache.getProperty("crystal-name") == null){
        cache.setProperty("crystal-name", DEF_CRYSTAL_NAME);
      }
      if (cache.getProperty("scaff-enabled") == null){
        cache.setProperty("scaff-enabled", "true");
      }
      if (cache.getProperty("scaff-limit") == null){
        cache.setProperty("scaff-limit", DEF_SCAFF_LIMIT);
      }
      
      //Write to the file
      PropertiesCache.getInstance().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
