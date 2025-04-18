package me.gv0id.arbalests.registry.tag;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public interface ModEnchantmentTypeTags {
    TagKey<Enchantment> DEADBEAT_CHARGE_ENCHANTMENTS = of("deadbeat_charge_enchantments");
    TagKey<Enchantment> SHOTVARIETY_ENCHANTMENTS = of("shotvariety_enchantments");
    TagKey<Enchantment> DEADBEAT_SPECIAL_EXCLUSIVE_SET = of("deadbeat_special_exclusive_set");


    private static TagKey<Enchantment> of(String id) {
        return TagKey.of(RegistryKeys.ENCHANTMENT, Arbalests.identifierOf(id));
    }
}
