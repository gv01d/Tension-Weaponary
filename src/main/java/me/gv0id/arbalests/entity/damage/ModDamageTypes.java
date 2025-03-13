package me.gv0id.arbalests.entity.damage;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface ModDamageTypes {
    RegistryKey<DamageType> COPPER_DISC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Arbalests.identifierOf("copper_disc"));
    RegistryKey<DamageType> SONIC_BOOM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Arbalests.identifierOf("sonic_boom"));
}
