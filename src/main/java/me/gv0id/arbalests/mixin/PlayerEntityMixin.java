package me.gv0id.arbalests.mixin;

import com.google.common.collect.Multimaps;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.effect.ModEffects;
import me.gv0id.arbalests.entity.attribute.ModEntityAttributes;
import net.minecraft.block.Blocks;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Predicate;

import static java.lang.Math.*;
import static java.lang.Math.PI;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    @Shadow
    public abstract boolean isSwimming();
    @Shadow
    public abstract void setIgnoreFallDamageFromCurrentExplosion(boolean ignoreFallDamageFromCurrentExplosion);

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Unique
    float maxAirVelocity = 40;
    @Unique
    float airAcceleration = 2;

    @Unique
    public float quakeStrafeGracePeriod = 0;
    @Unique
    public float QUAKE_STRAFE_GRACE_PERIOD = 5;



    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void createPlayerAttributesMixin(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir){
        cir.setReturnValue(cir.getReturnValue().add(ModEntityAttributes.STRAFE_JUMP,0.0));
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travelInject(Vec3d movementInput, CallbackInfo ci) {
        if (this.travel(movementInput)) {
            ci.cancel();
        }
        else{
            if (quakeStrafeGracePeriod > 0){
                quakeStrafeGracePeriod--;
            }
            else{
                PlayerEntity player = (PlayerEntity) (Object) this; // TODO : REMOVE THIS AND OTHERS SIMILAR
                if(quakeStrafeGracePeriod > -10){


                    player.getStatusEffects().removeIf(inst -> inst.equals(ModEffects.Strafe) && inst.isAmbient());

                    quakeStrafeGracePeriod = -10;
                }

                /*
                if(player.getWorld().isClient && player.getAttributes().hasAttribute(ModEntityAttributes.STRAFE_JUMP)){
                    if (player.getAttributes().getValue(ModEntityAttributes.STRAFE_JUMP) > 0.0F && quakeStrafeGracePeriod > -9){
                        player.getAttributes().removeModifiers(
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
                        quakeStrafeGracePeriod = -10;
                        Arbalests.LOGGER.info("STRAFE_JUMP negation = {}",player.getAttributes().getValue(ModEntityAttributes.STRAFE_JUMP));
                    }
                }

                 */
            }
        }
    }

    @Unique
    public boolean travel(Vec3d movementInput){

        PlayerEntity player = (PlayerEntity)(Object)this;
        /*
        if(!player.getAttributes().hasAttribute(ModEntityAttributes.STRAFE_JUMP)){
            return false;
        }
        if (player.getAttributes().getValue(ModEntityAttributes.STRAFE_JUMP) < 1.0F){
            return false;
        }
         */





        if (player.isGliding()||
            player.getAbilities().flying ||
            player.hasVehicle() ||
            player.isInSwimmingPose() ||
            player.isClimbing() ||
            player.isTouchingWater()  ||// No custom movement when touching water
            player.isOnGround()
        ) {
            return false;
        }
        boolean gotIt = false;
        for (StatusEffectInstance inst : player.getStatusEffects()){
            if(inst.equals(ModEffects.Strafe)){
                gotIt = true;
            }
        }

        quakeStrafeGracePeriod = QUAKE_STRAFE_GRACE_PERIOD;
        if(!gotIt || !player.getWorld().isClient){
            return false;
        }

        double preX = player.getX();
        double preY = player.getY();
        double preZ = player.getZ();


        return travelQuake(player,movementInput);
    }

    @Unique
    public boolean travelQuake(PlayerEntity player, Vec3d movementInput){

        // Vanilla slipperiness
        BlockPos blockPos = player.getVelocityAffectingPos();
        float f = player.isOnGround() ? player.getWorld().getBlockState(blockPos).getBlock().getSlipperiness() : 1.0F;
        float g = f * 0.91F;
        // - - -

        // Vanilla velocity
        //player.updateVelocity(this.callGetMovementSpeed(f), movementInput);
        float moveSpeed = this.callGetMovementSpeed(f);
        maxAirVelocity = (moveSpeed / (1.0F - g)) + moveSpeed;
        //


        Vec2f tempVel = new Vec2f((float)player.getVelocity().x,(float)player.getVelocity().z);

        // Quake movement prep
        Vec2f wishVelocity = getMovementDirection(player,new Vec2f((float)movementInput.x,(float)movementInput.z));
        double wishSpeed = new Vec2f(maxAirVelocity,maxAirVelocity).length();

        //
        Vec2f vel = Vec2f.ZERO;
        vel = airAccelerate(wishVelocity,wishSpeed,tempVel,wishSpeed,new Vec2f(1.0f - g, 1.0f - g).length());
        player.setVelocity(tempVel.x + vel.x, player.getVelocity().y, tempVel.y + vel.y);
        // - - -


        player.move(MovementType.SELF, player.getVelocity());

        applyGravity(player);

        return true;
    }

    @Unique
    public Vec2f airAccelerate( Vec2f wishVelocity, double wishSpeedInit, Vec2f velocity, double unchangedWishSpeed, double acceleration){

        double wishSpeed = wishSpeedInit;
        wishSpeed = min(wishSpeed, new Vec2f(maxAirVelocity,maxAirVelocity).length()*0.5);
        double projectedCurrentSpeed;
        double addSpeed;
        double accelSpeed;
        // - - -
        projectedCurrentSpeed = (velocity.x * wishVelocity.x + velocity.y * wishVelocity.y);
        addSpeed = wishSpeed - projectedCurrentSpeed;
        if (addSpeed <= 0)
            return Vec2f.ZERO;
        // - - -
        accelSpeed = acceleration * unchangedWishSpeed;
        if (accelSpeed > addSpeed)
            accelSpeed = addSpeed;
        // - -
        return new Vec2f((float )(wishVelocity.x * accelSpeed),(float)(wishVelocity.y * accelSpeed));

    }

    @Unique
    private Vec2f getMovementDirection(PlayerEntity player, Vec2f movementInput){
        float lenght = movementInput.length();
        lenght = lenght < 1.0F ? 1.0F : 1.0F / lenght;

        Vec2f mov = new Vec2f(movementInput.y * lenght,movementInput.x * lenght);
        float s = (float) sin(player.getYaw() * PI / (double) 180 );
        float c = (float) cos(player.getYaw() * PI / (double) 180 );

        return new Vec2f(
                mov.y * c - mov.x * s,
                mov.x * c + mov.y * s
        );
    }

    @Unique
    public void applyGravity(PlayerEntity player){
        boolean isLevitating = player.hasStatusEffect(StatusEffects.LEVITATION);
        double ySpeed = player.getVelocity().y;
        double gravity = -(player.hasStatusEffect(StatusEffects.SLOW_FALLING) ? Math.min(player.getFinalGravity(), 0.01) : player.getFinalGravity());

        if ((player.horizontalCollision)
                && (player.isClimbing() || player.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW)
                && PowderSnowBlock.canWalkOnPowderSnow(player))){
            ySpeed += 0.2;
        }
        if (player.hasStatusEffect(StatusEffects.SLOW_FALLING)){
            player.onLanding();
        }
        if (isLevitating){
            ySpeed += ((double)(0.05 * (Objects.requireNonNull(player.getStatusEffect(StatusEffects.LEVITATION)).getAmplifier() + 1)) - ySpeed) * 0.2;
            player.onLanding();
        }

        if (!player.getWorld().isClient ||
                player.getWorld().getChunkManager().isChunkLoaded(
                        ChunkSectionPos.getSectionCoord(
                                player.getBlockPos().getX()),
                        ChunkSectionPos.getSectionCoord(
                                player.getBlockPos().getZ())
                )
        ){
            if (!player.hasNoGravity() && !isLevitating)
                ySpeed += gravity;

            double airResistance = 0.9800000190734863;
            ySpeed *= airResistance;
        }
        else{
            ySpeed = player.getY() > player.getWorld().getBottomY() ? -0.1 : 0.0;
        }
        player.setVelocity(new Vec3d(
                player.getVelocity().x,
                ySpeed,
                player.getVelocity().z
        ));

    }
}
