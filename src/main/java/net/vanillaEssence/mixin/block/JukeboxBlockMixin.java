package net.vanillaEssence.mixin.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.vanillaEssence.util.TweaksEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(JukeboxBlock.class)
public abstract class JukeboxBlockMixin extends BlockWithEntity {
  @Mutable
  @Final
  @Shadow
  public static final BooleanProperty HAS_RECORD;

  @Shadow protected abstract void removeRecord(World world, BlockPos pos);

  public JukeboxBlockMixin(AbstractBlock.Settings settings) {
    super(settings);
  }

  @Override
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
    if (TweaksEnum.REDSTONED_JUKEBOXES.getBoolean()) {
      boolean powered = world.isReceivingRedstonePower(pos);
      if (powered) {
        this.removeRecord(world, pos);
        state = state.with(HAS_RECORD, false);
        world.setBlockState(pos, state, 2);
      } else {
        if (!world.isClient && !state.get(HAS_RECORD)) {
          world.syncWorldEvent(1010, pos, 0);
        }
      }
    }
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    boolean powered = TweaksEnum.REDSTONED_JUKEBOXES.getBoolean() && world.isReceivingRedstonePower(pos);

    if (state.get(HAS_RECORD) || (!state.get(HAS_RECORD) && powered)) {
      this.removeRecord(world, pos);
      state = state.with(HAS_RECORD, false);
      world.setBlockState(pos, state, 2);
      return ActionResult.success(world.isClient);
    } else {
      return ActionResult.PASS;
    }
  }

  static {
    HAS_RECORD = Properties.HAS_RECORD;
  }
}
