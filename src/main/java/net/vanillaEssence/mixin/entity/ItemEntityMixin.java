package net.vanillaEssence.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.vanillaEssence.util.PropertiesCache;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

  public ItemEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  public int renderTicks;
  public int orbAge;
  public int pickupDelay;
  private PlayerEntity target;
  private int lastTargetUpdateTick;

  private void applyWaterMovement() {
    Vec3d vec3d = this.getVelocity();
    this.setVelocity(vec3d.x * 0.9900000095367432D, Math.min(vec3d.y + 5.000000237487257E-4D, 0.05999999865889549D), vec3d.z * 0.9900000095367432D);
 }

  @Inject(
    method = "tick",
    at = @At("HEAD")
  )
  public void tick(
    CallbackInfo cir
  ) {
    PropertiesCache cache = PropertiesCache.getInstance();
    if (Boolean.parseBoolean(cache.getProperty("magnetic-lure-enabled"))) {
      if (this.pickupDelay > 0) {
        --this.pickupDelay;
      }

      this.prevX = this.getX();
      this.prevY = this.getY();
      this.prevZ = this.getZ();
      
      if (this.isSubmergedIn(FluidTags.WATER)) {
          this.applyWaterMovement();
      } else if (!this.hasNoGravity()) {
          this.setVelocity(this.getVelocity().add(0.0D, -0.03D, 0.0D));
      }

      if (this.world.getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA)) {
          this.setVelocity((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
          this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
      }

      if (!this.world.isSpaceEmpty(this.getBoundingBox())) {
          this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
      }

      if (this.lastTargetUpdateTick < this.renderTicks - 20 + this.getEntityId() % 100) {
          if (this.target == null || this.target.squaredDistanceTo(this) > 64.0D) {
            this.target = this.world.getClosestPlayer(this, 8.0D);
          }

          this.lastTargetUpdateTick = this.renderTicks;
      }

      if (this.target != null && this.target.isSpectator()) {
          this.target = null;
      }

      if (this.target != null) {
        Iterable<ItemStack> equipped = target.getItemsEquipped();
        for (ItemStack itemStack : equipped) {
          ListTag listTag = itemStack.getEnchantments();
          if (!itemStack.getItem().equals(Items.FISHING_ROD)) {
            for (Tag tag : listTag) {
              
              // Example: {lvl:1s,id:"minecraft:protection"};
              Boolean hasLure = tag.toString().contains("id:\"minecraft:lure\"");
      
              if (hasLure) {
                int lvl = Integer.parseInt(Character.toString(tag.toString().charAt(5)));

                // attracts every item as an xp orb
                Vec3d vec3d = new Vec3d(this.target.getX() - this.getX(), this.target.getY() + (double)this.target.getStandingEyeHeight() / 2.0D - this.getY(), this.target.getZ() - this.getZ());
                double e = vec3d.lengthSquared();
                if (e < 64.0D) {
                  double f = 1.0D - Math.sqrt(e) / 8.0D;
                  this.setVelocity(this.getVelocity().add(vec3d.normalize().multiply(lvl * f * f * 0.1D)));
                }

                this.move(MovementType.SELF, this.getVelocity());
                float g = 0.98F;
                if (this.onGround) {
                    g = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getSlipperiness() * 0.98F;
                }

                this.setVelocity(this.getVelocity().multiply((double)g, 0.98D, (double)g));
                if (this.onGround) {
                    this.setVelocity(this.getVelocity().multiply(1.0D, -0.9D, 1.0D));
                }

                ++this.renderTicks;
                ++this.orbAge;
                if (this.orbAge >= 6000) {
                    this.remove();
                }
              }
            }
          }
        }
      }
    }
  }
}