package me.gv0id.arbalests.client.render.item.tint;

import me.gv0id.arbalests.Arbalests;
import net.minecraft.client.render.item.tint.TintSourceTypes;

public class ModTintSource {

    public static void initialization(){
        TintSourceTypes.ID_MAPPER.put(Arbalests.identifierOf("main_charge_potion"), MainChargePotionTintSource.CODEC);
        TintSourceTypes.ID_MAPPER.put(Arbalests.identifierOf("second_charge_potion"), SecondChargePotionTintSource.CODEC);
        TintSourceTypes.ID_MAPPER.put(Arbalests.identifierOf("third_charge_potion"), ThirdChargePotionTintSource.CODEC);
    }
}
