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
                if(inst.equals(ModEffects.Strafe)){
                    gotIt = true;
                }
            }
            if (!gotIt) {
                this.addStatusEffect(new StatusEffectInstance(ModEffects.Strafe,1200,0,true,true,true));
            }
        }
        /*
        if (entity != null && entity.getType().isIn(ModEntityTypeTags.STRAFE_JUMP) && this.getAttributes().hasAttribute(ModEntityAttributes.STRAFE_JUMP)){
            this.getAttributes().resetToBaseValue(ModEntityAttributes.STRAFE_JUMP);
            if (this.getAttributes().getValue(ModEntityAttributes.STRAFE_JUMP) < 1.0F){
                this.getAttributes().addTemporaryModifiers(
                        Multimaps.forMap(
                                new HashMap<>() {{
                                    put(
                                            ModEntityAttributes.STRAFE_JUMP,
                                            new EntityAttributeModifier(
                                                    Identifier.of(
                                                            "arbalets",
                                                            "enable_strafe"
                                                    ),
                                                    1.0F,
                                                    EntityAttributeModifier.Operation.ADD_VALUE
                                            )
                                    );
                                }}
                        )
                );
            }
            Arbalests.LOGGER.info("STRAFE_JUMP = {}",this.getAttributes().getValue(ModEntityAttributes.STRAFE_JUMP));
        }
         */
    }
}