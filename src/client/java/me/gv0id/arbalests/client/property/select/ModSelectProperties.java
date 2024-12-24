package me.gv0id.arbalests.client.property.select;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import me.gv0id.arbalests.client.property.numeric.DeadbeatCrossbowPullProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModSelectProperties {

    public ModSelectProperties() {
    }

    public static void inicialization(){
        SelectProperties.ID_MAPPER.put(Identifier.of("arbalests","deadbeat_crossbow/charge_type"), DeadbeatCrossbowChargeTypeProperty.TYPE);
        SelectProperties.ID_MAPPER.put(Identifier.of("arbalests","deadbeat_crossbow/second_charge_type"), DeadbeatCrossbowSecondChargeTypeProperty.TYPE);
        SelectProperties.ID_MAPPER.put(Identifier.of("arbalests","deadbeat_crossbow/third_charge_type"), DeadbeatCrossbowThirdChargeTypeProperty.TYPE);
    }
}
