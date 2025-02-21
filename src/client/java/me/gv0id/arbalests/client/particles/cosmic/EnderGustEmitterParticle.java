package me.gv0id.arbalests.client.particles.cosmic;

import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class EnderGustEmitterParticle extends NoRenderParticle {
    private final int interval;

    protected EnderGustEmitterParticle(ClientWorld clientWorld, double d, double e, double f, int interval, int maxAge) {
        super(clientWorld, d, e, f);
        this.maxAge = maxAge;
        this.interval = interval;
        this.world.addParticle(ModParticles.ENDER_GUST, d, e, f, 0.0, 0.0, 0.0);
        for (int i = 0; i < 10; i++) {
            double td = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4;
            double te = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4;
            double tf = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4;
            this.world.addParticle(ModParticles.SMALL_COSMIC_BOOM, td, te, tf, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
        }

        for (int i = 0; i < 30; i++) {
            double td = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4;
            double te = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4;
            double tf = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4;
            this.world.addParticle(ParticleTypes.PORTAL, td, te, tf, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
        }

    }

    @Override
    public void tick() {
        for (int i = 0; i < 5; i++) {
            double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 3;
            double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 3;
            double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 3;
            this.world.addParticle(ModParticles.SMALL_COSMIC_BOOM, d, e, f, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
        }

        for (int i = 0; i < 15; i++) {
            double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 3;
            double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 3;
            double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 3;
            this.world.addParticle(ParticleTypes.PORTAL, d, e, f, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
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
            return new EnderGustEmitterParticle(world, x, y, z, this.interval, this.maxAge);
        }
    }
}
