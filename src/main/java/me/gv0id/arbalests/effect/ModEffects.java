package me.gv0id.arbalests.effect;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {


    public static final RegistryEntry<StatusEffect> STRAFE = registerStatusEffect(
            "strafe", new StrafeEffect(StatusEffectCategory.BENEFICIAL,0x29ffa9).addAttributeModifier(EntityAttributes.SAFE_FALL_DISTANCE, Identifier.of(Arbalests.MOD_ID,"effect.strafe"), 10.0, EntityAttributeModifier.Operation.ADD_VALUE)
    );
    public static final RegistryEntry<StatusEffect> UNSTABLE_STRAFE_EXPLOSION = registerStatusEffect(
            "unstable_strafe_explosion", new InstantExplosionStatusEffect(StatusEffectCategory.NEUTRAL, 0xe5ff8f)
    );

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect){
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Arbalests.MOD_ID,name),statusEffect);
    }

    public static void registerEffects(){

    }
}
