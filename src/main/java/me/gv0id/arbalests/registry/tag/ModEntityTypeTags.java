package me.gv0id.arbalests.registry.tag;


import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface ModEntityTypeTags {

    TagKey<EntityType<?>> WIND_EXPLOSIVES = of("wind_explosives");
    TagKey<EntityType<?>> RESET_FALL = of("reset_fall");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("arbalests",id));
    }
}
