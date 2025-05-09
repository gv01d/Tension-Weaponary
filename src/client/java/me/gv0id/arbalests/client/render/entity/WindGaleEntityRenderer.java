package me.gv0id.arbalests.client.render.entity;

import me.gv0id.arbalests.entity.projectile.AbstractWindGaleEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WindChargeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class WindGaleEntityRenderer extends EntityRenderer<AbstractWindGaleEntity, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/wind_charge.png");
    private final WindChargeEntityModel model;

    public WindGaleEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new WindChargeEntityModel(context.getPart(EntityModelLayers.WIND_CHARGE));
    }

    @Override
    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBreezeWind(TEXTURE, this.getXOffset(state.age) % 1.0F, 0.0F));
        this.model.setAngles(state);
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        super.render(state, matrices, vertexConsumers, light);
    }

    protected float getXOffset(float tickDelta) {
        return tickDelta * 0.03F;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
