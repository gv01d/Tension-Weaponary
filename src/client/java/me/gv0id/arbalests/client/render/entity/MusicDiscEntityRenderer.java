package me.gv0id.arbalests.client.render.entity;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.client.particles.HighlightParticle;
import me.gv0id.arbalests.client.render.entity.model.HighlightModels;
import me.gv0id.arbalests.client.render.entity.state.MusicDiscEntityState;
import me.gv0id.arbalests.entity.projectile.MusicDiscEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.DragonFireballEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class MusicDiscEntityRenderer extends EntityRenderer<MusicDiscEntity,MusicDiscEntityState> {
    public static final int GLOW_FRAME_BLOCK_LIGHT = 5;
    private final ItemModelManager itemModelManager;

    private static Identifier TEXTURE = Arbalests.identifierOf("textures/entity/highlight.png");


    private final EntityRenderDispatcher renderDispatcher;
    private final ModelPart highlight;
    private final ModelPart lightCircle;

    public MusicDiscEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
        this.renderDispatcher = context.getRenderDispatcher();
        this.highlight = getHighlightTexturedModelData().createModel();
        this.lightCircle = HighlightModels.getCircleTexturedModelData();
    }


    @Override
    public void render(MusicDiscEntityState musicDiscEntityState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(musicDiscEntityState, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();

        matrixStack.translate(0,0.15,0);

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(musicDiscEntityState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(musicDiscEntityState.pitch + 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));

        if (!musicDiscEntityState.itemRenderState.isEmpty()) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(musicDiscEntityState.rotation));
            int j = this.getLight(musicDiscEntityState.glow, 15728880, i);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            musicDiscEntityState.itemRenderState.render(matrixStack, vertexConsumerProvider, j, OverlayTexture.DEFAULT_UV);
        }
        matrixStack.pop();


        if (!musicDiscEntityState.onGround){
            Camera camera = this.dispatcher.camera;
            float mod = 0.8f;
            float p = (float) Math.sin(musicDiscEntityState.age * mod);
            p *= 0.1F;
            float m = -camera.getYaw();

            int col;
            if (musicDiscEntityState.owner instanceof ClientPlayerEntity){
                col = ColorHelper.fromFloats(0.2f,1F,1F,1F);
            }
            else {
                col = ColorHelper.fromFloats(0.6f,1F,0.180F,0.180F);
            }
            matrixStack.push();
            matrixStack.translate(0,0.15,0);
            matrixStack.scale(1.4F , 1.4F, 1.4F);
            matrixStack.multiply(new Quaternionf().rotationYXZ(m * (float) (Math.PI / 180.0), camera.getPitch() * (float) (Math.PI / 180.0), (float)Math.PI));
            this.highlight.render(
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayer.getTranslucentParticle(HighlightModels.CIRCLE_HIGHLIGHT_TEXTURE)),
                    15728880,
                    OverlayTexture.DEFAULT_UV,
                    col
            );
            matrixStack.pop();
        }
    }


    public void renderBillboard(VertexConsumer vertexConsumer){
        Camera camera = this.renderDispatcher.camera;
    }

    public static TexturedModelData getHighlightTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("highlight", ModelPartBuilder.create().uv(0, 16).cuboid(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new Dilation(1F)), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 16, 16);
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
        musicDiscEntityState.owner = musicDiscEntity.getOwner();
        musicDiscEntityState.onGround = musicDiscEntity.ground;
    }
}
