package me.gv0id.arbalests.particle;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.Arbalests;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModParticles{


    public static final ModParticleType STRAFE = register("strafe", true);
    public static final ModParticleType SNOW_GUST = register("snow_gust", true);
    public static final ModParticleType ENDER_GUST = register("ender_gust", true);
    public static final ModParticleType FIRE_GUST = register("fire_gust", true);
    public static final ModParticleType LIGHT_FLASH = register("light_flash", true);
    public static final ModParticleType SMALL_LIGHT_FLASH = register("small_light_flash", true);
    public static final ModParticleType BOOM = register("boom", true);
    public static final ModParticleType RED_BOOM = register("red_boom", true);
    public static final ParticleType<StreakParticleEffect> STREAK = register("streak", true, StreakParticleEffect::createCodec, StreakParticleEffect::createPacketCodec);



    public static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Arbalests.MOD_ID, name), type);
    }

    private static <T extends ParticleEffect> ParticleType<T> register(
            String name,
            boolean alwaysShow,
            Function<ParticleType<T>, MapCodec<T>> codecGetter,
            Function<ParticleType<T>, PacketCodec<? super RegistryByteBuf, T>> packetCodecGetter
    ) {
        return Registry.register(Registries.PARTICLE_TYPE, Arbalests.identifierOf(name), new ParticleType<T>(alwaysShow) {
            @Override
            public MapCodec<T> getCodec() {
                return (MapCodec<T>)codecGetter.apply(this);
            }

            @Override
            public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
                return (PacketCodec<? super RegistryByteBuf, T>)packetCodecGetter.apply(this);
            }
        });
    }


    private static ModParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Arbalests.MOD_ID, name), new ModParticleType(alwaysShow));
    }


    public static void init() {

    }
}
