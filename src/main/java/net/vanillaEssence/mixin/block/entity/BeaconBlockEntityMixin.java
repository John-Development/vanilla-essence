package net.vanillaEssence.mixin.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import net.vanillaEssence.util.PropertiesCache;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntity implements NamedScreenHandlerFactory {

  public BeaconBlockEntityMixin(BlockPos pos, BlockState state) {
		super(BlockEntityType.BEACON, pos, state);
	}

  @Shadow
  private int level;
  @Shadow
  private StatusEffect primary;
  @Shadow
  private StatusEffect secondary;
  @Shadow
  private PropertyDelegate propertyDelegate;
  @Shadow
  private List<BeamSegment> beamSegments = Lists.newArrayList();
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
              if (!BeaconBlockEntityMixin.this.world.isClient && !BeaconBlockEntityMixin.this.beamSegments.isEmpty()) {
                BeaconBlockEntityMixin.this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
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

        public int size() {
          return 4;
        }
      };
    }
  }

  public void playSound(SoundEvent soundEvent) {
    this.world.playSound((PlayerEntity) null, this.pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
  }

  @Nullable
  private static StatusEffect getPotionEffectById(int id) {
    StatusEffect statusEffect = StatusEffect.byRawId(id);
    return EFFECTS.contains(statusEffect) ? statusEffect : null;
  }

  @Inject(
    method = "writeNbt",
    at = @At("HEAD")
  )
  public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
    if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("beacons-enabled"))) {
      nbt.putInt("payment", Item.getRawId(this.payment));
    }
  }

  @Inject(
    method = "readNbt",
    at = @At("HEAD")
  )
  public void readNbt(BlockState state, NbtCompound nbt, CallbackInfo cir) {
    if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("beacons-enabled"))) {
      this.payment = Item.byRawId(nbt.getInt("payment"));
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
    if (!this.world.isClient && this.primary != null) {
      double d;
      int i, j;
      if (Boolean.parseBoolean(PropertiesCache.getInstance().getProperty("beacons-enabled"))) {
        // La formula del nivel: f(x) = f(x-1) + (2x+1)^2

        int blocks = this.totalBlocks(this.level);

        this.range = floorDouble(
          (double)this.ironBlocks * floorDouble(((double)this.level * 10 + 10)/blocks)
          + (double)this.goldBlocks * floorDouble(((double)this.level * 15 + 15)/blocks)
          + (double)this.emeraldBlocks * floorDouble(((double)this.level * 25 + 25)/blocks)
          + (double)this.diamondBlocks * floorDouble(((double)this.level * 30 + 30)/blocks)
          + (double)this.netheriteBlocks * floorDouble(((double)this.level * 40 + 40)/blocks)
        );

        i = 0;

        if (this.level >= 4 && this.primary == this.secondary) {
          i = 1;
        }

        j = (9 + this.level * 2) * 20;
        if (this.payment != null) {
          if (this.payment.equals(Items.IRON_INGOT)) {
            // Nothing
          } else if (this.payment.equals(Items.GOLD_INGOT)) {
            j += j * 25/100;
          } else if (this.payment.equals(Items.EMERALD)) {
            this.bonus = this.range * 5/100;
          } else if (this.payment.equals(Items.DIAMOND)) {
            this.bonus = this.range * 20/100;
          } else if (this.payment.equals(Items.NETHERITE_INGOT)) { 
            this.bonus = this.range * 20/100;
            j += j * 25/100;
          }
        }

        d = this.range + this.bonus;

        Box box = (new Box(this.pos)).expand(d).stretch(0.0D, (double)this.world.getHeight(), 0.0D);
        List<PlayerEntity> list = this.world.getNonSpectatingEntities(PlayerEntity.class, box);
        Iterator<PlayerEntity> var7 = list.iterator();

        PlayerEntity playerEntity2;
        while(var7.hasNext()) {
          playerEntity2 = (PlayerEntity)var7.next();
          playerEntity2.addStatusEffect(new StatusEffectInstance(this.primary, j, i, true, true));
        }

        if (this.level >= 4 && this.primary != this.secondary && this.secondary != null) {
          var7 = list.iterator();

          while(var7.hasNext()) {
            playerEntity2 = (PlayerEntity)var7.next();
            playerEntity2.addStatusEffect(new StatusEffectInstance(this.secondary, j, 0, true, true));
          }
        }
        ci.cancel(); 
      }
    }
  }
}