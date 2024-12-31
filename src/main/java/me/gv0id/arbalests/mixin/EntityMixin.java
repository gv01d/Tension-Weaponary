package me.gv0id.arbalests.mixin;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.projectile.SnowProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
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

    @Inject(method = "onExplodedBy", at = @At("TAIL"))
    public void onExplodedBy(@Nullable Entity entity, CallbackInfo ci){
        if ((Entity)(Object)this instanceof LivingEntity livingEntity){
            if (entity instanceof SnowProjectileEntity){
                livingEntity.setFrozenTicks(300);
            }
        }
    }
}
