package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SonicBoomProjectile extends PersistentProjectileEntity {
    protected double ownerKnockback = 1;
    protected boolean ignoreOwnerArmor = false;

    protected double knockback = 4;
    protected boolean ignoreArmor = false;

    public SonicBoomProjectile(EntityType<? extends SonicBoomProjectile> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.age = 0;
    }

    public SonicBoomProjectile(World world, LivingEntity owner, double OwnerKnockback, boolean ignoreOwnerArmor) {
        super(ModEntityType.SONIC_BOOM_PROJECTILE, owner, world, ModItems.ECHO_CRYSTAL.getDefaultStack(), null);
        this.ownerKnockback = OwnerKnockback;
        this.ignoreOwnerArmor = ignoreOwnerArmor;
        this.setNoGravity(true);
        this.age = 0;
        Arbalests.LOGGER.info("SonicBoomProjectile created");
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    // TODO : Locked rotation sonic booms (better sonic boom vfx) and actual piercing effect

    @Override
    public void tick() {
        if(this.age > 1) {
            this.discard();
        }
        else {
            Vec3d vec3d = this.getPos();
            Vec3d vec3d2 = this.getVelocity().normalize();
            double multiplier = this.getVelocity().length() / 10;
            for (int i = 0; i < 10; i++) {
                Vec3d vec3d3 = vec3d.add(vec3d2.multiply(((double)i + 1) * multiplier));
                this.getWorld().addParticle(ParticleTypes.SONIC_BOOM, vec3d3.getX(), vec3d3.getY(), vec3d3.getZ(), 0.0D, 0.0D, 0.0D);
            }

            // Sound
            RegistryEntry<SoundEvent> registryEntry = RegistryEntry.of(SoundEvents.ENTITY_WARDEN_SONIC_BOOM);
            long l = this.getWorld().random.nextLong();
            this.getWorld().playSound(null, vec3d.x, vec3d.y, vec3d.z, registryEntry, SoundCategory.PLAYERS, 3.0F, 1.0F, l);
        }
        super.tick();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        Arbalests.LOGGER.info("SonicBoomProjectile collision");
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult)hitResult;
            Entity entity = entityHitResult.getEntity();
            if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity projectileEntity) {
                projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this.getOwner(), this.getOwner(), true);
            }
            this.onEntityHit((EntityHitResult)hitResult);
        } else if (type == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult)hitResult);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.getWorld() instanceof ServerWorld serverWorld){
            Entity entity = entityHitResult.getEntity();
            if (entity == this.getOwner()) {
                return;
            }
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.damage(serverWorld,serverWorld.getDamageSources().sonicBoom(getOwner()),10F);
                Vec3d vel = this.getVelocity().normalize();

                double strength = this.knockback;
                if(!this.ignoreArmor) {
                    strength *= livingEntity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);
                }
                vel = vel.multiply(strength);
                livingEntity.setVelocity(livingEntity.getVelocity().add(vel));
            }
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ModItems.ECHO_CRYSTAL.getDefaultStack();
    }
}
