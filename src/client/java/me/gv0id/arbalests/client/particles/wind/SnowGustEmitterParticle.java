package me.gv0id.arbalests.client.particles.wind;

import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.world.ClientWorld;

public class SnowGustEmitterParticle extends NoRenderParticle {
    private final int interval;

    protected SnowGustEmitterParticle(ClientWorld clientWorld, double d, double e, double f, int interval) {
        super(clientWorld, d, e, f);
        this.interval = interval;
    }

    @Override
    public void tick() {
        if (this.age % (this.interval + 1) == 0) {
            for (int i = 0; i < 5; i++) {
                double d = this.x + (this.random.nextDouble() - this.random.nextDouble());
                double e = this.y + (this.random.nextDouble() - this.random.nextDouble());
                double f = this.z + (this.random.nextDouble() - this.random.nextDouble());
                this.world.addParticle(ModParticles.SMALL_COSMIC_BOOM, d, e, f, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
            }
        }

        if (this.age++ == this.maxAge) {
            this.markDead();
        }
    }
}
