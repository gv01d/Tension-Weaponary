package me.gv0id.arbalests.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin{
    @Shadow public abstract boolean isOnGround();

    @Unique
    public int arbalests_coyoteTime;
    @Unique
    public boolean Arbalest_JumpI;

    @Inject(method = "setMovement(ZZLnet/minecraft/util/math/Vec3d;)V",
            at = @At("TAIL")
    )
    void setMovementInject(boolean onGround, boolean horizontalCollision, Vec3d movement, CallbackInfo ci){

        if (onGround) {
            arbalests_coyoteTime = 0;
        }
        else if (arbalests_coyoteTime > 0){
            arbalests_coyoteTime--;
        }
    }
}
