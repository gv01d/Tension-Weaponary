package me.gv0id.arbalests.entity.projectile;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SnowProjectileEntity extends ThrownItemEntity {
    public SnowProjectileEntity(EntityType<? extends SnowProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SnowProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityType.SNOWBALL, owner, world, stack);
    }

    public SnowProjectileEntity(World world, double x, double y, double z, ItemStack stack) {
        super(EntityType.SNOWBALL, x, y, z, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SNOWBALL;
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; i++) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        int f = entity.isFrozen() ? (int) Math.min(400, entity.getFrozenTicks() * 2) : 200;
        int i = (entity.isFrozen() ? 2 : 1 ) * (entity instanceof BlazeEntity ? 5 : 2);
        entity.addVelocity(this.getVelocity().multiply(0.2));
        entity.setFrozenTicks(f);
        entity.serverDamage(this.getDamageSources().thrown(this, this.getOwner()), (float)i);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }
}
