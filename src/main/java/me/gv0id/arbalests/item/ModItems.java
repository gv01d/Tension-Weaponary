package me.gv0id.arbalests.item;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.DeadbeatCrossbowCharging;
import me.gv0id.arbalests.item.custom.CopperDiscItem;
import me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem;
import me.gv0id.arbalests.item.potion.ModPotions;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.*;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModItems {

    public static final Item TRIAL_ESSENCE = register(
            "trial_essence",
            Item::new,
            new Item.Settings().rarity(Rarity.UNCOMMON)
    );

    //Tension Repeater
    public static final Item DEADBEAT_CROSSBOW = register(
            "deadbeat_crossbow",
            DeadbeatCrossbowItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .maxDamage(465)
                    .component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT)
                    .component(ModDataComponentTypes.ARBALEST_COOLDOWN, new ArbalestCooldown(0.5f))
                    .enchantable(1)
                    .useCooldown(1)
                    .component(ModDataComponentTypes.DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE, DeadbeatCrossbowCharging.DEFAULT)
    );

    public static final Item COPPER_DISC = register(
            "copper_disc",
            CopperDiscItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.UNCOMMON)
                    .useCooldown(0.5F)
                    .component(DataComponentTypes.CHARGED_PROJECTILES,ChargedProjectilesComponent.DEFAULT)
    );

    // ----------------------------------------------------------------------------------------------------------

    public static ArrayList<Item> discList = new ArrayList<>(
            List.of(
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
            )
    );

    public static void initialize() {
        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.TRIAL_ESSENCE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> {
                    itemGroup.addAfter(Items.CROSSBOW,ModItems.DEADBEAT_CROSSBOW);
                    itemGroup.add(ModItems.COPPER_DISC);
                    for (Item item : discList){

                        ItemStack temp = ModItems.COPPER_DISC.getDefaultStack();
                        temp.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(item.getDefaultStack()));

                        itemGroup.add(temp);
                    }
                });



        ModPotions.registerPotions();

        FabricBrewingRecipeRegistryBuilder.BUILD.register( builder -> {
            builder.registerPotionRecipe(Potions.WIND_CHARGED, Items.FERMENTED_SPIDER_EYE, ModPotions.UNSTABLE_STRAFE_POTION);
        });
        FabricBrewingRecipeRegistryBuilder.BUILD.register( builder -> {
            builder.registerPotionRecipe(ModPotions.UNSTABLE_STRAFE_POTION, ModItems.TRIAL_ESSENCE, ModPotions.STRAFE_POTION);
        });
    }

    // ----------------------------------------------------------------------------------------------------------
    // registry functions
    private static Function<Item.Settings, Item> createBlockItemWithUniqueName(Block block) {
        return settings -> new BlockItem(block, settings.useItemPrefixedTranslationKey());
    }

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla(id));
    }

    private static RegistryKey<Item> keyOf(RegistryKey<Block> blockKey) {
        return RegistryKey.of(RegistryKeys.ITEM, blockKey.getValue());
    }

    private static Item register(Block block) {
        return register(block, BlockItem::new);
    }

    private static Item register(Block block, Item.Settings settings) {
        return register(block, BlockItem::new, settings);
    }

    private static Item register(Block block, UnaryOperator<Item.Settings> settingsOperator) {
        return register(block, (BiFunction<Block, Item.Settings, Item>)((blockx, settings) -> new BlockItem(blockx, (Item.Settings)settingsOperator.apply(settings))));
    }

    private static Item register(Block block, Block... blocks) {
        Item item = register(block);

        for (Block block2 : blocks) {
            Item.BLOCK_ITEMS.put(block2, item);
        }

        return item;
    }

    private static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory) {
        return register(block, factory, new Item.Settings());
    }

    private static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory, Item.Settings settings) {
        return register(
                keyOf(block.getRegistryEntry().registryKey()),
                itemSettings -> (Item)factory.apply(block, itemSettings),
                settings.useBlockPrefixedTranslationKey()
        );
    }

    // register itens
    private static Item register(String id, Function<Item.Settings, Item> factory) {
        return register(keyOf(id), factory, new Item.Settings());
    }

    private static Item register(
            String id,
            Function<Item.Settings, Item> factory,
            Item.Settings settings)
    {
        Identifier itemID = Identifier.of(Arbalests.MOD_ID, id);


        return register(RegistryKey.of(RegistryKeys.ITEM, itemID), factory, settings);
    }

    private static Item register(String id, Item.Settings settings) {
        return register(keyOf(id), Item::new, settings);
    }

    private static Item register(String id) {
        return register(keyOf(id), Item::new, new Item.Settings());
    }

    private static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory) {
        return register(key, factory, new Item.Settings());
    }

    private static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings) {
        Item item = (Item)factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registries.ITEM, key, item);
    }

}