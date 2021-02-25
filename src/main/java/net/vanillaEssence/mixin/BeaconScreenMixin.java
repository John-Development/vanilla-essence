package net.vanillaEssence.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.PropertyDelegate;

@Mixin(BeaconScreenHandler.class)
public abstract class BeaconScreenMixin {

  @Shadow
  Inventory payment;
  @Shadow
  PropertyDelegate propertyDelegate;

  @Inject(
    method = "setEffects",
    at = @At("HEAD"),
    cancellable = true
  )
  public void setEffectsMethod(
    int primaryEffectId,
    int secondaryEffectId,
    CallbackInfo cir
  ) {
    if (this.payment != null) {
      System.out.println("patata " + Item.getRawId(this.payment.getStack(0).getItem()));
      this.propertyDelegate.set(3, Item.getRawId(this.payment.getStack(0).getItem()));
    }
  }
}