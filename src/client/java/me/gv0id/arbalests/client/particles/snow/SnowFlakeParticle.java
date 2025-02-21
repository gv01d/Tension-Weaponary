package me.gv0id.arbalests.client.particles.snow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SnowFlakeParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final float deaccel = 0.60F;
    private final float gravity = 0.02F;

    protected SnowFlakeParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 6;
        this.scale = (float) MathHelper.lerp(Math.random(), 0.3, 0.7);

        float rand = this.random.nextFloat();
        this.setColor(
                MathHelper.lerp( rand, 0.462F, 0.937F),
                MathHelper.lerp( rand, 0.831F, 0.984F),
                MathHelper.lerp( rand, 0.956F, 0.956F)
        );

        //this.setBoundingBoxSpacing(1.0F, 1.0F);
    }

    protected SnowFlakeParticle(ClientWorld world, Vec3d pos, SpriteProvider spriteProvider, Vec3d speed){
        this(world, pos.x, pos.y, pos.z, spriteProvider);
        this.setVelocity(speed.x, speed.y, speed.z);
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

        this.alpha = 1.0F - (float)this.age / (float)this.maxAge;
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
            SnowFlakeParticle snowFlakeParticle = new SnowFlakeParticle(clientWorld, d, e, f, this.spriteProvider);
            snowFlakeParticle.setVelocity(g, h, i);
            return snowFlakeParticle;

        }
    }
}
