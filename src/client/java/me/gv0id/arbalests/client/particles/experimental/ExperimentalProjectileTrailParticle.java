package me.gv0id.arbalests.client.particles.experimental;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.particle.RecisableTrailParticleEffect;
import me.gv0id.arbalests.particle.TrailParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class ExperimentalProjectileTrailParticle extends SpriteBillboardParticle {
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


    int index;

    ArrayList<Vec3d> vertexPoint;
    ArrayList<Vec3d> positionList = new ArrayList<>();
    ArrayList<Float> tickDeltas = new ArrayList<>();

    protected ExperimentalProjectileTrailParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 30;
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

    public void setStart(Vec3d pos){
        this.positionList.add(new Vec3d(pos.x,pos.y,pos.z));
        this.tickDeltas.add(0.0F);
    }

    protected float headDelta(){
        float amount =  Math.min(this.age + this.index + 1, this.maxAge);
        float pos = Math.min(this.age, amount);
        return pos / amount;
    }

    protected float tailDelta(){
        float amount =  Math.min(this.age + this.index + 1, this.maxAge);
        float pos = Math.min(this.age, amount);
        return (pos + 1) / amount;
    }

    public float getAlpha(float tickDelta){
        float halpha = MathHelper.lerp(headDelta(), this.startAlpha, this.endAlpha);
        float talpha = MathHelper.lerp(tailDelta(), this.startAlpha, this.endAlpha);

        return tickDelta == 0? talpha : tickDelta == 1F? halpha : MathHelper.lerp(tickDelta, talpha, halpha);
    }


    public float getGap(float tickDelta){
        float hgap = MathHelper.lerp(headDelta(), this.startGap, this.endGap);
        float tgap = MathHelper.lerp(tailDelta(), this.startGap, this.endGap);

        return tickDelta == 0? tgap : tickDelta == 1F? hgap : MathHelper.lerp(tickDelta, tgap, hgap);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        //super.render(vertexConsumer,camera,tickDelta);
        vertexPoint = new ArrayList<>();
        if (this.age == 0 && (this.tickDeltas.isEmpty() || tickDelta >= this.tickDeltas.getLast())) // Create trail points on 1st tick
        {
            if (this.positionList.isEmpty()) // Ensure 1st point is set to the starting position
            {
                setStart(this.position);
            } else if (this.tickDeltas.getLast() != tickDelta) // Ensure there is no repetition of the same point and creates all other points
            {
                Vec3d pos = new Vec3d(
                        MathHelper.lerp(tickDelta, this.position.x, this.x),
                        MathHelper.lerp(tickDelta, this.position.y, this.y),
                        MathHelper.lerp(tickDelta, this.position.z, this.z)
                );

                this.positionList.add(pos);
                this.tickDeltas.add(tickDelta);
            }
        } else if (!this.tickDeltas.isEmpty() && this.tickDeltas.getLast() != 1) // Ensure the last point is set to the ending position
        {
            this.positionList.add(new Vec3d(this.x, this.y, this.z));
            this.tickDeltas.add(1F);
        }

        renderTrailQuads(vertexConsumer, camera);


        //this.method_60373(vertexConsumer, camera, quaternionf, tickDelta);
    }
    protected void renderTrailQuads(VertexConsumer vertexConsumer, Camera camera){

        Vec3d cameraPos = camera.getPos();
        // Calculate the vertex points for the trail
        for (int i = 0; i < this.positionList.size(); i++) {
            this.RotatePoints(vertexConsumer, cameraPos, i);
        }

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
                        this.getBrightness(this.tickDeltas.get(i)),
                        getAlpha(this.tickDeltas.get(i)), getAlpha(this.tickDeltas.get(i+1))
                );
            }
        }

    }

    protected void renderQuad(VertexConsumer vertexConsumer, Vec3d posA1, Vec3d posA2, Vec3d posB1, Vec3d posB2, float u1, float u2, float v1, float v2, int light, float alpha1 ,float alpha2){
        this.addVertex(vertexConsumer, posA2, u1, v1, light, alpha1);
        this.addVertex(vertexConsumer, posA1, u2, v1, light, alpha1);
        this.addVertex(vertexConsumer, posB1, u2, v2, light, alpha2);
        this.addVertex(vertexConsumer, posB2, u1, v2, light, alpha2);
    }

    protected void RotatePoints(VertexConsumer vertexConsumer, Vec3d cameraPos, int index){

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

        float gapSize = getGap(this.tickDeltas.get(index)) * this.getSize(this.tickDeltas.get(index));

        vertexPoint.add(pos.add(
                normal.multiply(
                        gapSize
                )).subtract(cameraPos)
        );
        vertexPoint.add(pos.add(
                normal.multiply(
                        -gapSize
                )).subtract(cameraPos)
        );
    }

    private void addVertex(
            VertexConsumer vertexConsumer, Vec3d pos, float u, float v, int light, float alpha
    ){
        vertexConsumer.vertex((float) pos.x, (float) pos.y, (float) pos.z).texture(u, v).color(this.red, this.green, this.blue, alpha).light(light);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
            ExperimentalProjectileTrailParticle streakParticle = new ExperimentalProjectileTrailParticle(clientWorld, d, e, f, this.spriteProvider);
            streakParticle.setColor(parameters.getRed(),parameters.getGreen(), parameters.getBlue());
            streakParticle.position = parameters.getPos();
            streakParticle.prevPosition = parameters.getPrevPos();
            streakParticle.index = parameters.getIndex();

            streakParticle.maxAge = parameters.age();
            streakParticle.setStartGap((this.startSize / 2) * parameters.getSize() , (this.endSize / 2) * parameters.getSize());
            streakParticle.setStartAlpha(this.startAlpha * parameters.getAlpha(), this.endAlpha * parameters.getAlpha());
            return streakParticle;
        }
    }
}
