package me.gv0id.arbalests.client.data.enchantments;

import me.gv0id.arbalests.enchantment.ModEnchantments;
import me.gv0id.arbalests.registry.tag.ModEnchantmentTypeTags;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModEnchantmentGen {
    public static void bootstrap(Registerable<Enchantment> registerable){

        RegistryEntryLookup<Enchantment> enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        RegistryEntryLookup<Item> items = registerable.getRegistryLookup(RegistryKeys.ITEM);

        register(
                registerable,
                ModEnchantments.STRAFE_CHARGE,
                Enchantment.builder(
                        Enchantment.definition(
                                items.getOrThrow(ModItemTypeTags.DEADBEAT_ENCHANTABLE),
                                4,
                                2,
                                Enchantment.constantCost(15),
                                Enchantment.constantCost(50),
                                6,
                                AttributeModifierSlot.MAINHAND
                        ))
                        .exclusiveSet(enchantments.getOrThrow(ModEnchantmentTypeTags.DEADBEAT_CHARGE_ENCHANTMENTS))
        );
        register(
                registerable,
                ModEnchantments.FAST_BALL,
                Enchantment.builder(
                        Enchantment.definition(
                                items.getOrThrow(ModItemTypeTags.DEADBEAT_ENCHANTABLE),
                                8,
                                3,
                                Enchantment.leveledCost(1, 9),
                                Enchantment.leveledCost(15, 10),
                                2,
                                AttributeModifierSlot.MAINHAND
                        ))
                        .exclusiveSet(enchantments.getOrThrow(ModEnchantmentTypeTags.DEADBEAT_SPECIAL_EXCLUSIVE_SET))
                        .exclusiveSet(enchantments.getOrThrow(ModEnchantmentTypeTags.SHOTVARIETY_ENCHANTMENTS))
        );
        register(
                registerable,
                ModEnchantments.SLOW_BALL,
                Enchantment.builder(
                                Enchantment.definition(
                                        items.getOrThrow(ModItemTypeTags.DEADBEAT_ENCHANTABLE),
                                        4,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        4,
                                        AttributeModifierSlot.MAINHAND
                                ))
                        .exclusiveSet(enchantments.getOrThrow(ModEnchantmentTypeTags.SHOTVARIETY_ENCHANTMENTS))
        );
        register(registerable,
                ModEnchantments.BEGGARS_BACKFIRE,
                Enchantment.builder(
                        Enchantment.definition(
                                items.getOrThrow(ModItemTypeTags.DEADBEAT_ENCHANTABLE),
                                3,
                                3,
                                Enchantment.leveledCost(10,9),
                                Enchantment.leveledCost(20, 10),
                                6,
                                AttributeModifierSlot.MAINHAND
                        ))
                        .exclusiveSet(enchantments.getOrThrow(ModEnchantmentTypeTags.DEADBEAT_SPECIAL_EXCLUSIVE_SET))
                        .addEffect(EnchantmentEffectComponentTypes.PROJECTILE_SPREAD, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(12.0F)))
                        .addNonListEffect(EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(4F)))
        );



        /*
        register(registerable, BEGGARS_BEST_FRIEND, Enchantment.builder(Enchantment.definition(

        )));
         */

    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder){
        registry.register(key,builder.build(key.getValue()));
    }
}
