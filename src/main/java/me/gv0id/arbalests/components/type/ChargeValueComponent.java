package me.gv0id.arbalests.components.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record ChargeValueComponent(int value, int points) {
    public static final ChargeValueComponent DEFAULT = new ChargeValueComponent(0, 0);

    public static final Codec<ChargeValueComponent> CODEC = RecordCodecBuilder.create(
            instanse -> instanse.group(
                    Codecs.NON_NEGATIVE_INT.fieldOf("charge_value").forGetter(ChargeValueComponent::value),
                    Codecs.NON_NEGATIVE_INT.fieldOf("charge_points").forGetter(ChargeValueComponent::points)
            ).apply(instanse, ChargeValueComponent::new)
    );

    public static final PacketCodec<RegistryByteBuf, ChargeValueComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            ChargeValueComponent::value,
            PacketCodecs.INTEGER,
            ChargeValueComponent::points,
            ChargeValueComponent::new
    );
}
