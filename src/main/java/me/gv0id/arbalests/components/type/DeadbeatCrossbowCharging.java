package me.gv0id.arbalests.components.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public record DeadbeatCrossbowCharging(int charging, Optional<Identifier> cooldownGroup) {

    public static final DeadbeatCrossbowCharging DEFAULT = new DeadbeatCrossbowCharging(0);
    public static final DeadbeatCrossbowCharging LOADED = new DeadbeatCrossbowCharging(1);
    public static final DeadbeatCrossbowCharging CHARGING = new DeadbeatCrossbowCharging(2);
    public static final DeadbeatCrossbowCharging CHARGED = new DeadbeatCrossbowCharging(3);
    public static final DeadbeatCrossbowCharging FULLYCHARGED = new DeadbeatCrossbowCharging(4);


    public static final Codec<DeadbeatCrossbowCharging> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.NON_NEGATIVE_INT.fieldOf("charging").forGetter(DeadbeatCrossbowCharging::charging),
                    Identifier.CODEC.optionalFieldOf("cooldown_group").forGetter(DeadbeatCrossbowCharging::cooldownGroup)
            ).apply(instance, DeadbeatCrossbowCharging::new)
    );
    public static final PacketCodec<RegistryByteBuf, DeadbeatCrossbowCharging> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            DeadbeatCrossbowCharging::charging,
            Identifier.PACKET_CODEC.collect(PacketCodecs::optional),
            DeadbeatCrossbowCharging::cooldownGroup,
            DeadbeatCrossbowCharging::new
    );


    public DeadbeatCrossbowCharging(int charging) {this(charging,Optional.empty());}

    public boolean isLoaded(){
        return charging == 1;
    }

    public boolean isCharging() {
        return charging == 2;
    }

    public boolean isCharged() {
        return charging >= 3;
    }

    public boolean isFullyCharged(){
        return charging == 4;
    }
}
