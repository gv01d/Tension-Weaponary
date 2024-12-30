package me.gv0id.arbalests.mixin;

import me.gv0id.arbalests.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {

    @Unique
    LivingEntity entity;

    @Inject(method = "update", at = @At("HEAD"))
    void updateInject(LivingEntity entity, Runnable overwriteCallback, CallbackInfoReturnable<Boolean> cir){
        this.entity = entity;
    }

    @Final
    @Shadow
    private RegistryEntry<StatusEffect> type;

    @Inject(method = "updateDuration", at = @At("HEAD"), cancellable = true)
    void updateDurationInject(CallbackInfoReturnable<Integer> cir){
        if ( type.equals(ModEffects.STRAFE) &&
                !(
                    entity.isTouchingWater() ||
                    entity.isOnGround()  ||
                    entity.isGliding()||
                    entity.hasVehicle() ||
                    entity.isInSwimmingPose() ||
                    entity.isClimbing() ||
                    entity.isTouchingWater()  ||
                    entity.isOnGround() ||
                    (entity instanceof PlayerEntity player && player.getAbilities().flying)
                )
        )
        {
            cir.cancel();
        }
    }
}
