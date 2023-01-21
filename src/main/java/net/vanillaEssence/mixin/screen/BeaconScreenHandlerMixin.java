package net.vanillaEssence.mixin.screen;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.vanillaEssence.util.PropertiesCache;
import net.vanillaEssence.util.Tweaks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BeaconScreenHandler.class)
public abstract class BeaconScreenHandlerMixin {
  @Final
  @Shadow
  private
  Inventory payment;
  @Final
  @Shadow
  private
  PropertyDelegate propertyDelegate;

  @ModifyArg(
    method = "<init>(ILnet/minecraft/inventory/Inventory;)V",
    at = @At(value = "INVOKE",
      target = "Lnet/minecraft/screen/BeaconScreenHandler;<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V"),
    index = 2
  )
  private static PropertyDelegate getArrayPropertyDelegate(PropertyDelegate array) {
    return PropertiesCache.getInstance().getBoolProperty(Tweaks.BETTER_BEACONS.getName())
      ? new ArrayPropertyDelegate(4)
      : array;
  }

  @ModifyArg(
    method = "<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V",
    at = @At(value = "INVOKE",
      target = "Lnet/minecraft/screen/BeaconScreenHandler;checkDataCount(Lnet/minecraft/screen/PropertyDelegate;I)V"
    ),
    index = 1
  )
  private int getNumber(int number) {
    return PropertiesCache.getInstance().getBoolProperty(Tweaks.BETTER_BEACONS.getName())
      ? 4
      : number;
  }

  @Inject(
    method = "setEffects",
    at = @At("HEAD")
  )
  public void setEffectsMethod(
    Optional<StatusEffect> primary, Optional<StatusEffect> secondary, CallbackInfo ci
  ) {
    if (this.payment != null) {
      this.propertyDelegate.set(3, Item.getRawId(this.payment.getStack(0).getItem()));
    }
  }
}