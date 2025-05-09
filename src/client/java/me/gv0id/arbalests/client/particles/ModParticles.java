package me.gv0id.arbalests.client.particles;

import me.gv0id.arbalests.client.particles.cosmic.*;
import me.gv0id.arbalests.client.particles.experimental.ExperimentalProjectileTrailParticle;
import me.gv0id.arbalests.client.particles.experimental.LazerBeamParticle;
import me.gv0id.arbalests.client.particles.fireball.*;
import me.gv0id.arbalests.client.particles.snow.SnowFlakeParticle;
import me.gv0id.arbalests.client.particles.snow.SnowGustEmitterParticle;
import me.gv0id.arbalests.client.particles.snow.SnowGustParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.client.particle.SpriteProvider;

public class ModParticles {

    static ProjectileTrailParticle.Factory defaultDiscFactory(SpriteProvider spriteProvider){
        return new ProjectileTrailParticle.Factory(spriteProvider, 0.5f, 0.0F, 1.0F, 0.0F , 8);
    }


    public static void initialization(){
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.STRAFE, GustParticle.SmallGustFactory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.LIGHT_FLASH, LightFlashParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SMALL_LIGHT_FLASH, LightFlashParticle.SmallFactory::new);

        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.BOOM, BoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.RED_BOOM, BoomParticle.RedFactory::new);

        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COSMIC_EXPANDING_BOOM, CosmicExpansionBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COSMIC_ANGULAR_BOOM, CosmicAngularBoomParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COLORED_BOOM, ColoredBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.ANGULAR_BOOM, AngularBoomlParticle.Factory::new);

        // Snow Gust
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SNOW_FLAKE, SnowFlakeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SNOW_GUST, SnowGustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SNOW_GUST_EMITTER, new SnowGustEmitterParticle.Factory(0, 3));

        // Fire Gust
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FIRE, FireParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FIRE_GUST, FireGustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FIRE_GUST_EMITTER, new FireGustEmitterParticle.Factory( 0, 3));

        // Ender Gust
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.ENDER_GUST, CosmicBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.ENDER_GUST_EMITTER, new EnderGustEmitterParticle.Factory( 0, 3));

        //
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.ENDER_TRAIL,
                (spriteProvider) -> new ShadedProjectileTrailParticle.Factory(spriteProvider, 0.3f, 0.0F, 1.0F, 0.0F , 14));

        //
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.STREAK, StreaklParticle.Factory::new);

        // Fire End Crystal Explosion
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FIRE_BOOM, FireBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FIRE_BOOM_EMITTER, new FireBoomEmitterParticle.Factory(0, 3));

        // Shaded Cosmic End Crystal Explosion
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COSMIC_BOOM, CosmicBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COSMIC_SPARK, CosmicBoomParticle.SparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SMALL_COSMIC_BOOM, CosmicBoomParticle.SmallFactory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COSMIC_BOOM_EMITTER, new CosmicBoomEmiterParticle.Factory( 5, 6, 0));

        // Sonic Boom
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SONIC_BOOM, AngularSonicBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.SONIC_BEAM,
                (spriteProvider) -> new LazerBeamParticle.Factory(spriteProvider, 0.5f, -1F, 1.0F, 0.0F , 5));

        // Disc Trails
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.GENERIC_DISC_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.COPPER_DISC_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.D13_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.D5_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.CHIRP_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.CREATOR_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.CREATOR_MUSIC_BOX_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.FAR_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.MELLOHI_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.OTHERSIDE_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.PIGSTEP_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.PRECIPICE_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.RELIC_TRAIL, ModParticles::defaultDiscFactory);
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.WARD_TRAIL, ModParticles::defaultDiscFactory);

        // Experimental
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.EXPERIMENTAL_TRAIL,
                (spriteProvider) -> new ExperimentalProjectileTrailParticle.Factory(spriteProvider, 0.2f, 0F, 1.0F, 0.1F , 10));

    }

}
