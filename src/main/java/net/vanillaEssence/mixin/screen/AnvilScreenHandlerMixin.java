package net.vanillaEssence.mixin.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.vanillaEssence.util.PropertiesCache;
import net.vanillaEssence.util.Tweaks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

  @Shadow
  private String newItemName;

  @Final
  @Shadow
  private Property levelCost;

  public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
    super(type, syncId, playerInventory, context);
  }

  @Inject(
    method = "updateResult",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;get()I", ordinal = 1)
  )
  public void updateResult2Mixin(CallbackInfo ci) {
    if (PropertiesCache.getInstance().getBoolProperty(Tweaks.ONE_LVL_RENAMING.getName())) {
      ItemStack itemStack = this.input.getStack(0);
      ItemStack itemStack3 = this.input.getStack(1);
      if(itemStack3.isEmpty() && !this.newItemName.equals(itemStack.getName().getString())) {
        this.levelCost.set(1);
      }
    }

    if (PropertiesCache.getInstance().getBoolProperty(Tweaks.INFINITE_ENCHANTING.getName())) {
      if (this.levelCost.get() >= 40) {
        this.levelCost.set(39);
      }
    }
  }
}

