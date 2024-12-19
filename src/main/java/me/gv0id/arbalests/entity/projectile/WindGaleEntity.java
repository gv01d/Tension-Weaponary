package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.entity.ModEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class WindGaleEntity extends AbstractWindGaleEntity {
    
    private static float KNOCKBACK_POWER = 2.0F;
    private float EXPLOSION_POWER = 3.0F;
    private static final float MAX_RENDER_DISTANCE_WHEN_NEWLY_SPAWNED = MathHelper.square(3.5F);
    private int deflectCooldown = 5;

    private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
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
    public void tick() {
        super.tick();
        if (this.deflectCooldown > 0) {
            this.deflectCooldown--;
        }
    }

    @Override
    public boolean deflect(ProjectileDeflection deflection, @Nullable Entity deflector, @Nullable Entity owner, boolean fromAttack) {
        return this.deflectCooldown > 0 ? false : super.deflect(deflection, deflector, owner, fromAttack);
    }


    @Override
    protected void createExplosion(Vec3d pos) {
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
    public boolean shouldRender(double distance) {
        return this.age < 2 && distance < (double)MAX_RENDER_DISTANCE_WHEN_NEWLY_SPAWNED ? false : super.shouldRender(distance);
    }
}
