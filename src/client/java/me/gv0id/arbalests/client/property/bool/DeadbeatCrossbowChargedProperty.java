package me.gv0id.arbalests.client.property.bool;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

public class DeadbeatCrossbowChargedProperty implements BooleanProperty {

    public static final MapCodec<DeadbeatCrossbowChargedProperty> CODEC = MapCodec.unit(new DeadbeatCrossbowChargedProperty());

    @Override
    public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        return DeadbeatCrossbowItem.isCharging(stack) || DeadbeatCrossbowItem.isCharged(stack);
    }

    public MapCodec<DeadbeatCrossbowChargedProperty> getCodec() {
        return CODEC;
    }
}
