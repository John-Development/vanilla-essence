package net.vanillaEssence.mixin.entity;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
  @Shadow
  protected int riptideTicks;

  @ModifyVariable(method = "travel", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
  private float travel(float input) {
    //    System.out.println(((LivingEntity) ((Object)(this))).world.isClient + " " + this.riptideTicks);
    //    if (!((LivingEntity) ((Object)(this))).world.isClient) {
    //      return input;
    //    }

//    Tweaks tweaks = Tweaks.getInstance();
//    if (tweaks.getBoolProperty("riptide-fix-enabled")) {
//      float h = (float) EnchantmentHelper.getDepthStrider((LivingEntity) (Object) this);
//
//      if (h > 3.0F) {
//        h = 3.0F;
//      }
//
//      return this.riptideTicks == 0 ? input : 0.8F + (float) (h * tweaks.getDoubleProperty("riptide-fix-multiplier") * 0.1);
//    } else {
      return input;
//    }
  }
}
