package me.gv0id.arbalests.client.property.numeric;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DeadbeatCrossbowPullProperty implements NumericProperty{

    public static final MapCodec<DeadbeatCrossbowPullProperty> CODEC = MapCodec.unit(new DeadbeatCrossbowPullProperty());

    public DeadbeatCrossbowPullProperty() {
    }

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        if (DeadbeatCrossbowItem.isFullyCharged(stack)){
            return 1.0F;
        }
        if (holder == null) {
            if (DeadbeatCrossbowItem.isCharged(stack))
                return 1.0F;
            return 0.0F;
        }
        if (DeadbeatCrossbowItem.isNotLoaded(stack) && !DeadbeatCrossbowItem.isCharging(stack) && !DeadbeatCrossbowItem.isCharged(stack) )
            return 0.0F;
        int i = DeadbeatCrossbowItem.getPullTime(stack, holder);
        return (float) UseDurationProperty.getTicksUsedSoFar(stack, holder) / (float)i;

    }

    public MapCodec<DeadbeatCrossbowPullProperty> getCodec() {
        return CODEC;
    }

}
