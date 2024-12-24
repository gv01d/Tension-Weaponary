package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.client.property.bool.DeadbeatCrossbowChargingProperty;
import me.gv0id.arbalests.client.property.numeric.DeadbeatCrossbowPullProperty;
import me.gv0id.arbalests.client.property.select.DeadbeatCrossbowChargeTypeProperty;
import me.gv0id.arbalests.client.property.select.DeadbeatCrossbowSecondChargeTypeProperty;
import me.gv0id.arbalests.client.property.select.DeadbeatCrossbowThirdChargeTypeProperty;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.ChargeTypeProperty;
import net.minecraft.client.render.item.property.select.TrimMaterialProperty;
import net.minecraft.client.render.item.tint.DyeTintSource;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    }


    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        this.registerDeadbeatCrossbow2(ModItems.DEADBEAT_CROSSBOW,itemModelGenerator);
    }

    public final void registerDeadbeatCrossbow2(Item item, ItemModelGenerator itemModelGenerator){
        ItemModel.Unbaked unbaked = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked unbaked2 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_0", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked3 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_1", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked4 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_2", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked5 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_3", ModModels.DEADBEAT_CROSSBOW));

        ItemModel.Unbaked unbaked20 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_nothing", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked21 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_arrow", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked22 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_firework", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked23 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_wind_charge", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked24 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_spectral_arrow", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));

        ItemModel.Unbaked unbaked30 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_second_no_charge", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked31 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_second_arrow", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked32 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_second_firework", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked33 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_second_wind_charge", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked34 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_second_spectral_arrow", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));

        ItemModel.Unbaked unbaked40 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_third_no_charge", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked41 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_third_arrow", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked42 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_third_firework", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked43 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_third_wind_charge", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));
        ItemModel.Unbaked unbaked44 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_third_spectral_arrow", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE));


        itemModelGenerator.output.accept(item,
                ItemModels.composite(
                        ItemModels.select(
                                new DeadbeatCrossbowChargeTypeProperty(),
                                unbaked20,
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ARROW, unbaked21),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ROCKET, unbaked22),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.WIND_CHARGE, unbaked23),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.SPECTRAL_ARROW, unbaked24)
                                ),
                        ItemModels.condition(
                                new DeadbeatCrossbowChargingProperty(),
                                ItemModels.rangeDispatch(
                                        new DeadbeatCrossbowPullProperty(),
                                        unbaked2,
                                        ItemModels.rangeDispatchEntry(unbaked3, 0.3F),
                                        ItemModels.rangeDispatchEntry(unbaked4, 0.6F),
                                        ItemModels.rangeDispatchEntry(unbaked5, 0.95F)),
                                unbaked
                        ),
                        ItemModels.select(
                                new DeadbeatCrossbowSecondChargeTypeProperty(),
                                unbaked30,
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ARROW, unbaked31),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ROCKET, unbaked32),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.WIND_CHARGE, unbaked33),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.SPECTRAL_ARROW, unbaked34)
                        ),
                        ItemModels.select(
                                new DeadbeatCrossbowThirdChargeTypeProperty(),
                                unbaked40,
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ARROW, unbaked41),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ROCKET, unbaked42),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.WIND_CHARGE, unbaked43),
                                ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.SPECTRAL_ARROW, unbaked44)
                        )

                )
        );


    }



    public final void registerDeadbeatCrossbow(Item item, ItemModelGenerator itemModelGenerator) {
        ItemModel.Unbaked unbaked = ItemModels.basic(ModelIds.getItemModelId(item));
        //ItemModel.Unbaked unbaked = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_standby", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked2 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_0", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked3 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_1", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked4 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_2", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked5 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_arrow", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked6 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_firework", ModModels.DEADBEAT_CROSSBOW));
        itemModelGenerator.output.accept(item,
                ItemModels.condition(
                        new DeadbeatCrossbowChargingProperty(),
                        ItemModels.rangeDispatch(
                                new DeadbeatCrossbowPullProperty(),
                                unbaked2,
                                new RangeDispatchItemModel.Entry[]{
                                        ItemModels.rangeDispatchEntry(unbaked3, 0.58F),
                                        ItemModels.rangeDispatchEntry(unbaked4, 0.9F)
                                }
                        ),
                        ItemModels.select(
                                new ChargeTypeProperty(),
                                unbaked,
                                new SelectItemModel.SwitchCase[]{
                                        ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ARROW, unbaked5),
                                        ItemModels.switchCase(DeadbeatCrossbowItem.ChargeType.ROCKET, unbaked6)
                                }
                        )
                )
        );
    }

}
