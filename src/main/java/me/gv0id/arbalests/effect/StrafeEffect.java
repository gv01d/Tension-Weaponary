package me.gv0id.arbalests.effect;

import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class StrafeEffect extends StatusEffect {
    protected StrafeEffect(StatusEffectCategory category, int color) {
        super(category, color, ModParticles.STRAFE);
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        return super.applyUpdateEffect(world, entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return super.canApplyUpdateEffect(duration, amplifier);
    }
}
