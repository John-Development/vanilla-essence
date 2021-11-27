package net.vanillaEssence.mixin.enchantment;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.LureEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.vanillaEssence.util.PropertiesCache;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LureEnchantment.class)
public class LureEnchantmentMixin extends Enchantment {
    protected LureEnchantmentMixin (Enchantment.Rarity rarity, EnchantmentTarget enchantmentTarget, EquipmentSlot ... equipmentSlots) {
        super(rarity, enchantmentTarget, equipmentSlots);
    }

    @ModifyArg(
        method = "<init>",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/enchantment/Enchantment;<init>(Lnet/minecraft/enchantment/Enchantment$Rarity;Lnet/minecraft/enchantment/EnchantmentTarget;[Lnet/minecraft/entity/EquipmentSlot;)V"),
        index = 1
    )
    private static EnchantmentTarget getType(EnchantmentTarget type) {
        PropertiesCache cache = PropertiesCache.getInstance();
        if (cache.getBoolProperty("magnetic-lure-enabled")) {
            // TODO: think which items to accept
            return EnchantmentTarget.BREAKABLE;
        } else {
            return type;
        }
    }
}
