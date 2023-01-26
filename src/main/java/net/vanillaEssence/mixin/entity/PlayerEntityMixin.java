package net.vanillaEssence.mixin.entity;

import net.vanillaEssence.util.TweaksEnum;
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
    if (TweaksEnum.DO_END_CRYSTALS_LIMIT_SPAWN.getBoolean()) {
      ItemStack itemStack = ((PlayerEntity) (Object) this).getStackInHand(hand);

      if (itemStack.getItem().equals(Items.NAME_TAG)
        && itemStack.hasCustomName()
        && entity instanceof EndCrystalEntity
        && !((EndCrystalEntity) entity).shouldShowBottom()
        && TweaksEnum.END_CRYSTAL_NAME.getString().equals(itemStack.getName().getString())
      ) {
        entity.setCustomName(itemStack.getName());

        itemStack.decrement(1);
        entity.setCustomNameVisible(true);

        cir.setReturnValue(ActionResult.success(((PlayerEntity) (Object) this).world.isClient));
      }
    }
  }
}