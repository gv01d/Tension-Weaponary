package me.gv0id.arbalests.client.render.entity;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.client.render.entity.model.EndCrystalProjectilEntityModel;
import me.gv0id.arbalests.client.render.entity.model.HighlightModels;
import me.gv0id.arbalests.client.render.entity.state.EndCrystalProjectileEntityRenderState;
import me.gv0id.arbalests.entity.projectile.EndCrystalProjectileEntity;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class EndCrystalProjectileEntityRenderer extends EntityRenderer<EndCrystalProjectileEntity, EndCrystalProjectileEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
    private static final RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private final EndCrystalProjectilEntityModel model;


    public final ModelPart highlight;

    public EndCrystalProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.model = new EndCrystalProjectilEntityModel(context.getPart(EntityModelLayers.END_CRYSTAL));
        this.highlight = HighlightModels.getSpecialTexturedModelData();
    }

    public void render(EndCrystalProjectileEntityRenderState endCrystalProjectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.translate(0.0F, -1.2F, 0.0F);



        this.model.setAngles(endCrystalProjectileEntityRenderState);
        this.model.render(matrixStack, vertexConsumerProvider.getBuffer(END_CRYSTAL), i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        /*
        Vec3d vec3d = endCrystalProjectileEntityRenderState.beamOffset;
        if (vec3d != null) {
            float f = getYOffset(endCrystalProjectileEntityRenderState.age);
            float g = (float)vec3d.x;
            float h = (float)vec3d.y;
            float j = (float)vec3d.z;
            matrixStack.translate(vec3d);
            EnderDragonEntityRenderer.renderCrystalBeam(-g, -h + f, -j, endCrystalProjectileEntityRenderState.age, matrixStack, vertexConsumerProvider, i);
        }
        */

        if (!endCrystalProjectileEntityRenderState.invisible)
            super.render(endCrystalProjectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
        else {
            Arbalests.LOGGER.info("HEH");
        }

        Camera camera = this.dispatcher.camera;
        float mod = 0.8f;
        float p = (float) Math.sin(endCrystalProjectileEntityRenderState.age * mod);
        p *= 0.1F;
        float m = -camera.getYaw();

        float alpha = 1.0F;
        float r = 1.0F;
        float g = 1.0F;
        float b = 1.0F;

        int col;
        if (EndCrystalProjectileEntityRenderState.owner instanceof ClientPlayerEntity){
            col = ColorHelper.fromFloats(0.2f,1F,1F,1F);
            alpha = 0.2f;
            r = 1F;
            g = 1F;
            b = 1F;
        }
        else {
            col = ColorHelper.fromFloats(0.6f,1F,0.180F,0.180F);
            alpha = 0.6f;
            r = 1F;
            g = 0.180F;
            b = 0.180F;
        }
        matrixStack.push();
        matrixStack.translate(0,0.6F,0);
        matrixStack.scale(1.4F , 1.4F, 1.4F);
        matrixStack.multiply(new Quaternionf().rotationYXZ(m * (float) (Math.PI / 180.0), camera.getPitch() * (float) (Math.PI / 180.0), (float)Math.PI));

        this.highlight.render(
                matrixStack,
                vertexConsumerProvider.getBuffer(RenderLayer.getTranslucentParticle(HighlightModels.SPECIAL_HIGHLIGHT_TEXTURE)),
                15728880,
                OverlayTexture.DEFAULT_UV,
                ColorHelper.fromFloats(alpha, r, g, b)
        );
        matrixStack.pop();

        int maxFuse = 600;
        float maxScale = 2.0F;
        //float maxRotation = 360F;



        if (EndCrystalProjectileEntityRenderState.fuse < 60) {
            if (EndCrystalProjectileEntityRenderState.fuse < 20) {
                r = 1F;
                alpha = 1.0F;
                g = 0F;
                b = 0F;
            }
            else {
                r = 1F;
                b = 0F;
                alpha = MathHelper.lerp(((float) EndCrystalProjectileEntityRenderState.fuse - 20F) / 40F , 1.0F, 0.6F);
                g = MathHelper.lerp(((float) EndCrystalProjectileEntityRenderState.fuse - 20F) / 40F , 0.0F, 1.0F);
            }
        }
        else if(EndCrystalProjectileEntityRenderState.fuse < 120){
            r = 1F;
            g = 1F;
            alpha = MathHelper.lerp((float) (EndCrystalProjectileEntityRenderState.fuse - 60) / 60F, 0.6F, 0.2F);
            b = MathHelper.lerp((float) (EndCrystalProjectileEntityRenderState.fuse - 60) / 60F, 0.0F, 1.0F);
        }
        else {
            r = 1F;
            g = 1F;
            b = 1F;
            alpha = 0.2F;
        }

        float scale = (maxScale) * (float)Math.pow(maxFuse - EndCrystalProjectileEntityRenderState.fuse, 10) / (float)Math.pow(maxFuse,10);
        scale = maxScale - scale;
        //float rotation = (maxRotation) * (float)Math.pow(maxFuse - EndCrystalProjectileEntityRenderState.fuse, 3) / (float)Math.pow(maxFuse,3);


        matrixStack.push();
        matrixStack.translate(0,0.6F,0);
        matrixStack.scale(scale , scale, scale);
        matrixStack.multiply(new Quaternionf().rotationYXZ(m * (float) (Math.PI / 180.0), camera.getPitch() * (float) (Math.PI / 180.0), 0)); //rotation * (float) (Math.PI / 180)));
        this.highlight.render(
                matrixStack,
                vertexConsumerProvider.getBuffer(RenderLayer.getTranslucentParticle(HighlightModels.FUSE_HIGHLIGHT_TEXTURE)),
                15728880,
                OverlayTexture.DEFAULT_UV,
                ColorHelper.fromFloats(alpha, r, g, b)
        );
        matrixStack.pop();

    }

    public static float getYOffset(float f) {
        float g = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        g = (g * g + g) * 0.4F;
        return g - 1.4F;
    }

    public EndCrystalProjectileEntityRenderState createRenderState() {
        return new EndCrystalProjectileEntityRenderState();
    }

    public void updateRenderState(EndCrystalProjectileEntity endCrystalEntity, EndCrystalProjectileEntityRenderState endCrystalProjectileEntityRenderState, float f) {
        super.updateRenderState(endCrystalEntity, endCrystalProjectileEntityRenderState, f);
        endCrystalProjectileEntityRenderState.invisible = endCrystalEntity.explode;
        endCrystalProjectileEntityRenderState.age = (float)endCrystalEntity.endCrystalAge + f;
        endCrystalProjectileEntityRenderState.baseVisible = endCrystalEntity.shouldShowBottom();
        EndCrystalProjectileEntityRenderState.owner = endCrystalEntity.getOwner();
        EndCrystalProjectileEntityRenderState.fuse = endCrystalEntity.fuseTimer;
    }
}
