package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.client.render.item.property.numeric.TensionRepeaterPullProperty;
import me.gv0id.arbalests.item.custom.TensionRepeaterItem;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.ChargeTypeProperty;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class ModItemGenerator {

    public final ItemModelOutput output;
    public final BiConsumer<Identifier, ModelSupplier> modelCollector;

    public ModItemGenerator(ItemModelOutput output, BiConsumer<Identifier, ModelSupplier> modelCollector) {
        this.output = output;
        this.modelCollector = modelCollector;
    }

    public final Identifier registerSubModel(Item item, String suffix, Model model) {
        return model.upload(ModelIds.getItemSubModelId(item, suffix), TextureMap.layer0(TextureMap.getSubId(item, suffix)), this.modelCollector);
    }

    public final void registerTensionRepeater(Item item) {
        ItemModel.Unbaked unbaked = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked unbaked2 = ItemModels.basic(this.registerSubModel(item, "_pulling_0", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked3 = ItemModels.basic(this.registerSubModel(item, "_pulling_1", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked4 = ItemModels.basic(this.registerSubModel(item, "_pulling_2", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked5 = ItemModels.basic(this.registerSubModel(item, "_arrow", ModModels.TENSION_REPEATER));
        ItemModel.Unbaked unbaked6 = ItemModels.basic(this.registerSubModel(item, "_firework", ModModels.TENSION_REPEATER));
        this.output.accept(item,
                ItemModels.condition(
                        ItemModels.usingItemProperty(),
                        ItemModels.rangeDispatch(
                                new TensionRepeaterPullProperty(),
                                unbaked2,
                                new RangeDispatchItemModel.Entry[]{
                                        ItemModels.rangeDispatchEntry(unbaked3, 0.58F),
                                        ItemModels.rangeDispatchEntry(unbaked4, 1.0F)
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
