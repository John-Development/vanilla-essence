package net.vanillaEssence.mixin.screen;

import net.minecraft.screen.ScreenHandlerContext;
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
public abstract class BeaconScreenHandlerMixin {

  @Shadow
  Inventory payment;
  // @Shadow
  // private PaymentSlotMixin paymentSlot;
  @Shadow
  PropertyDelegate propertyDelegate;

  // @Inject(
  //   method = "<init>(ILnet/minecraft/inventory/Inventory;)V",
  //   at = @At("HEAD")
  // )
  // public void start(int syncId, Inventory inventory, CallbackInfo cir) {
  //   this.(syncId, inventory, new ArrayPropertyDelegate(4), ScreenHandlerContext.EMPTY);
  // }

   public BeaconScreenHandlerMixin(int syncId, Inventory inventory, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
  //   super(ScreenHandlerType.BEACON, syncId);
  //   this.payment = new SimpleInventory(1) {
  //       public boolean isValid(int slot, ItemStack stack) {
  //           return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS);
  //       }

  //       public int getMaxCountPerStack() {
  //           return 1;
  //       }
  //   };
  //   checkDataCount(propertyDelegate, 3);
  //   this.propertyDelegate = propertyDelegate;
  //   this.context = context;
  //   this.paymentSlot = new BeaconScreenHandler.PaymentSlot(this.payment, 0, 136, 110);
  //   this.addSlot(this.paymentSlot);
  //   this.addProperties(propertyDelegate);

  //   int m;
  //   for(m = 0; m < 3; ++m) {
  //       for(int l = 0; l < 9; ++l) {
  //           this.addSlot(new Slot(inventory, l + m * 9 + 9, 36 + l * 18, 137 + m * 18));
  //       }
  //   }

  //   for(m = 0; m < 9; ++m) {
  //       this.addSlot(new Slot(inventory, m, 36 + m * 18, 195));
  //   }

   }

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
      // System.out.println(propertyDelegate);
      this.propertyDelegate.set(3, Item.getRawId(this.payment.getStack(0).getItem()));
    }
  }

  // private class PaymentSlotMixin extends Slot {
  //   public PaymentSlotMixin(Inventory inventory, int index, int x, int y) {
  //       super(inventory, index, x, y);
  //   }

  //   public boolean canInsert(ItemStack stack) {
  //       return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS);
  //   }

  //   public int getMaxItemCount() {
  //       return 1;
  //   }
  // }
}