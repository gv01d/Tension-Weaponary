package me.gv0id.arbalests.client.data.enchantments;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentSource;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.item.CrossbowItem;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Optional;

public class ModEnchantmentGen {
    public static void bootstrap(Registerable<Enchantment> registerable){

        var enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        var items = registerable.getRegistryLookup(RegistryKeys.ITEM);

        EnchantmentEvents.ALLOW_ENCHANTING.register(((enchantment, target, enchantingContext) -> {
            if (enchantment.equals(Enchantments.QUICK_CHARGE) && target.isIn(ModItemTypeTags.QUICK_CHARGE_COMPATIBLE)){
                return TriState.TRUE;
            }
            return net.fabricmc.fabric.api.util.TriState.DEFAULT;
        }));
        /*
        EnchantmentEvents.MODIFY.register(((key, builder, source) -> {
            if (source == EnchantmentSource.VANILLA && key.isOf(Enchantments.QUICK_CHARGE.getRegistryRef())){
                builder = new Enchantment.Builder(Enchantment.definition(
                        items.getOrThrow(ModItemTypeTags.QUICK_CHARGE_COMPATIBLE),
                        5,
                        3,
                        Enchantment.leveledCost(12, 20),
                        Enchantment.constantCost(50),
                        2,
                        AttributeModifierSlot.MAINHAND,
                        AttributeModifierSlot.OFFHAND
                )).addNonListEffect(EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(-0.25F)))
                        .addNonListEffect(
                                EnchantmentEffectComponentTypes.CROSSBOW_CHARGING_SOUNDS,
                                List.of(
                                        new CrossbowItem.LoadingSounds(
                                                Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                        ),
                                        new CrossbowItem.LoadingSounds(
                                                Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                        ),
                                        new CrossbowItem.LoadingSounds(
                                                Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                        )
                                )
                        );
            }
        }));

        EnchantmentEvents.MODIFY.invoker().modify(Enchantments.QUICK_CHARGE,Enchantment.builder(
                                Enchantment.definition(
                                        items.getOrThrow(ModItemTypeTags.QUICK_CHARGE_COMPATIBLE),
                                        5,
                                        3,
                                        Enchantment.leveledCost(12, 20),
                                        Enchantment.constantCost(50),
                                        2,
                                        AttributeModifierSlot.MAINHAND,
                                        AttributeModifierSlot.OFFHAND
                                )
                        )
                        .addNonListEffect(EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(-0.25F)))
                        .addNonListEffect(
                                EnchantmentEffectComponentTypes.CROSSBOW_CHARGING_SOUNDS,
                                List.of(
                                        new CrossbowItem.LoadingSounds(
                                                Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                        ),
                                        new CrossbowItem.LoadingSounds(
                                                Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                        ),
                                        new CrossbowItem.LoadingSounds(
                                                Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                        )
                                )
                        ),
                EnchantmentSource.VANILLA
                );

         */

        /*
        register(registerable, BEGGARS_BEST_FRIEND, Enchantment.builder(Enchantment.definition(

        )));
         */

    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder){
        registry.register(key,builder.build(key.getValue()));
    }
}
