package me.gv0id.arbalests.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

    @Environment(EnvType.CLIENT)
public class ModNumericProperties {

    public static final Codecs.IdMapper<Identifier, MapCodec<? extends NumericProperty>> ID_MAPPER = new Codecs.IdMapper();
    public static final MapCodec<NumericProperty> CODEC;

    public ModNumericProperties() {
    }

    public static void bootstrap() {
        ID_MAPPER.put(Identifier.of("arbalests","tension_repeater/pull"), TensionRepeaterPullProperty.CODEC);
    }

    static {
        CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatchMap(
                "property",
                NumericProperty::getCodec,
                (codec) -> codec
        );
    }

}
