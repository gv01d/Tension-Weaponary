package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.client.property.bool.TensionRepeaterChargingProperty;
import me.gv0id.arbalests.client.property.numeric.TensionRepeaterPullProperty;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.item.custom.TensionRepeaterItem;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.ChargeTypeProperty;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        this.registerTensionRepeater(ModItems.TENSION_REPEATER,itemModelGenerator);
    }

    public final void registerTensionRepeaterCrossbow(Item item, ItemModelGenerator itemModelGenerator){
        Identifier modelid = itemModelGenerator.upload(ModItems.TENSION_REPEATER, Models.GENERATED);


    }

    public final void registerTensionRepeater(Item item, ItemModelGenerator itemModelGenerator) {
        ItemModel.Unbaked unbaked = ItemModels.basic(ModelIds.getItemModelId(item));
        //ItemModel.Unbaked unbaked = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_standby", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked2 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_0", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked3 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_1", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked4 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_2", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked5 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_arrow", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked6 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_firework", ModModels.TENSION_REPEATER));
        itemModelGenerator.output.accept(item,
                ItemModels.condition(
                        new TensionRepeaterChargingProperty(),
                        ItemModels.rangeDispatch(
                                new TensionRepeaterPullProperty(),
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
                                        ItemModels.switchCase(TensionRepeaterItem.ChargeType.ARROW, unbaked5),
                                        ItemModels.switchCase(TensionRepeaterItem.ChargeType.ROCKET, unbaked6)
                                }
                        )
                )
        );
    }

}
