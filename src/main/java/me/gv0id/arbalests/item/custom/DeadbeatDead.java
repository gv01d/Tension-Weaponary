/*
package me.gv0id.arbalests.item.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.DeadbeatCrossbowCharging;
import me.gv0id.arbalests.entity.projectile.WindGaleEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
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

public class DeadbeatDead {

    public class DeadbeatCrossbowItem extends RangedWeaponItem {
        public static final int ROUND = 1;
        public static final int AMMO = 3;
        public static final Predicate<ItemStack> DEADBEAT_CROSSBOW_HELD_PROJECTILES = CROSSBOW_HELD_PROJECTILES.or(stack -> stack.isOf(Items.WIND_CHARGE));


        protected static List<ItemStack> repeaterLoad(ItemStack stack, ItemStack projectileStack, LivingEntity shooter) {
            if (projectileStack.isEmpty()) {
                return List.of();
            } else {
                int i = shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND;
                int s = i + Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size();
                Arbalests.LOGGER.info("Stack: {}", Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size());
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



        private static final float DEFAULT_PULL_TIME = 3.0F;
        public static final int RANGE = 8;
        private boolean charged = false;
        private boolean loaded = false;
        private static final float CHARGE_PROGRESS = 0.1F;
        private static final float LOAD_PROGRESS = 0.5F;
        private static final float DEFAULT_SPEED = 3.15F;
        private static final float FIREWORK_ROCKET_SPEED = 1.6F;
        public static final float field_49258 = 1.6F;
        private static final CrossbowItem.LoadingSounds DEFAULT_LOADING_SOUNDS = new CrossbowItem.LoadingSounds(
                Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_START),
                Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE),
                Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
        );

        public DeadbeatCrossbowItem(Item.Settings settings) {
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

        public void maxCooldown(ItemStack stack, PlayerEntity user){
            ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
            float colldown = 0.0F;
            if (chargedProjectilesComponent == null)
                return;
            List<ItemStack> list = chargedProjectilesComponent.getProjectiles();
            for (  ItemStack it : list ){
                colldown += getCooldown(it);
            }
            stack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(colldown));
            UseCooldownComponent cooldownComponent = stack.get(DataComponentTypes.USE_COOLDOWN);
            assert cooldownComponent != null;
            cooldownComponent.set(stack,user);

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
                Arbalests.LOGGER.info("Amount of projectiles : {}",chargedProjectilesComponent.getProjectiles().size());

                if (chargedProjectilesComponent.getProjectiles().size() < 2) {
                    if (arbalestCooldown != null){
                        maxCooldown(itemStack,user);
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
                        itemStack.set(DataComponentTypes.USE_COOLDOWN,new UseCooldownComponent(getCooldown(chargedProjectilesComponent.getProjectiles().getFirst())));
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
                return ActionResult.SUCCESS;
            } else if (!user.getProjectileType(itemStack).isEmpty()) {
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
                itemStack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.CHARGING);
                return ActionResult.FAIL;
            }
        }

        public static boolean isCharging(ItemStack stack){
            DeadbeatCrossbowCharging deadbeatCrossbowCharging = stack.get(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE);
            assert deadbeatCrossbowCharging != null;
            return deadbeatCrossbowCharging.isCharging();
        }

        private static float getSpeed(ChargedProjectilesComponent stack) {
            return stack.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
        }

        @Override
        public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
            stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.DEFAULT);
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float f = getPullProgress(i, stack, user);
            if (isCharged(stack))
                stack.set(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE,DeadbeatCrossbowCharging.CHARGED);
            return false;
        }

        protected static List<ItemStack> loadRound(ItemStack stack, ItemStack projectileStack, LivingEntity shooter) {
            if (projectileStack.isEmpty()) {
                return List.of();
            } else {
                int i = shooter.getWorld() instanceof ServerWorld serverWorld ? EnchantmentHelper.getProjectileCount(serverWorld, stack, shooter, ROUND) : ROUND;
                List<ItemStack> list = new ArrayList<>(i);
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

        private void onContentChanged(PlayerEntity user) {
            ScreenHandler screenHandler = user.currentScreenHandler;
            if (screenHandler != null) {
                screenHandler.onContentChanged(user.getInventory());
            }
        }


        @Override
        public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference){
            if ((clickType == ClickType.LEFT) || otherStack.isEmpty()) return false;

            if (DEADBEAT_CROSSBOW_HELD_PROJECTILES.test(otherStack)) {
                if (stack.get(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE) != DeadbeatCrossbowCharging.CHARGED) {
                    ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT);
                    ArrayList<ItemStack> list = new ArrayList<>(List.copyOf(chargedProjectilesComponent.getProjectiles()));
                    if (list.size() < AMMO) {
                        Arbalests.LOGGER.info("its getting here");
                        CrossbowItem.LoadingSounds loadingSounds = this.getLoadingSounds(stack);
                        ArrayList<ItemStack> temp = new ArrayList<>(list.size() + 1);
                        for (ItemStack it : list){
                            temp.add(it);
                        }
                        float f = ((float) list.size()) / AMMO;
                        temp.add(otherStack.split(1));
                        loadingSounds.end()
                                .ifPresent(sound -> player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), (SoundEvent) sound.value(), SoundCategory.PLAYERS, 1.0F, 0.5F + (f / 2)));
                        maxCooldown(stack, player);

                        stack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.of(temp));
                        onContentChanged(player);
                        return true;
                    }
                }
            }
            return false;
        }

        private static void loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
            List<ItemStack> list = repeaterLoad(crossbow, shooter.getProjectileType(crossbow), shooter);
            if (!list.isEmpty()) {
                crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
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
                return new WindGaleEntity(shooter,world,shooter.getX() ,shooter.getEyeY() - 0.15F, shooter.getZ(),shooter.getVelocity(),3F,3F);
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

        // ----------------------------------------------------------------------------------------------

    /*
        SHOOT FUNCTIONS


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

                //Loading charges
                if (f >= (((float)(Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES))).getProjectiles().size() + 1F)/AMMO) && f <= 1F){
                    Arbalests.LOGGER.info("loading Projectiles...");
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
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
            ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
            if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                List<ItemStack> itemList = chargedProjectilesComponent.getProjectiles();
                MutableText itemText = Text.translatable("item.arbalests.deadbeat_crossbow.projectile");
                int j = 0;
                for (ItemStack itemStack : itemList){
                    if (j > 0){
                        itemText.append(ScreenTexts.LINE_BREAK);
                    }
                    tooltip.add((itemStack.toHoverableText()));
                    if (type.isAdvanced() && itemStack.isOf(Items.FIREWORK_ROCKET)) {
                        List<Text> list = Lists.<Text>newArrayList();
                        Items.FIREWORK_ROCKET.appendTooltip(itemStack, context, list, type);
                        if (!list.isEmpty()) {
                            list.replaceAll(text -> Text.literal("  ").append((Text) text).formatted(Formatting.GRAY));
                            tooltip.addAll(list);
                        }
                    }
                    j++;
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

        float getCooldown(ItemStack stack){
            if (stack.isOf(Items.FIREWORK_ROCKET))
                return me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.CooldownType.ROCKET.getValue();
            if (stack.isOf(Items.SPECTRAL_ARROW))
                return me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.CooldownType.SPECTRAL_ARROW.getValue();
            if (stack.isOf(Items.WIND_CHARGE))
                return me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.CooldownType.WIND_CHARGE.getValue();
            if (stack.isOf(Items.ARROW))
                return me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.CooldownType.ARROW.getValue();
            return me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.CooldownType.NONE.getValue();
        }

        public static enum CooldownType{
            NONE(0.5F),
            ARROW(1F),
            WIND_CHARGE(0.5F),
            SPECTRAL_ARROW(0.8F),
            ROCKET(0.5F);

            private float value;
            public float getValue(){
                return this.value;
            }


            CooldownType(float v) {

            }
        }

        public static enum ChargeType implements StringIdentifiable {
            NONE("none"),
            ARROW("arrow"),
            WIND_CHARGE("wind_charge"),
            SPECTRAL_ARROW("spectral_arrow"),
            ROCKET("rocket");

            public static final EnumCodec<me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.ChargeType> CODEC = StringIdentifiable.createCodec(me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.ChargeType::values);
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
            public static final Codec<me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.LoadingSounds> CODEC = RecordCodecBuilder.create(
                    instance -> instance.group(
                                    SoundEvent.ENTRY_CODEC.optionalFieldOf("start").forGetter(me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.LoadingSounds::start),
                                    SoundEvent.ENTRY_CODEC.optionalFieldOf("mid").forGetter(me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.LoadingSounds::mid),
                                    SoundEvent.ENTRY_CODEC.optionalFieldOf("end").forGetter(me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.LoadingSounds::end)
                            )
                            .apply(instance, me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem.LoadingSounds::new)
            );
        }
    }

}
*/
