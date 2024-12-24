package me.gv0id.arbalests.mixin;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow protected boolean jumping;

    @Shadow public abstract void jump();

    @Override
    public void Arbalests_getJumpInject(){
        this.jump();
    }


    @Inject(
            method = "jump",
            at = @At("HEAD")
    )
    void jumpInjectHead(CallbackInfo ci){
        this.arbalests_coyoteTime = 0;
    }


    @Unique
    public boolean isJumping(EntityMixin entity){
        return this.jumping;
    }

    // Quake Style movement
    @Inject(method = "jump", at = @At("TAIL"))
    public void jumpInjectTail(CallbackInfo ci) {
        LivingEntity player = (LivingEntity)(Object)this;
        quakeInstance.afterJump(player);
    }
}


