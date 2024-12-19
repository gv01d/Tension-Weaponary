package me.gv0id.arbalests.components.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.gv0id.arbalests.item.custom.TensionRepeaterItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public record TensionRepeaterCharging(int charging, Optional<Identifier> cooldownGroup) {

    public static final TensionRepeaterCharging DEFAULT = new TensionRepeaterCharging(0);
    public static final TensionRepeaterCharging CHARGED = new TensionRepeaterCharging(1);

    public static final Codec<TensionRepeaterCharging> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.NON_NEGATIVE_INT.fieldOf("charging").forGetter(TensionRepeaterCharging::charging),
                    Identifier.CODEC.optionalFieldOf("cooldown_group").forGetter(TensionRepeaterCharging::cooldownGroup)
            ).apply(instance, TensionRepeaterCharging::new)
    );
    public static final PacketCodec<RegistryByteBuf, TensionRepeaterCharging> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            TensionRepeaterCharging::charging,
            Identifier.PACKET_CODEC.collect(PacketCodecs::optional),
            TensionRepeaterCharging::cooldownGroup,
            TensionRepeaterCharging::new
    );


    public TensionRepeaterCharging(int charging) {this(charging,Optional.empty());}

    public boolean isCharging() {
        return charging == 1;
    }
}
