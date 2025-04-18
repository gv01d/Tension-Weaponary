package me.gv0id.arbalests.enchantment;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEnchantmentEffects {
    private static MapCodec<? extends EnchantmentEntityEffect> registryEntityEffect(String name, MapCodec<? extends  EnchantmentEntityEffect> codec){
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Arbalests.identifierOf(name), codec);
    }

    public static void registerEnchantmentEffects() {
        Arbalests.LOGGER.info("Registering enchantments for:" + Arbalests.MOD_ID);

    }
}
