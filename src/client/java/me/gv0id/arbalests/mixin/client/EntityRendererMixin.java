package me.gv0id.arbalests.mixin.client;


import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.client.render.entity.model.HighlightModels;
import me.gv0id.arbalests.helper.EntityInterface;
import me.gv0id.arbalests.helper.EntityRenderStateInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Unique
    private static final Identifier TEXTURE = Arbalests.identifierOf("textures/entity/target.png");

    @Unique
    public final ModelPart target = HighlightModels.getTaggedTextureModelData();

    protected EntityRendererMixin(EntityRenderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Inject( method = "updateRenderState", at = @At("TAIL"))
    public void updateRenderStateInject(T entity, S state, float tickDelta, CallbackInfo ci){
        ((EntityRenderStateInterface) state).arbalests$setTag(((EntityInterface) entity).arbalests$isTagged());
    }

    @Mutable
    @Final
    @Shadow protected final EntityRenderDispatcher dispatcher;

    @Shadow @Final private S state;

    @Inject( method = "render", at = @At("TAIL"))
    public void renderInject(S state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci){
        if (((EntityRenderStateInterface) state).arbalests$isTagged()){
            Camera camera = this.dispatcher.camera;
            matrixStack.push();
            matrixStack.translate(0,1F,0);
            matrixStack.scale(1.4F , 1.4F, 1.4F);
            matrixStack.multiply(new Quaternionf().rotationYXZ(-camera.getYaw() * (float) (Math.PI / 180.0), camera.getPitch() * (float) (Math.PI / 180.0), (float)Math.PI));
            this.target.render(
                    matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayer.getTranslucentParticle(TEXTURE)),
                    15728880,
                    OverlayTexture.DEFAULT_UV,
                    ColorHelper.fromFloats(1F, 1F, 1F, 1F)
            );
            matrixStack.pop();
        }
    }



}
