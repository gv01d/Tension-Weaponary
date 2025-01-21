package me.gv0id.arbalests.client.data.tag;

import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModValueLookupItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ModValueLookupItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    public ModValueLookupItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModItemTypeTags.DISCS)
                .add(

                        Items.MUSIC_DISC_13,
                        Items.MUSIC_DISC_11,
                        Items.MUSIC_DISC_BLOCKS,
                        Items.MUSIC_DISC_CAT,
                        Items.MUSIC_DISC_5,
                        Items.MUSIC_DISC_CHIRP,
                        Items.MUSIC_DISC_CREATOR,
                        Items.MUSIC_DISC_CREATOR_MUSIC_BOX,
                        Items.MUSIC_DISC_FAR,
                        Items.MUSIC_DISC_MALL,
                        Items.MUSIC_DISC_MELLOHI,
                        Items.MUSIC_DISC_OTHERSIDE,
                        Items.MUSIC_DISC_PIGSTEP,
                        Items.MUSIC_DISC_PRECIPICE,
                        Items.MUSIC_DISC_RELIC,
                        Items.MUSIC_DISC_STAL,
                        Items.MUSIC_DISC_STRAD,
                        Items.MUSIC_DISC_WAIT,
                        Items.MUSIC_DISC_WARD
                );
        getOrCreateTagBuilder(ItemTags.ARROWS);
        getOrCreateTagBuilder(ModItemTypeTags.DEADBEAT_PROJECTILE)
                .addTag(ItemTags.ARROWS)
                .add(
                        Items.FIREWORK_ROCKET,
                        Items.WIND_CHARGE,
                        Items.SNOWBALL,
                        Items.EGG,
                        Items.ENDER_PEARL,
                        Items.FIRE_CHARGE,
                        Items.NETHER_STAR,
                        ModItems.COPPER_DISC,
                        Items.END_CRYSTAL
                );
    }
}
