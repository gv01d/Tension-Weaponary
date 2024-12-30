package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.client.data.tag.ModValueLookupEntityTagProvider;
import me.gv0id.arbalests.client.data.tag.ModValueLookupItemTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModValueLookupEntityTagProvider::new);
        pack.addProvider(ModValueLookupItemTagProvider::new);
    }
}
