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
public class DeadbeatCrossbowChargeTypeProperty implements SelectProperty<DeadbeatCrossbowItem.Projectiles> {
    public static final Type<DeadbeatCrossbowChargeTypeProperty, DeadbeatCrossbowItem.Projectiles> TYPE = Type.create(
            MapCodec.unit(new DeadbeatCrossbowChargeTypeProperty()), DeadbeatCrossbowItem.Projectiles.CODEC
    );

    public DeadbeatCrossbowItem.Projectiles getValue(
            ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ModelTransformationMode modelTransformationMode
    ) {
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent)itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
            return DeadbeatCrossbowItem.Projectiles.NONE;
        } else {
            ItemStack temp = chargedProjectilesComponent.getProjectiles().getFirst();
            return DeadbeatCrossbowItem.getProjectileData(temp);
        }
    }

    @Override
    public Type<DeadbeatCrossbowChargeTypeProperty, DeadbeatCrossbowItem.Projectiles> getType() {
        return TYPE;
    }
}
