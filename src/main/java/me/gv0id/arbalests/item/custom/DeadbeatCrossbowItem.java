package me.gv0id.arbalests.item.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.ChargeValueComponent;
import me.gv0id.arbalests.components.type.DeadbeatCrossbowCharging;
import me.gv0id.arbalests.effect.ModEffects;
import me.gv0id.arbalests.entity.damage.ModDamageSources;
import me.gv0id.arbalests.entity.damage.ModDamageTypes;
import me.gv0id.arbalests.entity.projectile.*;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class DeadbeatCrossbowItem extends RangedWeaponItem {
    public static final int ROUND = 1;
    public static final int AMMO = 3;
    private static final float DEFAULT_PULL_TIME = 3.0F;
    public static final int ENTITY_RANGE = 8;
    private boolean charged = false;
    private boolean loaded = false;
    private static final CrossbowItem.LoadingSounds DEFAULT_LOADING_SOUNDS = new CrossbowItem.LoadingSounds(
            Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_START),
            Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE),
            Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
    );

    public DeadbeatCrossbowItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getHeldProjectiles() {
        return DEADBEAT_CROSSBOW_HELD_PROJECTILES;
    }
    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    public static boolean isLoaded(ItemStack stack){ return stack.getOrDefault(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.DEFAULT).isLoaded(); }

    public static boolean isChamberFull(ItemStack stack, LivingEntity shooter){
        ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
        return chargedProjectilesComponent.getProjectiles().size() >= ((shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND) * AMMO);
    }

    public static boolean isFullyCharged(ItemStack stack){ return stack.getOrDefault(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.DEFAULT).isFullyCharged(); }
    public static boolean isCharged(ItemStack stack) { return stack.getOrDefault(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.DEFAULT).isCharged(); }
    public static boolean isCharging(ItemStack stack){
        DeadbeatCrossbowCharging deadbeatCrossbowCharging = stack.get(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE);
        assert deadbeatCrossbowCharging != null;
        return deadbeatCrossbowCharging.isCharging();
    }

    ///
    /// Funtion for charging - receives deadbeat crossbow ItemStack and item to be charged with
    ///
    public boolean charge(ItemStack crossbow,ItemStack stack, LivingEntity user){
        ChargeValueComponent chargeValueComponent = crossbow.get(ModDataComponentTypes.CHARGE_VALUE);

        if (chargeValueComponent.value() < AMMO){
            Projectiles projectile = getProjectileData(stack);
            if (chargeValueComponent.points() <= AMMO - projectile.slots){
                ArrayList<ItemStack> stackList = new ArrayList<>(Objects.requireNonNull(crossbow.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles());

                // TODO : suport for multicharge
                stackList.add(stack.split(1));

                crossbow.set(ModDataComponentTypes.CHARGE_VALUE,new ChargeValueComponent(
                        chargeValueComponent.value() + 1,
                        chargeValueComponent.points() + projectile.slots
                        )
                );

                crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(stackList));
                if (isChamberFull(crossbow, user))
                    crossbow.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.LOADED);
                else
                    crossbow.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.DEFAULT);
                return true;
            }
        }
        return false;
    }

    /*
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference){
        if ((clickType == ClickType.LEFT) || otherStack.isEmpty()) return false;

        if (DEADBEAT_CROSSBOW_HELD_PROJECTILES.test(otherStack)) {
            if (!isFullyCharged(stack)) {
                ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT);
                List<ItemStack> list = new ArrayList<>(List.copyOf(chargedProjectilesComponent.getProjectiles()));
                int slots = AMMO;
                for (ItemStack stack1 : list){
                    slots -= getProjectileData(stack1).slots;
                }

                if (getProjectileData(otherStack).slots > slots){
                    return false;
                }

                if (list.size() < AMMO) {
                    CrossbowItem.LoadingSounds loadingSounds = this.getLoadingSounds(stack);
                    list.add(otherStack.split(1));
                    float f = ((float) list.size()) / AMMO;
                    loadingSounds.end()
                            .ifPresent(sound -> player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),sound.value(), SoundCategory.PLAYERS, 1.0F, 0.5F + (f / 2)));

                    stack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.of(list));
                    if (isChamberFull(stack, player))
                        stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.LOADED);
                    else
                        stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.DEFAULT);
                    return true;
                }
            }
        }
        return false;
    }
     */

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference){
        if ((clickType == ClickType.LEFT) || otherStack.isEmpty()) return false;

        if (DEADBEAT_CROSSBOW_HELD_PROJECTILES.test(otherStack)) {
            if (charge(stack,otherStack, player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if(user.getItemCooldownManager().isCoolingDown(itemStack))
            return ActionResult.FAIL;

        ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty() && isCharged(itemStack)) {
            // TODO : NEED SUPPORT FOR MULTISHOOT ON GETPROJECTILESDATA
            ArbalestCooldown arbalestCooldown = itemStack.get(ModDataComponentTypes.ARBALEST_COOLDOWN);
            ArrayList<ItemStack> tempList = new ArrayList<>(List.copyOf(Objects.requireNonNull(itemStack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles()));

            Projectiles selected = getProjectileData(chargedProjectilesComponent.getProjectiles().getFirst());
            float projectileSpeed = selected.getSpeed();

            this.shootAll(world, user, hand, itemStack, projectileSpeed, 1.0F, null);

            // ------------------------------------------ //
            // Cooldown Manager
            ItemStack Projectile = tempList.getFirst();
            if (!tempList.isEmpty()){

                // THERE IS SOME DESYNC, SO THIS NEVER GETS CALLED ON SERVER GOING INTO THE ELSE
                if (chargedProjectilesComponent.getProjectiles().size() < 2) {
                    assert arbalestCooldown != null;
                    itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(arbalestCooldown.seconds()));
                    UseCooldownComponent useCooldownComponent = itemStack.get(DataComponentTypes.USE_COOLDOWN);
                    assert useCooldownComponent != null;
                    useCooldownComponent.set(itemStack,user);
                    itemStack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.DEFAULT);
                }
                else{
                    Projectiles selectedProjectile = getProjectileData(Projectile);
                    float cooldown = selectedProjectile.cooldown;
                    if (cooldown > 0){
                        itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(cooldown));
                        UseCooldownComponent useCooldownComponent = itemStack.get(DataComponentTypes.USE_COOLDOWN);
                        assert useCooldownComponent != null;
                        useCooldownComponent.set(itemStack,user);
                    }
                    itemStack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.CHARGED);
                }
            }
            else {
                // THIS IS WHAT ACTUALLY GETS CALLED IN THE SERVER
                assert arbalestCooldown != null;
                itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(arbalestCooldown.seconds()));
                UseCooldownComponent useCooldownComponent = itemStack.get(DataComponentTypes.USE_COOLDOWN);
                assert useCooldownComponent != null;
                useCooldownComponent.set(itemStack,user);
                itemStack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.DEFAULT);
            }
            // ------------------------------------------ //
            return ActionResult.SUCCESS;
        } else if (!user.getProjectileType(itemStack).isEmpty() || !Objects.requireNonNull(itemStack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().isEmpty()) {
            this.charged = false;
            this.loaded = false;
            // ------------------------------------------ //
            // Cooldown Manager
            itemStack.remove(DataComponentTypes.USE_COOLDOWN);
            // ------------------------------------------ //
            itemStack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.CHARGING);
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        } else {
            itemStack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.DEFAULT);
            return ActionResult.FAIL;
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i, stack, user);
        float s = (float) Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size();
        // Testing if it can charge the crossbow
        if (f >= (s/AMMO) && s >= 1)
        {
            // Creating cooldown with each ammo loaded
            float cooldown = 0.1F;
            ArrayList<ItemStack> list = new ArrayList<>(List.copyOf((Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles())));

            for (ItemStack st : list){

                Projectiles selected = getProjectileData(st);
                cooldown += selected.cooldown;
            }
            if (cooldown > 8.0F)
                cooldown = 8.0F;

            stack.set(ModDataComponentTypes.ARBALEST_COOLDOWN,new ArbalestCooldown(cooldown));
            // - - -
            // Setting charge type to CHARGED
            if (isChamberFull(stack,user))
                stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.FULLYCHARGED);
            else
                stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.CHARGED);
            return true;
        }
        // Setting charge type to DEFAULT ( could not charge crossbow )
        stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.DEFAULT);
        return false;
    }

    protected static List<ItemStack> repeaterLoad(ItemStack stack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.isEmpty()) {
            return List.of();
        } else {
            int i = shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND;
            int s = i + Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size();
            List<ItemStack> list = new ArrayList<>(s);

            int slots = AMMO;
            for (ItemStack stack1 : list){
                slots -= getProjectileData(stack1).slots;
            }

            if (getProjectileData(projectileStack).slots > slots){
                return list;
            }

            list.addAll(Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles());
            ItemStack itemStack = projectileStack.copy();

            for (int j = 0; j < i; j++) {
                ItemStack itemStack2 = getProjectile(stack, j == 0 ? projectileStack : itemStack, shooter, j > 0);
                if (!itemStack2.isEmpty()) {
                    list.add(itemStack2);
                }
            }
            return list;
        }
    }

    private static void loadProjectiles(LivingEntity shooter, ItemStack crossbow) {


        List<ItemStack> list = repeaterLoad(crossbow, shooter.getProjectileType(crossbow), shooter);
        if (!list.isEmpty()) {
            crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
        }
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
        Vector3f vector3f;
        if (target != null) {
            double d = target.getX() - shooter.getX();
            double e = target.getZ() - shooter.getZ();
            double f = Math.sqrt(d * d + e * e);
            double g = target.getBodyY(0.3333333333333333) - projectile.getY() + f * 0.2F;
            vector3f = calcVelocity(shooter, new Vec3d(d, g, e), yaw);
        } else {
            Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
            Quaternionf quaternionf = new Quaternionf().setAngleAxis((double)(yaw * (float) (Math.PI / 180.0)), vec3d.x, vec3d.y, vec3d.z);
            Vec3d vec3d2 = shooter.getRotationVec(1.0F);
            vector3f = vec3d2.toVector3f().rotate(quaternionf);
        }

        projectile.setVelocity((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), speed, divergence);
        float h = getSoundPitch(shooter.getRandom(), index);
        shooter.getWorld().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, shooter.getSoundCategory(), 1.0F, h);
    }

    private static Vector3f calcVelocity(LivingEntity shooter, Vec3d direction, float yaw) {
        Vector3f vector3f = direction.toVector3f().normalize();
        Vector3f vector3f2 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double)vector3f2.lengthSquared() <= 1.0E-7) {
            Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
            vector3f2 = new Vector3f(vector3f).cross(vec3d.toVector3f());
        }

        Vector3f vector3f3 = new Vector3f(vector3f).rotateAxis((float) (Math.PI / 2), vector3f2.x, vector3f2.y, vector3f2.z);
        return new Vector3f(vector3f).rotateAxis(yaw * (float) (Math.PI / 180.0), vector3f3.x, vector3f3.y, vector3f3.z);
    }


    // PROJECTILES

    /*
    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {

        Projectiles[] list = (Projectiles.values());

        for (Projectiles projectile : list){
            if (projectile.isPartOfCollection() && projectileStack.isIn(Projectiles.valueOf(projectile.enumName).getTagKey())){
                Arbalests.LOGGER.info(projectile.getName());
                return Projectiles.valueOf(projectile.enumName).projectileBuilder.create(world,shooter,weaponStack,projectileStack,critical);
            }
            if (!projectile.isPartOfCollection() && projectileStack.isOf(projectile.item)){
                return projectile.projectileBuilder.create(world,shooter,weaponStack,projectileStack,critical);
            }
        }
        return Projectiles.NONE.projectileBuilder.create(world,shooter,weaponStack,projectileStack,critical);
    }
     */

    protected static ProjectileEntity createArrow(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical){
        ArrowItem arrowItem2 = projectileStack.getItem() instanceof ArrowItem arrowItem ? arrowItem : (ArrowItem)Items.ARROW;
        PersistentProjectileEntity persistentProjectileEntity = arrowItem2.createArrow(world, projectileStack, shooter, weaponStack);
        if (critical) {
            persistentProjectileEntity.setCritical(true);
        }
        persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);

        return persistentProjectileEntity;
    }

    @Override
    protected int getWeaponStackDamage(ItemStack projectile) {
        return projectile.isOf(Items.FIREWORK_ROCKET) ? 3 : 1;
    }

    protected void shootRound(
            ServerWorld world,
            LivingEntity shooter,
            Hand hand,
            ItemStack stack,
            List<ItemStack> projectiles, // get the component stack of projectiles
            float speed,
            float divergence,
            boolean critical,
            @Nullable LivingEntity target
    ){
        float f = EnchantmentHelper.getProjectileSpread(world, stack, shooter, 0.0F);
        float g = projectiles.size() == 1 ? 0.0F : 2.0F * f / (float)(projectiles.size() - 1);
        float h = (float)((projectiles.size() - 1) % 2) * g / 2.0F;
        float i = 1.0F;
        int s = (shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND); // shoots amount based on enchantment

        float roll = 0F;
        float power = 3F;

        for (int j = 0; j < s; j++) {
            ItemStack itemStack = (ItemStack)projectiles.get(j);
            if (!itemStack.isEmpty()) {
                float k = h + i * (float)((j + 1) / 2) * g;
                i = -i;
                int l = j;

                ProjectileEntity projectileEntity;

                Projectiles proj = getProjectileData(itemStack);
                projectileEntity = proj.projectileBuilder.create(world,shooter,stack,itemStack,critical);
                ProjectileEntity.spawn(
                        projectileEntity,
                        world,
                        itemStack,
                        projectile -> this.shoot(shooter, projectile, l, speed, divergence, k, target)
                );

                //

                stack.damage(this.getWeaponStackDamage(itemStack), shooter, LivingEntity.getSlotForHand(hand));
                if (stack.isEmpty()) {
                    break;
                }
            }
        }
    }

    public void shootAll(World world,
                         LivingEntity shooter,
                         Hand hand,
                         ItemStack stack,
                         float speed,
                         float divergence,
                         @Nullable LivingEntity target)
    {
        ChargedProjectilesComponent temp = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (world instanceof ServerWorld serverWorld) {
            ChargeValueComponent chargeValueComponent = stack.get(ModDataComponentTypes.CHARGE_VALUE);

            assert temp != null;
            List<ItemStack> tList = temp.getProjectiles();
            int amt = EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, 1);

            List<ItemStack> l2 = new ArrayList<ItemStack>();
            ChargedProjectilesComponent chargedProjectilesComponent;
            if (amt < tList.size()) {
                for (int i = amt; i < tList.size(); i++) {
                    l2.add(tList.get(i));
                }
                temp = ChargedProjectilesComponent.of(l2);
                chargedProjectilesComponent = stack.set(DataComponentTypes.CHARGED_PROJECTILES, temp);
                assert chargeValueComponent != null;
                stack.set(ModDataComponentTypes.CHARGE_VALUE, new ChargeValueComponent(
                        Math.max(chargeValueComponent.value() - 1, 0),
                        Math.max(chargeValueComponent.points() - getProjectileData(tList.getFirst()).slots, 0)
                ));
            }
            else {
                chargedProjectilesComponent = stack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT);
                stack.set(ModDataComponentTypes.CHARGE_VALUE, ChargeValueComponent.DEFAULT);
            }



            if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                //shoot projectiles
                this.shootRound(
                        serverWorld,
                        shooter,
                        hand,
                        stack,
                        chargedProjectilesComponent.getProjectiles(), // Component stack of projectiles
                        speed,
                        divergence,
                        shooter instanceof PlayerEntity,
                        target);
                if (shooter instanceof ServerPlayerEntity serverPlayerEntity) {
                    // Triggers Criteria and Stat Increment
                    Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
                    serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                }
            }
        }
        else {
            assert temp != null;
            ItemStack projStack = temp.getProjectiles().getFirst();

            if (!projStack.isEmpty()) {
                Projectiles projectiles = getProjectileData(projStack);
                if (projectiles.hasClientShooting())
                    projectiles.clientShootingInterface.create(world,shooter,stack,projStack,target, speed, divergence);
            }
        }
    }

    public static void clientShootSonicBoom(LivingEntity shooter){
        Vec3d vec3d = shooter.getRotationVec(1.0F).normalize();
        Vec3d vel = shooter.getVelocity();
        vel = new Vec3d(vel.x,vel.y < 0 && vec3d.y < 0? 0: vel.y , vel.z);

        shooter.setVelocity((-vec3d.x * 1D) + vel.x, (-vec3d.y * 1D) + vel.y, (-vec3d.z * 1) + vel.z);
        //shooter.addStatusEffect(new StatusEffectInstance(ModEffects.STRAFE,5,0,true,true,true));
    }

    private static float getSoundPitch(Random random, int index) {
        return index == 0 ? 1.0F : getSoundPitch((index & 1) == 1, random);
    }

    private static float getSoundPitch(boolean flag, Random random) {
        float f = flag ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    public boolean testLoad(LivingEntity user,ItemStack stack){
        return user.getProjectileType(stack) != null;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {

        if (!world.isClient) {

            if (user instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(stack)){
                return;
            }

            CrossbowItem.LoadingSounds loadingSounds = this.getLoadingSounds(stack);
            float f = (float)(stack.getMaxUseTime(user) - remainingUseTicks) / (float)getPullTime(stack, user);
            float s = (float) Objects.requireNonNull(stack.get(ModDataComponentTypes.CHARGE_VALUE)).value();

            if ( s < AMMO && f >= (( s + 1F)/AMMO)){
                int t = Objects.requireNonNull(stack.get(ModDataComponentTypes.CHARGE_VALUE)).points();

                if (charge(stack, user.getProjectileType(stack), user)){
                    stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.CHARGING);
                    loadingSounds.end()
                            .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundCategory.PLAYERS, 1.0F, 0.5F + (f/2)));
                }
                //loadProjectiles(user,stack);
            }

            if (f < 0.1F) {
                this.charged = false;
                this.loaded = false;
            }

            if (f >= 0.1F && !this.charged) {
                this.charged = true;
                loadingSounds.start()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundCategory.PLAYERS, 0.5F, 1.0F));
            }

            if (f >= 0.5F && !this.loaded) {
                this.loaded = true;
                loadingSounds.mid()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundCategory.PLAYERS, 0.5F, 1.0F));
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return getPullTime(stack, user) + 3;
    }

    public static int getPullTime(ItemStack stack, LivingEntity user) {
        float f = EnchantmentHelper.getCrossbowChargeTime(stack, user, DEFAULT_PULL_TIME);
        return MathHelper.floor(f * 20.0F);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    CrossbowItem.LoadingSounds getLoadingSounds(ItemStack stack) {
        return (CrossbowItem.LoadingSounds)EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.CROSSBOW_CHARGING_SOUNDS)
                .orElse(DEFAULT_LOADING_SOUNDS);
    }

    private static float getPullProgress(int useTicks, ItemStack stack, LivingEntity user) {
        float f = (float)useTicks / (float)getPullTime(stack, user);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (!isFullyCharged(stack) && !isLoaded(stack)){

            assert chargedProjectilesComponent != null;
            List<ItemStack> list = new ArrayList<>(List.copyOf(chargedProjectilesComponent.getProjectiles()));
            int slots = AMMO;
            for (ItemStack stack1 : list){
                slots -= getProjectileData(stack1).slots;
            }
            if (slots > 0){
                tooltip.add(Text.translatable("item.arbalests.deadbeat_crossbow.right_click_request").formatted(Formatting.GRAY));

                if (!chargedProjectilesComponent.getProjectiles().isEmpty())
                    tooltip.add(ScreenTexts.SPACE);
            }

        }

        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            List<ItemStack> itemList = chargedProjectilesComponent.getProjectiles();
            tooltip.add(Text.translatable("item.arbalests.deadbeat_crossbow.projectile").formatted(Formatting.DARK_PURPLE));

            for (ItemStack itemStack : itemList){
                tooltip.add((itemStack.toHoverableText().copy().formatted(Formatting.BLUE)));
                if (type.isAdvanced() && itemStack.isOf(Items.FIREWORK_ROCKET)) {
                    List<Text> list = Lists.<Text>newArrayList();
                    Items.FIREWORK_ROCKET.appendTooltip(itemStack, context, list, type);
                    if (!list.isEmpty()) {
                        list.replaceAll(text -> Text.literal("  ").append((Text) text).formatted(Formatting.GRAY));
                        tooltip.addAll(list);
                    }
                }
            }
        }
    }

    public static MusicDiscEntity spawnDisc(World world, LivingEntity shooter, ItemStack stack, ItemStack projectileStack, boolean critical){

        return new MusicDiscEntity(world,shooter,projectileStack,4,false,true);
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public int getRange() {
        return ENTITY_RANGE;
    }

    public static ProjectileEntity spawnSonicBoom(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical){

        shooter.addStatusEffect(new StatusEffectInstance(ModEffects.STRAFE,5,0,true,true,true));

        return new SonicBoomProjectile(world, shooter, 5F, true);
    }

    public static Projectiles getProjectileData(ItemStack stack){
        if (stack.isIn(ModItemTypeTags.DEADBEAT_PROJECTILE)){
            for (Projectiles projectile : Projectiles.values()){
                if (stack.isOf(projectile.getItem())){
                    return projectile;
                }
            }
        }
        return Projectiles.NONE;
    }


    public enum Projectiles implements StringIdentifiable {
        NONE((Item) null,1.0F,3.0F, DeadbeatCrossbowItem::createArrow),
        ARROW(Items.ARROW, 0.9F, 3.0F, DeadbeatCrossbowItem::createArrow),
        TIPPED_ARROW(Items.TIPPED_ARROW, 1.0F, 2.5F, DeadbeatCrossbowItem::createArrow, "POTION", "head", "base"),
        SPECTRAL_ARROW(Items.SPECTRAL_ARROW,1.0F, 3.2F, DeadbeatCrossbowItem::createArrow),

        ROCKET(Items.FIREWORK_ROCKET,0.8F,1.6F,
                (world,shooter,weaponStack, projectileStack, critical) -> new FireworkRocketEntity(
                        world, projectileStack, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true
                )
        ),

        WIND_CHARGE(Items.WIND_CHARGE, 0.2F, 2.5F,
                (world,shooter,weaponStack, projectileStack, critical) -> new WindGaleEntity(
                        shooter,world,shooter.getX() ,shooter.getEyeY() - 0.15F, shooter.getZ(),shooter.getVelocity(),1.8F,3F
                )
        ),

        SNOWBALL(Items.SNOWBALL, 0.0F, 5.0F,
                (world,shooter,weaponStack, projectileStack, critical) -> new SnowProjectileEntity(
                        world, shooter, projectileStack
                )
        ),

        EGG(Items.EGG,0.0F, 6.0F,
                (world,shooter,weaponStack, projectileStack, critical) -> new CustomEggProjectileEntity(
                        world,shooter,projectileStack
                )
        ),

        ENDER_PEARL(Items.ENDER_PEARL, 2.0F, 5.0F,
                (world,shooter,weaponStack, projectileStack, critical) -> new CustomEnderPearlEntity(
                        world, shooter, projectileStack
                )
        ),
        FIREBALL(Items.FIRE_CHARGE, 1.0F, 3.2F,
                (world,shooter,weaponStack, projectileStack, critical) -> new CustomFireBallEntity(
                        shooter,world,shooter.getX() ,shooter.getEyeY() - 0.15F, shooter.getZ(),shooter.getVelocity(),2.8F,2F
                )
        ),
        COPPER_DISC(ModItems.COPPER_DISC, 0.5F, 4.0F,
                DeadbeatCrossbowItem::spawnDisc,
                "MUSIC"),
        CRYSTAL(Items.END_CRYSTAL, 4F,1.0F, 3,
                (world,shooter,weaponStack, projectileStack, critical) ->
                new EndCrystalProjectileEntity(world, shooter)),
        ECHO_CRYSTAL(ModItems.ECHO_CRYSTAL, 5F, 10.0F, 3,
                DeadbeatCrossbowItem::spawnSonicBoom,
                ((world, shooter, weapon, projectile, target, speed, divergence) -> DeadbeatCrossbowItem.clientShootSonicBoom(shooter) )),

        NETHER_STAR(Items.NETHER_STAR, 2F,1F,DeadbeatCrossbowItem::createArrow);


        public static final EnumCodec<Projectiles> CODEC = StringIdentifiable.createCodec(Projectiles::values);
        Item item = null;
        TagKey<Item> tagKey = null;
        String enumName;

        float cooldown = 0;
        float speed = 0;
        float damage = 0;
        int slots = 1;

        ProjectileInterface projectileBuilder;
        ClientShootingInterface clientShootingInterface;

        boolean tinted = false;
        boolean clientShooting = false;
        boolean variation = false;

        String[] layers = null;

        Projectiles(Item item, float cooldown, float speed, ProjectileInterface projectileBuilder){
            this.item = item;
            this.cooldown = cooldown;
            this.speed = speed;
            this.projectileBuilder = projectileBuilder;
        }

        Projectiles(Item item, float cooldown, float speed, ProjectileInterface projectileBuilder, ClientShootingInterface clientShootingInterface ){
            this.item = item;
            this.cooldown = cooldown;
            this.speed = speed;
            this.projectileBuilder = projectileBuilder;
            this.clientShootingInterface = clientShootingInterface;
            this.clientShooting = true;
        }

        Projectiles(Item item, float cooldown, float speed, int slots, ProjectileInterface projectileBuilder){
            this.item = item;
            this.cooldown = cooldown;
            this.speed = speed;
            this.projectileBuilder = projectileBuilder;
            this.slots = slots;
        }

        Projectiles(Item item, float cooldown, float speed, int slots, ProjectileInterface projectileBuilder, ClientShootingInterface clientShootingInterface){
            this.item = item;
            this.cooldown = cooldown;
            this.speed = speed;
            this.projectileBuilder = projectileBuilder;
            this.slots = slots;
            this.clientShootingInterface = clientShootingInterface;
            this.clientShooting = true;
        }

        Projectiles(Item item, float cooldown, float speed, ProjectileInterface projectileBuilder, String tintSourceEnumName, String... layerSuffix){
            this.item = item;
            this.cooldown = cooldown;
            this.speed = speed;
            this.projectileBuilder = projectileBuilder;
            this.enumName = tintSourceEnumName;
            this.tinted = true;
            this.layers = layerSuffix;
        }

        Projectiles(Item item, float cooldown, float speed, ProjectileInterface projectileBuilder, String variationEnumName){
            this.item = item;
            this.cooldown = cooldown;
            this.speed = speed;
            this.projectileBuilder = projectileBuilder;
            this.enumName = variationEnumName;
            this.variation = true;
        }

        public float getSpeed() {
            return speed;
        }
        public float getCooldown() {
            return cooldown;
        }

        public boolean hasClientShooting(){
            return clientShooting;
        }

        public String getName() {
            if (this.item == null)
                return "none";
            return (item.toString().split(":"))[1];
        }

        public Item getItem() {
            return item;
        }
        public TagKey<Item> getTagKey() {
            return tagKey;
        }

        public boolean isTinted(){
            return tinted;
        }
        public boolean isVariation(){
            return variation;
        }

        public String getEnumName(){
            return enumName;
        }

        public String[] getLayers(){
            return layers;
        }

        @Override
        public String asString() {
            if (this.item == null) {
                return this.tagKey != null ? this.tagKey.toString() : "none";
            }
            return (item.toString().split(":"))[1];
        }
    }

    @FunctionalInterface
    interface ProjectileInterface {
        ProjectileEntity create(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical);
    }

    @FunctionalInterface
    interface ClientShootingInterface {
        void create(World world, LivingEntity shooter, ItemStack weapon, ItemStack projectile, Entity target, float speed, float divergence);
    }


    public static final Predicate<ItemStack> DEADBEAT_CROSSBOW_HELD_PROJECTILES = stack -> stack.isIn(ModItemTypeTags.DEADBEAT_PROJECTILE);

    public static record LoadingSounds(Optional<RegistryEntry<SoundEvent>> start, Optional<RegistryEntry<SoundEvent>> mid, Optional<RegistryEntry<SoundEvent>> end) {
        public static final Codec<CrossbowItem.LoadingSounds> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                SoundEvent.ENTRY_CODEC.optionalFieldOf("start").forGetter(CrossbowItem.LoadingSounds::start),
                                SoundEvent.ENTRY_CODEC.optionalFieldOf("mid").forGetter(CrossbowItem.LoadingSounds::mid),
                                SoundEvent.ENTRY_CODEC.optionalFieldOf("end").forGetter(CrossbowItem.LoadingSounds::end)
                        )
                        .apply(instance, CrossbowItem.LoadingSounds::new)
        );
    }
}
