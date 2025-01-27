package me.gv0id.arbalests.client.render.entity.model;

import me.gv0id.arbalests.client.render.entity.state.EndCrystalProjectileEntityRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EndCrystalProjectilEntityModel extends EntityModel<EndCrystalProjectileEntityRenderState> {
    private static final String OUTER_GLASS = "outer_glass";
    private static final String INNER_GLASS = "inner_glass";
    private static final String BASE = "base";
    private static final float field_52906 = (float)Math.sin(Math.PI / 4);
    public final ModelPart base;
    public final ModelPart outerGlass;
    public final ModelPart innerGlass;
    public final ModelPart cube;

    public EndCrystalProjectilEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.base = modelPart.getChild("base");
        this.outerGlass = modelPart.getChild("outer_glass");
        this.innerGlass = this.outerGlass.getChild("inner_glass");
        this.cube = this.innerGlass.getChild(EntityModelPartNames.CUBE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 0.875F;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        ModelPartData modelPartData2 = modelPartData.addChild("outer_glass", modelPartBuilder, ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("inner_glass", modelPartBuilder, ModelTransform.NONE.withScale(0.875F));
        modelPartData3.addChild(
                EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE.withScale(0.765625F)
        );
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public void setAngles(EndCrystalProjectileEntityRenderState endCrystalProjectileEntityRenderState) {
        super.setAngles(endCrystalProjectileEntityRenderState);
        this.base.visible = endCrystalProjectileEntityRenderState.baseVisible;
        this.cube.visible = !endCrystalProjectileEntityRenderState.invisible;
        this.outerGlass.visible = !endCrystalProjectileEntityRenderState.invisible;
        this.innerGlass.visible = !endCrystalProjectileEntityRenderState.invisible;
        float f = endCrystalProjectileEntityRenderState.age * 3.0F;
        float g = EndCrystalEntityRenderer.getYOffset(endCrystalProjectileEntityRenderState.age) * 16.0F;
        //this.outerGlass.pivotY += g / 2.0F;
        this.outerGlass.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(f).rotateAxis((float) (Math.PI / 3), field_52906, 0.0F, field_52906));
        this.innerGlass.rotate(new Quaternionf().setAngleAxis((float) (Math.PI / 3), field_52906, 0.0F, field_52906).rotateY(f * (float) (Math.PI / 180.0)));
        this.cube.rotate(new Quaternionf().setAngleAxis((float) (Math.PI / 3), field_52906, 0.0F, field_52906).rotateY(f * (float) (Math.PI / 180.0)));

        float fuse = EndCrystalProjectileEntityRenderState.fuse;
        float mod;
        float a;

        float scaleMod2 = 0F;
        mod = MathHelper.lerp((fuse) / 600F, 1.0F, 1F);
        a = MathHelper.lerp((fuse) / 600F, 0.1F, 0.1F);


        if (fuse < 20){
            scaleMod2 = MathHelper.lerp((float) fuse / 10F, -2.0F, 0F);
        }

        //float scaleMod = ((endCrystalProjectileEntityRenderState.age % a) / (a/2));
        float scaleMod = (float) Math.sin(endCrystalProjectileEntityRenderState.age * a);
        scaleMod *= (0.1F * mod) + scaleMod2;
        this.innerGlass.scale(new Vector3f(scaleMod,scaleMod,scaleMod));
        scaleMod *= (0.5F * mod) + scaleMod2;
        this.outerGlass.scale(new Vector3f(scaleMod,scaleMod,scaleMod));
    }
}