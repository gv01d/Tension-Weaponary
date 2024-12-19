package me.gv0id.arbalests.item.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.TensionRepeaterCharging;
import me.gv0id.arbalests.entity.projectile.WindGaleEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TensionRepeaterItem extends RangedWeaponItem {
    public static final int ROUND = 1;
    public static final int AMMO = 3;
    public static final Predicate<ItemStack> TENSION_REPEATER_HELD_PROJECTILES = CROSSBOW_HELD_PROJECTILES.or(stack -> stack.isOf(Items.WIND_CHARGE));


    protected static List<ItemStack> repeaterLoad(ItemStack stack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.isEmpty()) {
            return List.of();
        } else {
            int i = shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND;
            int s = i + stack.get(DataComponentTypes.CHARGED_PROJECTILES).getProjectiles().size();
            Arbalests.LOGGER.info("Stack: {}",stack.get(DataComponentTypes.CHARGED_PROJECTILES).getProjectiles().size());
            List<ItemStack> list = new ArrayList<>(s);
            list.addAll(stack.get(DataComponentTypes.CHARGED_PROJECTILES).getProjectiles());
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



    private static final float DEFAULT_PULL_TIME = 2.5F;
    public static final int RANGE = 8;
    private boolean charged = false;
    private boolean loaded = false;
    private static final float CHARGE_PROGRESS = 0.2F;
    private static final float LOAD_PROGRESS = 0.5F;
    private static final float DEFAULT_SPEED = 3.15F;
    private static final float FIREWORK_ROCKET_SPEED = 1.6F;
    public static final float field_49258 = 1.6F;
    private static final CrossbowItem.LoadingSounds DEFAULT_LOADING_SOUNDS = new CrossbowItem.LoadingSounds(
            Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_START),
            Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE),
            Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
    );

    public TensionRepeaterItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getHeldProjectiles() {
        return TENSION_REPEATER_HELD_PROJECTILES;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            this.shootAll(world, user, hand, itemStack, getSpeed(chargedProjectilesComponent), 1.0F, null);


            // ------------------------------------------ //
            // Cooldown Manager

            ArbalestCooldown arbalestCooldown = itemStack.get(ModDataComponentTypes.ARBALEST_COOLDOWN);
            if (chargedProjectilesComponent.isEmpty()) {
                if (arbalestCooldown != null){
                    itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(arbalestCooldown.seconds() * 2));
                }
                else {
                    itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(2));
                }
                UseCooldownComponent useCooldownComponent = itemStack.get(DataComponentTypes.USE_COOLDOWN);
                if (useCooldownComponent != null) {
                    useCooldownComponent.set(itemStack, user);
                }
            }
            else{
                if (arbalestCooldown != null){
                    itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(arbalestCooldown.seconds()));
                }
                else {
                    itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(2));
                }
                UseCooldownComponent useCooldownComponent = itemStack.get(DataComponentTypes.USE_COOLDOWN);
                if (useCooldownComponent != null){
                    useCooldownComponent.set(itemStack,user);
                }
            }
            // ------------------------------------------ //
            return ActionResult.CONSUME;
        } else if (!user.getProjectileType(itemStack).isEmpty()) {
            this.charged = false;
            this.loaded = false;
            // ------------------------------------------ //
            // Cooldown Manager
            itemStack.remove(DataComponentTypes.USE_COOLDOWN);
            // ------------------------------------------ //
            itemStack.set(ModDataComponentTypes.TENSION_REPEATER_CHARGING_COMPONENT_TYPE,TensionRepeaterCharging.CHARGED);
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        } else {
            itemStack.set(ModDataComponentTypes.TENSION_REPEATER_CHARGING_COMPONENT_TYPE,TensionRepeaterCharging.CHARGED);
            return ActionResult.FAIL;
        }
    }

    public static boolean isCharging(ItemStack stack){
        TensionRepeaterCharging tensionRepeaterCharging = stack.get(ModDataComponentTypes.TENSION_REPEATER_CHARGING_COMPONENT_TYPE);
        return tensionRepeaterCharging.isCharging();
    }

    private static float getSpeed(ChargedProjectilesComponent stack) {
        return stack.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.set(ModDataComponentTypes.TENSION_REPEATER_CHARGING_COMPONENT_TYPE, TensionRepeaterCharging.DEFAULT);
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i, stack, user);
        if (f >= 1.0F && !isFullyCharged(stack, user) && loadProjectiles(user, stack)) {
            CrossbowItem.LoadingSounds loadingSounds = this.getLoadingSounds(stack);
            loadingSounds.end()
                    .ifPresent(
                            sound -> world.playSound(
                                    null,
                                    user.getX(),
                                    user.getY(),
                                    user.getZ(),
                                    (SoundEvent)sound.value(),
                                    user.getSoundCategory(),
                                    1.0F,
                                    1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
                            )
                    );
            return true;
        } else {
            return false;
        }
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

    public static boolean isFullyCharged(ItemStack stack, LivingEntity shooter){
        ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
        return chargedProjectilesComponent.getProjectiles().size() < (shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND);
    }

    public static boolean isCharged(ItemStack stack) {
        ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
        return !chargedProjectilesComponent.isEmpty();
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


    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        if (projectileStack.isOf(Items.FIREWORK_ROCKET)) {
            return new FireworkRocketEntity(world, projectileStack, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true);
        }
        else if(projectileStack.isOf(Items.WIND_CHARGE))
        {
            Entity ent = shooter;
            return new WindGaleEntity(ent,world,shooter.getX() ,shooter.getEyeY() - 0.15F, shooter.getZ(),shooter.getVelocity(),3F,3F);
        }
        else {
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

                Arbalests.LOGGER.info("NotWind");
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
            Arbalests.LOGGER.info("size: {}", amt);
            Arbalests.LOGGER.info("size List: {}", tList.size());

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
            CrossbowItem.LoadingSounds loadingSounds = this.getLoadingSounds(stack);
            float f = (float)(stack.getMaxUseTime(user) - remainingUseTicks) / (float)getPullTime(stack, user);

            Arbalests.LOGGER.info("F : {}, stack: {}, AMMO :{} , st + 1 / AMMO = {} ",
                    f,
                    stack.get(DataComponentTypes.CHARGED_PROJECTILES).getProjectiles().size(),
                    AMMO,
                    ((float)stack.get(DataComponentTypes.CHARGED_PROJECTILES).getProjectiles().size() + 1F)/AMMO
                    );
            if (f >= (((float)(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size() + 1F)/AMMO) && f < 1F){
                Arbalests.LOGGER.info("loading Projectiles...");
                loadProjectiles(user,stack);
                loadingSounds.end()
                        .ifPresent(sound -> world.playSound(null, user.getX(), user.getY(), user.getZ(), (SoundEvent)sound.value(), SoundCategory.PLAYERS, 1.0F, 0.5F + (f/2)));
            }

            if (f < 0.2F) {
                this.charged = false;
                this.loaded = false;
            }

            if (f >= 0.2F && !this.charged) {
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
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            ItemStack itemStack = (ItemStack)chargedProjectilesComponent.getProjectiles().get(0);
            tooltip.add(Text.translatable("item.minecraft.crossbow.projectile").append(ScreenTexts.SPACE).append(itemStack.toHoverableText()));
            if (type.isAdvanced() && itemStack.isOf(Items.FIREWORK_ROCKET)) {
                List<Text> list = Lists.<Text>newArrayList();
                Items.FIREWORK_ROCKET.appendTooltip(itemStack, context, list, type);
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        list.set(i, Text.literal("  ").append((Text)list.get(i)).formatted(Formatting.GRAY));
                    }

                    tooltip.addAll(list);
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
        return 8;
    }

    public static enum ChargeType implements StringIdentifiable {
        NONE("none"),
        ARROW("arrow"),
        ROCKET("rocket");

        public static final Codec<CrossbowItem.ChargeType> CODEC = StringIdentifiable.createCodec(CrossbowItem.ChargeType::values);
        private final String name;

        private ChargeType(final String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

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
