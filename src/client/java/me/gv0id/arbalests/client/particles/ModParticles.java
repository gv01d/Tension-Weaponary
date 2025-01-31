package me.gv0id.arbalests.client.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.GustParticle;

public class ModParticles {
    public static void initialization(){
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.STRAFE, GustParticle.SmallGustFactory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SNOW_GUST, SnowGustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.ENDER_GUST, EnderGustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FIRE_GUST, FireGustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.LIGHT_FLASH, LightFlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SMALL_LIGHT_FLASH, LightFlashParticle.SmallFactory::new);

        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.BOOM, BoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.RED_BOOM, BoomParticle.RedFactory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COLORED_BOOM, ColoredBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.ANGULAR_BOOM, AngularBoomlParticle.Factory::new);


        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.STREAK, StreaklParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COSMIC_BOOM, CosmicBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SMALL_COSMIC_BOOM, CosmicBoomParticle.SmallFactory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COSMIC_BOOM_EMITTER, new CosmicBoomEmiterParticle.Factory( 4, 6, 0));
    }

}
