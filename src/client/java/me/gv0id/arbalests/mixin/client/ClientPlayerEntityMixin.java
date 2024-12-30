package me.gv0id.arbalests.mixin.client;

import me.gv0id.arbalests.mixin.PlayerEntityMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {

    @Inject(
            method = "tickMovement",
            at = @At("HEAD")
    )
    void tickMovementInject(CallbackInfo ci){
        if (this.Arbalest_JumpI && !this.isSwimming()){
            this.jump();
            this.arbalests_coyoteTime = 0;
            this.Arbalest_JumpI = false;
        }else if(this.arbalests_coyoteTime > 0 && !this.isSwimming() && !this.isOnGround() && this.jumping){
            this.jump();
            this.arbalests_coyoteTime = 0;
        }
    }
}
