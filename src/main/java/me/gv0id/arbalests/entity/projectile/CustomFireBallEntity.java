package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;
import java.util.function.Function;

public class CustomFireBallEntity extends AbstractFireballEntity {
    private float EXPLOSION_POWER = 1;
    private float KNOCKBACK_POWER = 1.0F;
    private float FIRE_EXPLOSION_POWER = 4;
    private float FIRE_KNOCKBACK_POWER = 5.0F;
    public ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
            true, false, Optional.of(KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );
    private Entity lastDeflectedEntity;

    /*
    public CustomFireBallEntity(EntityType<? extends CustomFireBallEntity> entityType, World world) {
        super(entityType, world);
    }
    */

    public CustomFireBallEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        super(EntityType.FIREBALL, owner, velocity, world);
        this.EXPLOSION_POWER = explosionPower;
    }

    public CustomFireBallEntity(Entity owner, World world, double x, double y, double z, Vec3d velocity,float knockback,float explosionPower) {
        super(EntityType.FIREBALL, x, y, z, velocity, world);
        this.setOwner(owner);
        this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
        this.refreshPosition();
        KNOCKBACK_POWER = knockback;
        EXPLOSION_POWER = explosionPower;
    }

    public CustomFireBallEntity(EntityType<FireballEntity> fireballEntityEntityType, World world) {
        super(fireballEntityEntityType, world);
    }

    @Override
    protected ProjectileDeflection hitOrDeflect(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();

            if (entity instanceof WindGaleEntity|| entity instanceof WindChargeEntity){
                fireExplosion(entity.getPos(),this);
                entity.discard();
                this.discard();
                return ProjectileDeflection.NONE;
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
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
                    true, true, Optional.of(KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
            );

            Vec3d vec3d = this.getPos();
            float pow = this.EXPLOSION_POWER;
            SimpleParticleType simpleParticleType = ParticleTypes.EXPLOSION;
            SimpleParticleType simpleParticleType1 = ParticleTypes.EXPLOSION_EMITTER;
            World.ExplosionSourceType explosionSourceType = World.ExplosionSourceType.TRIGGER;

            if (hitResult.getType() == HitResult.Type.ENTITY ){
                Entity entity = ((EntityHitResult) hitResult).getEntity();
                if (!entity.isOnGround() && !entity.isTouchingWater()){
                    serverWorld.spawnParticles(ModParticles.LIGHT_FLASH, vec3d.x,vec3d.y,vec3d.z,1,0,0,0,0);
                    simpleParticleType = simpleParticleType1 = ModParticles.RED_BOOM;
                    Vec3d vec3d1 = entity.getVelocity();
                    entity.setVelocity(vec3d1.x,Math.max(0, vec3d1.y),vec3d1.z);
                    pow *= 1.5F;
                    explosionSourceType = World.ExplosionSourceType.TNT;

                    RegistryEntry<SoundEvent> registryEntry = RegistryEntry.of(SoundEvents.ITEM_TOTEM_USE);
                    for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()){
                        serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(registryEntry, SoundCategory.PLAYERS,vec3d.x,vec3d.y,vec3d.z,5F,0.5f, serverWorld.getRandom().nextLong()));
                    }
                }
            }

            //serverWorld.spawnParticles(ModParticles.RED_BOOM, vec3d.x,vec3d.y,vec3d.z,1,0,0,0,0);


            this.getWorld().createExplosion(
                    this,
                    null,
                    EXPLOSION_BEHAVIOR,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    pow,
                    true,
                    explosionSourceType,
                    simpleParticleType,
                    simpleParticleType1,
                    SoundEvents.ENTITY_GENERIC_EXPLODE
            );
            this.discard();
        }
    }

    public void fireExplosion(Vec3d pos, Entity me){
        EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
                true, true, Optional.of(FIRE_KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
        );
        this.getWorld().createExplosion(
                this,
                null,
                EXPLOSION_BEHAVIOR,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                (float)this.FIRE_EXPLOSION_POWER,
                true,
                World.ExplosionSourceType.TRIGGER,
                ModParticles.FIRE_GUST,
                ModParticles.FIRE_GUST,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity var6 = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            DamageSource damageSource = this.getDamageSources().fireball(this, entity2);
            var6.damage(serverWorld, damageSource, 6.0F);
            EnchantmentHelper.onTargetDamaged(serverWorld, var6, damageSource);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("ExplosionPower", (byte)this.EXPLOSION_POWER);
        nbt.putFloat("KnockbackPower", (byte)this.KNOCKBACK_POWER);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("ExplosionPower", NbtElement.NUMBER_TYPE)) {
            this.EXPLOSION_POWER = nbt.getFloat("ExplosionPower");
        }
        if (nbt.contains("KnockbackPower", NbtElement.NUMBER_TYPE)) {
            this.KNOCKBACK_POWER = nbt.getFloat("KnockbackPower");
        }
    }
}
