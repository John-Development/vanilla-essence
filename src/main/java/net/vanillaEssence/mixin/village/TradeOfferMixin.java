package net.vanillaEssence.mixin.village;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.village.TradeOffer;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(TradeOffer.class)
public class TradeOfferMixin {

  @Shadow
  private int demandBonus;

  @Inject(
    method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V",
    at = @At("TAIL")
  )
  private void init(
    CompoundTag nbt,
    CallbackInfo cir
  ) {
    this.demandBonus = nbt.getInt("demand") - 15 * PropertiesCache.getInstance().getIntProperty("vill-daily-restocks");
  }
}