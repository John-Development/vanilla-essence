package net.vanillaEssence.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.village.TradeOffer;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(TradeOffer.class)
public class TradeMixin {

  @Shadow
  private int demandBonus;

  @Inject(
    method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V",
    at = @At("TAIL")
  )
  private void init(
    CompoundTag compoundTag,
    CallbackInfo cir
  ) {
    this.demandBonus = compoundTag.getInt("demand") - 15 * Integer.parseInt(PropertiesCache.getInstance().getProperty("vill-daily-restocks"));
  }

  // @Inject(
  //   method = "updatePriceOnDemand",
  //   at = @At("HEAD"),
  //   cancellable = true
    
  // )
  // private void handleCustomRestockRate(
  //   CallbackInfo cir
  // ) {
  //   System.out.println("patatita cosa " + this.maxUses + " " + this.uses + " " + this.demandBonus);
  //   this.demandBonus = this.demandBonus + this.uses - (this.maxUses - this.uses);
  // }
}