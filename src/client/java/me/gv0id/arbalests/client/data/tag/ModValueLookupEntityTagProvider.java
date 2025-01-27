package me.gv0id.arbalests.client.data.tag;

import me.gv0id.arbalests.entity.ModEntityType;
import me.gv0id.arbalests.registry.tag.ModEntityTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class ModValueLookupEntityTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public ModValueLookupEntityTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        // - - - ENTITY TAGS - - -
        // VANILLA
        getOrCreateTagBuilder(EntityTypeTags.IMPACT_PROJECTILES)
                .add(ModEntityType.WIND_GALE);
        getOrCreateTagBuilder(EntityTypeTags.REDIRECTABLE_PROJECTILE)
                .add(ModEntityType.WIND_GALE)
                .add(ModEntityType.MUSIC_DISC)
                .add(ModEntityType.END_CRYSTAL_PROJECTILE);

        // MODDED
        getOrCreateTagBuilder(ModEntityTypeTags.WIND_EXPLOSIVES)
                .add(ModEntityType.WIND_GALE)
                .add(EntityType.WIND_CHARGE)
                .add(EntityType.BREEZE_WIND_CHARGE);
        getOrCreateTagBuilder(ModEntityTypeTags.RESET_FALL)
                .add(ModEntityType.WIND_GALE)
                .add(EntityType.WIND_CHARGE)
                .add(EntityType.BREEZE_WIND_CHARGE);
        getOrCreateTagBuilder(ModEntityTypeTags.STRAFE_JUMP)
                .add(ModEntityType.WIND_GALE)
                .add(EntityType.WIND_CHARGE)
                .add(EntityType.BREEZE_WIND_CHARGE);
    }
}


