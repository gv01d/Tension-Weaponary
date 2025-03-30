package me.gv0id.arbalests.enchantment;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.registry.tag.ModEnchantmentTypeTags;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentSource;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.item.EnchantmentUtil;
import net.fabricmc.fabric.mixin.item.EnchantmentBuilderAccessor;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.item.CrossbowItem;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModEnchantments {

    public static final RegistryKey<Enchantment> BEGGARS_BACKFIRE = RegistryKey.of(RegistryKeys.ENCHANTMENT, Arbalests.identifierOf("beggars_backfire"));
    public static final RegistryKey<Enchantment> FAST_BALL = RegistryKey.of(RegistryKeys.ENCHANTMENT, Arbalests.identifierOf("fast_ball"));
    public static final RegistryKey<Enchantment> SLOW_BALL = RegistryKey.of(RegistryKeys.ENCHANTMENT, Arbalests.identifierOf("slow_ball"));
    public static final RegistryKey<Enchantment> STRAFE_CHARGE = RegistryKey.of(RegistryKeys.ENCHANTMENT, Arbalests.identifierOf("strafe_charge"));

    public static void init(){
        EnchantmentEvents.ALLOW_ENCHANTING.register(((enchantment, target, enchantingContext) -> {
            if (target.isIn(ModItemTypeTags.DEADBEAT_ENCHANTABLE)){
                if (enchantment.matchesKey(Enchantments.QUICK_CHARGE)){

                    return TriState.TRUE;
                }
                if (enchantment.matchesKey(Enchantments.MULTISHOT)){
                    return TriState.TRUE;
                }
            }
            return TriState.DEFAULT;
        }));

    }

}
