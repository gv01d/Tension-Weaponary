package me.gv0id.arbalests.client.property.bool;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.client.property.numeric.TensionRepeaterPullProperty;
import me.gv0id.arbalests.item.custom.TensionRepeaterItem;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

public class TensionRepeaterChargingProperty implements BooleanProperty {

    public static final MapCodec<TensionRepeaterChargingProperty> CODEC = MapCodec.unit(new TensionRepeaterChargingProperty());

    @Override
    public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        if (user == null) {
            return false;
        } else {
            return TensionRepeaterItem.isCharging(stack);
        }
    }

    public MapCodec<TensionRepeaterChargingProperty> getCodec() {
        return CODEC;
    }
}
