package me.gv0id.arbalests.client;

import me.gv0id.arbalests.client.render.item.property.numeric.ModNumericProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModBootstrap {
    private static volatile boolean initialized;

    public ModBootstrap() {
    }

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            ModNumericProperties.bootstrap();
        }
    }
}
