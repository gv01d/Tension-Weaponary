package me.gv0id.arbalests.entity.projectile;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.entity.damage.ModDamageTypes;
import me.gv0id.arbalests.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MusicDiscEntity extends PersistentProjectileEntity {
    private int dealtDamage = 3;
    private Entity lastAffected = null;
    public boolean interactedWithJukebox = false;

    private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(MusicDiscEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Float> ROTATION = DataTracker.registerData(MusicDiscEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private Direction facing = Direction.SOUTH;
    public float ROTATION_SPEED = 105.0F;

    public MusicDiscEntity(EntityType<? extends MusicDiscEntity> entityType, World world) {
        super(entityType, world);
    }

    public MusicDiscEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntityType.MUSIC_DISC, owner, world, stack, null);
        setStack(stack);
        this.dataTracker.set(ITEM_STACK,stack);
        setAsStackHolder(stack);
    }

    public MusicDiscEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntityType.MUSIC_DISC, x, y, z, world, stack, stack);
        setAsStackHolder(stack);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = 0;
        }
        else {
            float rotation = this.getDataTracker().get(ROTATION);
            this.getDataTracker().set(ROTATION, (rotation + (float)(ROTATION_SPEED * (this.getVelocity().length()/10F))) % 360);
        }

        super.tick();
    }

    @Nullable
    @Override
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage < 1 ? null : super.getEntityCollision(currentPosition, nextPosition);
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

        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity.equals(lastAffected))
            return;

        lastAffected = entity;
        float f = 1.0F;
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

        Float min = null;
        float dis;
        for (Entity entity1 : entities){
            if (!entity1.equals(entity) && !entity1.equals(this) && entity1 instanceof LivingEntity){
                dis = entity1.distanceTo(this);
                if (min == null) min = dis;
                if (min > dis) min = dis;
                hashMap.put(dis, entity1);
            }
        }


        Vec3d vec3d;
        if (min != null){
            Entity target = hashMap.get(min);
            Arbalests.LOGGER.info("Selected <{}> : {} ", min , target);
            Vec3d targetPos = new Vec3d(target.getPos().x, (target.getPos().y + target.getEyePos().y) / 2, target.getPos().z);
            double vel = this.getVelocity().length();
            vec3d = targetPos.add(this.getPos().negate()).normalize().multiply(vel * 1);
        }
        else {
            this.deflect(ProjectileDeflection.SIMPLE, entity, this.getOwner(), false);
            vec3d = this.getVelocity().multiply(0.1, 0.3, 0.1);
        }

        this.setVelocity(vec3d);
        this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0F, 1.0F);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
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
        if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED) {
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
