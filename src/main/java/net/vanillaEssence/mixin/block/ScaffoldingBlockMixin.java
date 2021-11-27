package net.vanillaEssence.mixin.block;

import net.minecraft.block.*;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin extends Block implements Waterloggable {

  public ScaffoldingBlockMixin(Settings settings) {
    super(settings);
  }

  private static final boolean SCAFF_ENABLED = Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("scaff-enabled"));
  private static final int SCAFF_LIMIT_CONFIG = Integer.parseInt((
    PropertiesCache.getInstance().getProperty("scaff-limit") != null
    && !PropertiesCache.getInstance().getProperty("scaff-limit").isEmpty()
  )
    ? PropertiesCache.getInstance().getProperty("scaff-limit")
    : "7");
  private static final int SCAFF_LIMIT = SCAFF_ENABLED
    ? SCAFF_LIMIT_CONFIG
    : 7;

  @Final
  @Shadow
  public static final IntProperty DISTANCE = IntProperty.of("distance_scaff", 0, SCAFF_LIMIT);
  @Final
  @Shadow
  public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
  @Final
  @Shadow
  public static final BooleanProperty BOTTOM = Properties.BOTTOM;

  @ModifyArg(
    method = "<init>",
    at = @At(value = "INVOKE",
      target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;",
      ordinal = 0
    ),
    index = 1
  )
  private int getBlockState(int limit) {
    return SCAFF_LIMIT;
  }

  @Inject(
    method = "scheduledTick",
    at = @At("HEAD"),
    cancellable = true
  )
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
    int i = ScaffoldingBlock.calculateDistance(world, pos);
    BlockState blockState = state.with(DISTANCE, i).with(BOTTOM, this.shouldBeBottom(world, pos, i));
    if (blockState.get(DISTANCE) == SCAFF_LIMIT) {
      if (state.get(DISTANCE) == SCAFF_LIMIT) {
        world.spawnEntity(new FallingBlockEntity(world, (double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, blockState.with(WATERLOGGED, false)));
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
        i = blockState.get(DISTANCE);
      } else if (blockState.isSideSolidFullSquare(world, mutable, Direction.UP)) {
        cir.setReturnValue(0);
        return;
      }

      for (Direction direction : Direction.Type.HORIZONTAL) {
        BlockState blockState2 = world.getBlockState(mutable.set(pos, direction));
        if (blockState2.isOf(Blocks.SCAFFOLDING)) {
          i = Math.min(i, blockState2.get(DISTANCE) + 1);
          if (i == 1) {
            break;
          }
        }
      }
      cir.setReturnValue(i);
    }
  }

  private boolean shouldBeBottom(BlockView world, BlockPos pos, int distance) {
    return distance > 0 && !world.getBlockState(pos.down()).isOf(((ScaffoldingBlock) (Object) this));
 }
 
}
