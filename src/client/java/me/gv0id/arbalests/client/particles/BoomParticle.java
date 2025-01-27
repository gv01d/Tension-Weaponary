package me.gv0id.arbalests.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class BoomParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;


    protected BoomParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 10;
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
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            BoomParticle boomParticle = new BoomParticle(clientWorld, d, e, f, this.spriteProvider);
            return boomParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class RedFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider field_50230;

        public RedFactory(SpriteProvider spriteProvider) {
            this.field_50230 = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = new BoomParticle(clientWorld, d, e, f, this.field_50230);
            particle.setColor(0.7F, 0F,0F);
            return particle;
        }
    }
}
