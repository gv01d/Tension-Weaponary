package me.gv0id.arbalests.mixin;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.movement.PlayerMovement;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private boolean onGround;

    @Shadow public abstract boolean isOnGround();

    @Shadow public float fallDistance;

    @Unique
    public int arbalests_coyoteTime;
    @Unique
    public boolean Arbalest_JumpI;
    @Unique
    void Arbalests_getJumpInject(){
    }

    @Inject(method = "setMovement(ZZLnet/minecraft/util/math/Vec3d;)V",
            at = @At("TAIL")
    )
    void setMovementInject(boolean onGround, boolean horizontalCollision, Vec3d movement, CallbackInfo ci){

        if (onGround) {
            arbalests_coyoteTime = 5;
        }
        else if (arbalests_coyoteTime > 0){
            arbalests_coyoteTime--;
        }
    }

    /*
    @Inject(
            method = "fall",
            at = @At("HEAD")
    )
    void fallInject(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci){
        Arbalests.LOGGER.info("fI - FALLEN {} , Zdif {}", this.fallDistance, heightDifference);
    }
    */


    // - - -

    @Unique
    public PlayerMovement quakeInstance = new PlayerMovement();

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    public void updateVelocityInject(float speed, Vec3d movementInput, CallbackInfo ci) {
        Entity player = (Entity)(Object)this;
        if (quakeInstance.updateVelocity(player, speed, movementInput)) {
            ci.cancel();
        }
    }

}
