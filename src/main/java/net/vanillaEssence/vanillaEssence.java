package net.vanillaEssence;

import net.fabricmc.api.ModInitializer;
import net.vanillaEssence.commands.GameRuleCustomCommand;
import net.vanillaEssence.events.CopperOxidizeEvent;
import net.vanillaEssence.loot.Sand;
import net.vanillaEssence.util.Tweaks;

public class vanillaEssence implements ModInitializer {
  @Override
  public void onInitialize() {
    // Loot table for sand
    Sand.getInstance().init();
    // Props init
    Tweaks.getDefaultInstance().init();
    // Init commands
    GameRuleCustomCommand.getInstance().init();
    // Register events
    CopperOxidizeEvent.registerOxidation();
  }
}
