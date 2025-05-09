package me.gv0id.arbalests.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractWindGaleEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
    public static final ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
            true, false, Optional.empty(), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );
    public static final double field_52224 = 0.25;
    protected Entity lastDeflectedEntity;

    public AbstractWindGaleEntity(EntityType<? extends AbstractWindGaleEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.0;
    }

    public AbstractWindGaleEntity(EntityType<? extends AbstractWindGaleEntity> type, World world, Entity owner, double x, double y, double z) {
        super(type, x, y, z, world);
        this.setOwner(owner);
        this.accelerationPower = 0.0;
    }

    public AbstractWindGaleEntity(EntityType<? extends AbstractWindGaleEntity> entityType, double d, double e, double f, Vec3d vec3d, World world) {
        super(entityType, d, e, f, vec3d, world);
        this.accelerationPower = 0.0;
    }
    public AbstractWindGaleEntity(Entity owner, EntityType<? extends AbstractWindGaleEntity> entityType, double d, double e, double f, Vec3d vec3d, World world) {
        super(entityType, d, e, f, vec3d, world);
        this.setOwner(owner);
        this.accelerationPower = 0.0;
    }

    @Override
    protected Box calculateDefaultBoundingBox(Vec3d pos) {
        float f = this.getType().getDimensions().width() / 2.0F;
        float g = this.getType().getDimensions().height();
        float h = 0.15F;
        return new Box(pos.x - (double)f, pos.y - 0.15F, pos.z - (double)f, pos.x + (double)f, pos.y - 0.15F + (double)g, pos.z + (double)f);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return other instanceof AbstractWindGaleEntity ? false : super.collidesWith(other);
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof AbstractWindGaleEntity) {
            return false;
        } else {
            return entity.getType() == EntityType.END_CRYSTAL || entity.getType() == EntityType.SNOWBALL ? false : super.canHit(entity);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof SnowballEntity || entityHitResult.getEntity() instanceof SnowProjectileEntity){
            this.iceExplosion(this.getPos(),entityHitResult.getEntity());
            this.discard();
            super.onEntityHit(entityHitResult);
        }
        super.onEntityHit(entityHitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            LivingEntity livingEntity2 = this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
            Entity entity = entityHitResult.getEntity();
            if (livingEntity2 != null) {
                livingEntity2.onAttacking(entity);
            }

            DamageSource damageSource = this.getDamageSources().windCharge(this, livingEntity2);
            if (entity.damage(serverWorld, damageSource, 1.0F) && entity instanceof LivingEntity livingEntity3) {
                EnchantmentHelper.onTargetDamaged(serverWorld, livingEntity3, damageSource);
            }

            this.createExplosion(this.getPos());
        }
    }

    @Override
    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
    }

    protected abstract void createExplosion(Vec3d pos);

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            Vec3i vec3i = blockHitResult.getSide().getVector();
            Vec3d vec3d = Vec3d.of(vec3i).multiply(0.25, 0.25, 0.25);
            Vec3d vec3d2 = blockHitResult.getPos().add(vec3d);
            this.createExplosion(vec3d2);
            this.discard();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.discard();
        }
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    @Override
    protected float getDrag() {
        return 1.0F;
    }

    @Override
    protected float getDragInWater() {
        return this.getDrag();
    }

    @Nullable
    @Override
    protected ParticleEffect getParticleType() {
        return null;
    }

    @Override
    public void tick() {
        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopYInclusive() + 30) {
            this.createExplosion(this.getPos());
            this.discard();
        } else {
            super.tick();
        }
    }

    public void iceExplosion(Vec3d pos, Entity entity){
    }
}