package net.vanillaEssence.mixin;

import java.util.Iterator;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingMixin {

  private static boolean SCAFF_ENABLED = Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("scaff-enabled"));
  private static int SCAFF_LIMIT_CONFIG = Integer.parseInt((
    PropertiesCache.getInstance().getProperty("scaff-limit") != null
    && !PropertiesCache.getInstance().getProperty("scaff-limit").isEmpty()
  )
    ? PropertiesCache.getInstance().getProperty("scaff-limit")
    : "14");
  private static int SCAFF_LIMIT = SCAFF_ENABLED
    ? SCAFF_LIMIT_CONFIG
    : 7;

  @Shadow
  public static final IntProperty DISTANCE = IntProperty.of("distance_0_7", 0, SCAFF_LIMIT);
  @Shadow
  public static final BooleanProperty WATERLOGGED = net.minecraft.state.property.Properties.WATERLOGGED;
  @Shadow
  public static final BooleanProperty BOTTOM = net.minecraft.state.property.Properties.BOTTOM;

  @Overwrite
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    int i = ScaffoldingBlock.calculateDistance(world, pos);
    BlockState blockState = (BlockState)((BlockState)state.with(DISTANCE, i)).with(BOTTOM, this.shouldBeBottom(world, pos, i));
    // System.out.println("patata Tk " + pos.getX() + " " + pos.getY() + " " + state.get(DISTANCE) + " " + blockState.get(DISTANCE) + " ");
    if ((Integer)blockState.get(DISTANCE) == SCAFF_LIMIT) {
      if ((Integer)state.get(DISTANCE) == SCAFF_LIMIT) {
        world.spawnEntity(new FallingBlockEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, (BlockState)blockState.with(WATERLOGGED, false)));
      } else {
        world.breakBlock(pos, true);
      }
    } else if (state != blockState) {
      System.out.println("patata At " + pos.getX() + " " + pos.getY() + " " + state.get(DISTANCE) + " " + blockState.get(DISTANCE));
      world.setBlockState(pos, blockState, 3);
    }
  }

  @Overwrite
  public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    return ScaffoldingBlock.calculateDistance(world, pos) < SCAFF_LIMIT;
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
    PropertiesCache cache = PropertiesCache.getInstance();
    if (Boolean.parseBoolean(cache.getProperty("scaff-enabled"))) {
      BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.DOWN);
      BlockState blockState = world.getBlockState(mutable);
      int i = SCAFF_LIMIT;
      if (blockState.isOf(Blocks.SCAFFOLDING)) {
        i = (Integer)blockState.get(DISTANCE);
        System.out.println("patata Di " + pos.getX() + i);
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
          System.out.println("patata Wh " + pos.getX() + " " + pos.getY() + " " + blockState2.get(DISTANCE) + "    " + direction.asString());
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
