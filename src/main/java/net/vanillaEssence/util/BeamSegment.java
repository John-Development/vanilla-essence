package net.vanillaEssence.util;

import net.minecraft.block.entity.BeaconBlockEntity;

public final class BeamSegment extends BeaconBlockEntity.BeamSegment {
  private int height;

  public BeamSegment(float[] color) {
    super(color);
    this.height = 1;
  }

  @Override
  public void increaseHeight() {
    ++this.height;
  }

  @Override
  public int getHeight() {
    return this.height;
  }
}
