package net.vanillaEssence.mixin.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.vanillaEssence.util.PropertiesCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    PropertiesCache cache = PropertiesCache.getInstance();
    return cache.getBoolProperty("beacons-enabled")
            ? new ArrayPropertyDelegate(4)
            : array;
  }

  @ModifyArg(
          method = "<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V",
          at = @At(value = "INVOKE",
                  target = "Lnet/minecraft/screen/BeaconScreenHandler;checkDataCount(Lnet/minecraft/screen/PropertyDelegate;I)V"),
          index = 1
  )
  private int getNumber(int number) {
    PropertiesCache cache = PropertiesCache.getInstance();
    return cache.getBoolProperty("beacons-enabled")
            ? 4
            : number;
  }

  @Inject(
          method = "setEffects",
          at = @At("HEAD")
  )
  public void setEffectsMethod(
          int primaryEffectId,
          int secondaryEffectId,
          CallbackInfo cir
  ) {
    if (this.payment != null) {
      this.propertyDelegate.set(3, Item.getRawId(this.payment.getStack(0).getItem()));
    }
  }
}