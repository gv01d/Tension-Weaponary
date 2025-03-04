package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.effect.ModEffects;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.item.custom.CopperDiscItem;
import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.RecisableTrailParticleEffect;
import me.gv0id.arbalests.particle.TrailParticleEffect;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.BreezeWindChargeEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CustomEnderPearlEntity extends EnderPearlEntity{
    private static final float ENDER_KNOCKBACK_POWER = 3.0F;
    private final float ENDER_EXPLOSION_POWER = 2.0F;
    private static final AdvancedExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
            true, false, Optional.of(ENDER_KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );

    Vec3d previousPreviousEyePos;
    Vec3d previousEyePos;
    int trailIndex = 0;

    public CustomEnderPearlEntity(EntityType<? extends EnderPearlEntity> entityType, World world) {
        super(entityType, world);
    }

    public CustomEnderPearlEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntityType.CUSTOM_ENDER_PEARL,world);
        this.setOwner(owner);
        this.setItem(stack);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
    }

    protected Entity hitEntity = null;

    @Override
    public void tick() {
        super.tick();
        spawnParticles();
    }

    private void spawnParticles() {
        Vec3d p = this.getEyePos();
        for (int i = 0; i < (int) MathHelper.lerp(this.random.nextDouble(), 2, 4); i++) {
            Vec3d r = new Vec3d(
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6)
            );
            float velR = (float) MathHelper.lerp(this.random.nextDouble(), -2F, 0F);
            Vec3d velocity = this.getVelocity().multiply(velR);
            this.getWorld().addParticle(ModParticles.COSMIC_SPARK, p.x + (r.x * 0.5) + velocity.x  , p.y + (r.y * 0.5) + velocity.y, p.z + (r.z * 0.5) + velocity.z,r.x,r.y,r.z);
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
                            ModParticles.EXPERIMENTAL_TRAIL, ColorHelper.fromFloats(1F,0.203F,0.6F,0.533F),
                            10, 1,
                            this.previousEyePos, this.previousPreviousEyePos,
                            trailIndex++
                    ), this.getEyePos().x, this.getEyePos().y,this.getEyePos().z,0,0,0
            );
        }

        this.previousPreviousEyePos = this.previousEyePos;
        this.previousEyePos = this.getEyePos();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {

        this.hitEntity = entityHitResult.getEntity();
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        projectileOnCollision(hitResult);

        for (int i = 0; i < 32; i++) {
            this.getWorld()
                    .addParticle(
                            ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian()
                    );
        }

        if (this.getWorld() instanceof ServerWorld serverWorld && !this.isRemoved()) {
            Entity entity = this.getOwner();
            Entity swap = this.hitEntity;

            Vec3d shooterPos = this.getLastRenderPos();
            Vec3d swapPos = entity.getPos();
            Vec3d targetVel = entity.getVelocity();
            Vec3d shooterVel = entity.getVelocity();
            Vec3d explosionPos = Vec3d.ZERO;
            if (swap != null){
                shooterVel = swap.getVelocity();
                shooterPos = swap.getPos();

                explosionPos = shooterPos.add(this.getVelocity().normalize().negate());
            }

            if (entity != null && canTeleportEntityTo(entity, serverWorld)) {
                if (entity.hasVehicle()) {
                    entity.detach();
                }

                teleportShooter(entity,serverWorld,shooterVel,shooterPos,1.0F);
                if (swap != null){
                    teleportTarget(swap,serverWorld,targetVel,swapPos, explosionPos,3.0F,
                            entity instanceof LivingEntity livingEntity && (livingEntity.getStatusEffect(ModEffects.STRAFE) != null));
                }
                this.discard();
                return;
            }

            this.discard();
            return;
        }
    }


    private void teleportTarget(Entity target, ServerWorld serverWorld, Vec3d targetVel, Vec3d teleportPos, Vec3d explosionPos, float damage, boolean strafe ){
        if (target instanceof ServerPlayerEntity serverPlayerEntity) {
            if (this.hasPortalCooldown()) {
                target.resetPortalCooldown();
            }

            ServerPlayerEntity serverPlayerEntity2 = serverPlayerEntity.teleportTo(
                    new TeleportTarget(serverWorld, teleportPos, Vec3d.ZERO, 0.0F, 0.0F, PositionFlag.combine(PositionFlag.ROT, PositionFlag.DELTA), TeleportTarget.NO_OP)
            );
            if (serverPlayerEntity2 != null) {
                serverPlayerEntity2.onLanding();
                serverPlayerEntity2.clearCurrentExplosion();
                serverPlayerEntity2.damage(serverPlayerEntity.getServerWorld(), this.getDamageSources().enderPearl(), damage);
                if (strafe){
                    serverPlayerEntity2.addStatusEffect(new StatusEffectInstance(ModEffects.STRAFE,5,0,true,true,true));
                }
            }
            this.playTeleportSound(serverWorld, teleportPos);
        }else {
            Entity entity2 = target.teleportTo(new TeleportTarget(serverWorld, teleportPos, targetVel, target.getYaw(), target.getPitch(), TeleportTarget.NO_OP));
            if (entity2 != null) {
                entity2.onLanding();
            }
            if (entity2 instanceof WindGaleEntity || entity2 instanceof WindChargeEntity || entity2 instanceof BreezeWindChargeEntity){
                enderExplosion(explosionPos,entity2);
                playEndExplosionSound(serverWorld,teleportPos);
            }
            else if (entity2 instanceof EndCrystalProjectileEntity endCrystalProjectileEntity){
                DamageSource damageSource = this.getDamageSources().explosion(this, this.getOwner());
                endCrystalProjectileEntity.cosmicExplosion(damageSource, this);
            }
            if (strafe && entity2 instanceof LivingEntity livingEntity){
                livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.STRAFE,5,0,true,true,true));
            }

            this.playTeleportSound(serverWorld, teleportPos);
        }

    }

    public void enderExplosion(Vec3d pos, Entity entity){
        this.getWorld()
                .createExplosion(
                        entity,
                        null,
                        EXPLOSION_BEHAVIOR,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        ENDER_EXPLOSION_POWER,
                        false,
                        World.ExplosionSourceType.TRIGGER,
                        ModParticles.ENDER_GUST_EMITTER,
                        ModParticles.ENDER_GUST_EMITTER,
                        SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
                );
        entity.discard();
    }

    private void teleportShooter(Entity entity, ServerWorld serverWorld, Vec3d shooterVel, Vec3d teleportPos, float damage){
        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.networkHandler.isConnectionOpen()) {
                if (this.random.nextFloat() < 0.05F && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                    EndermiteEntity endermiteEntity = EntityType.ENDERMITE.create(serverWorld, SpawnReason.TRIGGERED);
                    if (endermiteEntity != null) {
                        endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                        serverWorld.spawnEntity(endermiteEntity);
                    }
                }

                if (this.hasPortalCooldown()) {
                    entity.resetPortalCooldown();
                }

                ServerPlayerEntity serverPlayerEntity2 = serverPlayerEntity.teleportTo(
                        new TeleportTarget(serverWorld, teleportPos, Vec3d.ZERO, 0.0F, 0.0F, PositionFlag.combine(PositionFlag.ROT, PositionFlag.DELTA), TeleportTarget.NO_OP)
                );
                if (serverPlayerEntity2 != null) {
                    serverPlayerEntity2.onLanding();
                    serverPlayerEntity2.clearCurrentExplosion();
                    serverPlayerEntity2.damage(serverPlayerEntity.getServerWorld(), this.getDamageSources().enderPearl(), damage);
                }

                this.playTeleportSound(serverWorld, teleportPos);
            }
        } else {
            Entity entity2 = entity.teleportTo(new TeleportTarget(serverWorld, teleportPos, shooterVel, entity.getYaw(), entity.getPitch(), TeleportTarget.NO_OP));
            if (entity2 != null) {
                entity2.onLanding();
            }

            this.playTeleportSound(serverWorld, teleportPos);
        }
    }

    private static boolean canTeleportEntityTo(Entity entity, World world) {
        if (entity.getWorld().getRegistryKey() == world.getRegistryKey()) {
            return !(entity instanceof LivingEntity livingEntity) ? entity.isAlive() : livingEntity.isAlive() && !livingEntity.isSleeping();
        } else {
            return entity.canUsePortals(true);
        }
    }

    private void playTeleportSound(World world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);
    }
    private void playEndExplosionSound(World world, Vec3d pos) {
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.PLAYERS);
    }

    protected void projectileOnCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();
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

}
