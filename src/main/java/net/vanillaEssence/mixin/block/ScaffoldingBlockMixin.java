package net.vanillaEssence.mixin.block;

import java.util.Iterator;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin extends Block {

  public ScaffoldingBlockMixin(Settings settings) {
		super(settings);
	}

  private static boolean SCAFF_ENABLED = Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("scaff-enabled"));
  private static int SCAFF_LIMIT_CONFIG = Integer.parseInt((
    PropertiesCache.getInstance().getProperty("scaff-limit") != null
    && !PropertiesCache.getInstance().getProperty("scaff-limit").isEmpty()
  )
    ? PropertiesCache.getInstance().getProperty("scaff-limit")
    : "7");
  private static int SCAFF_LIMIT = SCAFF_ENABLED
    ? SCAFF_LIMIT_CONFIG
    : 7;

  @Shadow
  public static final IntProperty DISTANCE = IntProperty.of("distance_scaff", 0, SCAFF_LIMIT);
  @Shadow
  public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
  @Shadow
  public static final BooleanProperty BOTTOM = Properties.BOTTOM;

  @Inject(
    method = "<init>()V",
    at = @At("TAIL")
  )
  private void init(CallbackInfo cir) {
    this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(DISTANCE, SCAFF_LIMIT)).with(WATERLOGGED, false)).with(BOTTOM, false));
  }

  @Inject(
    method = "scheduledTick",
    at = @At("HEAD"),
    cancellable = true
  )
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
    int i = ScaffoldingBlock.calculateDistance(world, pos);
    BlockState blockState = (BlockState)((BlockState)state.with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(world, pos, i));
    if ((Integer)blockState.get(DISTANCE) == SCAFF_LIMIT) {
      if ((Integer)state.get(DISTANCE) == SCAFF_LIMIT) {
        world.spawnEntity(new FallingBlockEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, (BlockState)blockState.with(WATERLOGGED, false)));
      } else {
        world.breakBlock(pos, true);
      }
    } else if (state != blockState) {
      world.setBlockState(pos, blockState, 3);
    }
    ci.cancel();
  }

  @Inject(
    method = "canPlaceAt",
    at = @At("HEAD"),
    cancellable = true
  )
  public void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(ScaffoldingBlock.calculateDistance(world, pos) < SCAFF_LIMIT);
  }

  @Inject(
    method = "calculateDistance",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void calculateDistanceMixin(
    BlockView world,
    BlockPos pos,
    CallbackInfoReturnable<Integer> cir
  ) {
    if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("scaff-enabled"))) {
      BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.DOWN);
      BlockState blockState = world.getBlockState(mutable);
      int i = SCAFF_LIMIT;
      if (blockState.isOf(Blocks.SCAFFOLDING)) {
        i = (Integer)blockState.get(DISTANCE);
      } else if (blockState.isSideSolidFullSquare(world, mutable, Direction.UP)) {
        cir.setReturnValue(0);
        return;
      }

      Iterator<Direction> var5 = Direction.Type.HORIZONTAL.iterator();

      while(var5.hasNext()) {
        Direction direction = (Direction)var5.next();
        BlockState blockState2 = world.getBlockState(mutable.set(pos, direction));
        if (blockState2.isOf(Blocks.SCAFFOLDING)) {
          i = Math.min(i, (Integer)blockState2.get(DISTANCE) + 1);
          if (i == 1) {
            break;
          }
        }
      }
      cir.setReturnValue(i);
    }
    return;
  }

  private boolean shouldBeBottom(BlockView world, BlockPos pos, int distance) {
    return distance > 0 && !world.getBlockState(pos.down()).isOf(((ScaffoldingBlock) (Object) this));
 }
 
}
