package net.vanillaEssence.mixin.entity;

import net.vanillaEssence.util.TweaksEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

  public ItemEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Unique
  public int orbAge;
  @Unique
  private PlayerEntity target;

  @Unique
  private void applyWaterMovement() {
    Vec3d vec3d = this.getVelocity();
    this.setVelocity(vec3d.x * 0.9900000095367432D, Math.min(vec3d.y + 5.000000237487257E-4D, 0.05999999865889549D), vec3d.z * 0.9900000095367432D);
  }

  @Inject(
    method = "tick",
    at = @At("HEAD")
  )
  public void tick(CallbackInfo cir) {
    if (TweaksEnum.MAGNETIC_LURE.getBoolean()) {
      super.tick();
      this.prevX = this.getX();
      this.prevY = this.getY();
      this.prevZ = this.getZ();

      if (this.isSubmergedIn(FluidTags.WATER)) {
        this.applyWaterMovement();
      } else if (!this.hasNoGravity()) {
        this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
      }

      if (this.getWorld().getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA)) {
        this.setVelocity((this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.20000000298023224, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      }

      if (!this.getWorld().isSpaceEmpty(this.getBoundingBox())) {
        this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
      }

      if (this.age % 20 == 1) {
        if (this.target == null || this.target.squaredDistanceTo(this) > 64.0) {
          this.target = this.getWorld().getClosestPlayer(this, 8.0);
        }
      }

      if (this.target != null && (this.target.isSpectator() || this.target.isDead())) {
        this.target = null;
      }

      if (this.target != null) {
        Iterable<ItemStack> equipped = target.getItemsEquipped();
        for (ItemStack itemStack : equipped) {
          NbtList nbtList = itemStack.getEnchantments();
          if (!itemStack.getItem().equals(Items.FISHING_ROD)) {
            for (NbtElement nbt : nbtList) {

              // Example: {id:"minecraft:lure",lvl:1s};
              boolean hasLure = nbt.toString().contains("id:\"minecraft:lure\"");

              if (hasLure) {
                int lvl = Integer.parseInt(Character.toString(nbt.toString().split(",")[1].charAt(4)));

                // attracts every item as an xp orb
                Vec3d vec3d = new Vec3d(this.target.getX() - this.getX(), this.target.getY() + (double)this.target.getStandingEyeHeight() / 2.0 - this.getY(), this.target.getZ() - this.getZ());
                double d = vec3d.lengthSquared();
                if (d < 64.0) {
                  double e = 1.0 - Math.sqrt(d) / 8.0;
                  this.setVelocity(this.getVelocity().add(vec3d.normalize().multiply(lvl * e * e * 0.1)));
                }
                this.move(MovementType.SELF, this.getVelocity());
                float f = 0.98F;
                if (this.isOnGround()) {
                  f = this.getWorld().getBlockState(BlockPos.ofFloored(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98F;
                }

                this.setVelocity(this.getVelocity().multiply(f, 0.98, f));
                if (this.isOnGround()) {
                  this.setVelocity(this.getVelocity().multiply(1.0, -0.9, 1.0));
                }

                ++this.orbAge;
                if (this.orbAge >= 6000) {
                  this.discard();
                }
              }
            }
          }
        }
      }
    }
  }
}