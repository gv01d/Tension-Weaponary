package me.gv0id.arbalests.client.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;

public class ModParticles {
    public static void initialization(){
        ParticleFactoryRegistry.getInstance().register(me.gv0id.arbalests.particle.ModParticles.STRAFE, GustParticle.SmallGustFactory::new);
    }

}
