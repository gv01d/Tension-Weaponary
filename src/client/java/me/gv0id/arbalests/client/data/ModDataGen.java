package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.client.data.enchantments.ModEnchantmentGen;
import me.gv0id.arbalests.client.data.models.ModModelProvider;
import me.gv0id.arbalests.client.data.tag.ModValueLookupEntityTagProvider;
import me.gv0id.arbalests.client.data.tag.ModValueLookupItemTagProvider;
import me.gv0id.arbalests.client.data.recipes.ModRecipesProvider;
import me.gv0id.arbalests.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class ModDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModValueLookupEntityTagProvider::new);
        pack.addProvider(ModValueLookupItemTagProvider::new);
        pack.addProvider(ModRecipesProvider::new);
        pack.addProvider(ModRegistryDataGeneration::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, ModEnchantmentGen::bootstrap);
    }
}
