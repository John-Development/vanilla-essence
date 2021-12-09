package net.vanillaEssence.mixin.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BeaconBlockEntity.BeamSegment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import net.vanillaEssence.util.PropertiesCache;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntity implements NamedScreenHandlerFactory, Tickable {

  public BeaconBlockEntityMixin() {
    super(BlockEntityType.BEACON);
  }

  @Shadow
  private int level;
  @Shadow
  private StatusEffect primary;
  @Shadow
  private StatusEffect secondary;
  @Mutable
  @Final
  @Shadow
  private PropertyDelegate propertyDelegate;
  @Shadow
  private List<BeamSegment> beamSegments = Lists.newArrayList();
  @Final
  @Shadow
  private static Set<StatusEffect> EFFECTS;

  Item payment;
  double bonus;
  double range;

  int ironBlocks = 0;
  int goldBlocks = 0;
  int emeraldBlocks = 0;
  int diamondBlocks = 0;
  int netheriteBlocks = 0;

  @Inject(
    method = "<init>()V",
    at = @At("TAIL")
  )
  private void init(CallbackInfo cir) {
    if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("beacons-enabled"))) {
      this.propertyDelegate = new PropertyDelegate() {
        public int get(int index) {
          switch (index) {
            case 0:
              return BeaconBlockEntityMixin.this.level;
            case 1:
              return StatusEffect.getRawId(BeaconBlockEntityMixin.this.primary);
            case 2:
              return StatusEffect.getRawId(BeaconBlockEntityMixin.this.secondary);
            case 3:
              return Item.getRawId(BeaconBlockEntityMixin.this.payment);
            default:
              return 0;
          }
        }

        public void set(int index, int value) {
          switch (index) {
            case 0:
              BeaconBlockEntityMixin.this.level = value;
              break;
            case 1:
              assert BeaconBlockEntityMixin.this.world != null;
              if (!BeaconBlockEntityMixin.this.world.isClient && !BeaconBlockEntityMixin.this.beamSegments.isEmpty()) {
                BeaconBlockEntityMixin.this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
              }
              BeaconBlockEntityMixin.this.primary = BeaconBlockEntityMixin.getPotionEffectById(value);
              break;
            case 2:
              BeaconBlockEntityMixin.this.secondary = BeaconBlockEntityMixin.getPotionEffectById(value);
            case 3:
              if (Item.byRawId(value).isIn(ItemTags.BEACON_PAYMENT_ITEMS)) {
                BeaconBlockEntityMixin.this.payment = Item.byRawId(value);
              }
          }
        }

        public int size() {
          return 4;
        }
      };
    }
  }

  public void playSound(SoundEvent soundEvent) {
    assert this.world != null;
    this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
  }

  @Nullable
  private static StatusEffect getPotionEffectById(int id) {
    StatusEffect statusEffect = StatusEffect.byRawId(id);
    return EFFECTS.contains(statusEffect) ? statusEffect : null;
  }

  @Inject(
    method = "toTag",
    at = @At("HEAD")
  )
  public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
    if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("beacons-enabled"))) {
      tag.putInt("payment", Item.getRawId(this.payment));
    }
  }

  @Inject(
    method = "fromTag",
    at = @At("HEAD")
  )
  public void fromTag(BlockState state, CompoundTag tag, CallbackInfo cir) {
    if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("beacons-enabled"))) {
      this.payment = Item.byRawId(tag.getInt("payment"));
    }
  }

  @Inject(
    method = "updateLevel",
    at = @At("HEAD"),
    cancellable = true
  )
  private void updateLevel(int x, int y, int z, CallbackInfo ci) {
    if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("beacons-enabled"))) {
      this.level = 0;
      this.range = 0;

      this.ironBlocks = 0;
      this.goldBlocks = 0;
      this.emeraldBlocks = 0;
      this.diamondBlocks = 0;
      this.netheriteBlocks = 0;

      for(int i = 1; i <= 4; this.level = i++) {
        int counterIron = 0;
        int counterGold = 0;
        int counterEmerald = 0;
        int counterDiamond = 0;
        int counterNetherite = 0;

        int j = y - i;
        if (j < 0) {
          break;
        }

        boolean bl = true;

        for(int k = x - i; k <= x + i && bl; ++k) {
          for(int l = z - i; l <= z + i; ++l) {
            assert this.world != null;
            if (!this.world.getBlockState(new BlockPos(k, j, l)).isIn(BlockTags.BEACON_BASE_BLOCKS)) {
              bl = false;
              break;
            }
            if (this.world.getBlockState(new BlockPos(k, j, l)).isOf(Blocks.IRON_BLOCK)) {
              counterIron++;
            } else if (this.world.getBlockState(new BlockPos(k, j, l)).isOf(Blocks.GOLD_BLOCK)) {
              counterGold++;
            } else if (this.world.getBlockState(new BlockPos(k, j, l)).isOf(Blocks.EMERALD_BLOCK)) {
              counterEmerald++;
            } else if (this.world.getBlockState(new BlockPos(k, j, l)).isOf(Blocks.DIAMOND_BLOCK)) {
              counterDiamond++;
            } else if (this.world.getBlockState(new BlockPos(k, j, l)).isOf(Blocks.NETHERITE_BLOCK)) {
              counterNetherite++;
            }
          }
        }

        if (!bl) {
          break;
        }

        ironBlocks += counterIron;
        goldBlocks += counterGold;
        emeraldBlocks += counterEmerald;
        diamondBlocks += counterDiamond;
        netheriteBlocks += counterNetherite;
      }
      ci.cancel();
    }
  }

  private int totalBlocks (int l) {
    return (l == 0) ? l : totalBlocks(l - 1) + (int)Math.pow(2 * l + 1, 2);
  }

  private double floorDouble (double l) {
    return Math.floor(l * 100) / 100;
  }

  @Inject(
    method = "applyPlayerEffects",
    at = @At("HEAD"),
    cancellable = true
  )
  private void applyPlayerEffects(CallbackInfo ci) {
    StatusEffect primaryEffect = this.primary;

    assert world != null;
    if (!world.isClient && primaryEffect != null) {
      StatusEffect secondaryEffect = this.secondary;

      int beaconLevel = this.level;
      double beaconRange;
      double beaconBonus = this.bonus;
      Item beaconPayment = this.payment;

      int beaconIronBlocks = this.ironBlocks;
      int beaconGoldBlocks = this.goldBlocks;
      int beaconEmeraldBlocks = this.emeraldBlocks;
      int beaconDiamondBlocks = this.diamondBlocks;
      int beaconNetheriteBlocks = this.netheriteBlocks;

      double d;

      int i = 0;
      if (beaconLevel >= 4 && primaryEffect == secondaryEffect) {
        i = 1;
      }

      int j = (9 + beaconLevel * 2) * 20;

      if (PropertiesCache.getInstance().getBoolProperty("beacons-enabled")) {
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
      ci.cancel();
    }
  }
}