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

public record StreakParticleEffect(ParticleType<StreakParticleEffect> type, int color, Vec3d RYP, Vec3d prev_RYP) implements ParticleEffect {

    public static MapCodec<StreakParticleEffect> createCodec(ParticleType<StreakParticleEffect> type) {
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codecs.RGB.fieldOf("color").forGetter(StreakParticleEffect::color),
                                Vec3d.CODEC.fieldOf("roll_yaw_pitch").forGetter(StreakParticleEffect::prev_RYP),
                                Vec3d.CODEC.fieldOf("previous_roll_yaw_pitch").forGetter(StreakParticleEffect::RYP)
                        )
                        .apply(instance, (color, RYP, prev_RYP) -> new StreakParticleEffect(type,color,RYP,prev_RYP))
                );
    }

    public static PacketCodec<? super ByteBuf, StreakParticleEffect> createPacketCodec(ParticleType<StreakParticleEffect> type) {
        return PacketCodec.tuple(
                PacketCodecs.INTEGER,
                StreakParticleEffect::color,
                Vec3d.PACKET_CODEC,
                StreakParticleEffect::RYP,
                Vec3d.PACKET_CODEC,
                StreakParticleEffect::prev_RYP,
                (color, RYP, prev_RYP) -> new StreakParticleEffect(type,color,RYP,prev_RYP)
        );
    }

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

    public static StreakParticleEffect create(ParticleType<StreakParticleEffect> type, int color, Vec3d RYP, Vec3d prev_RYP) {
        return new StreakParticleEffect(type, color, RYP, prev_RYP);
    }

    public static StreakParticleEffect create(ParticleType<StreakParticleEffect> type, float r, float g, float b, float roll, float yaw, float pitch, float previousRoll, float previousYaw, float previousPitch) {
        return create(type, ColorHelper.fromFloats(1.0F, r, g, b), new Vec3d(roll, yaw, pitch), new Vec3d(previousRoll,previousYaw, previousPitch));
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }


}
