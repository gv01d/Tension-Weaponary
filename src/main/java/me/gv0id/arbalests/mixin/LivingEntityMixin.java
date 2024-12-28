package me.gv0id.arbalests.mixin;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow protected boolean jumping;

    @Shadow public abstract void jump();

    @Shadow public float lastHandSwingProgress;

    @Override
    public void Arbalests_getJumpInject(){
        this.jump();
    }

    public Vec2f jB4Vel = Vec2f.ZERO;

    @Unique
    public Vec2f preJumpVel = Vec2f.ZERO;
    @Inject(
            method = "jump",
            at = @At("HEAD")
    )
    void jumpInjectHead(CallbackInfo ci){
        LivingEntity player = (LivingEntity)(Object)this;
        this.arbalests_coyoteTime = 0;
        jB4Vel = new Vec2f((float)player.getVelocity().x,(float)player.getVelocity().z);
    }


    @Unique
    public boolean isJumping(EntityMixin entity){
        return this.jumping;
    }

    @Unique
    public Vec2f lastNonRecVel = Vec2f.ZERO;
    @Unique
    public Vec2f jumpForce = Vec2f.ZERO;
    @Unique
    public Vec2f lastVel = Vec2f.ZERO;

    // Quake Style movement
    @Inject(method = "jump", at = @At("TAIL"))
    public void jumpInjectTail(CallbackInfo ci) {
        LivingEntity player = (LivingEntity)(Object)this;
        jumpForce = jumpForce.add(new Vec2f((float)player.getVelocity().x - jB4Vel.x,(float)player.getVelocity().z - jB4Vel.y));
    }

    @Invoker("getMovementSpeed")
    public abstract float callGetMovementSpeed(float slipperiness);

}


