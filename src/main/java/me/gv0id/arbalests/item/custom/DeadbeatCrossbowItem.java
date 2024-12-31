package me.gv0id.arbalests.item.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.DeadbeatCrossbowCharging;
import me.gv0id.arbalests.entity.projectile.EggProjectileEntity;
import me.gv0id.arbalests.entity.projectile.SnowProjectileEntity;
import me.gv0id.arbalests.entity.projectile.WindGaleEntity;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class DeadbeatCrossbowItem extends RangedWeaponItem {
    public static final int ROUND = 1;
    public static final int AMMO = 3;



    protected static List<ItemStack> repeaterLoad(ItemStack stack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.isEmpty()) {
            return List.of();
        } else {
            int i = shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND;
            int s = i + Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size();
            List<ItemStack> list = new ArrayList<>(s);
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

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference){
        if ((clickType == ClickType.LEFT) || otherStack.isEmpty()) return false;

        if (DEADBEAT_CROSSBOW_HELD_PROJECTILES.test(otherStack)) {
            if (!isFullyCharged(stack)) {
                ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT);
                List<ItemStack> list = new ArrayList<>(List.copyOf(chargedProjectilesComponent.getProjectiles()));
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

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient()){
            Arbalests.LOGGER.info("run by server");
        }

        if(user.getItemCooldownManager().isCoolingDown(itemStack))
            return ActionResult.FAIL;

        ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty() && isCharged(itemStack)) {
            // TODO : NEED SUPPORT FOR MULTISHOOT ON GETPROJECTILESDATA
            this.shootAll(world, user, hand, itemStack, getProjectileData(chargedProjectilesComponent.getProjectiles().getFirst()).speed, 1.0F, null);

            // ------------------------------------------ //
            // Cooldown Manager

            ArbalestCooldown arbalestCooldown = itemStack.get(ModDataComponentTypes.ARBALEST_COOLDOWN);
            ArrayList<ItemStack> tempList = new ArrayList<>(Objects.requireNonNull(itemStack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles());
            if (!tempList.isEmpty()){
                ItemStack temp = tempList.getFirst();

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
                    float cooldown = getProjectileData(temp).cooldown;
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
                cooldown += getProjectileData(st).cooldown;
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

    protected static List<ItemStack> loadRound(ItemStack stack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.isEmpty()) {
            return List.of();
        } else {
            int i = shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND;
            List<ItemStack> list = new ArrayList(i);
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

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        List<ItemStack> list = repeaterLoad(crossbow, shooter.getProjectileType(crossbow), shooter);
        if (!list.isEmpty()) {
            crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
            return true;
        } else {
            return false;
        }
    }


    public static boolean isLoaded(ItemStack stack){
        DeadbeatCrossbowCharging deadbeatCrossbowCharging = stack.getOrDefault(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.DEFAULT);
        return deadbeatCrossbowCharging.isLoaded();
    }

    public static boolean isChamberFull(ItemStack stack, LivingEntity shooter){

        ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
        return chargedProjectilesComponent.getProjectiles().size() >= ((shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND) * AMMO);
    }

    public static boolean isFullyCharged(ItemStack stack){
        DeadbeatCrossbowCharging deadbeatCrossbowCharging = stack.getOrDefault(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.DEFAULT);
        return deadbeatCrossbowCharging.isFullyCharged();
    }

    public static boolean isCharged(ItemStack stack) {
        DeadbeatCrossbowCharging deadbeatCrossbowCharging = stack.getOrDefault(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.DEFAULT);
        return deadbeatCrossbowCharging.isCharged();
    }

    public static boolean isCharging(ItemStack stack){
        DeadbeatCrossbowCharging deadbeatCrossbowCharging = stack.get(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE);
        assert deadbeatCrossbowCharging != null;
        return deadbeatCrossbowCharging.isCharging();
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

    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        if (projectileStack.isOf(Items.FIREWORK_ROCKET)) {
            return new FireworkRocketEntity(world, projectileStack, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true);
        } else if(projectileStack.isOf(Items.WIND_CHARGE)) {
            return new WindGaleEntity(shooter,world,shooter.getX() ,shooter.getEyeY() - 0.15F, shooter.getZ(),shooter.getVelocity(),1.8F,3F);
        } else if (projectileStack.isOf(Items.SNOWBALL)) {
            return new SnowProjectileEntity(world, shooter, projectileStack);
        }else if(projectileStack.isOf(Items.EGG)){
            return new EggProjectileEntity(world,shooter,projectileStack);
        }else if(projectileStack.isOf(Items.ENDER_PEARL)){
            return new EnderPearlEntity(world,shooter,projectileStack);
        } else {
            ProjectileEntity projectileEntity = super.createArrowEntity(world, shooter, weaponStack, projectileStack, critical);
            if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
                persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
            }

            return projectileEntity;
        }
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

                ProjectileEntity.spawn(
                        this.createArrowEntity(world, shooter, stack, itemStack, critical),
                        world,
                        itemStack,
                        projectile -> this.shoot(shooter, projectile, l, speed, divergence, k, target)
                );

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
        if (world instanceof ServerWorld serverWorld) {
            ChargedProjectilesComponent temp = stack.get(DataComponentTypes.CHARGED_PROJECTILES);

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
            }
            else {
                chargedProjectilesComponent = stack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT);
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
    }

    private static float getSoundPitch(Random random, int index) {
        return index == 0 ? 1.0F : getSoundPitch((index & 1) == 1, random);
    }

    private static float getSoundPitch(boolean flag, Random random) {
        float f = flag ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {

        if (!world.isClient) {

            if (user instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(stack)){
                return;
            }

            CrossbowItem.LoadingSounds loadingSounds = this.getLoadingSounds(stack);
            float f = (float)(stack.getMaxUseTime(user) - remainingUseTicks) / (float)getPullTime(stack, user);
            float s = (float) Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size();

            if ( s < AMMO && f >= (( s + 1F)/AMMO)){
                if (user.getProjectileType(stack).isEmpty()){
                    return;
                }
                stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.CHARGING);
                loadProjectiles(user,stack);
                loadingSounds.end()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundCategory.PLAYERS, 1.0F, 0.5F + (f/2)));
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
            tooltip.add(Text.translatable("item.arbalests.deadbeat_crossbow.right_click_request").formatted(Formatting.GRAY));

            assert chargedProjectilesComponent != null;
            if (!chargedProjectilesComponent.getProjectiles().isEmpty())
            tooltip.add(ScreenTexts.SPACE);
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

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public int getRange() {
        return ENTITY_RANGE;
    }


    public static Projectiles getProjectileData(ItemStack stack){
        if (stack.isIn(ModItemTypeTags.DEADBEAT_PROJECTILE)){
            // Normal Projectiles
            if (stack.isOf(Items.ARROW))
                return Projectiles.ARROW;
            if (stack.isOf(Items.TIPPED_ARROW))
                return Projectiles.TIPPED_ARROW;
            if (stack.isOf(Items.SPECTRAL_ARROW))
                return Projectiles.SPECTRAL_ARROW;
            if (stack.isOf(Items.FIREWORK_ROCKET))
                return Projectiles.ROCKET;
            if (stack.isOf(Items.WIND_CHARGE))
                return Projectiles.WIND_CHARGE;
            if (stack.isOf(Items.SNOWBALL))
                return Projectiles.SNOWBALL;
            if (stack.isOf(Items.EGG))
                return Projectiles.EGG;
            if (stack.isOf(Items.ENDER_PEARL))
                return Projectiles.ENDER_PEARL;
            // Discs
            if (stack.isIn(ModItemTypeTags.DISCS))
                return Projectiles.DISC;

        }
        return Projectiles.NONE;
    }

    public enum Projectiles implements StringIdentifiable {
        NONE(null,"none",1.0F,3.0F),
        ARROW(Items.ARROW, "arrow", 0.9F, 3.0F),
        TIPPED_ARROW(Items.TIPPED_ARROW,"tipped_arrow", 1.0F, 2.5F),
        SPECTRAL_ARROW(Items.SPECTRAL_ARROW,"spectral_arrow",1.0F, 3.2F),
        ROCKET(Items.FIREWORK_ROCKET,"rocket",0.8F,1.6F),
        WIND_CHARGE(Items.WIND_CHARGE,"wind_charge", 0.5F, 3.5F),
        SNOWBALL(Items.SNOWBALL,"snowball", 0.0F, 5.0F),
        EGG(Items.EGG,"egg",0.0F, 6.0F),
        ENDER_PEARL(Items.ENDER_PEARL,"ender_pearl", 2.0F, 5.0F),
        DISC(ModItemTypeTags.DISCS, 0.5F, 4.0F);

        public static final EnumCodec<Projectiles> CODEC = StringIdentifiable.createCodec(Projectiles::values);
        Item item;
        TagKey<Item> tagKey;
        String name;
        float cooldown;
        float speed;

        Projectiles(final String name){
            this.name = name;
        }

        Projectiles(Item item, String name, float cooldown, float speed){
            this.item = item;
            this.name = name;
            this.cooldown = cooldown;
            this.speed = speed;
        }
        Projectiles(TagKey<Item> itemTagKey, float cooldown, float speed){
            this.item = null;
            this.name = null;
            this.tagKey = itemTagKey;
            this.cooldown = cooldown;
            this.speed = speed;
        }

        public float getSpeed() {
            return speed;
        }

        public float getCooldown() {
            return cooldown;
        }

        public String getName() {
            return name;
        }

        public Item getItem() {
            return item;
        }

        public TagKey<Item> getTagKey() {
            return tagKey;
        }

        @Override
        public String asString() {
            return this.name;
        }
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
