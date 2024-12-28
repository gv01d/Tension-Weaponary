package me.gv0id.arbalests.entity.attribute;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEntityAttributes {
    public static final RegistryEntry<EntityAttribute> STRAFE_JUMP = register(
            "strafe_jump", new ClampedEntityAttribute("attribute.name.strafe_jump", 0.0, 0.0, 1.0).setTracked(true)
    );

    private static RegistryEntry<EntityAttribute> register(String id, EntityAttribute attribute) {
        return Registry.registerReference(Registries.ATTRIBUTE, Identifier.of("arbalets",id), attribute);
    }
}
