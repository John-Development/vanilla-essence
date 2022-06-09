package net.vanillaEssence.events.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Callback for shearing a sheep.
 * Called before the sheep is sheared, items are dropped, and items are damaged.
 * Upon return:
 * - SUCCESS cancels further processing and continues with normal behavior.
 * - PASS falls back to further processing and defaults to SUCCESS if no other listeners are available
 * - FAIL cancels further processing and does not oxidize the block.
 **/

public interface CopperOxidizeCallback {
  Event<CopperOxidizeCallback> EVENT = EventFactory.createArrayBacked(CopperOxidizeCallback.class,
    (listeners) -> (pos, world) -> {
      for (CopperOxidizeCallback listener : listeners) {
        ActionResult result = listener.oxidize(pos, world);

        if (result != ActionResult.PASS) {
          return result;
        }
      }

      return ActionResult.PASS;
    });

  ActionResult oxidize(BlockPos pos, World world);
}