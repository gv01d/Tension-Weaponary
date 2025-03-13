package me.gv0id.arbalests.mixin.client;

import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderStateInterface {

    @Unique
    boolean tagged;

    @Override
    public void arbalests$setTag(boolean b) {
        this.tagged = b;
    }

    @Override
    public boolean arbalests$isTagged() {
        return this.tagged;
    }
}
