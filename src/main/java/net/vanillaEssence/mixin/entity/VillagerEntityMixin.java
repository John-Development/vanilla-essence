package net.vanillaEssence.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.VillagerEntity;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

  @Shadow
  public int restocksToday;
  @Shadow
  public long lastRestockTime;
  
  @Inject(
    method = "canRestock",
    at = @At("HEAD"),
    cancellable = true
  )
  private void handleCustomRestockRate(
    CallbackInfoReturnable<Boolean> cir
  ) {
    PropertiesCache cache = PropertiesCache.getInstance();
    if (Boolean.parseBoolean(cache.getProperty("vill-enabled"))) {

      Integer restocks = Integer.parseInt(cache.getProperty("vill-daily-restocks"));
      Long cooldown = Long.parseLong(cache.getProperty("vill-time-between-restocks"));

      if (restocks == 0) {
        cir.setReturnValue(this.restocksToday == 0 || ((VillagerEntity) (Object) this).world.getTime() > (this.lastRestockTime + cooldown));
      }
      else {
        cir.setReturnValue(
          this.restocksToday == 0
          || this.restocksToday < restocks
          && ((VillagerEntity) (Object) this).world.getTime() > this.lastRestockTime + cooldown
        );
      }
    }
    return;
  }
}
