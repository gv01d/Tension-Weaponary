package me.gv0id.arbalests.client.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.GustParticle;

public class ModParticles {
    public static void initialization(){
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.STRAFE, GustParticle.SmallGustFactory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SNOW_GUST, SnowGustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.ENDER_GUST, EnderGustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FIRE_GUST, FireGustParticle.Factory::new);
    }

}
