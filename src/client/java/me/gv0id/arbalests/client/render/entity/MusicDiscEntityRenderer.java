package me.gv0id.arbalests.client.render.entity;

import me.gv0id.arbalests.client.render.entity.state.MusicDiscEntityState;
import me.gv0id.arbalests.entity.projectile.MusicDiscEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MusicDiscEntityRenderer extends EntityRenderer<MusicDiscEntity,MusicDiscEntityState> {
    public static final int GLOW_FRAME_BLOCK_LIGHT = 5;
    private final ItemModelManager itemModelManager;

    public MusicDiscEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public void render(MusicDiscEntityState musicDiscEntityState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(musicDiscEntityState, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(musicDiscEntityState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(musicDiscEntityState.pitch + 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));


        if (!musicDiscEntityState.itemRenderState.isEmpty()) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)musicDiscEntityState.rotation * 360.0F / 8.0F));
            int j = this.getLight(musicDiscEntityState.glow, 15728880, i);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            musicDiscEntityState.itemRenderState.render(matrixStack, vertexConsumerProvider, j, OverlayTexture.DEFAULT_UV);
        }

        matrixStack.pop();
    }

    private int getLight(boolean glow, int glowLight, int regularLight) {
        return glow ? glowLight : regularLight;
    }


    private static ModelIdentifier getModelId(MusicDiscEntityState state) {
        return state.glow ? BlockStatesLoader.GLOW_ITEM_FRAME_MODEL_ID : BlockStatesLoader.ITEM_FRAME_MODEL_ID;
    }


    @Override
    public Vec3d getPositionOffset(MusicDiscEntityState itemFrameEntityRenderState) {
        return Vec3d.ZERO;
    }

    @Override
    public MusicDiscEntityState createRenderState() {
        return new MusicDiscEntityState();
    }

    @Override
    public void updateRenderState(MusicDiscEntity musicDiscEntity, MusicDiscEntityState musicDiscEntityState, float f) {
        super.updateRenderState(musicDiscEntity, musicDiscEntityState, f);
        musicDiscEntityState.yaw = musicDiscEntity.getLerpedYaw(f);
        musicDiscEntityState.pitch = musicDiscEntity.getLerpedPitch(f);
        musicDiscEntityState.direction = musicDiscEntity.getVelocity().normalize();
        ItemStack itemStack = musicDiscEntity.getStack();
        this.itemModelManager.updateForNonLivingEntity(musicDiscEntityState.itemRenderState, itemStack, ModelTransformationMode.FIXED, musicDiscEntity);
        musicDiscEntityState.rotation = musicDiscEntity.getRotation();
        musicDiscEntityState.glow = musicDiscEntity.getType() == EntityType.GLOW_ITEM_FRAME;
    }
}
