package me.gv0id.arbalests.mixin;

import com.mojang.authlib.GameProfile;
import me.gv0id.arbalests.registry.tag.ModEntityTypeTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "onExplodedBy", at = @At("TAIL"))
    void onExplodeResetFall(Entity entity, CallbackInfo ci){
        this.setIgnoreFallDamageFromCurrentExplosion(entity != null && entity.getType().isIn(ModEntityTypeTags.RESET_FALL));
    }
}
