package net.vanillaEssence.mixin.village;

import net.vanillaEssence.util.Tweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOffer;
import net.vanillaEssence.util.PropertiesCache;

// TODO: think if want to use this
@Mixin(TradeOffer.class)
public class TradeOfferMixin {

  @Shadow
  private int demandBonus;

  @Inject(
    method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V",
    at = @At("TAIL")
  )
  private void init(
    NbtCompound nbt,
    CallbackInfo cir
  ) {
    this.demandBonus = PropertiesCache.getInstance().getBoolProperty(Tweaks.MODIFY_VILLAGERS.getName())
      ? nbt.getInt("demand") - 15 * PropertiesCache.getInstance().getIntProperty(Tweaks.DAILY_VILLAGER_RESTOCKS.getName())
      : nbt.getInt("demand");
  }
}