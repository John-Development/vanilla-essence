package net.vanillaEssence.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.vanillaEssence.loot.Sand;
import net.vanillaEssence.util.PropertiesCache;
import net.vanillaEssence.util.Tweaks;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GameRuleCustomCommand {

  private static class LazyHolder {
    private static final GameRuleCustomCommand INSTANCE = new GameRuleCustomCommand();
  }

  public static GameRuleCustomCommand getInstance() {
    return LazyHolder.INSTANCE;
  }

  public void init() {
    doHuskDropSandInit();
    dailyVillagerRestocksInit();
    doEndCrystalsLimitSpawnInit();
    betterBeaconsInit();
    magneticLureInit();
    redstonedJukeboxInit();
    oxidizeInit();
    infiniteEnchantingInit();
    oneLvlRenamingInit();
//    riptideMultiplierInit();
  }

  // Command example: /gamerule doHusksDropSand <value>
  private void doHuskDropSandInit() {
    genericCommandBuilderInit("doHusksDropSand", Tweaks.HUSK_DROP_SAND.getName());
  }

  // Command example: /gamerule betterBeacons <value>
  private void betterBeaconsInit() {
    genericCommandBuilderInit("betterBeacons", Tweaks.BETTER_BEACONS.getName());
  }

  // Command example: /gamerule magneticLure <value>
  private void magneticLureInit() {
    genericCommandBuilderInit("magneticLure", Tweaks.MAGNETIC_LURE.getName());
  }

  // Command example: /gamerule redstonedJukeboxes <value>
  private void redstonedJukeboxInit() {
    genericCommandBuilderInit("redstonedJukeboxes", Tweaks.REDSTONED_JUKEBOXES.getName());
  }

  // Command example: /gamerule splashOxidize <value>
  private void oxidizeInit() {
    genericCommandBuilderInit("splashOxidize", Tweaks.SPLASH_OXIDIZE.getName());
  }

  // Command example: /gamerule oneLvlRenaming <value>
  private void oneLvlRenamingInit() {
    genericCommandBuilderInit("oneLvlRenaming", Tweaks.ONE_LVL_RENAMING.getName());
  }

  // Command example: /gamerule infiniteEnchanting <value>
  private void infiniteEnchantingInit() {
    genericCommandBuilderInit("infiniteEnchanting", Tweaks.INFINITE_ENCHANTING.getName());
  }

  // Common builder
  private void genericCommandBuilderInit(String rule, String configValue) {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
      dispatcher.register(literal("gamerule")
        .requires(source -> source.hasPermissionLevel(4))
        .then(genericCommandBuilderHelper(rule, configValue))
      );
      dispatcher.register(literal("gamerule")
        .requires(source -> source.hasPermissionLevel(4))
        .then(literal("default")
          .then(genericCommandBuilderHelperDefault(rule, configValue))
        )
      );
    });
  }

  private ArgumentBuilder<ServerCommandSource, ?> genericCommandBuilderHelper(String rule, String configValue) {
    return literal(rule)
      .executes((context) -> printValue(context, PropertiesCache.getInstance(), configValue))
      .then(argument("value", BoolArgumentType.bool())
        .executes(context -> genericCommandBuilderCommonHelperExecute(context, configValue, PropertiesCache.getInstance()))
      );
  }

  private ArgumentBuilder<ServerCommandSource, ?> genericCommandBuilderHelperDefault(String rule, String configValue) {
    return literal(rule)
      .executes((context) -> printValue(context, PropertiesCache.getDefaultInstance(), configValue))
      .then(argument("value", BoolArgumentType.bool())
        .executes(context -> genericCommandBuilderCommonHelperExecute(context, configValue, PropertiesCache.getDefaultInstance()))
      );
  }

  private int genericCommandBuilderCommonHelperExecute(CommandContext<ServerCommandSource> context, String configValue, PropertiesCache cache) {
    cache.setProperty(configValue, ((Boolean) BoolArgumentType.getBool(context, "value")).toString());

    try {
      cache.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (Objects.equals(configValue, "doHusksDropSand")) {
      Sand.getInstance().init();
    }

    return reload(context);
  }

  // Command example: /gamerule riptideFix <value> <multiplier>
//  private void riptideMultiplierInit() {
//    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
//      dispatcher.register(literal("gamerule")
//        .requires(source -> source.hasPermissionLevel(4))
//        .then(riptideMultiplierHelper())
//      );
//      dispatcher.register(literal("gamerule")
//        .requires(source -> source.hasPermissionLevel(4))
//        .then(literal("default")
//          .then(riptideMultiplierHelperDefault())
//        )
//      );
//    });
//  }

//  private ArgumentBuilder<ServerCommandSource, ?> riptideMultiplierHelper() {
//    return literal("riptideFix")
//      .executes((context) -> printValue(context, PropertiesCache.getInstance(), "riptide-fix-enabled"))
//      .then(argument("value", BoolArgumentType.bool())
//        .then(argument("multiplier", DoubleArgumentType.doubleArg(0, 1))
//          .executes(context -> riptideFixCommonHelperExecute(context, PropertiesCache.getInstance()))
//        )
//      );
//  }
//
//  private ArgumentBuilder<ServerCommandSource, ?> riptideMultiplierHelperDefault() {
//    return literal("riptideFix")
//      .executes((context) -> printValue(context, PropertiesCache.getDefaultInstance(), "riptide-fix-enabled"))
//      .then(argument("value", BoolArgumentType.bool())
//        .then(argument("multiplier", DoubleArgumentType.doubleArg(0, 1))
//          .executes(context -> riptideFixCommonHelperExecute(context, PropertiesCache.getDefaultInstance()))
//        )
//      );
//  }

//  private int riptideFixCommonHelperExecute(CommandContext<ServerCommandSource> context, PropertiesCache cache) {
//    boolean value = BoolArgumentType.getBool(context, "value");
//    double multiplier = DoubleArgumentType.getDouble(context, "multiplier");
//
//    cache.setProperty("riptide-fix-enabled", Boolean.toString(value));
//    cache.setProperty("riptide-fix-multiplier", Double.toString(multiplier));
//
//    try {
//      cache.flush();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//    return reload(context);
//  }

  // Command example: /gamerule dailyVillagerRestocks <dailyRestocks> <timeBetweenRestocks>
  private void dailyVillagerRestocksInit() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
      dispatcher.register(literal("gamerule")
        .requires(source -> source.hasPermissionLevel(4))
        .then(dailyVillagersHelper())
      );
      dispatcher.register(literal("gamerule")
        .requires(source -> source.hasPermissionLevel(4))
        .then(literal("default")
          .then(dailyVillagersHelperDefault())
        )
      );
    });
  }

  private ArgumentBuilder<ServerCommandSource, ?> dailyVillagersHelper() {
    return literal("dailyVillagerRestocks")
      .executes((context) -> printValue(context, PropertiesCache.getInstance(), Tweaks.MODIFY_VILLAGERS.getName()))
      .then(argument("dailyRestocks", IntegerArgumentType.integer(0, 999))
        .then(argument("timeBetweenRestocks", IntegerArgumentType.integer(20, 2400))
          .executes(context -> dailyVillagersCommonHelperExecute(context, PropertiesCache.getInstance()))
        )
      );
  }

  private ArgumentBuilder<ServerCommandSource, ?> dailyVillagersHelperDefault() {
    return literal("dailyVillagerRestocks")
      .executes((context) -> printValue(context, PropertiesCache.getDefaultInstance(), Tweaks.MODIFY_VILLAGERS.getName()))
      .then(argument("dailyRestocks", IntegerArgumentType.integer(0, 999))
        .then(argument("timeBetweenRestocks", IntegerArgumentType.integer(20, 2400))
          .executes(context -> dailyVillagersCommonHelperExecute(context, PropertiesCache.getDefaultInstance()))
        )
      );
  }

  private int dailyVillagersCommonHelperExecute(CommandContext<ServerCommandSource> context, PropertiesCache cache) {
    int restocks = IntegerArgumentType.getInteger(context, "dailyRestocks");
    int cooldown = IntegerArgumentType.getInteger(context, "timeBetweenRestocks");

    cache.setProperty(Tweaks.MODIFY_VILLAGERS.getName(), ((Boolean) (restocks != 2)).toString());
    cache.setProperty(Tweaks.DAILY_VILLAGER_RESTOCKS.getName(), Integer.toString(restocks));
    cache.setProperty(Tweaks.TIME_BETWEEN_VILLAGER_RESTOCKS.getName(), Integer.toString(cooldown));

    try {
      cache.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return reload(context);
  }

  // Command example: /gamerule doEndCrystalsLimitSpawn <value> <radius> <lowDistance> <name>
  private void doEndCrystalsLimitSpawnInit() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
      dispatcher.register(literal("gamerule")
        .requires(source -> source.hasPermissionLevel(4))
          .then(endCrystalHelper())
        );
      dispatcher.register(literal("gamerule")
        .requires(source -> source.hasPermissionLevel(4))
        .then(literal("default")
          .then(endCrystalHelperDefault())
        )
      );
    });
  }

  private ArgumentBuilder<ServerCommandSource, ?> endCrystalHelper() {
    return literal("doEndCrystalsLimitSpawn")
      .executes((context) -> printValue(context, PropertiesCache.getInstance(), Tweaks.DO_END_CRYSTALS_LIMIT_SPAWN.getName()))
      .then(argument("value", BoolArgumentType.bool())
        .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getInstance()))
      )
      .then(argument("value", BoolArgumentType.bool())
        .then(argument("name", StringArgumentType.string())
          .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getInstance()))
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getInstance()))
          )
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .then(argument("name", StringArgumentType.string())
              .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getInstance()))
            )
          )
        )
      );
  }

  private ArgumentBuilder<ServerCommandSource, ?> endCrystalHelperDefault() {
    return literal("doEndCrystalsLimitSpawn")
      .executes((context) -> printValue(context, PropertiesCache.getDefaultInstance(), Tweaks.DO_END_CRYSTALS_LIMIT_SPAWN.getName()))
      .then(argument("value", BoolArgumentType.bool())
        .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getDefaultInstance()))
      )
      .then(argument("value", BoolArgumentType.bool())
        .then(argument("name", StringArgumentType.string())
          .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getDefaultInstance()))
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getDefaultInstance()))
          )
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .then(argument("name", StringArgumentType.string())
              .executes((context) -> endCrystalCommonHelperExecute(context, PropertiesCache.getDefaultInstance()))
            )
          )
        )
      );
  }

  private int endCrystalCommonHelperExecute(CommandContext<ServerCommandSource> context, PropertiesCache cache) {
    boolean value = BoolArgumentType.getBool(context, "value");
    int radius;
    int lowDistance;
    String name;

    cache.setProperty(Tweaks.DO_END_CRYSTALS_LIMIT_SPAWN.getName(), Boolean.toString(value));
    try {
      radius = IntegerArgumentType.getInteger(context, "radius");
      cache.setProperty(Tweaks.END_CRYSTAL_RADIUS.getName(), Integer.toString(radius));
    } catch (Exception ignored) {}
    try {
      lowDistance = IntegerArgumentType.getInteger(context, "lowDistance");
      cache.setProperty(Tweaks.END_CRYSTAL_LOWER_LIMIT_DISTANCE.getName(), Integer.toString(lowDistance));
    } catch (Exception ignored) {}
    try {
      name = StringArgumentType.getString(context, "name");
      if (name.equals("-")) {
        cache.setProperty(Tweaks.END_CRYSTAL_NAME.getName(), "");
      } else {
        cache.setProperty(Tweaks.END_CRYSTAL_NAME.getName(), name);
      }
    } catch (Exception ignored) {}

    try {
      cache.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return reload(context);
  }

  // Utils
  private static int printValue(CommandContext<ServerCommandSource> context, PropertiesCache cache, String property) {
    ServerCommandSource serverCommandSource = context.getSource();
    serverCommandSource.sendFeedback(Text.literal(cache.getProperty(property)), true);

    return 1;
  }

  private static int reload(CommandContext<ServerCommandSource> context) {
    ServerCommandSource serverCommandSource = context.getSource();
    MinecraftServer minecraftServer = serverCommandSource.getServer();
    Collection<String> collection = getResourcePacks(minecraftServer);
    serverCommandSource.sendFeedback(Text.translatable("commands.custom.reload.success"), true);
    ReloadCommand.tryReloadDataPacks(collection, serverCommandSource);

    return 1;
  }

  private static Collection<String> getResourcePacks(MinecraftServer minecraftServer) {
    ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
    resourcePackManager.scanPacks();
    Collection<String> collection2 = Lists.newArrayList(resourcePackManager.getEnabledNames());
    Collection<String> collection3 = minecraftServer.getSaveProperties().getDataConfiguration().dataPacks().getDisabled();

    for (String string : resourcePackManager.getNames()) {
      if (!collection3.contains(string) && !collection2.contains(string)) {
        collection2.add(string);
      }
    }

    return collection2;
  }
}
