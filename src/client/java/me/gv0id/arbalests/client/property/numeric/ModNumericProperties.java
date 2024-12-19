package me.gv0id.arbalests.client.property.numeric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.*;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModNumericProperties {

    public ModNumericProperties() {
    }

    public static void inicialization(){
        NumericProperties.ID_MAPPER.put(Identifier.of("arbalests","tension_repeater/pull"), TensionRepeaterPullProperty.CODEC);
    }
}
