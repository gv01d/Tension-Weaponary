package me.gv0id.arbalests.client.data.recipes;

import me.gv0id.arbalests.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends FabricRecipeProvider {

    public ModRecipesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
                createShaped(RecipeCategory.COMBAT, ModItems.ECHO_CRYSTAL, 1)
                        .pattern(" E ")
                        .pattern("ECE")
                        .pattern(" E ")
                        .input('E', Items.ECHO_SHARD)
                        .input('C', Items.AMETHYST_SHARD)
                        .criterion("has_echo_shard", conditionsFromItem(Items.ECHO_SHARD))
                        .criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD))
                        .offerTo(exporter);
                offerSmelting(
                        List.of(Items.TRIAL_KEY),
                        RecipeCategory.BREWING,
                        ModItems.CURSED_COPPER_NUGGET,
                        1F,
                        350,
                        "cursed_copper_nugget_extraction"
                );
                createShaped(RecipeCategory.COMBAT, ModItems.DEADBEAT_CROSSBOW, 1)
                        .pattern("NCN")
                        .pattern("STS")
                        .pattern(" N ")
                        .input('N', ModItems.CURSED_COPPER_NUGGET)
                        .input('C', Items.COPPER_INGOT)
                        .input('S', Items.STRING)
                        .input('T', Items.TRIPWIRE_HOOK)
                        .criterion("has_copper", conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(exporter);
                createShaped(RecipeCategory.COMBAT, ModItems.COPPER_DISC, 1)
                        .pattern(" N ")
                        .pattern("NCN")
                        .pattern(" N ")
                        .input('N', ModItems.CURSED_COPPER_NUGGET)
                        .input('C', Items.COPPER_INGOT)
                        .criterion("has_copper", conditionsFromItem(Items.COPPER_INGOT))
                        .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "FabricDocsReferenceRecipeProvider";
    }
}
