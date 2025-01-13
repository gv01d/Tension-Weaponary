package me.gv0id.arbalests.item.custom;

import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.DeadbeatCrossbowCharging;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ClickType;
import net.minecraft.util.StringIdentifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class CopperDiscItem extends Item {
    public Predicate<ItemStack> DISCS = stack -> stack.isIn(ModItemTypeTags.DISCS);


    public CopperDiscItem(Settings settings) {
        super(settings);
    }

    public static boolean isCharged(ItemStack stack) {
        return !Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).isEmpty();
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference){
        if ((clickType == ClickType.LEFT) || otherStack.isEmpty()) return false;

        if (DISCS.test(otherStack)) {
            if (Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).isEmpty()) {
                ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT);
                List<ItemStack> list = new ArrayList<>(List.copyOf(chargedProjectilesComponent.getProjectiles()));
                
                list.add(otherStack.split(1));
                stack.set(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.of(list));

                return true;
            }
        }
        return false;
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
