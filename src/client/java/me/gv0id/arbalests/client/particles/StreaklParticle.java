package me.gv0id.arbalests.client.particles;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.particle.StreakParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StreaklParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    float roll;
    float yaw;
    float pitch;

    Vec3d camera;

    protected StreaklParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 8;
        this.scale = 0.1F;
        float a = (float)Math.random() * (float) (Math.PI * 2);
        this.angle = a;
        this.prevAngle = a;
        this.setBoundingBoxSpacing(1.0F, 1.0F);
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }


    @Override
    public void tick() {
        //this.alpha -= 1F /this.maxAge;
        //this.scale += 0.02F * (this.scale * this.scale);
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        //super.render(vertexConsumer,camera,tickDelta);

        Quaternionf quaternionf = new Quaternionf();
        MatrixStack matrixStack = new MatrixStack();

        quaternionf = RotationAxis.POSITIVE_Y.rotationDegrees(this.yaw - 90.0F)
                .mul(RotationAxis.POSITIVE_Z.rotationDegrees(this.pitch + 90.0F))
                .mul(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));

        this.camera = camera.getPos();


        if (this.angle != 0.0F) {
            quaternionf.rotateZ(MathHelper.lerp(tickDelta, this.prevAngle, this.angle));
        }

        this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
    }

    @Override
    protected void method_60374(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i) {
        float j = this.getSize(i);
        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(i);

        this.method_60375(vertexConsumer, quaternionf, f, g, h, -1.0F, -1.0F, j, k, n, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, -1.0F, 1.0F, j, k, m, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, 1.0F, 1.0F, j, l, m, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, 1.0F, -1.0F, j, l, n, o);

        this.method_60375(vertexConsumer, quaternionf, f, g, h, 1.0F, -1.0F, j, l, n, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, 1.0F, 1.0F, j, l, m, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, -1.0F, 1.0F, j, k, m, o);
        this.method_60375(vertexConsumer, quaternionf, f, g, h, -1.0F, -1.0F, j, k, n, o);

    }

    private void method_60375(
            VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n
    ) {
        Vector3f vector3f = new Vector3f(i, j, 0.0F).rotate(quaternionf).mul(k).add(f, g, h);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(l, m).color(this.red, this.green, this.blue, this.alpha).light(n);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<StreakParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(StreakParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            StreaklParticle streakParticle = new StreaklParticle(clientWorld, d, e, f, this.spriteProvider);
            streakParticle.setColor(parameters.getRed(),parameters.getGreen(), parameters.getBlue());
            streakParticle.setAlpha(parameters.getAlpha());
            streakParticle.roll = parameters.getLerpedRoll(1);
            streakParticle.yaw = parameters.getLerpedYaw(1);
            streakParticle.pitch = parameters.getLerpedPitch(1);
            return streakParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class TestFactory implements ParticleFactory<SimpleParticleType>{
        private final SpriteProvider spriteProvider;

        public TestFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            StreaklParticle streaklParticle = new StreaklParticle(clientWorld, d, e, f, this.spriteProvider);
            return streaklParticle;
        }
    }



}
