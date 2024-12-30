package me.gv0id.arbalests.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;

public class ModParticleType extends SimpleParticleType{

    protected ModParticleType(boolean alwaysShow) {
        super(alwaysShow);
    }
}
