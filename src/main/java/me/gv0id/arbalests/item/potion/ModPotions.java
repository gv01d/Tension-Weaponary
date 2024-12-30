package me.gv0id.arbalests.item.potion;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.effect.InstantExplosionStatusEffect;
import me.gv0id.arbalests.effect.ModEffects;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModPotions {

    public static final RegistryEntry<Potion> STRAFE_POTION = registerPotion("strafe_potion", new Potion("strafe_potion",new StatusEffectInstance(ModEffects.STRAFE, 1200,0)));
    public static final RegistryEntry<Potion> UNSTABLE_STRAFE_POTION = registerPotion("unstable_strafe_potion", new Potion("unstable_strafe_potion",new StatusEffectInstance(ModEffects.UNSTABLE_STRAFE_EXPLOSION, 1))); // Explode a WIND GALE on its feet

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion){
        return Registry.registerReference(Registries.POTION, Identifier.of(Arbalests.MOD_ID,name),potion);
    }

    public static void registerPotions() {

    }
}
