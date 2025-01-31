package me.gv0id.arbalests.client.particles;

import me.gv0id.arbalests.particle.ColoredParticleEffect;
import me.gv0id.arbalests.particle.StreakParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class ColoredBoomParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;


    protected ColoredBoomParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 7;
        this.scale = 6.0F;
        this.setBoundingBoxSpacing(1.0F, 1.0F);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.alpha -= 1F /this.maxAge;
        this.scale += 0.02F * (this.scale * this.scale);
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        super.render(vertexConsumer, camera, tickDelta);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<ColoredParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(ColoredParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ColoredBoomParticle coloredBoomParticle = new ColoredBoomParticle(clientWorld, d, e, f, this.spriteProvider);
            coloredBoomParticle.setColor(parameters.getRed(),parameters.getGreen(), parameters.getBlue());
            coloredBoomParticle.setAlpha(parameters.getAlpha());
            return coloredBoomParticle;
        }
    }
}
