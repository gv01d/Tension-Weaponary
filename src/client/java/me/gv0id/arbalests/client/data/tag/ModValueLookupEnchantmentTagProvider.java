package me.gv0id.arbalests.client.data.tag;

import me.gv0id.arbalests.enchantment.ModEnchantments;
import me.gv0id.arbalests.registry.tag.ModEnchantmentTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModValueLookupEnchantmentTagProvider extends FabricTagProvider.EnchantmentTagProvider {


    public ModValueLookupEnchantmentTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        getOrCreateTagBuilder(ModEnchantmentTypeTags.DEADBEAT_CHARGE_ENCHANTMENTS)
                .add(ModEnchantments.STRAFE_CHARGE)
                .add(Enchantments.QUICK_CHARGE);

        getOrCreateTagBuilder(ModEnchantmentTypeTags.SHOTVARIETY_ENCHANTMENTS)
                .add(Enchantments.MULTISHOT)
                .add(ModEnchantments.FAST_BALL)
                .add(ModEnchantments.SLOW_BALL);

        getOrCreateTagBuilder(ModEnchantmentTypeTags.DEADBEAT_SPECIAL_EXCLUSIVE_SET)
                .add(Enchantments.MULTISHOT)
                .add(ModEnchantments.BEGGARS_BACKFIRE);

    }
}
