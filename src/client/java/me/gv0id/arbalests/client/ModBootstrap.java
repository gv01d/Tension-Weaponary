package me.gv0id.arbalests.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModBootstrap {
    private static volatile boolean initialized;

    public ModBootstrap() {
    }

    public static void initialize() {
    }
}
