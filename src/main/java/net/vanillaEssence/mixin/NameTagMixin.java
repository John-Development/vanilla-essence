package net.vanillaEssence.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.vanillaEssence.util.PropertiesCache;

// TODO: rename file
@Mixin(PlayerEntity.class)
public abstract class NameTagMixin {

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

  @Inject(
    method = "tick",
    at = @At("TAIL")
  )
  public void tick(
    CallbackInfo cir
  ) {
    System.out.println("holas");
    Iterable<ItemStack> armor = ((PlayerEntity) (Object) this).getArmorItems();
    for (ItemStack itemStack : armor) {
      ListTag listTag = itemStack.getEnchantments();
      for (Tag tag : listTag) {
        System.out.println(tag.toString());
        if (tag.toString().equals("Lure")) {
          System.out.println("luresito mio");
        }
      }
    }
  }
}