package net.vanillaEssence.mixin.world.level;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Lifecycle;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.timer.Timer;
import net.vanillaEssence.util.Tweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

@Mixin(LevelProperties.class)
public class LevelPropertiesMixin {
  @Inject(
    method = "<init>(" +
      "Lcom/mojang/datafixers/DataFixer;" +
      "I" +
      "Lnet/minecraft/nbt/NbtCompound;" +
      "ZIIIFJJIIIZIZZZ" +
      "Lnet/minecraft/world/border/WorldBorder$Properties;" +
      "II" +
      "Ljava/util/UUID;" +
      "Ljava/util/Set;" +
      "Ljava/util/Set;" +
      "Lnet/minecraft/world/timer/Timer;" +
      "Lnet/minecraft/nbt/NbtCompound;" +
      "Lnet/minecraft/entity/boss/dragon/EnderDragonFight$Data;" +
      "Lnet/minecraft/world/level/LevelInfo;" +
      "Lnet/minecraft/world/gen/GeneratorOptions;" +
      "Lnet/minecraft/world/level/LevelProperties$SpecialProperty;" +
      "Lcom/mojang/serialization/Lifecycle;" +
    ")V",
    at = @At("TAIL")
  )
  void initializer(DataFixer dataFixer, int dataVersion, NbtCompound playerData, boolean modded, int spawnX, int spawnY, int spawnZ, float spawnAngle, long time, long timeOfDay, int version, int clearWeatherTime, int rainTime, boolean raining, int thunderTime, boolean thundering, boolean initialized, boolean difficultyLocked, WorldBorder.Properties worldBorder, int wanderingTraderSpawnDelay, int wanderingTraderSpawnChance, UUID wanderingTraderId, Set<String> serverBrands, Set<String> removedFeatures, Timer<MinecraftServer> scheduledEvents, NbtCompound customBossEvents, EnderDragonFight.Data dragonFight, LevelInfo levelInfo, GeneratorOptions generatorOptions, LevelProperties.SpecialProperty specialProperty, Lifecycle lifecycle, CallbackInfo ci) {
    Tweaks.setLevelName(levelInfo.getLevelName());
  }
}
