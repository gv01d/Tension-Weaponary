package me.gv0id.arbalests.client.render;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.mixin.client.ShaderProgramKeysMixin;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

public class RenderLayer {

    public static final Identifier STARS = Arbalests.identifierOf("textures/particle/white_stars.png");

    public static final ShaderProgramKey END_CUTOUT_KEY =
            new ShaderProgramKey(
                    Arbalests.identifierOf("core/" + "rendertype_end_cutout"), VertexFormats.POSITION_TEXTURE, Defines.EMPTY
            );
    public static final RenderPhase.ShaderProgram END_CUTOUT_PROGRAM = new RenderPhase.ShaderProgram(END_CUTOUT_KEY);


    public static final Function<Identifier, net.minecraft.client.render.RenderLayer> END_CUTOUT = Util.memoize(
            (texture) ->
                    net.minecraft.client.render.RenderLayer.of(
                            "end_portal",
                            VertexFormats.POSITION_TEXTURE,
                            VertexFormat.DrawMode.QUADS,
                            1536,
                            false,
                            false,
                            net.minecraft.client.render.RenderLayer.MultiPhaseParameters.builder()
                                    .program(END_CUTOUT_PROGRAM)
                                    .texture(
                                            RenderPhase.Textures.create()
                                                    .add(texture, false, false)
                                                    .add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
                                                    .add(STARS, false, false)
                                                    .build()
                                    )
                                    .texturing(DEFAULT_TEXTURING)
                                    .transparency(TRANSLUCENT_TRANSPARENCY)
                                    .target(PARTICLES_TARGET)
                                    .writeMaskState(ALL_MASK)
                                    .build(false)
                    )
    );

}
