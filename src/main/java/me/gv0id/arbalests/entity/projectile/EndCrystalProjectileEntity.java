package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.AngularColoredParticleEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EndCrystalProjectileEntity extends ExplosiveProjectileEntity {
    public double BOUNCE_STRENGTH = 0.6D;
    public float DRAG = 0.98F;
    public float WATER_DRAG = 0.8F;
    public float DEFLECT_SPEED = 0.5F;
    public int endCrystalAge;
    public int EXPLOSION_FREEZE_TIME = 2;
    public int tickFreezed = -1;
    public boolean explode = false;
    private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EndCrystalProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final int MAX_FUSE_TIMER = 600;
    public int fuseTimer = MAX_FUSE_TIMER;



    public EndCrystalProjectileEntity(EntityType<? extends EndCrystalProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.endCrystalAge = this.random.nextInt(100000);
        accelerationPower = 0;
    }

    public EndCrystalProjectileEntity(World world, Entity owner){
        this(ModEntityType.END_CRYSTAL_PROJECTILE, world);
        this.setOwner(owner);
        Vec3d ownerEyePos = owner.getEyePos();
        this.setPosition(ownerEyePos.x,ownerEyePos.y - 1, ownerEyePos.z);
    }

    @Override
    protected float getDrag() { return DRAG; }

    @Override
    protected float getDragInWater() {
        return WATER_DRAG;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Nullable
    @Override
    protected ParticleEffect getParticleType() {
        return explode? ModParticles.LIGHT_FLASH : null;
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SHOW_BOTTOM, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("ShowBottom", this.shouldShowBottom());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("ShowBottom", NbtElement.BYTE_TYPE)) {
            this.setShowBottom(nbt.getBoolean("ShowBottom"));
        }
    }

    @Override
    public boolean isCollidable() {
        return tickFreezed <= 0 && super.isCollidable();
    }

    @Override
    public void tick() {
        if (this.tickFreezed > 0){
            tickFreezed--;

        }
        else {
            if (explode){
                createExplosion();
            }
            this.endCrystalAge++;
            if (this.fuseTimer > 0){
                this.fuseTimer--;
            }
            else {
                if (!this.isRemoved()) trigger(false);
            }
            super.tick();
        }
    }

    private void addParticle() {
        ParticleEffect particleEffect = ModParticles.LIGHT_FLASH;
        Vec3d vec3d = this.getPos();
        this.getWorld().addImportantParticle(particleEffect, vec3d.x, vec3d.y + 0.5, vec3d.z, 0.0, 0.0, 0.0);
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
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.getWorld().isClient) Arbalests.LOGGER.info("Server");
        else Arbalests.LOGGER.info("Client");

        Entity target = entityHitResult.getEntity();
        if (target instanceof ProjectileEntity){
            if (!this.isRemoved()) trigger(false);
        }
    }

    @Override
    public ProjectileDeflection getProjectileDeflection(ProjectileEntity projectile) {
        return  ProjectileDeflection.NONE;
    }

    @Override
    public boolean deflect(ProjectileDeflection deflection, @Nullable Entity deflector, @Nullable Entity owner, boolean fromAttack) {
        if (deflector instanceof ProjectileEntity) {
            if (!this.isRemoved()) trigger(false);
            return false;
        }

        deflection.deflect(this, deflector, this.random);
        if (!this.getWorld().isClient) {
            this.setOwner(owner);
            this.onDeflected(deflector, fromAttack);
        }
        return true;
    }

    @Override
    protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
        if (deflector instanceof LivingEntity){
            if (this.getVelocity().lengthSquared() < DEFLECT_SPEED * DEFLECT_SPEED){
                this.setVelocity(
                        deflector.getEyePos().subtract(this.getEyePos()).normalize().multiply(DEFLECT_SPEED)
                );
            }
        }
    }

    @Override
    public void onExplodedBy(@Nullable Entity entity) {
        if (!this.isRemoved() && !explode) {
            trigger(true);
        }
    }

    public void trigger(boolean subExplosion){
        Vec3d vec3d = this.getEyePos();
        this.setVelocity(Vec3d.ZERO);
        Arbalests.LOGGER.info("Particle where? : {}", vec3d);
        if (this.getWorld() instanceof ServerWorld serverWorld){
            serverWorld.spawnParticles(ModParticles.LIGHT_FLASH,vec3d.x,vec3d.y,vec3d.z,1,0,0,0,0);
            RegistryEntry<SoundEvent> registryEntry = RegistryEntry.of(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
            float volume = 10F;
            if (subExplosion){
                volume = 1F;
            }
            for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()){
                serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(registryEntry,SoundCategory.PLAYERS,vec3d.x,vec3d.y,vec3d.z,volume,1.5f, serverWorld.getRandom().nextLong()));
            }
        }
        //this.getWorld().addParticle(ModParticles.LIGHT_FLASH, vec3d.x, vec3d.y + 2, vec3d.z, 0.0, 0.0, 0.0);

        this.tickFreezed = EXPLOSION_FREEZE_TIME;
        this.setInvisible(true);
        this.explode = true;
    }

    public void createExplosion(){
        if (!this.isRemoved()) {
            this.remove(RemovalReason.KILLED);
            DamageSource damageSource = this.getOwner() != null ? this.getDamageSources().explosion(this, this.getOwner()) : null;
            this.getWorld().addParticle(AngularColoredParticleEffect.create(ModParticles.ANGULAR_BOOM,1F,1F,1F,0F,0F,0F,0F,0F,0F), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            this.getWorld().createExplosion(this, damageSource, null, this.getX(), this.getY(), this.getZ(), 6.0F, false, World.ExplosionSourceType.BLOCK);

        }
    }


    @Override
    public final boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.isOf(DamageTypes.EXPLOSION)){
            if (!this.isRemoved() && !explode) {
                trigger(true);
                return true;
            }
        }
        if (this.isAlwaysInvulnerableTo(source)) {
            return false;
        } else if (source.getAttacker() instanceof EnderDragonEntity) {
            return false;
        } else {
            return true;
        }
    }
}
