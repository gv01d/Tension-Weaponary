package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MusicDiscEntity extends PersistentProjectileEntity {
    private int dealtDamage = 3;
    private int returnSpeed = 3;
    private Entity lastAffected = null;
    public boolean interactedWithJukebox = false;
    public boolean BOOMERANG = false;
    public boolean wasInWater = false;
    public double BOUNCE_STRENGHT = 0.5;
    public float LIFE_CYCLE = 10;
    public float countDown = LIFE_CYCLE;
    public float MAX_DAMAGE = 6;
    public float pDmg = 2;

    public Box defaultBoundingBox;

    public float PARRY_TIME = 3;
    public float parryCountDown = PARRY_TIME;

    private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(MusicDiscEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Float> ROTATION = DataTracker.registerData(MusicDiscEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private Direction facing = Direction.SOUTH;
    public float ROTATION_SPEED = 105;
    private int returnTimer;

    public MusicDiscEntity(EntityType<? extends MusicDiscEntity> entityType, World world) {
        super(entityType, world);
        defaultBoundingBox = this.getBoundingBox();
    }

    public MusicDiscEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntityType.MUSIC_DISC, owner, world, stack, null);
        setStack(stack);
        this.dataTracker.set(ITEM_STACK,stack);
        setAsStackHolder(stack);
        defaultBoundingBox = this.getBoundingBox();
    }

    public MusicDiscEntity(World world, LivingEntity owner, ItemStack stack,int hitAmount, boolean boomerang, boolean gravity) {
        super(ModEntityType.MUSIC_DISC, owner, world, stack, null);
        setStack(stack);
        this.dataTracker.set(ITEM_STACK,stack);
        setAsStackHolder(stack);
        this.dealtDamage = hitAmount;
        this.BOOMERANG = boomerang;
        this.setNoGravity(!gravity);
        defaultBoundingBox = this.getBoundingBox();
    }

    @Override
    protected double getGravity() {
        return super.getGravity();
    }

    public MusicDiscEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntityType.MUSIC_DISC, x, y, z, world, stack, stack);
        setAsStackHolder(stack);
        defaultBoundingBox = this.getBoundingBox();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayerEntity) || !entity.isSpectator());
    }

    @Override
    public void tick() {
        if (this.isTouchingWater()){
            if (!wasInWater){
                boolean b = this.getWorld().getBlockState(this.getBlockPos()).isAir();
                boolean a = this.getWorld().getBlockState(BlockPos.ofFloored(this.getPos().add(0D,1D,0D))).isAir();
                double blockLevel = this.getPos().y - this.getBlockY();
                if (Math.abs(this.getPitch()) < 45 && (b || (!b && blockLevel > 0.1 && a))){
                    this.setVelocity(this.getVelocity().multiply(1,-BOUNCE_STRENGHT,1));
                }
            }

            wasInWater = true;
        }

        if (this.inGroundTime > 4) {
            this.dealtDamage = 0;
        }
        if (this.inGroundTime < 1){
            float rotation = this.getDataTracker().get(ROTATION);
            this.getDataTracker().set(ROTATION, (rotation + ROTATION_SPEED) % 360);

            //this.setVelocity(this.getVelocity().add(0,Math.min( (float)(ROTATION_SPEED * (this.getVelocity().lengthSquared()) / 1000), 0.04),0));
        }

        Entity owner = this.getOwner();
        if ( BOOMERANG && (dealtDamage < 1 || countDown < 1) && owner != null){
            if (!this.isOwnerAlive()) {
                if (this.getWorld() instanceof ServerWorld serverWorld && this.pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(serverWorld, this.asItemStack(), 0.1F);
                }

                this.discard();
            } else {
                if (!(owner instanceof PlayerEntity) && this.getPos().distanceTo(owner.getEyePos()) < (double)owner.getWidth() + 1.0) {
                    this.discard();
                    return;
                }
                if (parryCountDown < PARRY_TIME) {
                    parryCountDown++;
                }
                else {

                }

                this.setNoClip(true);
                Vec3d vec3d = owner.getEyePos().add(owner.getFacing().getDoubleVector().multiply(0.5)).subtract(this.getEyePos());
                this.setPos(this.getX(), this.getY() + vec3d.y * 0.015 * (double)returnSpeed, this.getZ());
                double d = 0.05 * (double)returnSpeed;
                if (this.distanceTo(owner) <= 2) this.setVelocity(this.getVelocity().multiply(0.05));
                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                this.returnTimer++;
            }
        }

        if (dealtDamage > 0 && countDown > 0){
            countDown--;
        }
        super.tick();
    }

    @Nullable
    @Override
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage < 1 ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    public Vec3d bounce( BlockHitResult blockHitResult,Vec3d entityVel, double bounceStrenght){

        Vec3d DdirVec = blockHitResult.getSide().getDoubleVector();
        Arbalests.LOGGER.info( "DdirVec : {}", DdirVec);
        Arbalests.LOGGER.info("Test X : {}", (Math.signum(entityVel.x)));

        return entityVel.multiply(
                (DdirVec.x == 0) || (Math.signum(entityVel.x) == DdirVec.x) ? 1 : -1,
                (DdirVec.y == 0) || (Math.signum(entityVel.y) == DdirVec.y) ? 1 : -1,
                (DdirVec.z == 0) || (Math.signum(entityVel.z) == DdirVec.z) ? 1 : -1
        ).multiply(bounceStrenght);
    }

    @Override
    protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
        super.onDeflected(deflector, fromAttack);
        dealtDamage = 3;
        countDown = LIFE_CYCLE * 2;
        lastAffected = null;

        if(deflector instanceof ProjectileEntity projectile) this.setOwner(projectile.getOwner());
        if(deflector instanceof LivingEntity livingEntity) this.setOwner(livingEntity);

        if (pDmg < MAX_DAMAGE){
            pDmg++;
        }
        this.setVelocity(this.getVelocity().multiply(1.3));
        this.setNoClip(false);
        this.setBoundingBox(defaultBoundingBox);
    }

    @Override
    public boolean canBeHitByProjectile() {
        return super.canBeHitByProjectile();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {


        if (!interactedWithJukebox){
            World world = this.getWorld();
            BlockPos blockPos = blockHitResult.getBlockPos();
            Block block = world.getBlockState(blockPos).getBlock();
            if (block instanceof JukeboxBlock jukeboxBlock && (world.getBlockEntity(blockPos) instanceof JukeboxBlockEntity jukeboxBlockEntity)){
                ItemStack itemStack = this.getStack();
                ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
                assert chargedProjectilesComponent != null;
                ArrayList<ItemStack> list = new ArrayList<>(chargedProjectilesComponent.getProjectiles());

                if (world.getBlockState(blockPos).get(JukeboxBlock.HAS_RECORD)){
                    if (list.isEmpty()){
                        ItemStack stack = jukeboxBlockEntity.getStack();
                        jukeboxBlockEntity.emptyStack();
                        world.setBlockState(blockPos, world.getBlockState(blockPos).with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);

                        itemStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(stack));
                        interactedWithJukebox = true;
                    }
                    else {
                        ItemStack stack = jukeboxBlockEntity.getStack();
                        jukeboxBlockEntity.setDisc(list.removeFirst());
                        jukeboxBlockEntity.reloadDisc();
                        world.setBlockState(blockPos, world.getBlockState(blockPos).with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);

                        list.add(stack);
                        itemStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
                        interactedWithJukebox = true;
                    }
                }
                else if (!list.isEmpty()){
                    jukeboxBlockEntity.setDisc(list.removeFirst());
                    jukeboxBlockEntity.reloadDisc();
                    world.setBlockState(blockPos, world.getBlockState(blockPos).with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);

                    itemStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
                    interactedWithJukebox = true;
                }
            }
        }

        Vec3d eyePos = this.getEyePos();
        Vec3d vel = this.getVelocity();
        super.onBlockHit(blockHitResult);

        if (vel.lengthSquared() > 0.5 && dealtDamage > 0){
            Arbalests.LOGGER.info("B4 : {}",vel);
            this.setPitch(getPitch() + 20F * (float)(Math.random() > 0.5 ? -1 : 1));
            this.setVelocity(bounce(blockHitResult,vel, BOUNCE_STRENGHT));
            this.setInGround(false);
            dealtDamage--;
            Arbalests.LOGGER.info("After : {}",this.getVelocity());
        }
        else {
            dealtDamage = 0;
        }
    }

    public class EntityDist implements Comparator<EntityDist>{

        EntityDist(Entity entity, double distance){
            this.entity = entity;
            this.distance = distance;
        }

        Entity entity;
        double distance;

        public EntityDist() {
        }

        @Override
        public int compare(EntityDist entityDist, EntityDist t1) {
            return (int) Math.signum((entityDist.distance - t1.distance));
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity.equals(lastAffected))
            return;

        countDown = LIFE_CYCLE;
        Entity lastTarget = lastAffected;
        lastAffected = entity;
        float f = pDmg;
        Entity entity2 = this.getOwner();
        DamageSource damageSource = this.getDamageSources().trident(entity, (entity2 == null ? this : entity2));
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            f = EnchantmentHelper.getDamage(serverWorld, Objects.requireNonNull(this.getWeaponStack()), entity, damageSource, f);
        }

        this.dealtDamage--;
        if (entity.sidedDamage(damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (this.getWorld() instanceof ServerWorld serverWorld) {
                EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource, this.getWeaponStack(), item -> this.kill(serverWorld));
            }

            if (entity instanceof LivingEntity livingEntity) {
                this.knockback(livingEntity, damageSource);
                this.onHit(livingEntity);
            }
        }


        ArrayList<Entity> entities = new ArrayList<>(getWorld().getOtherEntities(entity2, Box.of(this.getPos(),20,10,20)));
        HashMap<Float, Entity> hashMap = new HashMap<>();

        Arbalests.LOGGER.info("Entities near: {}", entities);
        if (dealtDamage > 0){
            Float min = null;
            float dis;
            ArrayList<EntityDist> entList = new ArrayList<EntityDist>();
            for (Entity entity1 : entities){
                dis = 0;
                if (!entity1.equals(entity) && !entity1.equals(this) && entity1 instanceof LivingEntity){
                    if (entity1.equals(lastTarget)) dis = 10;
                    dis += entity1.distanceTo(this);
                    if (min == null) min = dis;
                    if (min > dis) min = dis;
                    hashMap.put(dis, entity1);
                    entList.add(new EntityDist(entity1, dis));
                }
            }

            entList.sort(new EntityDist());


            Vec3d vec3d;

            Entity target = null;
            Vec3d targetPos = null;
            for (EntityDist entD : entList){
                targetPos = new Vec3d(entD.entity.getEyePos().x, (entD.entity.getPos().y + entD.entity.getEyePos().y) / 2, entD.entity.getEyePos().z);
                BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(this.getEyePos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,this));
                if (blockHitResult.getType() == HitResult.Type.ENTITY || blockHitResult.getType() == HitResult.Type.MISS){
                    target = entD.entity;
                    break;
                }
            }
            if (target != null){
                double vel = this.getVelocity().length();
                vec3d = targetPos.add(this.getPos().negate()).normalize().multiply(vel * 1);
            }
            else {
                this.deflect(ProjectileDeflection.SIMPLE, entity, this.getOwner(), false);
                vec3d = this.getVelocity().multiply(0.1, 0.3, 0.1);
                countDown = 0;
            }

            this.setVelocity(vec3d);
            this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0F, 1.0F);
        }
        else {
            countDown = 0;
        }

    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        if (parryCountDown <= 0 || !BOOMERANG){
            return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
        }
        Box temp = this.defaultBoundingBox.expand(2,2,2);
        this.setBoundingBox(temp);
        this.parryCountDown -= 2;
        return false;
    }

    @Override
    public ItemStack getWeaponStack() {
        return this.getItemStack();
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.COPPER_DISC);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    public void setHeldItemStack(ItemStack value) {
        if (!value.isEmpty()) {
            value = value.copyWithCount(1);
        }

        this.setAsStackHolder(value);
        this.getDataTracker().set(ITEM_STACK, value);
    }

    public void setRotation(float value) {
        this.getDataTracker().set(ROTATION,(180 + value) % 360);
    }

    public float getRotation(){
        return this.getDataTracker().get(ROTATION) - 180F;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        ItemStack itemStack;
        if (nbt.contains("Item", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("Item");
            itemStack = ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
        } else {
            itemStack = ItemStack.EMPTY;
        }

        this.setHeldItemStack(itemStack);
        if (!itemStack.isEmpty()) {
            this.setRotation(nbt.getFloat("ItemRotation"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.getStack().isEmpty()) {
            nbt.put("Item", this.getStack().toNbt(this.getRegistryManager()));
            nbt.putFloat("ItemRotation", (byte)this.getRotation());
        }
    }

    @Override
    public void age() {
        if (this.pickupType != PickupPermission.ALLOWED) {
            super.age();
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ITEM_STACK, getDefaultItemStack());
        builder.add(ROTATION, 0F);
    }

    private void setAsStackHolder(ItemStack stack) {
        if (!stack.isEmpty() && stack.getHolder() != this) {
            stack.setHolder(this);
        }
    }


    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (data.equals(ITEM_STACK)) {
            this.setAsStackHolder(this.getStack());
        }
    }

    public ItemStack getStack(){
        return this.getDataTracker().get(ITEM_STACK);
    }

}
