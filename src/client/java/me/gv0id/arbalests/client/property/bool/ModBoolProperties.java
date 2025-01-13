package me.gv0id.arbalests.client.property.bool;

import me.gv0id.arbalests.Arbalests;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModBoolProperties {

    public ModBoolProperties() {
    }


    public static void initialization(){
        BooleanProperties.ID_MAPPER.put(Identifier.of("arbalests","deadbeat_crossbow/charging"), DeadbeatCrossbowChargingProperty.CODEC);
        BooleanProperties.ID_MAPPER.put(Arbalests.identifierOf("copper_disc/charged"), CopperDiscChargedProperty.CODEC);
    }


}
