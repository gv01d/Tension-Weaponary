package me.gv0id.arbalests.client.property.bool;

import com.mojang.serialization.MapCodec;
import me.gv0id.arbalests.client.property.numeric.TensionRepeaterPullProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModBoolProperties {

    public ModBoolProperties() {
    }


    public static void inicialization(){
        BooleanProperties.ID_MAPPER.put(Identifier.of("arbalests","tension_repeater/charging"), TensionRepeaterChargingProperty.CODEC);
    }


}
