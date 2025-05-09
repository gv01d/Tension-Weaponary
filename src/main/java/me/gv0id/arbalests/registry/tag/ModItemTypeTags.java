package me.gv0id.arbalests.registry.tag;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public interface ModItemTypeTags {
    TagKey<Item> DEADBEAT_PROJECTILE = of("deadbeat_projectile");
    TagKey<Item> DISCS = of("record_discs");

    TagKey<Item> IGNORE_EXTRA_LAYER_GLINT = of("ignore_extra_layer_glint");
    TagKey<Item> DEADBEAT_ENCHANTABLE = of("deadbeat_enchantable");

    private static TagKey<Item> of(String id) {

        return TagKey.of(RegistryKeys.ITEM, Arbalests.identifierOf(id));
    }
}
