package me.gv0id.arbalests.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MusicDiscEntityState extends EntityRenderState {
    public final ItemRenderState itemRenderState = new ItemRenderState();
    public int rotation;
    public boolean glow;
    public float pitch;
    public float yaw;
    public Vec3d direction;
}