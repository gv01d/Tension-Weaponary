package me.gv0id.arbalests.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public record ColoredParticleEffect(ParticleType<ColoredParticleEffect> type, int color) implements ParticleEffect {

    public static MapCodec<ColoredParticleEffect> createCodec(ParticleType<ColoredParticleEffect> type) {
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codecs.RGB.fieldOf("color").forGetter(ColoredParticleEffect::color)
                        )
                        .apply(instance, (color) -> new ColoredParticleEffect(type,color))
        );
    }

    public static PacketCodec<? super ByteBuf, ColoredParticleEffect> createPacketCodec(ParticleType<ColoredParticleEffect> type) {
        return PacketCodec.tuple(
                PacketCodecs.INTEGER,
                ColoredParticleEffect::color,
                (color) -> new ColoredParticleEffect(type,color)
        );
    }

    public static ColoredParticleEffect create(ParticleType<ColoredParticleEffect> type, int color) {
        return new ColoredParticleEffect(type, color);
    }

    public static ColoredParticleEffect create(ParticleType<ColoredParticleEffect> type, float alpha, float r, float g, float b) {
        return create(type, ColorHelper.fromFloats(alpha, r, g, b));
    }

    public float getRed() {
        return (float) ColorHelper.getRed(this.color) / 255.0F;
    }

    public float getGreen() {
        return (float) ColorHelper.getGreen(this.color) / 255.0F;
    }

    public float getBlue() {
        return (float) ColorHelper.getBlue(this.color) / 255.0F;
    }

    public float getAlpha() {
        return (float) ColorHelper.getAlpha(this.color) / 255.0F;
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
