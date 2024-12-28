package me.gv0id.arbalests.effect;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {


    public static final RegistryEntry<StatusEffect> Strafe = registerStatusEffect("strafe", new StrafeEffect(StatusEffectCategory.NEUTRAL,0x29ffa9));

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect){
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Arbalests.MOD_ID,name),statusEffect);
    }

    public static void registerEffects(){

    }
}
