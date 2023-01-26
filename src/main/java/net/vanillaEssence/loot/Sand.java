package net.vanillaEssence.loot;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.util.Identifier;
import net.vanillaEssence.util.TweaksEnum;

public class Sand {
  private static final Identifier HUSK_LOOT_TABLE_ID = new Identifier("minecraft", "entities/husk");

  private static class LazyHolder {
    private static final Sand INSTANCE = new Sand();
  }

  public static Sand getInstance() {
    return LazyHolder.INSTANCE;
  }

  public void init() {
    LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
      if (HUSK_LOOT_TABLE_ID.equals(id) && TweaksEnum.HUSK_DROP_SAND.getBoolean()) {
        LootPool.Builder poolBuilder = LootPool.builder()
          .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0, 1.5f)).build())
          .with(ItemEntry.builder(Items.SAND)
            .weight(1)
            .conditionally(KilledByPlayerLootCondition.builder())
            .build()
          );
        supplier.pool(poolBuilder);
      }
    });
  }
}
