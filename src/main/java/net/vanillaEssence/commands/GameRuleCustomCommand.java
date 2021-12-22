package net.vanillaEssence.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.SaveProperties;
import net.vanillaEssence.loot.Sand;
import net.vanillaEssence.util.PropertiesCache;

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
  }

  // Command example: /gamerule doHusksDropSand <value>
  private void doHuskDropSandInit() {
    genericCommandBuilderInit("doHusksDropSand", "sand-enabled");
  }

  // Command example: /gamerule betterBeacons <value>
  private void betterBeaconsInit() {
    genericCommandBuilderInit("betterBeacons", "beacons-enabled");
  }

  // Command example: /gamerule magneticLure <value>
  private void magneticLureInit() {
    genericCommandBuilderInit("magneticLure", "magnetic-lure-enabled");
  }

  // Command example: /gamerule redstonedJukeboxes <value>
  private void redstonedJukeboxInit() {
    genericCommandBuilderInit("redstonedJukeboxes", "redstoned-jukeboxes-enabled");
  }

  // Common builder
  private void genericCommandBuilderInit(String rule, String configValue) {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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

  // Command example: /gamerule dailyVillagerRestocks <dailyRestocks> <timeBetweenRestocks>
  private void dailyVillagerRestocksInit() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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
      .executes((context) -> printValue(context, PropertiesCache.getInstance(), "vill-enabled"))
      .then(argument("dailyRestocks", IntegerArgumentType.integer(0, 999))
        .then(argument("timeBetweenRestocks", IntegerArgumentType.integer(20, 2400))
          .executes(context -> dailyVillagersCommonHelperExecute(context, PropertiesCache.getInstance()))
        )
      );
  }

  private ArgumentBuilder<ServerCommandSource, ?> dailyVillagersHelperDefault() {
    return literal("dailyVillagerRestocks")
      .executes((context) -> printValue(context, PropertiesCache.getDefaultInstance(), "vill-enabled"))
      .then(argument("dailyRestocks", IntegerArgumentType.integer(0, 999))
        .then(argument("timeBetweenRestocks", IntegerArgumentType.integer(20, 2400))
          .executes(context -> dailyVillagersCommonHelperExecute(context, PropertiesCache.getDefaultInstance()))
        )
      );
  }

  private int dailyVillagersCommonHelperExecute(CommandContext<ServerCommandSource> context, PropertiesCache cache) {
    int restocks = IntegerArgumentType.getInteger(context, "dailyRestocks");
    int cooldown = IntegerArgumentType.getInteger(context, "timeBetweenRestocks");

    cache.setProperty("vill-enabled", ((Boolean) (restocks != 2)).toString());
    cache.setProperty("vill-daily-restocks", Integer.toString(restocks));
    cache.setProperty("vill-time-between-restocks", Integer.toString(cooldown));

    try {
      cache.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return reload(context);
  }

  // Command example: /gamerule doEndCrystalsLimitSpawn <value> <radius> <lowDistance> <name>
  private void doEndCrystalsLimitSpawnInit() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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
      .executes((context) -> printValue(context, PropertiesCache.getInstance(), "crystal-enabled"))
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
      .executes((context) -> printValue(context, PropertiesCache.getDefaultInstance(), "crystal-enabled"))
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

    cache.setProperty("crystal-enabled", Boolean.toString(value));
    try {
      radius = IntegerArgumentType.getInteger(context, "radius");
      cache.setProperty("crystal-radius", Integer.toString(radius));
    } catch (Exception ignored) {}
    try {
      lowDistance = IntegerArgumentType.getInteger(context, "lowDistance");
      cache.setProperty("crystal-lower-limit-distance", Integer.toString(lowDistance));
    } catch (Exception ignored) {}
    try {
      name = StringArgumentType.getString(context, "name");
      if (name.equals("-")) {
        cache.setProperty("crystal-name", "");
      } else {
        cache.setProperty("crystal-name", name);
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
    serverCommandSource.sendFeedback(new LiteralText(cache.getProperty(property)), true);
    return 1;
  }

  private static int reload(CommandContext<ServerCommandSource> context) {
    ServerCommandSource serverCommandSource = context.getSource();
    MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
    ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
    SaveProperties saveProperties = minecraftServer.getSaveProperties();
    Collection<String> collection = resourcePackManager.getEnabledNames();
    Collection<String> collection2 = getResourcePacks(resourcePackManager, saveProperties, collection);
    serverCommandSource.sendFeedback(new TranslatableText("commands.custom.reload.success"), true);
    ReloadCommand.method_29480(collection2, serverCommandSource);
    return 1;
  }

  private static Collection<String> getResourcePacks(ResourcePackManager resourcePackManager, SaveProperties saveProperties, Collection<String> collection) {
    resourcePackManager.scanPacks();
    Collection<String> collection2 = Lists.newArrayList(collection);
    Collection<String> collection3 = saveProperties.getDataPackSettings().getDisabled();

    for (String string : resourcePackManager.getNames()) {
      if (!collection3.contains(string) && !collection2.contains(string)) {
        collection2.add(string);
      }
    }

    return collection2;
  }
}