package me.gv0id.arbalests.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.enchantment.ModEnchantments;
import me.gv0id.arbalests.mixin.PlayerEntityMixin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {

    @Inject(
            method = "tickMovement",
            at = @At("HEAD")
    )
    void tickMovementInject(CallbackInfo ci){
        if (this.Arbalest_JumpI && !this.isSwimming()){
            this.jump();
            this.arbalests_coyoteTime = 0;
            this.Arbalest_JumpI = false;
        }else if(this.arbalests_coyoteTime > 0 && !this.isSwimming() && !this.isOnGround() && this.jumping){
            this.jump();
            this.arbalests_coyoteTime = 0;
        }
    }

    @WrapOperation(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0)
    )
    private boolean tickMovementWrap(ClientPlayerEntity instance, Operation<Boolean> original){
        ArrayList<RegistryEntry<Enchantment>> enchants = new ArrayList<>(instance.getMainHandStack().getEnchantments().getEnchantments());
        for (RegistryEntry<Enchantment> echantment : enchants){
            if (echantment.matchesKey(ModEnchantments.STRAFE_CHARGE)){
                return false;
            }
        }
        return original.call(instance);
    }

    @WrapOperation(
            method = "canStartSprinting",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", ordinal = 0)
    )
    private boolean canStartSprintingWrap(ClientPlayerEntity instance, Operation<Boolean> original){
        ItemEnchantmentsComponent itemEnchantmentsComponent = instance.getMainHandStack().getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()){
            if (entry.getKey().matchesKey(ModEnchantments.STRAFE_CHARGE) && entry.getIntValue() > 1){
                return false;
            }
        }
        return original.call(instance);
    }
}
