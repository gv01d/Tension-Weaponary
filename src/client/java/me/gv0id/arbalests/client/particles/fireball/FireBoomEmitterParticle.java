package me.gv0id.arbalests.client.particles.fireball;

import me.gv0id.arbalests.particle.AngularColoredParticleEffect;
import me.gv0id.arbalests.particle.ColoredParticleEffect;
import me.gv0id.arbalests.particle.ModParticles;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

public class FireBoomEmitterParticle extends NoRenderParticle {
    private final int interval;

    protected FireBoomEmitterParticle(ClientWorld clientWorld, double d, double e, double f, int interval, int maxAge) {
        super(clientWorld, d, e, f);
        this.maxAge = maxAge;
        this.interval = interval;
        this.world.addParticle(ModParticles.FIRE_BOOM, d, e, f, 0.0, 0.0, 0.0);
        if (this.world.raycast(
                new RaycastContext(new Vec3d(this.x,this.y,this.z),new Vec3d(this.x,this.y,this.z).add(0,-5,0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent())
        ).getType() == HitResult.Type.BLOCK){
            this.world.addParticle(AngularColoredParticleEffect.create(ModParticles.ANGULAR_BOOM, ColorHelper.fromFloats(1.0F,0.1F, 0.0F, 0.0F), Vec3d.ZERO, Vec3d.ZERO ), d, e, f, 0.0, 0.0, 0.0);
        }
        this.world.addParticle(ColoredParticleEffect.create(ModParticles.COLORED_BOOM, ColorHelper.fromFloats(1.0F,0.1F, 0.0F, 0.0F)), d, e, f, 0.0, 0.0, 0.0);
        for (int i = 0; i < 40; i++) {

            Vec3d r = new Vec3d (
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6)
            );

            this.world.addParticle(ModParticles.FIRE, this.x + r.x, this.y + r.y, this.z + r.z, r.x * 3, r.y * 3, r.z * 3);
            float rng = (this.random.nextFloat());
            if (rng > 0.5){
                this.world.addParticle(
                        ParticleTypes.SMOKE,
                        this.x + (this.random.nextDouble() - 0.5F),
                        this.y + (this.random.nextDouble() - 0.5F),
                        this.z + (this.random.nextDouble() - 0.5F),
                        (this.random.nextFloat() - 0.5F),
                        (this.random.nextFloat() - 0.5F),
                        (this.random.nextFloat() - 0.5F)
                );
            }
        }
    }

    @Override
    public void tick() {
        for (int i = 0; i < 35; i++) {
            Vec3d r = new Vec3d (
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6),
                    MathHelper.lerp(this.random.nextDouble(), -0.6, 0.6)
            );

            this.world.addParticle(ModParticles.FIRE, this.x + r.x, this.y + r.y, this.z + r.z, r.x * 3, r.y * 3, r.z * 3);

            float rng = (this.random.nextFloat());
            if (rng > 0.7){
                this.world.addParticle(
                        ParticleTypes.SMOKE,
                        this.x + (this.random.nextDouble() - 0.5F),
                        this.y + (this.random.nextDouble() - 0.5F),
                        this.z + (this.random.nextDouble() - 0.5F),
                        (this.random.nextFloat() - 0.5F) / 2,
                        (this.random.nextFloat() - 0.5F) / 2,
                        (this.random.nextFloat() - 0.5F) / 2
                );
            }
        }

        if (this.age++ == this.maxAge) {
            this.markDead();
        }
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final int interval;
        private final int maxAge;

        public Factory(int interval, int maxAge) {
            this.interval = interval;
            this.maxAge = maxAge;
        }


        @Override
        public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new FireBoomEmitterParticle(world, x, y, z, this.interval, this.maxAge);
        }
    }
}
