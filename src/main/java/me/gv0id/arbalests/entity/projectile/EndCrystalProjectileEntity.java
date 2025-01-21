package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.entity.ModEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EndCrystalProjectileEntity extends ProjectileEntity {
    public double BOUNCE_STRENGTH = 0.8D;
    public int endCrystalAge;
    private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EndCrystalProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public EndCrystalProjectileEntity(EntityType<? extends EndCrystalProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.endCrystalAge = this.random.nextInt(100000);
    }

    public EndCrystalProjectileEntity(World world, Entity Owner){
        this(ModEntityType.END_CRYSTAL_PROJECTILE, world);
        this.setOwner(Owner);
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SHOW_BOTTOM, false);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("ShowBottom", this.shouldShowBottom());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("ShowBottom", NbtElement.BYTE_TYPE)) {
            this.setShowBottom(nbt.getBoolean("ShowBottom"));
        }
    }

    @Override
    public void tick() {
        this.endCrystalAge++;
        super.tick();
    }

    public boolean shouldShowBottom() {
        return this.getDataTracker().get(SHOW_BOTTOM);
    }

    public void setShowBottom(boolean showBottom) {
        this.getDataTracker().set(SHOW_BOTTOM, showBottom);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        Vec3d dirVec = blockHitResult.getSide().getDoubleVector();
        Vec3d entityVel = this.getVelocity();
        this.setVelocity(
                entityVel.multiply(
                        (dirVec.x == 0) || (Math.signum(entityVel.x) == dirVec.x) ? 1 : -1,
                        (dirVec.y == 0) || (Math.signum(entityVel.y) == dirVec.y) ? 1 : -1,
                        (dirVec.z == 0) || (Math.signum(entityVel.z) == dirVec.z) ? 1 : -1
                ).multiply(BOUNCE_STRENGTH)
        );
    }

    @Override
    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.isAlwaysInvulnerableTo(source)) {
            return false;
        } else if (source.getAttacker() instanceof EnderDragonEntity) {
            return false;
        } else {
            if (!this.isRemoved()) {
                this.remove(Entity.RemovalReason.KILLED);
                if (!source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                    DamageSource damageSource = source.getAttacker() != null ? this.getDamageSources().explosion(this, source.getAttacker()) : null;
                    world.createExplosion(this, damageSource, null, this.getX(), this.getY(), this.getZ(), 6.0F, false, World.ExplosionSourceType.BLOCK);
                }
            }

            return true;
        }
    }
}
