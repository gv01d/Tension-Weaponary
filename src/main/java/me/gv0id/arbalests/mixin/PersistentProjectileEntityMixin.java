package me.gv0id.arbalests.mixin;

import me.gv0id.arbalests.entity.ModEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private void setPierceLevel(byte level){
    }
    @Shadow
    public byte getPierceLevel(){
        return (byte) 50;
    }

    @Shadow
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return null;
    }


    @Inject(method = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;<init>(Lnet/minecraft/entity/EntityType;DDDLnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void arbalests$init(EntityType type, double x, double y, double z, World world, ItemStack stack, ItemStack weapon, CallbackInfo ci) {
        if (type == ModEntityType.SONIC_BOOM_PROJECTILE) {
            setPierceLevel((byte) 50);
        }
    }


    @Inject( method = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;applyCollision(Lnet/minecraft/util/hit/BlockHitResult;)V", at = @At("HEAD"), cancellable = true)
    private void arbalests$onBlockCollision(BlockHitResult blockHitResult, CallbackInfo ci) {
        if (((Object) this) instanceof PersistentProjectileEntity) {
            if (((PersistentProjectileEntity) (Object) this).getType() == ModEntityType.SONIC_BOOM_PROJECTILE) {
                ci.cancel();

                Vec3d endPos = this.getPos().add(this.getVelocity());
                while (this.isAlive()) {
                    Vec3d vec3d = this.getPos();
                    EntityHitResult entityHitResult = this.getEntityCollision(vec3d, endPos);
                    Vec3d vec3d2 = ((HitResult) Objects.requireNonNullElse(entityHitResult, blockHitResult)).getPos();
                    this.setPosition(vec3d2);
                    this.tickBlockCollision(vec3d, vec3d2);
                    if (this.portalManager != null && this.portalManager.isInPortal()) {
                        this.tickPortalTeleportation();
                    }

                    if (entityHitResult == null) {
                        if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS) {
                            this.hitOrDeflect(blockHitResult);
                            this.velocityDirty = true;
                        }
                        break;
                    } else if (this.isAlive() && !this.noClip) {
                        ProjectileDeflection projectileDeflection = this.hitOrDeflect(entityHitResult);
                        this.velocityDirty = true;
                        if (this.getPierceLevel() > 0 && projectileDeflection == ProjectileDeflection.NONE) {
                            continue;
                        }
                        break;
                    }
                }
            }
        }
    }
}
