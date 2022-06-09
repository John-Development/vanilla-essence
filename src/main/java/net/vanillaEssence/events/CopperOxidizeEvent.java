package net.vanillaEssence.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.vanillaEssence.events.interfaces.CopperOxidizeCallback;

public class CopperOxidizeEvent {
  public static void registerOxidation() {
    CopperOxidizeCallback.EVENT.register((pos, world) -> {
      BlockState blockState = world.getBlockState(pos);
      if (blockState.isOf(Blocks.COPPER_BLOCK)) {
        Block.replace(blockState, Blocks.EXPOSED_COPPER.getDefaultState(), world, pos, 2);
      } else if (blockState.isOf(Blocks.EXPOSED_COPPER)) {
        Block.replace(blockState, Blocks.WEATHERED_COPPER.getDefaultState(), world, pos, 2);
      } else if (blockState.isOf(Blocks.WEATHERED_COPPER)) {
        Block.replace(blockState, Blocks.OXIDIZED_COPPER.getDefaultState(), world, pos, 2);
      }
      return ActionResult.PASS;
    });
  }
}