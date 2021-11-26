package net.vanillaEssence.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.SaveProperties;
import net.vanillaEssence.util.PropertiesCache;

import java.io.IOException;
import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GameRuleCustomCommand {

  private final PropertiesCache cache = PropertiesCache.getInstance();

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
  }

  // Command example: /gamerule doHusksDropSand <value>
  private void doHuskDropSandInit() {
    commandHelper("doHusksDropSand", "sand-enabled");
  }

  // Command example: /gamerule betterBeacons <value>
  private void betterBeaconsInit() {
    commandHelper("betterBeacons", "beacons-enabled");
  }

  // Command example: /gamerule magneticLure <value>
  private void magneticLureInit() {
    commandHelper("magneticLure", "magnetic-lure-enabled");
  }

  // Command example: /gamerule dailyVillagerRestocks <dailyRestocks> <timeBetweenRestocks>
  private void dailyVillagerRestocksInit() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("gamerule")
      .requires(source -> source.hasPermissionLevel(4))
      .then(literal("dailyVillagerRestocks")
        .then(argument("dailyRestocks", IntegerArgumentType.integer(0, 999))
          .then(argument("timeBetweenRestocks", IntegerArgumentType.integer(20, 2400))
            .executes(context -> {
              int restocks = IntegerArgumentType.getInteger(context, "dailyRestocks");
              int cooldown = IntegerArgumentType.getInteger(context, "timeBetweenRestocks");

              cache.setProperty("vill-enabled", ((Boolean)(restocks != 2)).toString());
              cache.setProperty("vill-daily-restocks", Integer.toString(restocks));
              cache.setProperty("vill-time-between-restocks", Integer.toString(cooldown));

              try {
                cache.flush();
              } catch (IOException e) {
                e.printStackTrace();
              }

              return reload(context);
            })
          )
        )
      )
    ));
  }

  // Command example: /gamerule doEndCrystalsLimitSpawn <value> <radius> <lowDistance> <name>
  private void doEndCrystalsLimitSpawnInit() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("gamerule")
    .requires(source -> source.hasPermissionLevel(4))
      .then(literal("doEndCrystalsLimitSpawn")
        .then(argument("value", BoolArgumentType.bool())
          .executes(this::executeCrystal)
        )
        .then(argument("value", BoolArgumentType.bool())
          .then(argument("name", StringArgumentType.string())
            .executes(this::executeCrystal)
          )
          .then(argument("radius", IntegerArgumentType.integer(1, 64))
            .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
              .executes(this::executeCrystal)
            )
          )
          .then(argument("radius", IntegerArgumentType.integer(1, 64))
            .then(argument("lowDistance", IntegerArgumentType.integer(-64, 64))
              .then(argument("name", StringArgumentType.string())
                .executes(this::executeCrystal)
              )
            )
          )
        )
      )
    ));
  }

  // Command example: /gamerule scaffoldingHangLimit <length>
  // private void scaffoldingHangLimitInit() {
  //   CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
  //     dispatcher.register(literal("gamerule")
  //     .requires(source -> source.hasPermissionLevel(4))
  //       .then(literal("scaffoldingHangLimit")
  //         .then(argument("length", IntegerArgumentType.integer(7, 64))
  //           .executes(context -> {
  //             Integer length = IntegerArgumentType.getInteger(context, "length");

  //             cache.setProperty("scaff-enabled", ((Boolean)(length != 7)).toString());
  //             cache.setProperty("scaff-limit", length.toString());
              
  //             try {
  //               cache.flush();
  //             } catch (IOException e) {
  //               e.printStackTrace();
  //             }

  //             return reload(context);
  //           })
  //         )
  //       )
  //     );
  //   });
  // }

  private void commandHelper(String rule, String configValue) {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("gamerule")
      .requires(source -> source.hasPermissionLevel(4))
      .then(literal(rule)
        .then(argument("value", BoolArgumentType.bool())
          .executes(context -> {
            cache.setProperty(configValue, ((Boolean) BoolArgumentType.getBool(context, "value")).toString());

            try {
              cache.flush();
            } catch (IOException e) {
              e.printStackTrace();
            }

            return reload(context);
          })
        )
      )
    ));
  }  
  
  private int executeCrystal(CommandContext<ServerCommandSource> context) {

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

  private static int reload(CommandContext<ServerCommandSource> context) {
    ServerCommandSource serverCommandSource = context.getSource();
    MinecraftServer minecraftServer = serverCommandSource.getServer();
    ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
    SaveProperties saveProperties = minecraftServer.getSaveProperties();
    Collection<String> collection = resourcePackManager.getEnabledNames();
    Collection<String> collection2 = getResourcePacks(resourcePackManager, saveProperties, collection);
    serverCommandSource.sendFeedback(new TranslatableText("commands.custom.reload.success"), true);
    ReloadCommand.tryReloadDataPacks(collection2, serverCommandSource);
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
