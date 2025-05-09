package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.RecisableTrailParticleEffect;
import me.gv0id.arbalests.particle.TrailParticleEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class WindGaleEntity extends AbstractWindGaleEntity {
    
    private float KNOCKBACK_POWER = 1.0F;
    private float EXPLOSION_POWER = 3.0F;
    private static final float MAX_RENDER_DISTANCE_WHEN_NEWLY_SPAWNED = MathHelper.square(3.5F);
    private int deflectCooldown = 5;

    private final float ICE_KNOCKBACK_POWER = 3.0F;
    private final float ICE_EXPLOSION_POWER = 2.0F;

    // Experimental Shit
    private Vec3d previousPreviousEyePos;
    private Vec3d previousEyePos;
    private int trailIndex = 0;

    public ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
            true, false, Optional.of(KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );
    
    public WindGaleEntity(EntityType<? extends AbstractWindGaleEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindGaleEntity(PlayerEntity player, World world, double x, double y, double z) {
        super(ModEntityType.WIND_GALE, world, player, x, y, z);
    }

    public WindGaleEntity( World world, double x, double y, double z, Vec3d velocity,float knockback,float explosionPower) {
        super(ModEntityType.WIND_GALE, x, y, z, velocity, world);
        KNOCKBACK_POWER = knockback;
        EXPLOSION_POWER = explosionPower;
        previousEyePos = this.getPos().subtract(velocity.normalize());
        previousPreviousEyePos = this.previousEyePos.subtract(velocity.normalize());
    }

    public WindGaleEntity(Entity owner, World world, double x, double y, double z, Vec3d velocity,float knockback,float explosionPower) {
        super(owner,ModEntityType.WIND_GALE, x, y, z, velocity, world);
        KNOCKBACK_POWER = knockback;
        EXPLOSION_POWER = explosionPower;

    }

    public WindGaleEntity(World world, double x, double y, double z, Vec3d velocity) {
        super(ModEntityType.WIND_GALE, x, y, z, velocity, world);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("knockback",KNOCKBACK_POWER);
        nbt.putFloat("power",EXPLOSION_POWER);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        KNOCKBACK_POWER = nbt.getFloat("knockback");
        EXPLOSION_POWER = nbt.getFloat("power");
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();

            if (entity instanceof SnowProjectileEntity){
                this.iceExplosion(this.getPos(),entity);
                this.discard();
            }

            if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity projectileEntity) {
                projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this.getOwner(), this.getOwner(), true);
            }

            this.onEntityHit(entityHitResult);
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            this.onBlockHit(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.getWorld().getBlockState(blockPos)));
        }
    }

    @Override
    protected ProjectileDeflection hitOrDeflect(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();

            if (entity instanceof SnowProjectileEntity){
                this.iceExplosion(this.getPos(),entity);
                this.discard();
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

    @Override
    public void tick() {
        super.tick();
        if (this.deflectCooldown > 0) {
            this.deflectCooldown--;
        }

        Vec3d p = this.getEyePos();
        for (int i = 0; i < 3; i++) {
            Vec3d r = new Vec3d(
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6)
            );
            float velR = (float) MathHelper.lerp(this.random.nextDouble(), -2F, 0F);
            Vec3d velocity = this.getVelocity().multiply(velR);
            this.getWorld().addParticle(ParticleTypes.SMALL_GUST, p.x + (r.x * 0.5) + velocity.x  , p.y + (r.y * 0.5) + velocity.y, p.z + (r.z * 0.5) + velocity.z,r.x * 0.5,r.y * 0.5,r.z * 0.5);
        }

        if (this.previousEyePos == null){
            previousEyePos = this.getEyePos().subtract(this.getVelocity().normalize());
        }
        if (this.previousPreviousEyePos == null){
            previousPreviousEyePos = this.previousEyePos.subtract(this.getVelocity().normalize());
        }

        if (this.age > 1){
            this.getWorld().addParticle(
                    RecisableTrailParticleEffect.create(
                            ModParticles.EXPERIMENTAL_TRAIL, ColorHelper.fromFloats(1F,0.75F,0.75F,1F),
                            10, 1F,
                            this.previousEyePos, this.previousPreviousEyePos,
                            trailIndex++
                    ), this.getPos().x, this.getPos().y,this.getPos().z,0,0,0
            );
        }

        this.previousPreviousEyePos = this.previousEyePos;
        this.previousEyePos = this.getEyePos();
    }

    @Override
    public boolean collidesWith(Entity other) {
        return other instanceof AbstractWindGaleEntity ? false : super.collidesWith(other);
    }

    @Override
    public boolean deflect(ProjectileDeflection deflection, @Nullable Entity deflector, @Nullable Entity owner, boolean fromAttack) {
        return this.deflectCooldown > 0 ? false : super.deflect(deflection, deflector, owner, fromAttack);
    }


    @Override
    public void createExplosion(Vec3d pos) {
        EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
                true, false, Optional.of(KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
        );
        this.getWorld()
                .createExplosion(
                        this,
                        null,
                        EXPLOSION_BEHAVIOR,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        EXPLOSION_POWER,
                        false,
                        World.ExplosionSourceType.TRIGGER,
                        ParticleTypes.GUST_EMITTER_SMALL,
                        ParticleTypes.GUST_EMITTER_LARGE,
                        SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
                );
    }

    @Override
    public void iceExplosion(Vec3d pos, Entity entity){
        EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
                false, true, Optional.of(ICE_KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
        );
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
    public boolean shouldRender(double distance) {
        return this.age < 2 && distance < (double)MAX_RENDER_DISTANCE_WHEN_NEWLY_SPAWNED ? false : super.shouldRender(distance);
    }
}
