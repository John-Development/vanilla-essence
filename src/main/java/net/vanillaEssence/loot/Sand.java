package net.vanillaEssence.loot;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Items;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.util.Identifier;
import net.vanillaEssence.util.PropertiesCache;

public class Sand {
  private static final Identifier HUSK_LOOT_TABLE_ID = new Identifier("minecraft", "entities/husk");
  
  private static class LazyHolder {
    private static final Sand INSTANCE = new Sand();
  }

  public static Sand getInstance() {
    return LazyHolder.INSTANCE;
  }

  public void init() {
    PropertiesCache cache = PropertiesCache.getInstance();

    LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
      if (HUSK_LOOT_TABLE_ID.equals(id) && Boolean.parseBoolean(cache.getProperty("sand-enabled"))) {
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
