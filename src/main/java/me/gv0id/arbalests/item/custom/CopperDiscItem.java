package me.gv0id.arbalests.item.custom;

import com.google.common.collect.Lists;
import me.gv0id.arbalests.entity.projectile.MusicDiscEntity;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.TrailParticleEffect;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.minecraft.block.Block;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CopperDiscItem extends Item {
    public Predicate<ItemStack> DISCS = stack -> stack.isIn(ModItemTypeTags.DISCS);
    public float POWER = 1.0F;
    public final int AMOUNT = 1;


    public CopperDiscItem(Settings settings) {
        super(settings);
    }

    public static boolean isCharged(ItemStack stack) {
        return !Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).isEmpty();
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player){
        ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        ItemStack slotStack = slot.getStack();
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {

            if (clickType.equals(ClickType.RIGHT) && !slotStack.isEmpty() && DISCS.test(slotStack)){
                stack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.of(slot.takeStack(1)));
                return true;
            }

            return super.onStackClicked(stack,slot,clickType,player);
        }

        if (!slotStack.isEmpty()) return super.onStackClicked(stack,slot,clickType,player);

        if (clickType.equals(ClickType.RIGHT)){
            ArrayList<ItemStack> stackList = new ArrayList<>(chargedProjectilesComponent.getProjectiles());
            slot.insertStack(stackList.removeFirst());

            stack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(stackList));

            return true;
        }
        return super.onStackClicked(stack,slot,clickType,player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference){
        if ((clickType == ClickType.LEFT) || otherStack.isEmpty()) return false;

        if (DISCS.test(otherStack)) {
            int siz = Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles().size();
            if (siz < AMOUNT) {
                ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT);
                List<ItemStack> list = new ArrayList<>(List.copyOf(chargedProjectilesComponent.getProjectiles()));
                
                list.add(otherStack.split(1));
                stack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.of(list));

                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.isCreative()){
            itemStack.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        }
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (world instanceof ServerWorld serverWorld) {
            ProjectileEntity.spawnWithVelocity((world1, owner, stack) -> new MusicDiscEntity(world1, owner, stack, 3, true, false ),
                    serverWorld, user.isCreative() ? itemStack : itemStack.copy(), user, 0.0F, POWER, 2.0F);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        ItemStack itemStack = context.getStack();
        Block block = world.getBlockState(blockPos).getBlock();

        if (!(block instanceof JukeboxBlock jukeboxBlock) || !(world.getBlockEntity(blockPos) instanceof JukeboxBlockEntity jukeboxBlockEntity)) {
            return ActionResult.PASS;
        }
        ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if ( world.getBlockState(blockPos).get(JukeboxBlock.HAS_RECORD) ){
            if (chargedProjectilesComponent.isEmpty()){
                ItemStack stack = jukeboxBlockEntity.getStack();
                ArrayList<ItemStack> list = new ArrayList<>();
                list.add(stack);
                itemStack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.of(list));
                jukeboxBlockEntity.emptyStack();
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
                return ActionResult.SUCCESS;
            }
            else {
                jukeboxBlockEntity.dropRecord();
            }
        }
        if (itemStack.isOf(ModItems.COPPER_DISC)) {
            if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()){
                ArrayList<ItemStack> list = new ArrayList<>(chargedProjectilesComponent.getProjectiles());
                jukeboxBlockEntity.setDisc(list.removeFirst());
                jukeboxBlockEntity.reloadDisc();
                world.setBlockState(blockPos, world.getBlockState(blockPos).with(JukeboxBlock.HAS_RECORD, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
                itemStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (!isCharged(stack)){
            tooltip.add(Text.translatable("item.arbalests.copper_disc.right_click_request").formatted(Formatting.GRAY));

            assert chargedProjectilesComponent != null;
            if (!chargedProjectilesComponent.getProjectiles().isEmpty())
                tooltip.add(ScreenTexts.SPACE);
        }

        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            List<ItemStack> itemList = chargedProjectilesComponent.getProjectiles();
            if (AMOUNT > 1){
                tooltip.add(Text.translatable("item.arbalests.copper_disc.music").formatted(Formatting.DARK_PURPLE));
            }

            for (ItemStack itemStack : itemList){

                tooltip.add((itemStack.toHoverableText().copy().formatted(Formatting.BLUE)));
                JukeboxPlayableComponent jukeboxPlayableComponent = itemStack.get(DataComponentTypes.JUKEBOX_PLAYABLE);

                if (type.isAdvanced()) {
                    List<Text> list = Lists.<Text>newArrayList();
                    Consumer<Text> textConsumer = list::add;
                    if (jukeboxPlayableComponent != null){
                        jukeboxPlayableComponent.appendTooltip(context, textConsumer,type);
                    }
                    itemStack.getItem().appendTooltip(itemStack, context, list, type);
                    if (!list.isEmpty()) {
                        list.replaceAll(text -> Text.literal("  ").append((Text) text).formatted(Formatting.GRAY));
                        tooltip.addAll(list);
                    }
                }
            }
        }
    }



    public static Music getMusic(ItemStack stack){
        if (stack == null) return Music.NONE;
        for (Music music : Music.values()){
            if (stack.isOf(music.getItem())){
                return music;
            }
        }
        return Music.NONE;
    }

    public enum Music implements StringIdentifiable {
        NONE((Item) null, ModParticles.COPPER_DISC_TRAIL),
        D13(Items.MUSIC_DISC_13, ModParticles.D13_TRAIL),
        D11(Items.MUSIC_DISC_11, ColorHelper.fromFloats(1F,0.533F,0.533F,0.533F)),
        D_BLOCKS(Items.MUSIC_DISC_BLOCKS, ColorHelper.fromFloats(1F,0.886F,0.329F,0.231F)),
        D_CAT(Items.MUSIC_DISC_CAT, ColorHelper.fromFloats(1F,0.298F,1F,0F)),
        D_5(Items.MUSIC_DISC_5, ModParticles.D5_TRAIL),
        D_CHIRP(Items.MUSIC_DISC_CHIRP, ModParticles.CHIRP_TRAIL),
        D_CREATOR(Items.MUSIC_DISC_CREATOR, ModParticles.CREATOR_TRAIL),
        D_CREATOR_MUSIC_BOX(Items.MUSIC_DISC_CREATOR_MUSIC_BOX, ModParticles.CREATOR_MUSIC_BOX_TRAIL),
        D_FAR(Items.MUSIC_DISC_FAR, ModParticles.FAR_TRAIL),
        D_MALL(Items.MUSIC_DISC_MALL, ColorHelper.fromFloats(1F,0.603F,0.458F,1F)),
        D_MELLOHI(Items.MUSIC_DISC_MELLOHI, ModParticles.MELLOHI_TRAIL),
        D_OTHERSIDE(Items.MUSIC_DISC_OTHERSIDE, ModParticles.OTHERSIDE_TRAIL),
        D_PIGSTEP(Items.MUSIC_DISC_PIGSTEP, ModParticles.PIGSTEP_TRAIL),
        D_PRECIPICE(Items.MUSIC_DISC_PRECIPICE, ModParticles.PRECIPICE_TRAIL),
        D_RELIC(Items.MUSIC_DISC_RELIC, ModParticles.RELIC_TRAIL),
        D_STAL(Items.MUSIC_DISC_STAL, ColorHelper.fromFloats(1F,0F,0F,0F)),
        D_STRAD(Items.MUSIC_DISC_STRAD, ColorHelper.fromFloats(1F,1F,1F,1F)),
        D_WAIT(Items.MUSIC_DISC_WAIT, ColorHelper.fromFloats(1F,0.282F,0.454F,0.701F)),
        D_WARD(Items.MUSIC_DISC_WARD, ModParticles.WARD_TRAIL);

        public static final EnumCodec<Music> CODEC = StringIdentifiable.createCodec(Music::values);

        Item item = null;
        ParticleType<me.gv0id.arbalests.particle.TrailParticleEffect> particleType = ModParticles.GENERIC_DISC_TRAIL;
        int color = ColorHelper.fromFloats(1F,1F, 1F, 1F);

        Music(Item item){
            this.item = item;
        }

        Music(Item item, int color){
            this.item = item;
            this.color = color;
        }

        Music(Item item, ParticleType<me.gv0id.arbalests.particle.TrailParticleEffect> particleType) {
            this.item = item;
            this.particleType = particleType;
        }

        Music(Item item, ParticleType<me.gv0id.arbalests.particle.TrailParticleEffect> particleType, int color){
            this.item = item;
            this.particleType = particleType;
            this.color = color;
        }

        public Item getItem() {
            return item;
        }
        public ParticleType<TrailParticleEffect> getParticleType() { return particleType; }
        public int getColor() { return color; }

        @Override
        public String asString() {
            if (this.item == null) {
                return "none";
            }
            return (item.toString().split(":"))[1];
        }
    }
}
