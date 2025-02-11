package me.gv0id.arbalests.particle;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.Arbalests;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModParticles{


    public static final ModParticleType STRAFE = register("strafe", true);
    public static final ModParticleType LIGHT_FLASH = register("light_flash", true);
    public static final ModParticleType SMALL_LIGHT_FLASH = register("small_light_flash", true);
    public static final ModParticleType BOOM = register("boom", true);
    public static final ModParticleType RED_BOOM = register("red_boom", true);

    public static final ModParticleType COSMIC_EXPANDING_BOOM = register("cosmic_expanding_boom", true);
    public static final ParticleType<AngularColoredParticleEffect> COSMIC_ANGULAR_BOOM = register("cosmic_angular_boom", true, AngularColoredParticleEffect::createCodec, AngularColoredParticleEffect::createPacketCodec);

    public static final ParticleType<ColoredParticleEffect> COLORED_BOOM = register("colored_boom", true, ColoredParticleEffect::createCodec, ColoredParticleEffect::createPacketCodec);
    public static final ParticleType<AngularColoredParticleEffect> STREAK = register("streak", true, AngularColoredParticleEffect::createCodec, AngularColoredParticleEffect::createPacketCodec);
    public static final ParticleType<AngularColoredParticleEffect> ANGULAR_BOOM = register("angular_boom", true, AngularColoredParticleEffect::createCodec, AngularColoredParticleEffect::createPacketCodec);

    // Snow Gust
    public static final ModParticleType SNOW_FLAKE = register("snow_flake", true);
    public static final ModParticleType SNOW_GUST = register("snow_gust", true);
    public static final ModParticleType SNOW_GUST_EMITTER = register("snow_gust_emitter", true);

    // Fire Gust
    public static final ModParticleType FIRE = register("fire", true);
    public static final ModParticleType FIRE_GUST = register("fire_gust", true);
    public static final ModParticleType FIRE_GUST_EMITTER = register("fire_gust_emitter", true);

    // Ender Gust
    public static final ModParticleType ENDER_GUST = register("ender_gust", true);
    public static final ModParticleType ENDER_GUST_EMITTER = register("ender_gust_emitter", true);

    // Shaded Cosmic End Crystal Explosion
    public static final ModParticleType COSMIC_BOOM = register("cosmic_boom", false);
    public static final ModParticleType SMALL_COSMIC_BOOM = register("small_cosmic_boom", true);
    public static final ModParticleType COSMIC_BOOM_EMITTER = register("cosmic_boom_emitter", true);

    // Trails
    public static final ParticleType<TrailParticleEffect> TRAIL = register("trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    // Double Trail
    public static final ParticleType<TrailParticleEffect> ENDER_TRAIL = register("ender_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    // Disc Trails
    public static final ParticleType<TrailParticleEffect> GENERIC_DISC_TRAIL = register("generic_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> COPPER_DISC_TRAIL = register("copper_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> D13_TRAIL = register("d13_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> D5_TRAIL = register("d5_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> CHIRP_TRAIL = register("chirp_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> CREATOR_TRAIL = register("creator_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> CREATOR_MUSIC_BOX_TRAIL = register("creator_music_box_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> FAR_TRAIL = register("far_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> MELLOHI_TRAIL = register("mellohi_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> OTHERSIDE_TRAIL = register("otherside_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> PIGSTEP_TRAIL = register("pigstep_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> PRECIPICE_TRAIL = register("precipice_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> RELIC_TRAIL = register("relic_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);
    public static final ParticleType<TrailParticleEffect> WARD_TRAIL = register("ward_disc_trail", true, TrailParticleEffect::createCodec, TrailParticleEffect::createPacketCodec);


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
