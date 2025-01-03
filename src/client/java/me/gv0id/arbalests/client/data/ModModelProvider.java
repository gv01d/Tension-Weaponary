package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.Arbalests;
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
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.item.Item;

import java.util.ArrayList;

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

        // - - -
        ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>> mainChargeList = new ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>>();
        ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>> secondChargeList = new ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>>();
        ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>> thirdChargeList = new ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>>();


        DeadbeatCrossbowItem.Projectiles[] projArray = DeadbeatCrossbowItem.Projectiles.values();

        int i = 0;
        for (DeadbeatCrossbowItem.Projectiles projectile : projArray){
            Arbalests.LOGGER.info("Generated charge name: _{}", projectile.getName());
            if (i > 0 && !projectile.isColection()) {
                ItemModel.Unbaked temp = ItemModels.basic(itemModelGenerator
                        .registerSubModel(item, "_" + projectile.getName(), ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE)
                );
                mainChargeList.add(
                        ItemModels.switchCase(projectile, temp)
                );

                secondChargeList.add(
                        ItemModels
                                .switchCase(projectile, ItemModels.basic(itemModelGenerator
                                                .registerSubModel(item, "_second_" + projectile.getName(), ModModels.DEADBEAT_CROSSBOW_CHARGE)
                                        )
                                )
                );
                thirdChargeList.add(
                        ItemModels
                                .switchCase(projectile, ItemModels.basic(itemModelGenerator
                                                .registerSubModel(item, "_third_" + projectile.getName(), ModModels.DEADBEAT_CROSSBOW_CHARGE)
                                        )
                                )
                );
            }
            i++;
        }
        // - - -

        itemModelGenerator.output.accept(item,
                ItemModels.composite(
                        ItemModels.select(
                                new DeadbeatCrossbowChargeTypeProperty(),
                                ItemModels.basic(itemModelGenerator.registerSubModel(item, "_" + projArray[0].getName(), ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE)),
                                mainChargeList
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
                                ItemModels.basic(itemModelGenerator.registerSubModel(item, "_second_" + projArray[0].getName(), ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE)),
                                secondChargeList
                        ),
                        ItemModels.select(
                                new DeadbeatCrossbowThirdChargeTypeProperty(),
                                ItemModels.basic(itemModelGenerator.registerSubModel(item, "_third_" + projArray[0].getName(), ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE)),
                                thirdChargeList
                        )

                )
        );


    }

}
