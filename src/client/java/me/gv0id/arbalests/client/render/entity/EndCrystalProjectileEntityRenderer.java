package me.gv0id.arbalests.client.render.entity;

import me.gv0id.arbalests.client.render.entity.model.EndCrystalProjectilEntityModel;
import me.gv0id.arbalests.entity.projectile.EndCrystalProjectileEntity;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EndCrystalProjectileEntityRenderer extends EntityRenderer<EndCrystalProjectileEntity, EndCrystalEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
    private static final RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private final EndCrystalProjectilEntityModel model;

    public EndCrystalProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.model = new EndCrystalProjectilEntityModel(context.getPart(EntityModelLayers.END_CRYSTAL));
    }

    public void render(EndCrystalEntityRenderState endCrystalEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.translate(0.0F, -0.8F, 0.0F);



        this.model.setAngles(endCrystalEntityRenderState);
        this.model.render(matrixStack, vertexConsumerProvider.getBuffer(END_CRYSTAL), i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        /*
        Vec3d vec3d = endCrystalEntityRenderState.beamOffset;
        if (vec3d != null) {
            float f = getYOffset(endCrystalEntityRenderState.age);
            float g = (float)vec3d.x;
            float h = (float)vec3d.y;
            float j = (float)vec3d.z;
            matrixStack.translate(vec3d);
            EnderDragonEntityRenderer.renderCrystalBeam(-g, -h + f, -j, endCrystalEntityRenderState.age, matrixStack, vertexConsumerProvider, i);
        }
        */

        super.render(endCrystalEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    public static float getYOffset(float f) {
        float g = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        g = (g * g + g) * 0.4F;
        return g - 1.4F;
    }

    public EndCrystalEntityRenderState createRenderState() {
        return new EndCrystalEntityRenderState();
    }

    public void updateRenderState(EndCrystalProjectileEntity endCrystalEntity, EndCrystalEntityRenderState endCrystalEntityRenderState, float f) {
        super.updateRenderState(endCrystalEntity, endCrystalEntityRenderState, f);
        endCrystalEntityRenderState.age = (float)endCrystalEntity.endCrystalAge + f;
        endCrystalEntityRenderState.baseVisible = endCrystalEntity.shouldShowBottom();
    }
}
