package me.gv0id.arbalests.client.particles.cosmic;

import me.gv0id.arbalests.particle.ModParticles;
import me.gv0id.arbalests.particle.AngularColoredParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class CosmicBoomEmiterParticle extends NoRenderParticle {
    private final double deviation;
    private final int interval;



    CosmicBoomEmiterParticle(ClientWorld world, double x, double y, double z, double deviation, int maxAge, int interval) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.deviation = deviation;
        this.maxAge = maxAge;
        this.interval = interval;
        this.world.addParticle(ModParticles.COSMIC_EXPANDING_BOOM, x, y, z, 0.0, 0.0, 0.0);
        this.world.addParticle(ModParticles.COSMIC_BOOM, x, y, z, 0.0, 0.0, 0.0);
        this.world.addParticle(AngularColoredParticleEffect.create(ModParticles.COSMIC_ANGULAR_BOOM, 0.838F,0.834F, 1F, 0F,0F,0F,0F,0F,0F), x, y, z, 0.0, 0.0, 0.0);
    }

    @Override
    public void tick() {
        if (this.age % (this.interval + 1) == 0) {
            for (int i = 0; i < 16; i++) {
                float mult = this.random.nextDouble() > 0.8f ? 3.0f : 1.0f;
                double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * (this.deviation + mult);
                double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * (this.deviation + mult);
                double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * (this.deviation + mult);

                this.world.addParticle(ModParticles.SMALL_COSMIC_BOOM, d, e, f, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
            }
        }

        if (this.age++ == this.maxAge) {
            this.markDead();
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final double deviation;
        private final int maxAge;
        private final int interval;

        public Factory(double deviation, int maxAge, int interval) {
            this.deviation = deviation;
            this.maxAge = maxAge;
            this.interval = interval;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new CosmicBoomEmiterParticle(clientWorld, d, e, f, this.deviation, this.maxAge, this.interval);
        }
    }
}
