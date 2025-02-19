package me.gv0id.arbalests.client.particles;

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
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.math.MathHelper;

public class CosmicBoomParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;


    protected CosmicBoomParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 9;
        this.scale = 4.0F;
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
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.setVelocity(this.velocityX * 0.6, this.velocityY * 0.6, this.velocityZ * 0.6);

        move(this.velocityX, this.velocityY, this.velocityZ);
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
            CosmicBoomParticle lightFlashParticle = new CosmicBoomParticle(clientWorld, d, e, f, this.spriteProvider);
            //lightFlashParticle.angle = (float)Math.random() * (float) (Math.PI * 2);
            return lightFlashParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class SmallFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider field_50230;

        public SmallFactory(SpriteProvider spriteProvider) {
            this.field_50230 = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = new CosmicBoomParticle(clientWorld, d, e, f, this.field_50230);
            particle.scale(0.15F);
            particle.setMaxAge(7);
            return particle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class SparkFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public SparkFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            CosmicBoomParticle smokeParticle = new CosmicBoomParticle(clientWorld, d, e, f, this.spriteProvider);
            smokeParticle.scale(0.05F);
            smokeParticle.setMaxAge((int)MathHelper.lerp(clientWorld.random.nextFloat(), 5.0F, 10.0F));
            smokeParticle.setVelocity(g, h, i);
            return smokeParticle;
        }
    }

    
}
