package me.gv0id.arbalests.client.data.models;

import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModels {
    public static final Model DEADBEAT_CROSSBOW;
    public static final Model DEADBEAT_CROSSBOW_CHARGE;
    public static final Model DEADBEAT_CROSSBOW_MAIN_CHARGE;
    public static final Model COPPER_DISC;



    public ModModels() {
    }

    private static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.of("arbalests","item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    static {
        DEADBEAT_CROSSBOW = item("deadbeat_crossbow", TextureKey.LAYER0);
        DEADBEAT_CROSSBOW_CHARGE = item("deadbeat_charge",  TextureKey.LAYER0);
        DEADBEAT_CROSSBOW_MAIN_CHARGE = item("deadbeat_main_charge",  TextureKey.LAYER0);
        COPPER_DISC = item("copper_disc",  TextureKey.LAYER0);
    }
}
