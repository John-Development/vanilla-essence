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
import net.vanillaEssence.util.Tweaks;
import net.vanillaEssence.util.TweaksEnum;

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
    genericCommandBuilderInit("doHusksDropSand", TweaksEnum.HUSK_DROP_SAND);
  }

  // Command example: /gamerule betterBeacons <value>
  private void betterBeaconsInit() {
    genericCommandBuilderInit("betterBeacons", TweaksEnum.BETTER_BEACONS);
  }

  // Command example: /gamerule magneticLure <value>
  private void magneticLureInit() {
    genericCommandBuilderInit("magneticLure", TweaksEnum.MAGNETIC_LURE);
  }

  // Command example: /gamerule redstonedJukeboxes <value>
  private void redstonedJukeboxInit() {
    genericCommandBuilderInit("redstonedJukeboxes", TweaksEnum.REDSTONED_JUKEBOXES);
  }

  // Command example: /gamerule splashOxidize <value>
  private void oxidizeInit() {
    genericCommandBuilderInit("splashOxidize", TweaksEnum.SPLASH_OXIDIZE);
  }

  // Command example: /gamerule oneLvlRenaming <value>
  private void oneLvlRenamingInit() {
    genericCommandBuilderInit("oneLvlRenaming", TweaksEnum.ONE_LVL_RENAMING);
  }

  // Command example: /gamerule infiniteEnchanting <value>
  private void infiniteEnchantingInit() {
    genericCommandBuilderInit("infiniteEnchanting", TweaksEnum.INFINITE_ENCHANTING);
  }

  // Common builder
  private void genericCommandBuilderInit(String rule, TweaksEnum configValue) {
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

  private ArgumentBuilder<ServerCommandSource, ?> genericCommandBuilderHelper(String rule, TweaksEnum configValue) {
    return literal(rule)
      .executes((context) -> printValue(context, Tweaks.getInstance(), configValue))
      .then(argument("value", BoolArgumentType.bool())
        .executes(context -> genericCommandBuilderCommonHelperExecute(context, configValue, Tweaks.getInstance()))
      );
  }

  private ArgumentBuilder<ServerCommandSource, ?> genericCommandBuilderHelperDefault(String rule, TweaksEnum configValue) {
    return literal(rule)
      .executes((context) -> printValue(context, Tweaks.getDefaultInstance(), configValue))
      .then(argument("value", BoolArgumentType.bool())
        .executes(context -> genericCommandBuilderCommonHelperExecute(context, configValue, Tweaks.getDefaultInstance()))
      );
  }

  private int genericCommandBuilderCommonHelperExecute(CommandContext<ServerCommandSource> context, TweaksEnum configValue, Tweaks tweaks) {
    configValue.set(((Boolean) BoolArgumentType.getBool(context, "value")).toString(), tweaks);

    try {
      tweaks.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (Objects.equals(configValue.getName(), "doHusksDropSand")) {
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
//      .executes((context) -> printValue(context, Tweaks.getInstance(), "riptide-fix-enabled"))
//      .then(argument("value", BoolArgumentType.bool())
//        .then(argument("multiplier", DoubleArgumentType.doubleArg(0, 1))
//          .executes(context -> riptideFixCommonHelperExecute(context, Tweaks.getInstance()))
//        )
//      );
//  }
//
//  private ArgumentBuilder<ServerCommandSource, ?> riptideMultiplierHelperDefault() {
//    return literal("riptideFix")
//      .executes((context) -> printValue(context, Tweaks.getDefaultInstance(), "riptide-fix-enabled"))
//      .then(argument("value", BoolArgumentType.bool())
//        .then(argument("multiplier", DoubleArgumentType.doubleArg(0, 1))
//          .executes(context -> riptideFixCommonHelperExecute(context, Tweaks.getDefaultInstance()))
//        )
//      );
//  }

//  private int riptideFixCommonHelperExecute(CommandContext<ServerCommandSource> context, Tweaks tweaks) {
//    boolean value = BoolArgumentType.getBool(context, "value");
//    double multiplier = DoubleArgumentType.getDouble(context, "multiplier");
//
//    tweaks.setProperty("riptide-fix-enabled", Boolean.toString(value));
//    tweaks.setProperty("riptide-fix-multiplier", Double.toString(multiplier));
//
//    try {
//      tweaks.flush();
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
      .executes((context) -> printValue(context, Tweaks.getInstance(), TweaksEnum.MODIFY_VILLAGERS))
      .then(argument("dailyRestocks", IntegerArgumentType.integer(0, 999))
        .then(argument("timeBetweenRestocks", IntegerArgumentType.integer(20, 2400))
          .executes(context -> dailyVillagersCommonHelperExecute(context, Tweaks.getInstance()))
        )
      );
  }

  private ArgumentBuilder<ServerCommandSource, ?> dailyVillagersHelperDefault() {
    return literal("dailyVillagerRestocks")
      .executes((context) -> printValue(context, Tweaks.getDefaultInstance(), TweaksEnum.MODIFY_VILLAGERS))
      .then(argument("dailyRestocks", IntegerArgumentType.integer(0, 999))
        .then(argument("timeBetweenRestocks", IntegerArgumentType.integer(20, 2400))
          .executes(context -> dailyVillagersCommonHelperExecute(context, Tweaks.getDefaultInstance()))
        )
      );
  }

  private int dailyVillagersCommonHelperExecute(CommandContext<ServerCommandSource> context, Tweaks tweaks) {
    int restocks = IntegerArgumentType.getInteger(context, "dailyRestocks");
    int cooldown = IntegerArgumentType.getInteger(context, "timeBetweenRestocks");

    TweaksEnum.MODIFY_VILLAGERS.set(((Boolean) (restocks != 2)).toString(), tweaks);
    TweaksEnum.DAILY_VILLAGER_RESTOCKS.set(Integer.toString(restocks), tweaks);
    TweaksEnum.TIME_BETWEEN_VILLAGER_RESTOCKS.set(Integer.toString(cooldown), tweaks);

    try {
      tweaks.flush();
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
      .executes((context) -> printValue(context, Tweaks.getInstance(), TweaksEnum.DO_END_CRYSTALS_LIMIT_SPAWN))
      .then(argument("value", BoolArgumentType.bool())
        .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getInstance()))
      )
      .then(argument("value", BoolArgumentType.bool())
        .then(argument("name", StringArgumentType.string())
          .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getInstance()))
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getInstance()))
          )
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .then(argument("name", StringArgumentType.string())
              .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getInstance()))
            )
          )
        )
      );
  }

  private ArgumentBuilder<ServerCommandSource, ?> endCrystalHelperDefault() {
    return literal("doEndCrystalsLimitSpawn")
      .executes((context) -> printValue(context, Tweaks.getDefaultInstance(), TweaksEnum.DO_END_CRYSTALS_LIMIT_SPAWN))
      .then(argument("value", BoolArgumentType.bool())
        .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getDefaultInstance()))
      )
      .then(argument("value", BoolArgumentType.bool())
        .then(argument("name", StringArgumentType.string())
          .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getDefaultInstance()))
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getDefaultInstance()))
          )
        )
        .then(argument("radius", IntegerArgumentType.integer(1, 64))
          .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
            .then(argument("name", StringArgumentType.string())
              .executes((context) -> endCrystalCommonHelperExecute(context, Tweaks.getDefaultInstance()))
            )
          )
        )
      );
  }

  private int endCrystalCommonHelperExecute(CommandContext<ServerCommandSource> context, Tweaks tweaks) {
    boolean value = BoolArgumentType.getBool(context, "value");
    int radius;
    int lowDistance;
    String name;

    TweaksEnum.DO_END_CRYSTALS_LIMIT_SPAWN.set(Boolean.toString(value), tweaks);
    try {
      radius = IntegerArgumentType.getInteger(context, "radius");
      TweaksEnum.END_CRYSTAL_RADIUS.set(Integer.toString(radius), tweaks);
    } catch (Exception ignored) {}
    try {
      lowDistance = IntegerArgumentType.getInteger(context, "lowDistance");
      TweaksEnum.END_CRYSTAL_LOWER_LIMIT_DISTANCE.set(Integer.toString(lowDistance), tweaks);
    } catch (Exception ignored) {}
    try {
      name = StringArgumentType.getString(context, "name");
      if (name.equals("-")) {
        TweaksEnum.END_CRYSTAL_NAME.set("", tweaks);
      } else {
        TweaksEnum.END_CRYSTAL_NAME.set(name, tweaks);
      }
    } catch (Exception ignored) {}

    try {
      tweaks.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return reload(context);
  }

  // Utils
  private static int printValue(CommandContext<ServerCommandSource> context, Tweaks tweaks, TweaksEnum property) {
    ServerCommandSource serverCommandSource = context.getSource();
    serverCommandSource.sendFeedback(Text.literal(property.getString(tweaks)), true);

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
