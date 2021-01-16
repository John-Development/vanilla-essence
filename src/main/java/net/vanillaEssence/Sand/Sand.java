package net.vanillaEssence.Sand;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.util.Identifier;

public class Sand {
  private static final Identifier COAL_ORE_LOOT_TABLE_ID = new Identifier("minecraft", "entities/husk");

  public static void init() {
    LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
      if (COAL_ORE_LOOT_TABLE_ID.equals(id)) {
        FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
          .withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0, 1.5f)).build())
          .withEntry(ItemEntry.builder(Items.SAND)
            .weight(1)
            .conditionally(KilledByPlayerLootCondition.builder())
            .build()
          );
        supplier.withPool(poolBuilder.build());
      }
  });
  }
}
