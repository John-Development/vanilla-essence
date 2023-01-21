package net.vanillaEssence.mixin.block.entity;

import com.google.common.collect.Lists;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.World;
import net.vanillaEssence.util.BeamSegment;
import net.vanillaEssence.util.PropertiesCache;
import net.vanillaEssence.util.Tweaks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntity implements NamedScreenHandlerFactory {
  public BeaconBlockEntityMixin(BlockPos pos, BlockState state) {
    super(BlockEntityType.BEACON, pos, state);
  }

  @Shadow
  int level;
  @Shadow
  StatusEffect primary;
  @Shadow
  StatusEffect secondary;
  @Final
  @Shadow
  @Mutable
  private PropertyDelegate propertyDelegate;
  @Shadow
  List<BeamSegment> beamSegments = Lists.newArrayList();
  @Mutable
  @Final
  @Shadow
  public static StatusEffect[][] EFFECTS_BY_LEVEL;
  @Mutable
  @Final
  @Shadow
  private static Set<StatusEffect> EFFECTS;
  @Shadow
  private int minY;
  @Shadow
  private List<BeamSegment> field_19178 = Lists.newArrayList();

  Item payment;
  double bonus;
  double range;

  int ironBlocks = 0;
  int goldBlocks = 0;
  int emeraldBlocks = 0;
  int diamondBlocks = 0;
  int netheriteBlocks = 0;

  @Inject(
    method = "<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
    at = @At("TAIL")
  )
  private void init(BlockPos pos, BlockState state, CallbackInfo cir) {
    if (PropertiesCache.getInstance().getBoolProperty(Tweaks.BETTER_BEACONS.getName())) {
      this.propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
          return switch (index) {
            case 0 -> BeaconBlockEntityMixin.this.level;
            case 1 -> StatusEffect.getRawId(BeaconBlockEntityMixin.this.primary);
            case 2 -> StatusEffect.getRawId(BeaconBlockEntityMixin.this.secondary);
            case 3 -> Item.getRawId(BeaconBlockEntityMixin.this.payment);
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {
          switch (index) {
            case 0:
              BeaconBlockEntityMixin.this.level = value;
              break;
            case 1:
              assert BeaconBlockEntityMixin.this.world != null;
              if (!BeaconBlockEntityMixin.this.world.isClient && !BeaconBlockEntityMixin.this.beamSegments.isEmpty()) {
                BeaconBlockEntity.playSound(BeaconBlockEntityMixin.this.world, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT);
              }
              BeaconBlockEntityMixin.this.primary = getPotionEffectById(value);
              break;
            case 2:
              BeaconBlockEntityMixin.this.secondary = getPotionEffectById(value);
            case 3:
              if (new ItemStack(Item.byRawId(value)).isIn(ItemTags.BEACON_PAYMENT_ITEMS)) {
                BeaconBlockEntityMixin.this.payment = Item.byRawId(value);
              }
          }
        }

        @Override
        public int size() {
          return 4;
        }
      };
    }
  }

  @Inject(
    method = "writeNbt",
    at = @At("HEAD")
  )
  public void writeNbt(NbtCompound nbt, CallbackInfo ci) {
    if (PropertiesCache.getInstance().getBoolProperty(Tweaks.BETTER_BEACONS.getName())) {
      nbt.putInt("payment", Item.getRawId(this.payment));
    }
  }

  @Inject(
    method = "readNbt",
    at = @At("HEAD")
  )
  public void readNbt(NbtCompound nbt, CallbackInfo ci) {
    if (PropertiesCache.getInstance().getBoolProperty(Tweaks.BETTER_BEACONS.getName())) {
      this.payment = Item.byRawId(nbt.getInt("payment"));
    }
  }

  @Nullable
  @Shadow
  static StatusEffect getPotionEffectById(int id) {
    return null;
  }

  @Inject(
    method = "tick",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void tick(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
    assert (blockEntity != null);
    BeaconBlockEntityMixin blockEntityMixin = ((BeaconBlockEntityMixin) (Object) blockEntity);

    int i = pos.getX();
    int j = pos.getY();
    int k = pos.getZ();
    BlockPos blockPos2;

    if (blockEntityMixin.minY < j) {
      blockPos2 = pos;
      blockEntityMixin.field_19178 = Lists.newArrayList();
      blockEntityMixin.minY = pos.getY() - 1;
    } else {
      blockPos2 = new BlockPos(i, blockEntityMixin.minY + 1, k);
    }

    BeamSegment beamSegment = blockEntityMixin.field_19178.isEmpty() ? null : blockEntityMixin.field_19178.get(blockEntityMixin.field_19178.size() - 1);
    int l = world.getTopY(Type.WORLD_SURFACE, i, k);

    int n;
    for(n = 0; n < 10 && blockPos2.getY() <= l; ++n) {
      BlockState blockState = world.getBlockState(blockPos2);
      Block block = blockState.getBlock();
      if (block instanceof Stainable) {
        float[] fs = ((Stainable)block).getColor().getColorComponents();
        if (blockEntityMixin.field_19178.size() <= 1) {
          beamSegment = new BeamSegment(fs);
          blockEntityMixin.field_19178.add(beamSegment);
        } else if (beamSegment != null) {
          if (Arrays.equals(fs, beamSegment.getColor())) {
            beamSegment.increaseHeight();
          } else {
            beamSegment = new BeamSegment(new float[]{(beamSegment.getColor()[0] + fs[0]) / 2.0F, (beamSegment.getColor()[1] + fs[1]) / 2.0F, (beamSegment.getColor()[2] + fs[2]) / 2.0F});
            blockEntityMixin.field_19178.add(beamSegment);
          }
        }
      } else {
        if (beamSegment == null || blockState.getOpacity(world, blockPos2) >= 15 && !blockState.isOf(Blocks.BEDROCK)) {
          blockEntityMixin.field_19178.clear();
          blockEntityMixin.minY = l;
          break;
        }

        beamSegment.increaseHeight();
      }

      blockPos2 = blockPos2.up();
      ++blockEntityMixin.minY;
    }

    n = blockEntityMixin.level;
    if (world.getTime() % 80L == 0L) {
      if (!blockEntityMixin.beamSegments.isEmpty()) {
        blockEntityMixin.level = updateLevel(blockEntityMixin, world, i, j, k); // differs from original
      }

      if (blockEntityMixin.level > 0 && !blockEntityMixin.beamSegments.isEmpty()) {
        applyPlayerEffects(world, pos, blockEntityMixin); // differs from original
        BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT);
      }
    }

    if (blockEntityMixin.minY >= l) {
      blockEntityMixin.minY = world.getBottomY() - 1;
      boolean bl = n > 0;
      blockEntityMixin.beamSegments = blockEntityMixin.field_19178;
      if (!world.isClient) {
        boolean bl2 = blockEntityMixin.level > 0;
        if (!bl && bl2) {
          BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);

          for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, (new Box(i, j, k, i, j - 4, k)).expand(10.0D, 5.0D, 10.0D))) {
            Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, blockEntityMixin.level);
          }
        } else if (bl && !bl2) {
          BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
        }
      }
    }

    ci.cancel();
  }

  private static int totalBlocks (int l) {
    return (l == 0) ? l : totalBlocks(l - 1) + (int)Math.pow(2 * l + 1, 2);
  }

  private static double floorDouble (double l) {
    return Math.floor(l * 100) / 100;
  }

  private static void applyPlayerEffects(World world, BlockPos pos, BeaconBlockEntityMixin blockEntityMixin) {
    StatusEffect primaryEffect = blockEntityMixin.primary;

    if (!world.isClient && primaryEffect != null) {
      StatusEffect secondaryEffect = blockEntityMixin.secondary;

      int beaconLevel = blockEntityMixin.level;
      double beaconRange;
      double beaconBonus = blockEntityMixin.bonus;
      Item beaconPayment = blockEntityMixin.payment;

      int beaconIronBlocks = blockEntityMixin.ironBlocks;
      int beaconGoldBlocks = blockEntityMixin.goldBlocks;
      int beaconEmeraldBlocks = blockEntityMixin.emeraldBlocks;
      int beaconDiamondBlocks = blockEntityMixin.diamondBlocks;
      int beaconNetheriteBlocks = blockEntityMixin.netheriteBlocks;

      double d;

      int i = 0;
      if (beaconLevel >= 4 && primaryEffect == secondaryEffect) {
        i = 1;
      }

      int j = (9 + beaconLevel * 2) * 20;

      if (PropertiesCache.getInstance().getBoolProperty(Tweaks.BETTER_BEACONS.getName())) {
        // La formula del nivel: f(x) = f(x-1) + (2x+1)^2

        int blocks = totalBlocks(beaconLevel);

        beaconRange = floorDouble(
          (double)beaconIronBlocks * floorDouble(((double)beaconLevel * 10 + 10)/blocks)
            + (double)beaconGoldBlocks * floorDouble(((double)beaconLevel * 15 + 15)/blocks)
            + (double)beaconEmeraldBlocks * floorDouble(((double)beaconLevel * 25 + 25)/blocks)
            + (double)beaconDiamondBlocks * floorDouble(((double)beaconLevel * 30 + 30)/blocks)
            + (double)beaconNetheriteBlocks * floorDouble(((double)beaconLevel * 40 + 40)/blocks)
        );

        beaconRange = Math.floor(beaconRange) + 1;

        if (beaconPayment != null) {
          if (beaconPayment.equals(Items.GOLD_INGOT)) {
            j += j * 25/100;
          } else if (beaconPayment.equals(Items.EMERALD)) {
            beaconBonus = beaconRange * 5/100;
          } else if (beaconPayment.equals(Items.DIAMOND)) {
            beaconBonus = beaconRange * 20/100;
          } else if (beaconPayment.equals(Items.NETHERITE_INGOT)) {
            beaconBonus = beaconRange * 20/100;
            j += j * 25/100;
          }
        }

        d = beaconRange + beaconBonus;
      } else {
        d = beaconLevel * 10 + 10;
      }

      Box box = (new Box(pos)).expand(d).stretch(0.0D, world.getHeight(), 0.0D);
      List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
      Iterator<PlayerEntity> var11 = list.iterator();

      PlayerEntity playerEntity2;
      while(var11.hasNext()) {
        playerEntity2 = var11.next();
        playerEntity2.addStatusEffect(new StatusEffectInstance(primaryEffect, j, i, true, true));
      }

      if (beaconLevel >= 4 && primaryEffect != secondaryEffect && secondaryEffect != null) {
        var11 = list.iterator();

        while(var11.hasNext()) {
          playerEntity2 = var11.next();
          playerEntity2.addStatusEffect(new StatusEffectInstance(secondaryEffect, j, 0, true, true));
        }
      }
    }
  }

  private static int updateLevel(BeaconBlockEntityMixin blockEntityMixin, World world, int x, int y, int z) {
    int internalLevel = 0;
    int internalRange = 0;

    int internalIronBlocks = 0;
    int internalGoldBlocks = 0;
    int internalEmeraldBlocks = 0;
    int internalDiamondBlocks = 0;
    int internalNetheriteBlocks = 0;

    for(int i = 1; i <= 4; internalLevel = i++) {
      int counterIron = 0;
      int counterGold = 0;
      int counterEmerald = 0;
      int counterDiamond = 0;
      int counterNetherite = 0;

      int j = y - i;
      if (j < world.getBottomY()) {
        break;
      }

      boolean bl = true;

      for(int k = x - i; k <= x + i && bl; ++k) {
        for(int l = z - i; l <= z + i; ++l) {
          BlockState blockState = world.getBlockState(new BlockPos(k, j, l));

          if (!blockState.isIn(BlockTags.BEACON_BASE_BLOCKS)) {
            bl = false;
            break;
          }

          if (blockState.isOf(Blocks.IRON_BLOCK)) {
            counterIron++;
          } else if (blockState.isOf(Blocks.GOLD_BLOCK)) {
            counterGold++;
          } else if (blockState.isOf(Blocks.EMERALD_BLOCK)) {
            counterEmerald++;
          } else if (blockState.isOf(Blocks.DIAMOND_BLOCK)) {
            counterDiamond++;
          } else if (blockState.isOf(Blocks.NETHERITE_BLOCK)) {
            counterNetherite++;
          }
        }
      }

      if (!bl) {
        break;
      }

      internalIronBlocks += counterIron;
      internalGoldBlocks += counterGold;
      internalEmeraldBlocks += counterEmerald;
      internalDiamondBlocks += counterDiamond;
      internalNetheriteBlocks += counterNetherite;
    }

    if (PropertiesCache.getInstance().getBoolProperty(Tweaks.BETTER_BEACONS.getName())) {
      blockEntityMixin.range = internalRange;
      blockEntityMixin.ironBlocks = internalIronBlocks;
      blockEntityMixin.goldBlocks = internalGoldBlocks;
      blockEntityMixin.emeraldBlocks = internalEmeraldBlocks;
      blockEntityMixin.diamondBlocks = internalDiamondBlocks;
      blockEntityMixin.netheriteBlocks = internalNetheriteBlocks;
    }

    return internalLevel;
  }

  static {
    EFFECTS_BY_LEVEL = new StatusEffect[][]{{StatusEffects.SPEED, StatusEffects.HASTE}, {StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST}, {StatusEffects.STRENGTH}, {StatusEffects.REGENERATION}};
    EFFECTS = Arrays.stream(EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
  }
}
