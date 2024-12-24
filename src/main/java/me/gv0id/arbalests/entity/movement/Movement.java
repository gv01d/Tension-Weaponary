package me.gv0id.arbalests.entity.movement;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.util.math.Vec3d;

public class Movement {
    /**
     * <h1>
     *     THE SECRET SAUCE TO AIRSTRAFING
     * </h1>
     * <h2>
     *         Quake air acceleration function
     * </h2>
     * <h3>
     *  Input
     * </h3>
     * <ul>
     * <li>
     *      <h4>< Wish Velocity > (input "wishveloc" in Quake) :</h4>
     *      Expected movement direction based on camera rotation
     *      Length equal to "Max Speed"
     * </li>
     * <li>
     *      <h4>< Unchanged Wish Speed > (global "wishspeed" in Quake) :</h4>
     *      Wish speed value, in theory, unchanged
     * </li>
     * <li>
     *      <h4>< Velocity > (global "velocity" in Quake) :</h4>
     *      Actual entity velocity
     * </li>
     * <li>
     *      <h4>< Acceleration > (global "sv_accelerate.value" in Quake) :</h4>
     *      acceleration constant
     * </li>
     * </ul>
     * <h3>
     *  Local:
     * </h3>
     * <ul>
     * <li>
     *      <h4>< Wish Speed > (local "wishspd" in Quake) :</h4>
     *      Expected Wish Length, that means, probably, "Max Speed"
     * </li>
     * <li>
     *      <h4>< Projected Current Speed > (local "currentspeed" in Quake) :</h4>
     *      DotProduct of velocity and wishSpeed.
     *      Probably the projection of how much speed is needed to achieve WishSpeed ( "Max Speed" ).
     * </li>
     * <li>
     *      <h4>< Add Speed > (local "addspeed" in Quake) :</h4>
     *      Difference between WishSpeed (Capped to 30) and ProjectedCurrentSpeed.
     *      Represents the amount of speed that needs to be added to reach WishSpeed ( "Max Speed" ).
     * </li>
     * <li>
     *      <h4>< Acceleration Speed > (local "accelspeed" in Quake) :</h4>
     *      The product of Acceleration * UnchangedWishSpeed (And * "host_frametime" - DeltaTime/time between last frame - in Quake).
     *      Represents the max acceleration per frame - in this context PER TICK, since minecraft.
     *      <ul>
     *          <li>
     *          <h4>OBS:</h4>
     *          Since minecraft uses TICK BASED processing it is not necessary to use "DeltaTime" equivalent, making it so movement
     *          cant have absurd bursts of speed with higher or absurdly low fps.
     *          Everything is based on ticks, so if everything moves slower,you do to
     *          </li>
     *      </ul>
     * </li>
     * </ul>
    */
    public void airAccelerate(Vec3d wishVelocity, Vec3d velocity, double unchangedWishSpeed, double acceleration){
        double wishSpeed = wishVelocity.length();
        double projectedCurrentSpeed;
        double addSpeed;
        double accelSpeed;
        // - - -
        wishVelocity.normalize();
        if (wishSpeed > 30)
            wishSpeed = 30;
        // - - -
        projectedCurrentSpeed = velocity.dotProduct(wishVelocity);
        addSpeed = wishSpeed - projectedCurrentSpeed;
        if (addSpeed <= 0)
            return;
        // - - -
        accelSpeed = acceleration * unchangedWishSpeed;
        if (accelSpeed > addSpeed)
            accelSpeed = addSpeed;
        // - -
        wishVelocity.multiply(accelSpeed);
        velocity.add(wishVelocity);
    }
}
