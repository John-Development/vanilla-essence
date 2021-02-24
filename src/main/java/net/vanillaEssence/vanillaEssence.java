package net.vanillaEssence;

import java.io.IOException;

import net.fabricmc.api.ModInitializer;
import net.vanillaEssence.commands.GameRuleCustomCommand;
import net.vanillaEssence.loot.Sand;
import net.vanillaEssence.util.PropertiesCache;

public class vanillaEssence implements ModInitializer {

  public static final String DEF_CRYSTAL_RAD = "32";
  public static final String DEF_CRYSTAL_LIM_DISTANCE = "1";
  public static final String DEF_CRYSTAL_NAME = "";
  public static final String DEF_SCAFF_LIMIT = "";
  public static final String DEF_VILL_RESTOCK = "2";
  public static final String DEF_VILL_RESTOCK_COOLDOWN = "2400";

  @Override
  public void onInitialize() {
    try {
      PropertiesCache cache = PropertiesCache.getInstance();
      if (cache.getProperty("crystal-enabled") == null) {
        cache.setProperty("crystal-enabled", "false");
      }
      if (cache.getProperty("crystal-radius") == null) {
        cache.setProperty("crystal-radius", DEF_CRYSTAL_RAD);
      }
      if (cache.getProperty("crystal-lower-limit-distance") == null) {
        cache.setProperty("crystal-lower-limit-distance", DEF_CRYSTAL_LIM_DISTANCE);
      }
      if (cache.getProperty("crystal-name") == null) {
        cache.setProperty("crystal-name", DEF_CRYSTAL_NAME);
      }
      if (cache.getProperty("scaff-enabled") == null) {
        cache.setProperty("scaff-enabled", "false");
      }
      if (cache.getProperty("scaff-limit") == null) {
        cache.setProperty("scaff-limit", DEF_SCAFF_LIMIT);
      }
      if (cache.getProperty("sand-enabled") == null) {
        cache.setProperty("sand-enabled", "false");
      }
      if (cache.getProperty("vill-enabled") == null){
        cache.setProperty("vill-enabled", "true");
      }
      if (cache.getProperty("vill-daily-restocks") == null){
        cache.setProperty("vill-daily-restocks", DEF_VILL_RESTOCK);
      }
      if (cache.getProperty("vill-time-between-restocks") == null){
        cache.setProperty("vill-time-between-restocks", DEF_VILL_RESTOCK_COOLDOWN);
      }
      
      //Write to the file
      cache.flush();

      // Loot table for sand
      Sand.getInstance().init();

      // Init commands
      GameRuleCustomCommand.getInstance().init();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
