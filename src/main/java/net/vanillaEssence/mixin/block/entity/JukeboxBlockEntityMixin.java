package net.vanillaEssence.mixin.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.vanillaEssence.util.JukeboxInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxBlockEntityMixin extends BlockEntity implements JukeboxInventory, SidedInventory {
  private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);

  public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  public DefaultedList<ItemStack> getItems() {
    return items;
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    Inventories.readNbt(nbt, items);
  }

  @Override
  public void writeNbt(NbtCompound nbt) {
    Inventories.writeNbt(nbt, items);
    super.writeNbt(nbt);
  }

  @Override
  public int[] getAvailableSlots(Direction var1) {
    // Just return an array of all slots
    int[] result = new int[getItems().size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = i;
    }

    return result;
  }

  @Override
  public boolean canInsert(int slot, ItemStack stack, Direction direction) {
    return direction == Direction.UP;
  }

  @Override
  public boolean canExtract(int slot, ItemStack stack, Direction direction) {
    return true;
  }
}
