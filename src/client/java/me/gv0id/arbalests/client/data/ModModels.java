package me.gv0id.arbalests.client.data;

import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModels {
    public static final Model TENSION_REPEATER;

    public ModModels() {
    }

    private static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.of("arbalests","item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    static {
        TENSION_REPEATER = item("tension_repeater", TextureKey.LAYER0);
    }
}
