package net.vanillaEssence.mixin.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.vanillaEssence.util.JukeboxInventory;
import net.vanillaEssence.util.PropertiesCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxBlockEntityMixin extends BlockEntity implements JukeboxInventory, SidedInventory {
  private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

  public JukeboxBlockEntityMixin(BlockEntityType<?> type) {
    super(type);
    this.inventory.set(0, ItemStack.EMPTY);
  }

  @Override
  public DefaultedList<ItemStack> getInventory() {
    return inventory;
  }

  @Override
  public int[] getAvailableSlots(Direction var1) {
    int[] result = new int[getInventory().size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = i;
    }

    return result;
  }

  @Inject(
    method = "getRecord",
    at = @At("RETURN"),
    cancellable = true
  )
  public void getRecord(CallbackInfoReturnable<ItemStack> cir) {
    cir.setReturnValue(this.inventory.get(0));
  }

  @Inject(
    method = "setRecord",
    at = @At("HEAD"),
    cancellable = true
  )
  public void setRecord(ItemStack stack, CallbackInfo ci) {
    this.inventory.set(0, stack);
    this.markDirty();
    ci.cancel();
  }

  @Override
  public boolean canInsert(int slot, ItemStack stack, Direction direction) {
    if (PropertiesCache.getInstance().getBoolProperty("redstoned-jukeboxes-enabled")) {
      assert this.world != null;
      boolean powered = this.world.isReceivingRedstonePower(pos);

      if (!powered && direction == Direction.UP) {
        if (!world.isClient) {
          world.syncWorldEvent(1010, pos, Item.getRawId(stack.getItem()));
        }
        this.world.setBlockState(pos, this.world.getBlockState(pos).with(Properties.HAS_RECORD, true), 2);
      }

      return !powered && direction == Direction.UP;
    }
    return false;
  }

  @Override
  public boolean canExtract(int slot, ItemStack stack, Direction direction) {
    if (PropertiesCache.getInstance().getBoolProperty("redstoned-jukeboxes-enabled")) {
      assert this.world != null;
      boolean powered = this.world.isReceivingRedstonePower(pos);

      if (!powered) {
        if (!world.isClient) {
          world.syncWorldEvent(1010, pos, 0);
        }
        this.world.setBlockState(pos, this.world.getBlockState(pos).with(Properties.HAS_RECORD, false), 2);
      }

      return !powered;
    }
    return false;
  }
}