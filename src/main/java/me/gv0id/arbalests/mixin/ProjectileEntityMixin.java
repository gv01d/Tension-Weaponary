package me.gv0id.arbalests.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @WrapOperation(
            method  = "onCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileEntity;deflect(Lnet/minecraft/entity/ProjectileDeflection;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Z)Z"
            )
    )
    boolean onColisionWrap(ProjectileEntity instance, ProjectileDeflection deflection, Entity deflector, Entity owner, boolean fromAttack, Operation<Boolean> original){
        return instance.deflect(deflection,(ProjectileEntity)(Object)this, owner,fromAttack);
    }

}
