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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public record RecisableTrailParticleEffect(ParticleType<RecisableTrailParticleEffect> type, int color, int age, float size, Vec3d pos, Vec3d prev_pos, int index) implements ParticleEffect {

    public static MapCodec<RecisableTrailParticleEffect> createCodec(ParticleType<RecisableTrailParticleEffect> type) {
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codecs.RGB.fieldOf("color").forGetter(RecisableTrailParticleEffect::color),
                                Codecs.NON_NEGATIVE_INT.fieldOf("age").forGetter(RecisableTrailParticleEffect::age),
                                Codecs.NON_NEGATIVE_FLOAT.fieldOf("size").forGetter(RecisableTrailParticleEffect::size),
                                Vec3d.CODEC.fieldOf("pos").forGetter(RecisableTrailParticleEffect::prev_pos),
                                Vec3d.CODEC.fieldOf("previous_pos").forGetter(RecisableTrailParticleEffect::pos),
                                Codecs.POSITIVE_INT.fieldOf("index").forGetter(RecisableTrailParticleEffect::index)
                        )
                        .apply(instance, (color, RYP, prev_RYP, pos, prev_pos, index) -> new RecisableTrailParticleEffect(type,color,RYP,prev_RYP, pos, prev_pos, index))
        );
    }

    public static PacketCodec<? super ByteBuf, RecisableTrailParticleEffect> createPacketCodec(ParticleType<RecisableTrailParticleEffect> type) {
        return PacketCodec.tuple(
                PacketCodecs.INTEGER,
                RecisableTrailParticleEffect::color,
                PacketCodecs.INTEGER,
                RecisableTrailParticleEffect::age,
                PacketCodecs.FLOAT,
                RecisableTrailParticleEffect::size,
                Vec3d.PACKET_CODEC,
                RecisableTrailParticleEffect::pos,
                Vec3d.PACKET_CODEC,
                RecisableTrailParticleEffect::prev_pos,
                PacketCodecs.INTEGER,
                RecisableTrailParticleEffect::index,
                (color, age, size, pos, prev_pos, index) -> new RecisableTrailParticleEffect(type,color,age,size, pos, prev_pos, index)
        );
    }

    public int getIndex() { return this.index; }

    // Position

    public Vec3d getPos() { return this.pos; }

    public Vec3d getPrevPos() { return this.prev_pos; }

    // Age

    public int getAge() { return this.age; }

    // Size

    public float getSize() { return this.size; }

    // Color

    public float getRed() { return (float) ColorHelper.getRed(this.color) / 255.0F; }

    public float getGreen() {
        return (float)ColorHelper.getGreen(this.color) / 255.0F;
    }

    public float getBlue() {
        return (float)ColorHelper.getBlue(this.color) / 255.0F;
    }

    public float getAlpha() {
        return (float)ColorHelper.getAlpha(this.color) / 255.0F;
    }

    // Factory methods

    public static RecisableTrailParticleEffect create(ParticleType<RecisableTrailParticleEffect> type, int color, int age, float size, Vec3d pos, Vec3d prev_pos, int index) {
        return new RecisableTrailParticleEffect(type, color, age, size, pos, prev_pos, index);
    }

    public static RecisableTrailParticleEffect create(ParticleType<RecisableTrailParticleEffect> type, float r, float g, float b, int age, float size, Vec3d pos, Vec3d prev_pos, int index) {
        return create(type, ColorHelper.fromFloats(1.0F, r, g, b), age, size, pos, prev_pos, index);
    }

    @Override
    public ParticleType<RecisableTrailParticleEffect> getType() {
        return this.type;
    }
}
