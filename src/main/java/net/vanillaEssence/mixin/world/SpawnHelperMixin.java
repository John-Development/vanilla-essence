package net.vanillaEssence.mixin.world;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.vanillaEssence.util.PropertiesCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin {

  @Inject(
    method = "canSpawn*",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void handleCustomSpawnRestriction(
    ServerWorld world,
    SpawnGroup group,
    StructureAccessor pAccessor,
    ChunkGenerator chunkGenerator,
    SpawnSettings.SpawnEntry spawnEntry,
    BlockPos.Mutable blockPos,
    double squaredDistance,
    CallbackInfoReturnable<Boolean> cir
  ) {
    PropertiesCache cache = PropertiesCache.getInstance();

    if (cache.getBoolProperty("crystal-enabled")
      && group != null
      && group.compareTo(SpawnGroup.MONSTER) == 0
    ) {
      double monsterX, monsterY, monsterZ;
      monsterX = blockPos.getX();
      monsterY = blockPos.getY();
      monsterZ = blockPos.getZ();

      int radius = cache.getIntProperty("crystal-radius");
      int lowerLimitDistance = cache.getIntProperty("crystal-lower-limit-distance");

      Box box = new Box(
        monsterX + radius - 0.49,
        monsterY + lowerLimitDistance,
        monsterZ + radius - 0.49,
        monsterX - radius + 0.5,
        monsterY - 2 * radius - lowerLimitDistance + 3,
        monsterZ - radius + 0.5
      );
      List<EndCrystalEntity> crystals = world.getEntitiesByClass(
        EndCrystalEntity.class,
        box,
        crystal -> cache.getProperty("crystal-name").trim().isEmpty()
          || cache.getProperty("crystal-name").equals(crystal.getName().asString())
      );

      if (crystals != null && !crystals.isEmpty()) {
        cir.setReturnValue(false);
      }
    }
  }
}
