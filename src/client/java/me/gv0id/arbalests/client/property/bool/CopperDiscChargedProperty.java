package me.gv0id.arbalests.client.property.bool;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.item.custom.CopperDiscItem;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import org.jetbrains.annotations.Nullable;

public class CopperDiscChargedProperty implements BooleanProperty {

    public static final MapCodec<CopperDiscChargedProperty> CODEC = MapCodec.unit(new CopperDiscChargedProperty());

    @Override
    public boolean getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        return CopperDiscItem.isCharged(stack);
    }

    public MapCodec<CopperDiscChargedProperty> getCodec() {
        return CODEC;
    }
}
