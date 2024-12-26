package me.gv0id.arbalests.mixin;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.entity.movement.PlayerMovement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin{
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
