package me.gv0id.arbalests.client.particles;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.particle.TrailParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class ProjectileTrailParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    float roll;
    float yaw;
    float pitch;

    float prevRoll;
    float prevYaw;
    float prevPitch;

    Vec3d position;
    Vec3d prevPosition;

    float startGap;
    float endGap;
    float gap;
    float prevGap;

    float prevAlpha;
    float startAlpha;
    float endAlpha;

    int index;

    ArrayList<Vec3d> prevPositions = new ArrayList<>();
    ArrayList<Quaternionf> angles = new ArrayList<>();
    ArrayList<Float> tickDeltas = new ArrayList<>();

    protected ProjectileTrailParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 8;
        this.scale = 1.0F;
        this.setBoundingBoxSpacing(1.0F, 1.0F);
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    public void setStartGap(float start, float end){
        this.startGap = start;
        this.endGap = end;
        this.gap = start;
        this.prevGap = start;
    }

    public void setStartAlpha(float start, float end){
        this.startAlpha = start;
        this.endAlpha = end;
        this.alpha = start;
        this.prevAlpha = start;
    }

    @Override
    public void tick() {
        this.prevGap = this.gap;
        this.prevAlpha = this.alpha;

        if (this.gap == this.maxAge - 1){
            this.gap = this.endGap;
        }
        else {
            this.gap = MathHelper.lerp(((float) this.age+1) / ((float) this.maxAge), this.startGap, this.endGap);
        }

        this.alpha = MathHelper.lerp(((float) this.age+1) / ((float) this.maxAge), this.startAlpha, this.endAlpha);

        //this.alpha -= 1F /this.maxAge;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    public void setStart(Vec3d pos, Vec3d angles){
        this.prevPosition = pos;

        Quaternionf quaternionf = RotationAxis.POSITIVE_Y.rotationDegrees((float) angles.y - 90.0F)
                .mul(RotationAxis.POSITIVE_Z.rotationDegrees((float) angles.z + 90.0F))
                .mul(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));

        this.angles.add(quaternionf);
        this.prevPositions.add(pos);
        this.tickDeltas.add(0.0F);
    }

    protected float getGap(float tickDelta){
        float headGap = this.startGap - (( this.startGap / Math.min(this.age + this.index, this.maxAge)) * Math.min(this.age, this.maxAge));
        //float tailGap = this.startGap - (( this.startGap / this.maxAge) * Math.min(Math.max(this.maxAge - this.index, 0) + this.age, this.maxAge));
        float tailGap = this.startGap - (( this.startGap / Math.min(this.age + this.index, this.maxAge)) * Math.min(this.age + 1, this.maxAge));
        return tickDelta == 0? tailGap : tickDelta == 1F? headGap : MathHelper.lerp(tickDelta, tailGap, headGap);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        //super.render(vertexConsumer,camera,tickDelta);

        if (this.age == 0){
            if (this.prevPositions.isEmpty()) {
                setStart(this.prevPosition, new Vec3d(this.prevRoll, this.prevYaw, this.prevPitch));
            }
            else if (this.tickDeltas.getLast() != tickDelta) {
                Quaternionf quaternionf = new Quaternionf();

                float yaw = MathHelper.lerp(tickDelta, this.prevYaw, this.yaw);
                float pitch = MathHelper.lerp(tickDelta, this.prevPitch, this.pitch);

                quaternionf = RotationAxis.POSITIVE_Y.rotationDegrees(yaw - 90.0F)
                        .mul(RotationAxis.POSITIVE_Z.rotationDegrees(pitch + 90.0F))
                        .mul(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
                float pX;
                float pY;
                float pZ;
                pX = (float) MathHelper.lerp(tickDelta, this.prevPosition.x, this.position.x);
                pY = (float) MathHelper.lerp(tickDelta, this.prevPosition.y, this.position.y);
                pZ = (float) MathHelper.lerp(tickDelta, this.prevPosition.z, this.position.z);
                Vec3d pos = new Vec3d(pX, pY, pZ);

                if (this.angle != 0.0F) {
                    quaternionf.rotateZ(MathHelper.lerp(tickDelta, this.prevAngle, this.angle));
                }

                this.angles.add(quaternionf);
                this.prevPositions.add(pos);
                this.tickDeltas.add(tickDelta);
            }
        }
        else if (!this.tickDeltas.isEmpty()  && this.tickDeltas.getLast() != 1){
            Quaternionf quaternionf = RotationAxis.POSITIVE_Y.rotationDegrees(this.yaw - 90.0F)
                    .mul(RotationAxis.POSITIVE_Z.rotationDegrees(this.pitch + 90.0F))
                    .mul(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));

            Vec3d pos = new Vec3d(this.position.x, this.position.y, this.position.z);

            if (this.angle != 0.0F) {
                quaternionf.rotateZ(MathHelper.lerp(1F, this.prevAngle, this.angle));
            }

            this.angles.add(quaternionf);
            this.prevPositions.add(pos);
            this.tickDeltas.add(1F);
        }

        renderTrailQuads(vertexConsumer, camera);


        //this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
    }
    protected void renderTrailQuads(VertexConsumer vertexConsumer, Camera camera){
        for (int i = 0; i < this.prevPositions.size() - 1; i++) {
            this.addQuad(vertexConsumer, camera, i);
        }
    }

    protected void addQuad(VertexConsumer vertexConsumer, Camera camera, int index){



        Vec3d pos1 = this.prevPositions.get(index);
        Vec3d pos2 = this.prevPositions.get(index + 1);
        Quaternionf angle1 = this.angles.get(index);
        Quaternionf angle2 = this.angles.get(index + 1);
        float tickDelta1 = this.tickDeltas.get(index);
        float tickDelta2 = this.tickDeltas.get(index + 1);

        float j = this.getSize(tickDelta1);
        float j2 = this.getSize(tickDelta2);
        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(tickDelta1);
        int o2 = this.getBrightness(tickDelta2);

        pos1 = pos1.subtract(camera.getPos());
        pos2 = pos2.subtract(camera.getPos());

        this.addVertex(vertexConsumer, angle1, (float) pos1.x, (float) pos1.y, (float) pos1.z, getGap(tickDelta1),0F, j, k, MathHelper.lerp(tickDelta1, n, m), o, MathHelper.lerp(tickDelta1, this.alpha, this.prevAlpha));
        this.addVertex(vertexConsumer, angle1, (float) pos1.x, (float) pos1.y, (float) pos1.z, -getGap(tickDelta1),0F, j, l, MathHelper.lerp(tickDelta1, n, m), o, MathHelper.lerp(tickDelta1, this.alpha, this.prevAlpha));
        this.addVertex(vertexConsumer, angle2, (float) pos2.x, (float) pos2.y, (float) pos2.z, -getGap(tickDelta2),0F, j2, l, MathHelper.lerp(tickDelta2, n, m), o2, MathHelper.lerp(tickDelta2, this.alpha, this.prevAlpha));
        this.addVertex(vertexConsumer, angle2, (float) pos2.x, (float) pos2.y, (float) pos2.z, getGap(tickDelta2),0F, j2, k, MathHelper.lerp(tickDelta2, n, m), o2, MathHelper.lerp(tickDelta2, this.alpha, this.prevAlpha));

        this.addVertex(vertexConsumer, angle2, (float) pos2.x, (float) pos2.y, (float) pos2.z, getGap(tickDelta2),0F, j2, k, MathHelper.lerp(tickDelta2, n, m), o2, MathHelper.lerp(tickDelta2, this.alpha, this.prevAlpha));
        this.addVertex(vertexConsumer, angle2, (float) pos2.x, (float) pos2.y, (float) pos2.z, -getGap(tickDelta2),0F, j2, l, MathHelper.lerp(tickDelta2, n, m), o2, MathHelper.lerp(tickDelta2, this.alpha, this.prevAlpha));
        this.addVertex(vertexConsumer, angle1, (float) pos1.x, (float) pos1.y, (float) pos1.z, -getGap(tickDelta1),0F, j, l, MathHelper.lerp(tickDelta1, n, m), o, MathHelper.lerp(tickDelta1, this.alpha, this.prevAlpha));
        this.addVertex(vertexConsumer, angle1, (float) pos1.x, (float) pos1.y, (float) pos1.z, getGap(tickDelta1),0F, j, k, MathHelper.lerp(tickDelta1, n, m), o, MathHelper.lerp(tickDelta1, this.alpha, this.prevAlpha));
    }

    private void addVertex(
            VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n , float alpha
    ){
        Vector3f vector3f = new Vector3f(i, j, 0.0F).rotate(quaternionf).mul(k).add(f, g, h);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(l, m).color(this.red, this.green, this.blue, alpha).light(n);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<TrailParticleEffect> {
        private final SpriteProvider spriteProvider;
        float startSize;
        float endSize;
        float startAlpha;
        float endAlpha;
        int maxAge;

        public Factory(SpriteProvider spriteProvider, float startSize, float endSize, float startAlpha, float endAlpha, int maxAge) {
            this.spriteProvider = spriteProvider;
            this.startSize = startSize;
            this.endSize = endSize;
            this.startAlpha = startAlpha;
            this.endAlpha = endAlpha;
            this.maxAge = maxAge;
        }

        public Particle createParticle(TrailParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ProjectileTrailParticle streakParticle = new ProjectileTrailParticle(clientWorld, d, e, f, this.spriteProvider);
            streakParticle.setColor(parameters.getRed(),parameters.getGreen(), parameters.getBlue());
            if (parameters.color() == ColorHelper.fromFloats(1F,1F,0F,0F)){
                Arbalests.LOGGER.info("Particle color received red");
            }
            streakParticle.roll = parameters.getRoll();
            streakParticle.yaw = parameters.getYaw();
            streakParticle.pitch = parameters.getPitch();
            streakParticle.prevRoll = parameters.getPrevRoll();
            streakParticle.prevYaw = parameters.getPrevYaw();
            streakParticle.prevPitch = parameters.getPrevPitch();
            streakParticle.position = parameters.getPos();
            streakParticle.prevPosition = parameters.getPrevPos();
            streakParticle.index = parameters.getIndex();

            streakParticle.maxAge = this.maxAge;
            streakParticle.setStartGap(this.startSize / 2, this.endSize / 2);
            streakParticle.setStartAlpha(this.startAlpha * parameters.getAlpha(), this.endAlpha * parameters.getAlpha());
            return streakParticle;
        }
    }
}
