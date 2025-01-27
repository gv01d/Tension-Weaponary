package me.gv0id.arbalests.client.render.entity.state;

import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.entity.Entity;

public class EndCrystalProjectileEntityRenderState extends EndCrystalEntityRenderState {
    public boolean invisible = false;
    public static Entity owner;
    public static int fuse;
}
