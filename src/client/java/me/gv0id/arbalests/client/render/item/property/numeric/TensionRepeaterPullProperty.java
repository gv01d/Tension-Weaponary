package me.gv0id.arbalests.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.item.custom.TensionRepeaterItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TensionRepeaterPullProperty implements NumericProperty{

    public static final MapCodec<TensionRepeaterPullProperty> CODEC = MapCodec.unit(new TensionRepeaterPullProperty());

    public TensionRepeaterPullProperty() {
    }

    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        if (holder == null) {
            return 0.0F;
        } else if (TensionRepeaterItem.isCharged(stack)) {
            return 0.0F;
        } else {
            int i = TensionRepeaterItem.getPullTime(stack, holder);
            return (float) UseDurationProperty.getTicksUsedSoFar(stack, holder) / (float)i;
        }
    }

    public MapCodec<TensionRepeaterPullProperty> getCodec() {
        return CODEC;
    }

}
