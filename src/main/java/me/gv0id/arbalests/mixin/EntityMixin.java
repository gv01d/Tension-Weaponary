package me.gv0id.arbalests.mixin;


import me.gv0id.arbalests.entity.projectile.SnowProjectileEntity;
import me.gv0id.arbalests.helper.EntityInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
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
public abstract class EntityMixin implements EntityInterface {

    @Unique
    boolean tagged;

    @Unique
    boolean justTagged;

    @Override
    public boolean arbalests$isTagged(){
        if (this.world.isClient) {
            return this.tagged;
        }
        return false;
    }

    @Override
    public void arbalests$setTag(boolean b){
        if (this.world.isClient) {
            this.tagged = b;
            this.justTagged = b;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci){
        if (justTagged){
            justTagged = false;
        } else if (tagged) {
            tagged = false;
        }
    }

    @Shadow public abstract boolean isOnGround();

    @Shadow private World world;
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
