package net.vanillaEssence.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

  @Inject(
    method = "interact",
    at = @At("RETURN"),
    cancellable = true
  )
  public void setCustomNameTag(
    Entity entity,
    Hand hand,
    CallbackInfoReturnable<ActionResult> cir
  ) {
    PropertiesCache cache = PropertiesCache.getInstance();
    if (Boolean.parseBoolean(cache.getProperty("crystal-enabled"))) {
      ItemStack itemStack = ((PlayerEntity) (Object) this).getStackInHand(hand);

      if (itemStack.getItem().equals(Items.NAME_TAG)
        && itemStack.hasCustomName()
        && entity instanceof EndCrystalEntity
        && !((EndCrystalEntity) entity).getShowBottom()
        && cache.getProperty("crystal-name").equals(itemStack.getName().asString())
      ) {
        entity.setCustomName(itemStack.getName());

        itemStack.decrement(1);
        entity.setCustomNameVisible(true);

        cir.setReturnValue(ActionResult.success(((PlayerEntity) (Object) this).world.isClient));
      }
    }
    return;
  }
}