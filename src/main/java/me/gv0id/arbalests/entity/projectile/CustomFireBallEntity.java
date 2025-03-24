package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.RecisableTrailParticleEffect;
import me.gv0id.arbalests.particle.TrailParticleEffect;
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
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
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
    private Vec3d previousEyePos;
    private Vec3d previousPreviousEyePos;
    private int trailIndex = 0;

    public CustomFireBallEntity(EntityType<? extends CustomFireBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public CustomFireBallEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        super(ModEntityType.CUSTOM_FIREBALL, owner, velocity, world);
        this.EXPLOSION_POWER = explosionPower;
    }

    public CustomFireBallEntity(Entity owner, World world, double x, double y, double z, Vec3d velocity,float knockback,float explosionPower) {
        super(ModEntityType.CUSTOM_FIREBALL, x, y, z, velocity, world);
        this.setOwner(owner);
        this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
        this.refreshPosition();
        KNOCKBACK_POWER = knockback;
        EXPLOSION_POWER = explosionPower;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3d p = this.getEyePos();
        for (int i = 0; i < 3; i++) {
            Vec3d r = new Vec3d(
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6)
            );
            float velR = (float) MathHelper.lerp(this.random.nextDouble(), -2F, 0F);
            Vec3d velocity = this.getVelocity().multiply(velR);
            this.getWorld().addParticle(ParticleTypes.SMALL_FLAME, p.x + (r.x * 0.5) + velocity.x  , p.y + (r.y * 0.5) + velocity.y, p.z + (r.z * 0.5) + velocity.z,r.x * 0.1,r.y * 0.1,r.z * 0.1);
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
                            ModParticles.EXPERIMENTAL_TRAIL, ColorHelper.fromFloats(1F,0.9F,0.4F,0F),
                            15, 2F,
                            this.previousEyePos, this.previousPreviousEyePos,
                            trailIndex++
                    ), this.getEyePos().x, this.getEyePos().y,this.getEyePos().z,0,0,0
            );
        }

        this.previousPreviousEyePos = this.previousEyePos;
        this.previousEyePos = this.getEyePos();
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
            else if (entity instanceof EndCrystalProjectileEntity endCrystalProjectileEntity){
                DamageSource damageSource = this.getDamageSources().explosion(this, this.getOwner());
                endCrystalProjectileEntity.fireExplosion(damageSource, this);
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
                ModParticles.FIRE_GUST_EMITTER,
                ModParticles.FIRE_GUST_EMITTER,
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
