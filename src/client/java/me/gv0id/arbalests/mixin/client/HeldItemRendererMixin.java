package me.gv0id.arbalests.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.gv0id.arbalests.item.ModItems;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @WrapOperation(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
            )
    )
    private boolean applyRenderCrossbows(ItemStack itSt,Item it , Operation<Boolean> original) {
        return itSt.isOf(ModItems.DEADBEAT_CROSSBOW) || original.call(itSt,it);
    }
}

