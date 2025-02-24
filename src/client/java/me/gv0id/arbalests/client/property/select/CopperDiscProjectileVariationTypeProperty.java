package me.gv0id.arbalests.client.property.select;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.client.data.models.ModModelProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CopperDiscProjectileVariationTypeProperty implements SelectProperty<ModModelProvider.ItemVariation> {
    public static final Type<CopperDiscProjectileVariationTypeProperty, ModModelProvider.ItemVariation> TYPE = Type.create(
            MapCodec.unit(new CopperDiscProjectileVariationTypeProperty()), ModModelProvider.ItemVariation.CODEC
    );

    public ModModelProvider.ItemVariation getValue(
            ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ModelTransformationMode modelTransformationMode
    ) {
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent)itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
            return ModModelProvider.ItemVariation.NONE;
        } else {
            ItemStack temp = chargedProjectilesComponent.getProjectiles().getFirst().copy();

            ChargedProjectilesComponent chargedProjectilesComponent1 = temp.get(DataComponentTypes.CHARGED_PROJECTILES);
            if (chargedProjectilesComponent1 == null || chargedProjectilesComponent1.isEmpty()){
                return ModModelProvider.ItemVariation.NONE;
            }
            temp = chargedProjectilesComponent1.getProjectiles().getFirst();

            return ModModelProvider.getItemVariation(temp);
        }
    }

    @Override
    public Type<CopperDiscProjectileVariationTypeProperty, ModModelProvider.ItemVariation> getType() {
        return TYPE;
    }
}
