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

public record TrailParticleEffect(ParticleType<TrailParticleEffect> type, int color, Vec3d RYP, Vec3d prev_RYP, Vec3d pos, Vec3d prev_pos, int index) implements ParticleEffect {

    public static MapCodec<TrailParticleEffect> createCodec(ParticleType<TrailParticleEffect> type) {
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codecs.RGB.fieldOf("color").forGetter(TrailParticleEffect::color),
                                Vec3d.CODEC.fieldOf("roll_yaw_pitch").forGetter(TrailParticleEffect::prev_RYP),
                                Vec3d.CODEC.fieldOf("previous_roll_yaw_pitch").forGetter(TrailParticleEffect::RYP),
                                Vec3d.CODEC.fieldOf("pos").forGetter(TrailParticleEffect::prev_pos),
                                Vec3d.CODEC.fieldOf("previous_pos").forGetter(TrailParticleEffect::pos),
                                Codecs.POSITIVE_INT.fieldOf("index").forGetter(TrailParticleEffect::index)
                        )
                        .apply(instance, (color, RYP, prev_RYP, pos, prev_pos, index) -> new TrailParticleEffect(type,color,RYP,prev_RYP, pos, prev_pos, index))
        );
    }

    public static PacketCodec<? super ByteBuf, TrailParticleEffect> createPacketCodec(ParticleType<TrailParticleEffect> type) {
        return PacketCodec.tuple(
                PacketCodecs.INTEGER,
                TrailParticleEffect::color,
                Vec3d.PACKET_CODEC,
                TrailParticleEffect::RYP,
                Vec3d.PACKET_CODEC,
                TrailParticleEffect::prev_RYP,
                Vec3d.PACKET_CODEC,
                TrailParticleEffect::pos,
                Vec3d.PACKET_CODEC,
                TrailParticleEffect::prev_pos,
                PacketCodecs.INTEGER,
                TrailParticleEffect::index,
                (color, RYP, prev_RYP, pos, prev_pos, index) -> new TrailParticleEffect(type,color,RYP,prev_RYP, pos, prev_pos, index)
        );
    }

    public int getIndex() { return this.index; }

    // Position

    public Vec3d getPos() { return this.pos; }

    public Vec3d getPrevPos() { return this.prev_pos; }

    // Roll, Yaw, Pitch

    public Vec3d getRYP() { return this.RYP; }

    public float getRoll() { return (float)this.RYP.x; }
    public float getYaw() { return (float)this.RYP.y; }
    public float getPitch() { return (float)this.RYP.z; }

    public Vec3d getPrevRYP() { return this.prev_RYP; }

    public float getPrevRoll() { return (float)this.prev_RYP.x; }
    public float getPrevYaw() { return (float)this.prev_RYP.y; }
    public float getPrevPitch() { return (float)this.prev_RYP.z; }

    public float getLerpedRoll(float tickDelta) {
        return tickDelta == 1.0F ? this.getRoll() : MathHelper.lerpAngleDegrees(tickDelta, this.getPrevRoll(), this.getRoll());
    }
    public float getLerpedYaw(float tickDelta) {
        return tickDelta == 1.0F ? this.getYaw() : MathHelper.lerpAngleDegrees(tickDelta, this.getPrevYaw(), this.getYaw());
    }
    public float getLerpedPitch(float tickDelta) {
        return tickDelta == 1.0F ? this.getPitch() : MathHelper.lerpAngleDegrees(tickDelta, this.getPrevPitch(), this.getPitch());
    }

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

    public static TrailParticleEffect create(ParticleType<TrailParticleEffect> type, int color, Vec3d RYP, Vec3d prev_RYP, Vec3d pos, Vec3d prev_pos, int index) {
        return new TrailParticleEffect(type, color, RYP, prev_RYP, pos, prev_pos, index);
    }

    public static TrailParticleEffect create(ParticleType<TrailParticleEffect> type, float r, float g, float b, float roll, float yaw, float pitch, float previousRoll, float previousYaw, float previousPitch, Vec3d pos, Vec3d prev_pos, int index) {
        return create(type, ColorHelper.fromFloats(1.0F, r, g, b), new Vec3d(roll, yaw, pitch), new Vec3d(previousRoll,previousYaw, previousPitch), pos, prev_pos, index);
    }

    @Override
    public ParticleType<TrailParticleEffect> getType() {
        return this.type;
    }
}
