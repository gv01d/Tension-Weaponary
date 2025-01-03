package me.gv0id.arbalests.client.render.item.tint;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.tint.PotionTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public record MainChargePotionTintSource (int defaultColor) implements TintSource {
    public static final MapCodec<MainChargePotionTintSource> CODEC =
            RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codecs.RGB.fieldOf("default").forGetter(MainChargePotionTintSource::defaultColor)).apply(instance, MainChargePotionTintSource::new)
    );

    public MainChargePotionTintSource() {
        this(-13083194);
    }

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        ArrayList<ItemStack> stk = new ArrayList<>(Objects.requireNonNull(stack.get(DataComponentTypes.CHARGED_PROJECTILES)).getProjectiles());
        if(stk.isEmpty()){
            return ColorHelper.fullAlpha(this.defaultColor);
        }

        PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stk.getFirst().get(DataComponentTypes.POTION_CONTENTS);
        return potionContentsComponent != null
                ? ColorHelper.fullAlpha(potionContentsComponent.getColor(this.defaultColor))
                : ColorHelper.fullAlpha(this.defaultColor);
    }

    @Override
    public MapCodec<MainChargePotionTintSource> getCodec() {
        return CODEC;
    }
}