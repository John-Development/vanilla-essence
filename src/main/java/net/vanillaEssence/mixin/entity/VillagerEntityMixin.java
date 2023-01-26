package net.vanillaEssence.mixin.entity;

import net.vanillaEssence.util.TweaksEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.VillagerEntity;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

  @Shadow
  private int restocksToday;
  @Shadow
  private long lastRestockTime;

  @Inject(
    method = "canRestock",
    at = @At("HEAD"),
    cancellable = true
  )
  private void handleCustomRestockRate(
    CallbackInfoReturnable<Boolean> cir
  ) {
    if (TweaksEnum.MODIFY_VILLAGERS.getBoolean()) {

      int restocks = TweaksEnum.DAILY_VILLAGER_RESTOCKS.getInt();
      long cooldown = TweaksEnum.TIME_BETWEEN_VILLAGER_RESTOCKS.getLong();

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
  }
}
