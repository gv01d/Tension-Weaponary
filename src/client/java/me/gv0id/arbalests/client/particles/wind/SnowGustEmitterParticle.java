package me.gv0id.arbalests.client.particles.wind;

import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class SnowGustEmitterParticle extends NoRenderParticle {
    private final int interval;

    protected SnowGustEmitterParticle(ClientWorld clientWorld, double d, double e, double f, int interval, int maxAge) {
        super(clientWorld, d, e, f);
        this.maxAge = maxAge;
        this.interval = interval;
        this.world.addParticle(ModParticles.SNOW_GUST, d, e, f, 0.0, 0.0, 0.0);
        for (int i = 0; i < 20; i++) {
            this.world.addParticle(ModParticles.SNOW_FLAKE, this.x, this.y, this.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void tick() {
        for (int i = 0; i < 15; i++) {
            this.world.addParticle(ModParticles.SNOW_FLAKE, this.x, this.y, this.z, 0.0, 0.0, 0.0);
        }

        if (this.age++ == this.maxAge) {
            this.markDead();
        }
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final int interval;
        private final int maxAge;

        public Factory(int interval, int maxAge) {
            this.interval = interval;
            this.maxAge = maxAge;
        }


        @Override
        public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new SnowGustEmitterParticle(world, x, y, z, this.interval, this.maxAge);
        }
    }
}
