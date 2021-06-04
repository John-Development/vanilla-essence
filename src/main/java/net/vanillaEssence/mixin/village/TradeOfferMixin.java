package net.vanillaEssence.mixin.village;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.NbtCompound;
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
    NbtCompound nbt,
    CallbackInfo cir
  ) {
    this.demandBonus = nbt.getInt("demand") - 15 * Integer.parseInt(PropertiesCache.getInstance().getProperty("vill-daily-restocks"));
  }
}