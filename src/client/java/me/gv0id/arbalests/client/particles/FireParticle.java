package me.gv0id.arbalests.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FireParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final float deaccel = 0.40F;
    private final float gravity = -0.02F;

    protected FireParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = (int) (this.random.nextFloat() * 8);
        this.scale = (float) MathHelper.lerp(Math.random(), 0.3, 0.7);
        Vec3d r = new Vec3d (
                MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6)
                );
        this.prevPosX = this.x + r.x;
        this.prevPosY = this.y + r.y;
        this.prevPosZ = this.z + r.z;
        this.setPos(this.x + r.x, this.y + r.x, this.z + r.x);
        this.setVelocity(r.x * 3, r.y * 3, r.z * 3);

        float rand = this.random.nextFloat();
        this.setColor(
                MathHelper.lerp( rand, 1F, 0.8F),
                MathHelper.lerp( rand, 1F, 0.8F),
                MathHelper.lerp( rand, 1F, 0.2F)
        );

        //this.setBoundingBoxSpacing(1.0F, 1.0F);
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
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.setVelocity(this.velocityX * deaccel, this.velocityY * deaccel, this.velocityZ * deaccel);
        this.velocityY -= gravity;

        //this.alpha = 1.0F - (float)this.age / (float)this.maxAge;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new FireParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class SmallFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider field_50230;

        public SmallFactory(SpriteProvider spriteProvider) {
            this.field_50230 = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = new FireParticle(clientWorld, d, e, f, this.field_50230);
            particle.scale(0.15F);
            return particle;
        }
    }
}
