package me.gv0id.arbalests.item.custom;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.DeadbeatCrossbowCharging;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CopperDiscItem extends Item {
    public Predicate<ItemStack> DISCS = stack -> stack.isIn(ModItemTypeTags.DISCS);
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
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) return super.onStackClicked(stack,slot,clickType,player);

        ItemStack slotStack = slot.getStack();
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
        for (Music music : Music.values()){
            if (stack.isOf(music.getItem())){
                return music;
            }
        }
        return Music.NONE;
    }

    public enum Music implements StringIdentifiable {
        NONE((Item) null),
        D13(Items.MUSIC_DISC_13),
        D11(Items.MUSIC_DISC_11),
        D_BLOCKS(Items.MUSIC_DISC_BLOCKS),
        D_CAT(Items.MUSIC_DISC_CAT),
        D_5(Items.MUSIC_DISC_5),
        D_CHIRP(Items.MUSIC_DISC_CHIRP),
        D_CREATOR(Items.MUSIC_DISC_CREATOR),
        D_CREATOR_MUSIC_BOX(Items.MUSIC_DISC_CREATOR_MUSIC_BOX),
        D_FAR(Items.MUSIC_DISC_FAR),
        D_MALL(Items.MUSIC_DISC_MALL),
        D_MELLOHI(Items.MUSIC_DISC_MELLOHI),
        D_OTHERSIDE(Items.MUSIC_DISC_OTHERSIDE),
        D_PIGSTEP(Items.MUSIC_DISC_PIGSTEP),
        D_PRECIPICE(Items.MUSIC_DISC_PRECIPICE),
        D_RELIC(Items.MUSIC_DISC_RELIC),
        D_STAL(Items.MUSIC_DISC_STAL),
        D_STRAD(Items.MUSIC_DISC_STRAD),
        D_WAIT(Items.MUSIC_DISC_WAIT),
        D_WARD(Items.MUSIC_DISC_WARD);

        public static final EnumCodec<Music> CODEC = StringIdentifiable.createCodec(Music::values);

        Item item = null;

        Music(Item item){
            this.item = item;
        }

        public Item getItem() {
            return item;
        }

        @Override
        public String asString() {
            if (this.item == null) {
                return "none";
            }
            return (item.toString().split(":"))[1];
        }
    }
}
