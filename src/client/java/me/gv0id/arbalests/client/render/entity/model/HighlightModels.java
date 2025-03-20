package me.gv0id.arbalests.client.render.entity.model;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;

public class HighlightModels
{

    public static Identifier CIRCLE_HIGHLIGHT_TEXTURE = Arbalests.identifierOf("textures/entity/light_circle.png");

    public static ModelPart getCircleTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("light_circle", ModelPartBuilder.create().uv(0, 16).cuboid(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32).createModel();
    }

    public static Identifier FUSE_HIGHLIGHT_TEXTURE = Arbalests.identifierOf("textures/entity/fuse_light_circle.png");

    public static Identifier SPECIAL_HIGHLIGHT_TEXTURE = Arbalests.identifierOf("textures/entity/light_special.png");

    public static ModelPart getSpecialTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("light_special", ModelPartBuilder.create().uv(0, 0).cuboid(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, new Dilation(0.01F)), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32).createModel();
    }

    public static ModelPart getTaggedTextureModelData(){
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("tagged", ModelPartBuilder.create().uv(0,0).cuboid(-8.0F, -8.0F, 0F,16.0F,16.0F,0.0F, new Dilation(10)), ModelTransform.NONE);
        return TexturedModelData.of(modelData,16,16).createModel();
    }
}
