package me.gv0id.arbalests.client.particles.experimental;

import me.gv0id.arbalests.particle.RecisableTrailParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class LazerBeamParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    Vec3d position;
    Vec3d prevPosition;

    float startGap;
    float endGap;
    float gap;
    float prevGap;

    float prevAlpha;
    float startAlpha;
    float endAlpha;

    float edgePorcentage;
    float maxEdgeSize;

    ArrayList<Vec3d> vertexPoint;
    ArrayList<Vec3d> positionList = new ArrayList<>();
    ArrayList<Float> tickDeltas = new ArrayList<>();

    protected LazerBeamParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 30;
        this.scale = 1.0F;
        this.setBoundingBoxSpacing(1.0F, 1.0F);

        this.edgePorcentage = 5;
        this.maxEdgeSize = 0.5F;
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void setGap(float start, float end){
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
        //this.world.addParticle(ParticleTypes.FLAME, this.x, this.y, this.z, 0, 0, 0);

        if (this.gap == this.maxAge - 1){
            this.gap = this.endGap;
        }
        else {
            this.gap = MathHelper.lerp(((float) this.age+1) / ((float) this.maxAge), this.startGap, this.endGap);
        }

        this.alpha = MathHelper.lerp(((float) this.age+1) / ((float) this.maxAge), this.startAlpha, this.endAlpha);

        //this.alpha -= 1F /this.maxAge;\
        this.age++;
        if (this.age >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        vertexPoint = new ArrayList<>();

        Vec3d pos = new Vec3d(this.x, this.y, this.z);

        double length = pos.subtract(this.position).length();
        double size = (edgePorcentage / 100) * length;
        if (size > maxEdgeSize){
            size = maxEdgeSize;
        }

        Vec3d vec3d = new Vec3d(this.x, this.y, this.z).subtract(position).normalize();

        if (this.positionList.isEmpty()){
            this.positionList.add(position);
            this.tickDeltas.add(0F);
            this.positionList.add(position.add(vec3d.multiply(size)));
            this.tickDeltas.add((float)(size / length));

            this.positionList.add(pos.subtract(vec3d.multiply(size)));
            this.tickDeltas.add((float)((1.0F) - (size / length)));
            this.positionList.add(pos);
            this.tickDeltas.add(1F);
        }


        renderTrailQuads(vertexConsumer, camera);


        //this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
    }

    protected void renderTrailQuads(VertexConsumer vertexConsumer, Camera camera){

        Vec3d cameraPos = camera.getPos();
        // Calculate the vertex points for the trail

        this.RotatePoints(vertexConsumer, cameraPos, 0, 0);
        this.RotatePoints(vertexConsumer, cameraPos, this.gap, 1);
        this.RotatePoints(vertexConsumer, cameraPos, this.gap, 2);
        this.RotatePoints(vertexConsumer, cameraPos, 0, 3);

        if (this.positionList.size() > 2){
            for (int i = 0; i < this.positionList.size() - 1; i++) {
                /*
                if (i != 1){
                    continue;
                }
                 */
                float k = this.getMinU();
                float l = this.getMaxU();
                float m = this.getMinV();
                float n = this.getMaxV();
                renderQuad(vertexConsumer,
                        this.vertexPoint.get(i * 2),
                        this.vertexPoint.get((i * 2) + 1),
                        this.vertexPoint.get((i * 2) + 2),
                        this.vertexPoint.get((i * 2) + 3),
                        k, l, MathHelper.lerp(this.tickDeltas.get(i),m,n), MathHelper.lerp(this.tickDeltas.get(i+1),m,n),
                        this.getBrightness(1F),
                        alpha, alpha
                );
            }
        }

    }

    protected void RotatePoints(VertexConsumer vertexConsumer, Vec3d cameraPos,float gap, int index){

        // Calculate the normal of the path for gap == 0
        if (gap == 0){
            vertexPoint.add((this.positionList.get(index)).subtract(cameraPos));
            vertexPoint.add((this.positionList.get(index)).subtract(cameraPos));
            return;
        }

        if (gap < 0){
            gap = -gap;
        }

        // Calculate the normal of the path
        Vec3d prevPos;
        if (index == 0){
            prevPos = this.prevPosition;
        }
        else {
            prevPos = this.positionList.get(index - 1);
        }
        Vec3d pos = this.positionList.get(index);

        Vec3d pathDirection = this.positionList.get(index).subtract(prevPos).normalize();

        Vec3d normal = pathDirection.crossProduct(cameraPos.subtract(pos.add(prevPos).multiply(0.5))).normalize();
        //normal = new Vec3d(1,0 ,0);

        vertexPoint.add(pos.add(
                normal.multiply(
                        gap
                )).subtract(cameraPos)
        );
        vertexPoint.add(pos.add(
                normal.multiply(
                        -gap
                )).subtract(cameraPos)
        );
    }

    protected void renderQuad(VertexConsumer vertexConsumer, Vec3d posA1, Vec3d posA2, Vec3d posB1, Vec3d posB2, float u1, float u2, float v1, float v2, int light, float alpha1 ,float alpha2){
        this.addVertex(vertexConsumer, posA2, u1, v1, light, alpha1);
        this.addVertex(vertexConsumer, posA1, u2, v1, light, alpha1);
        this.addVertex(vertexConsumer, posB1, u2, v2, light, alpha2);
        this.addVertex(vertexConsumer, posB2, u1, v2, light, alpha2);
    }

    private void addVertex(
            VertexConsumer vertexConsumer, Vec3d pos, float u, float v, int light, float alpha
    ){
        vertexConsumer.vertex((float) pos.x, (float) pos.y, (float) pos.z).texture(u, v).color(this.red, this.green, this.blue, alpha).light(light);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<RecisableTrailParticleEffect> {
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

        public Particle createParticle(RecisableTrailParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LazerBeamParticle lazerBeamParticle = new LazerBeamParticle(clientWorld, d, e, f, this.spriteProvider);
            lazerBeamParticle.setColor(parameters.getRed(),parameters.getGreen(), parameters.getBlue());
            lazerBeamParticle.position = parameters.getPos();
            lazerBeamParticle.prevPosition = parameters.getPrevPos();

            lazerBeamParticle.maxAge = parameters.age();
            lazerBeamParticle.setGap((this.startSize / 2) * parameters.getSize() , (this.endSize / 2) * parameters.getSize());
            lazerBeamParticle.setStartAlpha(this.startAlpha * parameters.getAlpha(), this.endAlpha * parameters.getAlpha());
            return lazerBeamParticle;
        }
    }
}
