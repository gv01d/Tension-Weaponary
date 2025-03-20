package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.particle.AngularColoredParticleEffect;
import me.gv0id.arbalests.particle.ColoredParticleEffect;
import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.RecisableTrailParticleEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleType;
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
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SonicBoomProjectile extends PersistentProjectileEntity {
    protected double ownerKnockback = 1;
    protected boolean ignoreOwnerArmor = false;

    protected double knockback = 4;
    protected boolean ignoreArmor = false;

    public float speed = 5F;
    public Vec3d direction = new Vec3d(0,0,0);

    public SonicBoomProjectile(EntityType<? extends SonicBoomProjectile> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.age = 0;
    }

    public SonicBoomProjectile(World world, LivingEntity owner, double OwnerKnockback, boolean ignoreOwnerArmor, float speed, Vec3d direction) {
        super(ModEntityType.SONIC_BOOM_PROJECTILE, owner, world, ModItems.ECHO_CRYSTAL.getDefaultStack(), null);
        this.ownerKnockback = OwnerKnockback;
        this.ignoreOwnerArmor = ignoreOwnerArmor;
        this.setNoGravity(true);
        this.age = 0;
        this.setYaw(owner.getYaw());
        this.setPitch(owner.getPitch());
        this.speed = speed;
        this.direction = direction;
        Vec3d p = this.getPos();
        this.setPos(p.x, p.y - (owner.getEyeHeight(owner.getPose())/2) - 0.5, p.z);

        Vec3d spd = this.direction.normalize().multiply(this.speed);
        this.setVelocity(spd);

        if (this.getWorld() instanceof ServerWorld serverWorld){
            Vec3d vec3d = this.getEyePos();

            Vec3d vec3d2 = this.getEyePos().add(this.getVelocity().multiply(2));
            Vec3d dir = this.getVelocity().normalize();
            vec3d = vec3d.add(dir.multiply(0.5F));
            double multiplier = (this.getVelocity().length() * 2) / 15;



            serverWorld.spawnParticles(ColoredParticleEffect
                            .create(ModParticles.COLORED_BOOM, ColorHelper.fromFloats(1F, 0.2F, 1F, 1F)),
                    vec3d.x, vec3d.y, vec3d.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < 15; i++) {
                Vec3d vec3d3 = vec3d.add(dir.multiply(((double)i + 1) * multiplier));
                serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM,
                        vec3d3.getX(), vec3d3.getY(), vec3d3.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            serverWorld.spawnParticles(RecisableTrailParticleEffect
                            .create(ModParticles.SONIC_BEAM,
                                    ColorHelper.getWhite(1F),
                                    16, 1F,
                                    vec3d, vec3d.subtract(dir), 0),
                    vec3d2.getX(), vec3d2.getY(), vec3d2.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);

            // Sound
            RegistryEntry<SoundEvent> registryEntry = RegistryEntry.of(SoundEvents.ENTITY_WARDEN_SONIC_BOOM);
            long l = this.getWorld().random.nextLong();
            this.getWorld().playSound(null, vec3d.x, vec3d.y, vec3d.z, registryEntry, SoundCategory.PLAYERS, 3.0F, 1.0F, l);
        }


        Arbalests.LOGGER.info("SonicBoomProjectile created");
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    // TODO : Locked rotation sonic booms (better sonic boom vfx) and actual piercing effect

    @Override
    public void tick() {

        Vec3d speed = this.direction.normalize().multiply(this.speed);
        this.setVelocity(speed);

        if(this.age > 1) {
            this.discard();
        }
        else {

        }
        super.tick();
    }

    @Override
    protected boolean shouldTickBlockCollision() {
        return false;
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
                livingEntity.damage(serverWorld,serverWorld.getDamageSources().sonicBoom(getOwner()),25F);
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
