package me.gv0id.arbalests.client.property.select;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DeadbeatCrossbowSecondChargeTypeProperty implements SelectProperty<DeadbeatCrossbowItem.ChargeType> {
    public static final Type<DeadbeatCrossbowSecondChargeTypeProperty, DeadbeatCrossbowItem.ChargeType> TYPE = Type.create(
            MapCodec.unit(new DeadbeatCrossbowSecondChargeTypeProperty()), DeadbeatCrossbowItem.ChargeType.CODEC
    );

    public DeadbeatCrossbowItem.ChargeType getValue(
            ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ModelTransformationMode modelTransformationMode
    ) {
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent)itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
            return DeadbeatCrossbowItem.ChargeType.NONE;
        } else {
            if (chargedProjectilesComponent.getProjectiles().size() >= 2) {
                Item temp = chargedProjectilesComponent.getProjectiles().get(1).getItem();
                if (temp.equals(Items.SPECTRAL_ARROW))
                    return DeadbeatCrossbowItem.ChargeType.SPECTRAL_ARROW;
                if (temp.equals(Items.FIREWORK_ROCKET))
                    return DeadbeatCrossbowItem.ChargeType.ROCKET;
                if (temp.equals(Items.WIND_CHARGE))
                    return DeadbeatCrossbowItem.ChargeType.WIND_CHARGE;
                return DeadbeatCrossbowItem.ChargeType.ARROW;
            }
            return DeadbeatCrossbowItem.ChargeType.NONE;
        }
    }

    @Override
    public Type<DeadbeatCrossbowSecondChargeTypeProperty, DeadbeatCrossbowItem.ChargeType> getType() {
        return TYPE;
    }
}
