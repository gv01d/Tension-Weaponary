package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.entity.ModEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
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
    public ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
            true, false, Optional.of(KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );

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
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
                    true, true, Optional.of(KNOCKBACK_POWER), Registries.BLOCK.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
            );
            this.getWorld().createExplosion(
                    this,
                    null,
                    EXPLOSION_BEHAVIOR,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    (float)this.EXPLOSION_POWER,
                    true,
                    World.ExplosionSourceType.TRIGGER,
                    ParticleTypes.GUST_EMITTER_SMALL,
                    ParticleTypes.GUST_EMITTER_LARGE,
                    SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
            );
            this.discard();
        }
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
