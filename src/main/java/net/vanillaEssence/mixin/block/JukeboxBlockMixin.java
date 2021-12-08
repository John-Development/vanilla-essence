package net.vanillaEssence.mixin.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(JukeboxBlock.class)
public abstract class JukeboxBlockMixin extends BlockWithEntity {
  @Mutable
  @Final
  @Shadow
  public static final BooleanProperty HAS_RECORD;

  private static final BooleanProperty TRIGGERED;

  @Shadow protected abstract void removeRecord(World world, BlockPos pos);

  @ModifyArg(
    method = "<init>",
    at = @At(value = "INVOKE",
      target = "Lnet/minecraft/block/JukeboxBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"
    ),
    index = 0
  )
  private BlockState getBlockState(BlockState state) {
    return state.with(TRIGGERED, false);
  }

  public JukeboxBlockMixin(AbstractBlock.Settings settings) {
    super(settings);
  }

  @Override
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
    this.updateEnabled(world, pos, state);
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    boolean powered = world.isReceivingRedstonePower(pos);

    if (state.get(HAS_RECORD) || (!state.get(HAS_RECORD) && powered)) {
      this.removeRecord(world, pos);
      state = state.with(HAS_RECORD, false);
      world.setBlockState(pos, state, 2);
      return ActionResult.success(world.isClient);
    } else {
      return ActionResult.PASS;
    }
  }

  private void updateEnabled(World world, BlockPos pos, BlockState state) {
    boolean powered = world.isReceivingRedstonePower(pos);
    if (powered == state.get(TRIGGERED)) {
      world.setBlockState(pos, state.with(TRIGGERED, !powered), 4);
      if (powered) {
        this.removeRecord(world, pos);
      }
    }
  }

  @Override
  public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(TRIGGERED, HAS_RECORD);
  }

  static {
    HAS_RECORD = Properties.HAS_RECORD;
    TRIGGERED = Properties.TRIGGERED;
  }
}
