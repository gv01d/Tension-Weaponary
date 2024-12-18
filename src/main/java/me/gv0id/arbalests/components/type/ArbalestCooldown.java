package me.gv0id.arbalests.components.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public record ArbalestCooldown(float seconds, Optional<Identifier> cooldownGroup) {
    public static final Codec<ArbalestCooldown> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.POSITIVE_FLOAT.fieldOf("seconds").forGetter(ArbalestCooldown::seconds),
                            Identifier.CODEC.optionalFieldOf("cooldown_group").forGetter(ArbalestCooldown::cooldownGroup)
                    )
                    .apply(instance, ArbalestCooldown::new)
    );
    public static final PacketCodec<RegistryByteBuf, ArbalestCooldown> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT,
            ArbalestCooldown::seconds,
            Identifier.PACKET_CODEC.collect(PacketCodecs::optional),
            ArbalestCooldown::cooldownGroup,
            ArbalestCooldown::new
    );

    public ArbalestCooldown(float seconds) {
        this(seconds, Optional.empty());
    }

    public int getCooldownTicks() {
        return (int)(this.seconds * 20.0F);
    }

    public void set(ItemStack stack, LivingEntity user) {
        if (user instanceof PlayerEntity playerEntity) {
            playerEntity.getItemCooldownManager().set(stack, this.getCooldownTicks());
        }
    }
}
