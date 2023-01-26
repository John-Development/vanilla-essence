package net.vanillaEssence.mixin.village;

import net.vanillaEssence.util.TweaksEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOffer;

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
    this.demandBonus = TweaksEnum.MODIFY_VILLAGERS.getBoolean()
      ? nbt.getInt("demand") - 15 * TweaksEnum.DAILY_VILLAGER_RESTOCKS.getInt()
      : nbt.getInt("demand");
  }
}