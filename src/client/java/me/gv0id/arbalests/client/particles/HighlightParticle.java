package me.gv0id.arbalests.client.particles;

import me.gv0id.arbalests.particle.HighlightParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;

public class HighlightParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    public float initScale = 1.0F;
    public float scaleBobStrength = 0.1F;
    public float scaleBobTime = 10F;


    public HighlightParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 10;
        this.scale = initScale;
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
        this.scale = initScale + ((this.age % scaleBobTime) * scaleBobStrength);
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
    public static class Factory implements ParticleFactory<HighlightParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }


        public Particle createParticle(HighlightParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            HighlightParticle highlightParticle = new HighlightParticle(world, x, y, z, this.spriteProvider);
            highlightParticle.setColor(parameters.getRed(), parameters.getGreen(), parameters.getBlue());
            highlightParticle.setAlpha(parameters.getAlpha());
            return  highlightParticle;
        }
    }
}

