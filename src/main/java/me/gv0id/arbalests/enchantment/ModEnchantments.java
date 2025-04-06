package me.gv0id.arbalests.enchantment;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.*;

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
