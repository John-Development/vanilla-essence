package net.vanillaEssence.mixin.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.vanillaEssence.util.PropertiesCache;
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

    PropertiesCache cache = PropertiesCache.getInstance();
    if (cache.getBoolProperty("riptide-fix-enabled")) {
      float h = (float) EnchantmentHelper.getDepthStrider((LivingEntity) (Object) this);

      if (h > 3.0F) {
        h = 3.0F;
      }

      return this.riptideTicks == 0 ? input : 0.8F + (float) (h * cache.getDoubleProperty("riptide-fix-multiplier") * 0.1);
    } else {
      return input;
    }
  }
}
