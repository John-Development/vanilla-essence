package net.vanillaEssence.mixin.enchantment;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.LureEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(LureEnchantment.class)
public class LureEnchantmentMixin extends Enchantment {

  public static EnchantmentTarget getType(EnchantmentTarget type) {
    PropertiesCache cache = PropertiesCache.getInstance();
    if (Boolean.parseBoolean(cache.getProperty("magnetic-lure-enabled"))) {
      return EnchantmentTarget.BREAKABLE;
    } else {
      return type;
    }
  }

  protected LureEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
    super(weight, getType(type), slotTypes);
  }
}
