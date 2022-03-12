package net.vanillaEssence.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.vanillaEssence.events.interfaces.CopperOxidizeCallback;
import net.vanillaEssence.util.PropertiesCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity {

  public PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(
    method = "onBlockHit",
    at = @At("RETURN")
  )
  public void onBlockHit(
    BlockHitResult blockHitResult,
    CallbackInfo ci
  ) {
    PropertiesCache cache = PropertiesCache.getInstance();
    if (cache.getBoolProperty("oxidation-enabled")) {
      if (!this.world.isClient) {
        ItemStack itemStack = this.getStack();
        BlockPos blockPos = blockHitResult.getBlockPos();
        Potion potion = PotionUtil.getPotion(itemStack);
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
        if (potion == Potions.WATER && list.isEmpty()) {
          CopperOxidizeCallback.EVENT.invoker().oxidize(blockPos, this.world);
        }
      }
    }
  }
}
