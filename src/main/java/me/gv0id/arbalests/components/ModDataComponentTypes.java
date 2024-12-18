package me.gv0id.arbalests.components;

import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.MultiChargedProjectilesComponent;
import me.gv0id.arbalests.mixin.ModDataComponentTypesMx;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.CodecCache;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<MultiChargedProjectilesComponent> MULTI_CHARGED_PROJECTILES = register(
            "multi_charged_projectiles",
            builder -> builder
                    .codec(MultiChargedProjectilesComponent.CODEC)
                    .packetCodec(MultiChargedProjectilesComponent.PACKET_CODEC)
                    .cache()
    );

    public static final ComponentType<ArbalestCooldown> ARBALEST_COOLDOWN = register(
            "arbalests:arbalest_cooldown", builder -> builder.codec(ArbalestCooldown.CODEC).packetCodec(ArbalestCooldown.PACKET_CODEC).cache()
    );

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(ComponentType.builder())).build());
    }


}
