package me.gv0id.arbalests.mixin;

import com.google.common.collect.Multimaps;
import com.mojang.authlib.GameProfile;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.effect.ModEffects;
import me.gv0id.arbalests.entity.attribute.ModEntityAttributes;
import me.gv0id.arbalests.registry.tag.ModEntityTypeTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "onExplodedBy", at = @At("TAIL"))
    void onExplodeResetFall(Entity entity, CallbackInfo ci){
        this.setIgnoreFallDamageFromCurrentExplosion(entity != null && entity.getType().isIn(ModEntityTypeTags.RESET_FALL));
        if (entity != null && entity.getType().isIn(ModEntityTypeTags.STRAFE_JUMP)){
            boolean gotIt = false;
            for (StatusEffectInstance inst : this.getStatusEffects()){
                if(inst.equals(ModEffects.STRAFE)){
                    gotIt = true;
                }
            }
            if (!gotIt) {
                this.addStatusEffect(new StatusEffectInstance(ModEffects.STRAFE,5,0,true,true,true));
            }
        }
    }
}