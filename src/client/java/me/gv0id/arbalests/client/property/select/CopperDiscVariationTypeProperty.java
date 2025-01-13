package me.gv0id.arbalests.client.property.select;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.item.custom.CopperDiscItem;
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
public class CopperDiscVariationTypeProperty implements SelectProperty<CopperDiscItem.Music> {
    public static final Type<CopperDiscVariationTypeProperty, CopperDiscItem.Music> TYPE = Type.create(
            MapCodec.unit(new CopperDiscVariationTypeProperty()), CopperDiscItem.Music.CODEC
    );

    public CopperDiscItem.Music getValue(
            ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ModelTransformationMode modelTransformationMode
    ) {
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent)itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
            return CopperDiscItem.Music.NONE;
        } else {
            ItemStack temp = chargedProjectilesComponent.getProjectiles().getFirst();
            return CopperDiscItem.getMusic(temp);
        }
    }

    @Override
    public Type<CopperDiscVariationTypeProperty, CopperDiscItem.Music> getType() {
        return TYPE;
    }
}
