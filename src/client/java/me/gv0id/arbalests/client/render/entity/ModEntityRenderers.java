package me.gv0id.arbalests.client.render.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.gv0id.arbalests.entity.ModEntityType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.Map;

public class ModEntityRenderers implements ClientModInitializer {
    private static final Map<EntityType<?>, EntityRendererFactory<?>> RENDERER_FACTORIES = new Object2ObjectOpenHashMap<>();

    private static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        RENDERER_FACTORIES.put(type, factory);
    }

    static{
        register(ModEntityType.WIND_GALE, WindGaleEntityRenderer::new);
        }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntityType.WIND_GALE, WindGaleEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityType.MUSIC_DISC, MusicDiscEntityRenderer::new);
        EntityRendererRegistry.register(ModEntityType.END_CRYSTAL_PROJECTILE, EndCrystalProjectileEntityRenderer::new);
    }

}
