package net.vanillaEssence.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AttractionEnchantment extends Enchantment {
  public AttractionEnchantment() {
    super(Enchantment.Rarity.RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {
      EquipmentSlot.CHEST,
      EquipmentSlot.MAINHAND,
      EquipmentSlot.OFFHAND,
      EquipmentSlot.FEET,
      EquipmentSlot.LEGS,
      EquipmentSlot.CHEST,
      EquipmentSlot.HEAD
    });
  }
}