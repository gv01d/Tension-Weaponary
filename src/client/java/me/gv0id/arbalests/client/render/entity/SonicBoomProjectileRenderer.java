package me.gv0id.arbalests.client.render.entity;

import me.gv0id.arbalests.entity.projectile.SonicBoomProjectile;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class SonicBoomProjectileRenderer extends EntityRenderer<SonicBoomProjectile,EntityRenderState> {
    protected SonicBoomProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
