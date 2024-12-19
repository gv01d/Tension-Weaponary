package me.gv0id.arbalests.item;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.components.ModDataComponentTypes;
import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.TensionRepeaterCharging;
import me.gv0id.arbalests.item.custom.TensionRepeaterItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModItems {
    public static Item pv_register(String id) {
        // create identifier
        Identifier itemID = Identifier.of(Arbalests.MOD_ID, id);

        // create key
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, itemID);

        // create item settings
        Item.Settings settings = new Item.Settings()
                .registryKey(key);

        // register item
        Item registeredItem = Registry.register(Registries.ITEM, key, new Item(settings));

        // return item
        return registeredItem;
    }

    // ----------------------------------------------------------------------------------------------------------

    public static final Item SUSPICIOUS_SUBSTANCE = pv_register(
            "suspicious_substance"
    );

    //Tension Repeater
    public static final Item TENSION_REPEATER = register(
            "tension_repeater",
            TensionRepeaterItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .maxDamage(465)
                    .component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT)
                    .component(ModDataComponentTypes.ARBALEST_COOLDOWN, new ArbalestCooldown(0.5f))
                    .enchantable(1)
                    .useCooldown(1)
                    .component(ModDataComponentTypes.TENSION_REPEATER_CHARGING_COMPONENT_TYPE, TensionRepeaterCharging.DEFAULT)
    );

    // ----------------------------------------------------------------------------------------------------------

    public static void initialize() {
        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our suspicious item to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.SUSPICIOUS_SUBSTANCE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register((itemGroup) -> itemGroup.add(ModItems.TENSION_REPEATER));
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

    public static Item register(Block block) {
        return register(block, BlockItem::new);
    }

    public static Item register(Block block, Item.Settings settings) {
        return register(block, BlockItem::new, settings);
    }

    public static Item register(Block block, UnaryOperator<Item.Settings> settingsOperator) {
        return register(block, (BiFunction<Block, Item.Settings, Item>)((blockx, settings) -> new BlockItem(blockx, (Item.Settings)settingsOperator.apply(settings))));
    }

    public static Item register(Block block, Block... blocks) {
        Item item = register(block);

        for (Block block2 : blocks) {
            Item.BLOCK_ITEMS.put(block2, item);
        }

        return item;
    }

    public static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory) {
        return register(block, factory, new Item.Settings());
    }

    public static Item register(Block block, BiFunction<Block, Item.Settings, Item> factory, Item.Settings settings) {
        return register(
                keyOf(block.getRegistryEntry().registryKey()),
                itemSettings -> (Item)factory.apply(block, itemSettings),
                settings.useBlockPrefixedTranslationKey()
        );
    }

    // register itens
    public static Item register(String id, Function<Item.Settings, Item> factory) {
        return register(keyOf(id), factory, new Item.Settings());
    }

    public static Item register(
            String id,
            Function<Item.Settings, Item> factory,
            Item.Settings settings)
    {
        Identifier itemID = Identifier.of(Arbalests.MOD_ID, id);


        return register(RegistryKey.of(RegistryKeys.ITEM, itemID), factory, settings);
    }

    public static Item register(String id, Item.Settings settings) {
        return register(keyOf(id), Item::new, settings);
    }

    public static Item register(String id) {
        return register(keyOf(id), Item::new, new Item.Settings());
    }

    public static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory) {
        return register(key, factory, new Item.Settings());
    }

    public static Item register(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings) {
        Item item = (Item)factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return Registry.register(Registries.ITEM, key, item);
    }

}