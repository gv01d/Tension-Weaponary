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
import net.minecraft.util.math.Direction;
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
        Direction direction = musicDiscEntityState.facing;
        Vec3d vec3d = this.getPositionOffset(musicDiscEntityState);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
        float f;
        float g;
        if (direction.getAxis().isHorizontal()) {
            f = 0.0F;
            g = 180.0F - direction.getPositiveHorizontalDegrees();
        } else {
            f = (float)(-90 * direction.getDirection().offset());
            g = 180.0F;
        }

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
        if (!musicDiscEntityState.invisible) {
            matrixStack.push();
            matrixStack.translate(-0.5F, -0.5F, -0.5F);
            matrixStack.pop();
        }

        if (musicDiscEntityState.invisible) {
            matrixStack.translate(0.0F, 0.0F, 0.5F);
        } else {
            matrixStack.translate(0.0F, 0.0F, 0.4375F);
        }

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
        return new Vec3d(
                (double)((float)itemFrameEntityRenderState.facing.getOffsetX() * 0.3F), -0.25, (double)((float)itemFrameEntityRenderState.facing.getOffsetZ() * 0.3F)
        );
    }

    @Override
    public MusicDiscEntityState createRenderState() {
        return new MusicDiscEntityState();
    }

    @Override
    public void updateRenderState(MusicDiscEntity musicDiscEntity, MusicDiscEntityState musicDiscEntityState, float f) {
        super.updateRenderState(musicDiscEntity, musicDiscEntityState, f);
        musicDiscEntityState.facing = musicDiscEntity.getHorizontalFacing();
        ItemStack itemStack = musicDiscEntity.getStack();
        this.itemModelManager.updateForNonLivingEntity(musicDiscEntityState.itemRenderState, itemStack, ModelTransformationMode.FIXED, musicDiscEntity);
        musicDiscEntityState.rotation = musicDiscEntity.getRotation();
        musicDiscEntityState.glow = musicDiscEntity.getType() == EntityType.GLOW_ITEM_FRAME;
    }
}
