package me.gv0id.arbalests.components;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.components.type.ArbalestCooldown;
import me.gv0id.arbalests.components.type.ChargeValueComponent;
import me.gv0id.arbalests.components.type.DeadbeatCrossbowCharging;
import me.gv0id.arbalests.components.type.MultiChargedProjectilesComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<MultiChargedProjectilesComponent> MULTI_CHARGED_PROJECTILES = register(
            "multi_charged_projectiles",
            builder -> builder
                    .codec(MultiChargedProjectilesComponent.CODEC)
                    .packetCodec(MultiChargedProjectilesComponent.PACKET_CODEC)
                    .cache()
    );

    public static final ComponentType<ChargeValueComponent> CHARGE_VALUE = register(
            "charge_value",
            builder -> builder.codec(ChargeValueComponent.CODEC).packetCodec(ChargeValueComponent.PACKET_CODEC).cache()
    );

    public static final ComponentType<ArbalestCooldown> ARBALEST_COOLDOWN = register(
            "arbalests:arbalest_cooldown",
            builder -> builder.codec(ArbalestCooldown.CODEC).packetCodec(ArbalestCooldown.PACKET_CODEC).cache()
    );

    public static final ComponentType<DeadbeatCrossbowCharging> DEADBEAT_CROSSBOW_CHARGING_COMPONENT_TYPE = register(
            "arbalest:deadbeat_crossbow_charging",
            builder -> builder.codec(DeadbeatCrossbowCharging.CODEC).packetCodec(DeadbeatCrossbowCharging.PACKET_CODEC).cache()
    );

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, (builderOperator.apply(ComponentType.builder())).build());
    }


}
