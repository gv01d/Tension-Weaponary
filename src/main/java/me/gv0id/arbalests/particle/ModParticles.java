package me.gv0id.arbalests.particle;

import me.gv0id.arbalests.Arbalests;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles{


    public static final ModParticleType STRAFE = register("strafe", true);


    //public static final ParticleType<SimpleParticleType> STRAFE = register("strafe", FabricParticleTypes.simple());

    public static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Arbalests.MOD_ID, name), type);
    }


    private static ModParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Arbalests.MOD_ID, name), new ModParticleType(alwaysShow));
    }


    public static void init() {

    }
}
