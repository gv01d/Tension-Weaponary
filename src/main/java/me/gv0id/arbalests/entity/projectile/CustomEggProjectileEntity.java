package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.RecisableTrailParticleEffect;
import me.gv0id.arbalests.particle.TrailParticleEffect;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CustomEggProjectileEntity extends ThrownItemEntity {
    private Vec3d previousEyePos;
    private Vec3d previousPreviousEyePos;
    private int trailIndex = 0;
    private static final EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);

    public CustomEggProjectileEntity(EntityType<? extends CustomEggProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public CustomEggProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntityType.CUSTOM_EGG, owner, world, stack);
    }

    public CustomEggProjectileEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntityType.CUSTOM_EGG, x, y, z, world, stack);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.previousEyePos == null){
            previousEyePos = this.getPos().subtract(this.getVelocity().normalize());
        }
        if (this.previousPreviousEyePos == null){
            previousPreviousEyePos = this.previousEyePos.subtract(this.getVelocity().normalize());
        }

        if (this.age > 1){
            this.getWorld().addParticle(
                    RecisableTrailParticleEffect.create(
                            ModParticles.EXPERIMENTAL_TRAIL, ColorHelper.fromFloats(1F,0.874F,0.807F,0.607F),
                            10,1F,
                            this.previousEyePos, this.previousPreviousEyePos,
                            trailIndex++
                    ), this.getPos().x, this.getPos().y,this.getPos().z,0,0,0
            );
        }

        this.previousPreviousEyePos = this.previousEyePos;
        this.previousEyePos = this.getPos();
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            double d = 0.08;

            for (int i = 0; i < 8; i++) {
                this.getWorld()
                        .addParticle(
                                new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()),
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                ((double)this.random.nextFloat() - 0.5) * 0.08,
                                ((double)this.random.nextFloat() - 0.5) * 0.08,
                                ((double)this.random.nextFloat() - 0.5) * 0.08
                        );
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        entityHitResult.getEntity().addVelocity(this.getVelocity().multiply(0.5F));
        entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.getOwner()), 1.0F);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }

                for (int j = 0; j < i; j++) {
                    ChickenEntity chickenEntity = EntityType.CHICKEN.create(this.getWorld(), SpawnReason.TRIGGERED);
                    if (chickenEntity != null) {
                        chickenEntity.setBreedingAge(-24000);
                        chickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                        if (!chickenEntity.recalculateDimensions(EMPTY_DIMENSIONS)) {
                            break;
                        }

                        this.getWorld().spawnEntity(chickenEntity);
                    }
                }
            }

            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EGG;
    }
}
