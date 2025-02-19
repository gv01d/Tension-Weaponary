package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;

import java.util.Optional;
import java.util.function.Function;

public class SnowProjectileEntity extends ThrownItemEntity {
    private static final float ICE_KNOCKBACK_POWER = 3.0F;
    private final float ICE_EXPLOSION_POWER = 2.0F;
    private static AdvancedExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
                false, true, Optional.of(ICE_KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
            );
    private Entity lastDeflectedEntity;

    public SnowProjectileEntity(EntityType<? extends SnowProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SnowProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntityType.CUSTOM_SNOWBALL, owner, world, stack);
    }

    public SnowProjectileEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntityType.CUSTOM_SNOWBALL, x, y, z, world, stack);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d p = this.getPos();
        for (int i = 0; i < 3; i++) {
            Vec3d r = new Vec3d(
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6)
            );
            this.getWorld().addParticle(ModParticles.SNOW_FLAKE, p.x, p.y, p.z,r.x * 0.5,r.y * 0.5,r.z * 0.5);
        }
    }

    @Override
    protected ProjectileDeflection hitOrDeflect(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();



            if (entity instanceof WindGaleEntity|| entity instanceof WindChargeEntity){
                iceExplosion(entity.getPos(),this);
                entity.discard();
            }

            ProjectileDeflection projectileDeflection = entity.getProjectileDeflection(this);
            if (projectileDeflection != ProjectileDeflection.NONE) {
                if (entity != this.lastDeflectedEntity && this.deflect(projectileDeflection, entity, this.getOwner(), false)) {
                    this.lastDeflectedEntity = entity;
                }

                return projectileDeflection;
            }
        } else if (this.deflectsAgainstWorldBorder() && hitResult instanceof BlockHitResult blockHitResult && blockHitResult.isAgainstWorldBorder()) {
            ProjectileDeflection projectileDeflection2 = ProjectileDeflection.SIMPLE;
            if (this.deflect(projectileDeflection2, null, this.getOwner(), false)) {
                this.setVelocity(this.getVelocity().multiply(0.2));
                return projectileDeflection2;
            }
        }

        this.onCollision(hitResult);
        return ProjectileDeflection.NONE;
    }

    public void iceExplosion(Vec3d pos, Entity entity){
        this.getWorld()
                .createExplosion(
                        entity,
                        null,
                        EXPLOSION_BEHAVIOR,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        ICE_EXPLOSION_POWER,
                        false,
                        World.ExplosionSourceType.TRIGGER,
                        ModParticles.SNOW_GUST_EMITTER,
                        ModParticles.SNOW_GUST_EMITTER,
                        SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
                );
        entity.discard();
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

        if (entity != this.getOwner()){
            int f = entity.isFrozen() ? (int) Math.min(400, entity.getFrozenTicks() * 2) : 200;
            int i = (entity.isFrozen() ? 2 : 1 ) * (entity instanceof BlazeEntity ? 5 : 2);
            entity.extinguish();
            entity.addVelocity(this.getVelocity().multiply(0.2));
            entity.setFrozenTicks(f);
            entity.serverDamage(this.getDamageSources().thrown(this, this.getOwner()), (float)i);
        }

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
