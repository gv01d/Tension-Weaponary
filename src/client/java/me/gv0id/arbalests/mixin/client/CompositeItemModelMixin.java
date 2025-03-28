package me.gv0id.arbalests.mixin.client;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.registry.tag.ModItemTypeTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.CompositeItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(CompositeItemModel.class)
public abstract class CompositeItemModelMixin {

    @Shadow
    private final List<ItemModel> models;

    protected CompositeItemModelMixin(List<ItemModel> models) {
        this.models = models;
    }


    @Inject(
            method = "update",
            at = @At("HEAD"),
            cancellable = true
    )
    void updateInject(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ModelTransformationMode transformationMode, ClientWorld world, LivingEntity user, int seed, CallbackInfo ci){
        if ( stack.isOf(ModItems.DEADBEAT_CROSSBOW)){
            boolean glint = stack.hasGlint();
            if (glint){
                ItemStack stk = stack.copy();
                state.addLayers(this.models.size());
                int i = 0;
                for (ItemModel itemModel : this.models) {
                    if (i == 1 ){
                        stk.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                    }
                    else {
                        stk.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
                    }
                    i++;
                    itemModel.update(state, stk, resolver, transformationMode, world, user, seed);
                }
                ci.cancel();
            }
        }
    }
}
