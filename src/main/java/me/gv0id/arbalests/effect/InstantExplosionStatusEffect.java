package me.gv0id.arbalests.effect;

import me.gv0id.arbalests.entity.projectile.WindGaleEntity;
import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;


public class InstantExplosionStatusEffect extends StatusEffect {
    public InstantExplosionStatusEffect(StatusEffectCategory statusEffectCategory, int color ) {
        super(statusEffectCategory, color);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 1;
    }

    @Override
    public void applyInstantEffect(ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        super.applyInstantEffect(world, effectEntity, attacker, target, amplifier, proximity);


        WindChargeEntity windCharge;
        WindGaleEntity windGale;
        if (attacker != null){
            windGale = new WindGaleEntity(attacker, world, target.getBlockX(), target.getBlockY(), target.getBlockZ(), Vec3d.ZERO,1.4F,1.0F);
        }
        else {
            windGale = new WindGaleEntity(world, target.getBlockX(), target.getBlockY(), target.getBlockZ(), Vec3d.ZERO,1.4F,1.0F);
        }
        Vec3d offset = Vec3d.ZERO;
        if (target.getVelocity().lengthSquared() > 0){
            offset = target.getVelocity();
            Vec2f temp = new Vec2f((float)offset.x,(float)offset.z);
            temp = temp.normalize().negate();
            offset = new Vec3d(temp.x,0,temp.y);
        }

        windGale.createExplosion(target.getPos().add(offset));

        /*
        world.createExplosion(
                windGale,
                null,
                EXPLOSION_BEHAVIOR,
                target.getBlockX() + offset.x,
                target.getBlockY() + offset.y,
                target.getBlockZ() + offset.z,
                5F,
                false,
                World.ExplosionSourceType.TRIGGER,
                ParticleTypes.GUST_EMITTER_SMALL,
                ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
            );
         */
    }
}
