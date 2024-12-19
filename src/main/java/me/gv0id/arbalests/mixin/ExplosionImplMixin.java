package me.gv0id.arbalests.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.registry.tag.ModEntityTypeTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ExplosionImpl.class)
public class ExplosionImplMixin {

    @Shadow @Final private @Nullable Entity entity;

    @ModifyVariable(
            method = "preservesDecorativeEntities",
            at = @At("STORE"),
            ordinal = 2
    )
    public boolean thing(boolean value){
        return value && ( this.entity == null || this.entity.getType().isIn(ModEntityTypeTags.WIND_EXPLOSIVES));
    }
}
