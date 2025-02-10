package me.gv0id.arbalests.client.particles.wind;

import me.gv0id.arbalests.client.particles.CosmicBoomParticle;
import me.gv0id.arbalests.client.render.RenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class EnderGustParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    protected EnderGustParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 7;
        this.scale = 3.5F;
        float a = (float)Math.random() * (float) (Math.PI * 2);
        this.angle = a;
        this.prevAngle = a;
        this.setBoundingBoxSpacing(1.0F, 1.0F);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }
    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Override
    public void renderCustom(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, float tickDelta) {

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.END_CUTOUT.apply(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE));
        render(vertexConsumer, camera, tickDelta);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new EnderGustParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }
}
