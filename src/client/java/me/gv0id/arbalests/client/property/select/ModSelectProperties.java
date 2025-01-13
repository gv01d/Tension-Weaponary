package me.gv0id.arbalests.client.property.select;

import me.gv0id.arbalests.Arbalests;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModSelectProperties {

    public ModSelectProperties() {
    }

    public static void initialization(){
        SelectProperties.ID_MAPPER.put(Identifier.of("arbalests","deadbeat_crossbow/charge_type"), DeadbeatCrossbowChargeTypeProperty.TYPE);
        SelectProperties.ID_MAPPER.put(Identifier.of("arbalests","deadbeat_crossbow/second_charge_type"), DeadbeatCrossbowSecondChargeTypeProperty.TYPE);
        SelectProperties.ID_MAPPER.put(Identifier.of("arbalests","deadbeat_crossbow/third_charge_type"), DeadbeatCrossbowThirdChargeTypeProperty.TYPE);
        SelectProperties.ID_MAPPER.put(Arbalests.identifierOf("copper_disc/music"), CopperDiscVariationTypeProperty.TYPE);
    }
}
