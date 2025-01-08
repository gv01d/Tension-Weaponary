package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.client.property.bool.DeadbeatCrossbowChargingProperty;
import me.gv0id.arbalests.client.property.numeric.DeadbeatCrossbowPullProperty;
import me.gv0id.arbalests.client.property.select.DeadbeatCrossbowChargeTypeProperty;
import me.gv0id.arbalests.client.property.select.DeadbeatCrossbowSecondChargeTypeProperty;
import me.gv0id.arbalests.client.property.select.DeadbeatCrossbowThirdChargeTypeProperty;
import me.gv0id.arbalests.client.render.item.tint.MainChargePotionTintSource;
import me.gv0id.arbalests.client.render.item.tint.SecondChargePotionTintSource;
import me.gv0id.arbalests.client.render.item.tint.ThirdChargePotionTintSource;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Optional;

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
        this.registerCopperDisc(ModItems.COPPER_DISC,itemModelGenerator);
    }

    public final void registerCopperDisc(Item item, ItemModelGenerator itemModelGenerator){
        ItemModel.Unbaked unbaked = ItemModels.basic(ModelIds.getItemModelId(item));

        itemModelGenerator.output.accept(item,unbaked);
    }

    public final void registerDeadbeatCrossbow2(Item item, ItemModelGenerator itemModelGenerator){
        ItemModel.Unbaked unbaked = ItemModels.basic(ModelIds.getItemModelId(item));
        ItemModel.Unbaked unbaked2 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_0", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked3 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_1", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked4 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_2", ModModels.DEADBEAT_CROSSBOW));
        ItemModel.Unbaked unbaked5 = ItemModels.basic(itemModelGenerator.registerSubModel(item, "_pulling_3", ModModels.DEADBEAT_CROSSBOW));

        ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>> mainChargeList = new ArrayList<>();
        ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>> secondChargeList = new ArrayList<>();
        ArrayList<SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles>> thirdChargeList = new ArrayList<>();

        DeadbeatCrossbowItem.Projectiles[] projArray = DeadbeatCrossbowItem.Projectiles.values();

        int i = 0;
        for (DeadbeatCrossbowItem.Projectiles projectile : projArray){
            Arbalests.LOGGER.info("Generated charge name: _{}", projectile.getName());
            if (i > 0 && !projectile.isCollection() && !projectile.isTinted()) {
                mainChargeList.add(ItemModels.switchCase(projectile, ItemModels.basic(itemModelGenerator
                                                .registerSubModel(item, "_" + projectile.getName(), ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE)
                                        )
                                )
                );

                secondChargeList.add(ItemModels.switchCase(projectile, ItemModels.basic(itemModelGenerator
                                                .registerSubModel(item, "_second_" + projectile.getName(), ModModels.DEADBEAT_CROSSBOW_CHARGE)
                                        )
                                )
                );
                thirdChargeList.add(ItemModels.switchCase(projectile, ItemModels.basic(itemModelGenerator
                                                .registerSubModel(item, "_third_" + projectile.getName(), ModModels.DEADBEAT_CROSSBOW_CHARGE)
                                        )
                                )
                );
            }
            else if (projectile.isTinted()){

                mainChargeList.add(unbaked_tinted_builder(item, "_", "deadbeat_main_charge", projectile ,itemModelGenerator, tintSourceEnum.valueOf(projectile.getTintSourceEnumName()).mainTintSource ));
                secondChargeList.add(unbaked_tinted_builder(item, "_second_", "deadbeat_charge", projectile ,itemModelGenerator, tintSourceEnum.valueOf(projectile.getTintSourceEnumName()).secondTintSource));
                thirdChargeList.add(unbaked_tinted_builder(item, "_third_", "deadbeat_charge", projectile ,itemModelGenerator, tintSourceEnum.valueOf(projectile.getTintSourceEnumName()).thirdTintSource));

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

    public final Identifier uploadLayered(Item item, String prefix , String referenceModel, ItemModelGenerator itemModelGenerator, Identifier... layers) {
        int amount = layers.length;

        TextureKey[] textureKeys = new TextureKey[amount];
        for (int i = 0; i < amount; i++){
            textureKeys[i] = TextureKey.of("layer" + i);
        }

        Model testModel = new Model(Optional.of(Identifier.of("arbalests","item/" + referenceModel )), Optional.empty(), textureKeys);

        return testModel.upload(ModelIds.getItemSubModelId(item, prefix), layered(textureKeys, layers, amount), itemModelGenerator.modelCollector);
    }

    public static TextureMap layered(TextureKey[] textureKeys, Identifier[] layers , int amount) {
        TextureMap textureMap = new TextureMap();
        for (int i = 0; i < amount; i++){
            textureMap.put(textureKeys[i], layers[i]);
            Arbalests.LOGGER.info("Texture Key : {} |  Layer : {}", textureKeys[i], layers[i]);
        }
        return textureMap;
    }

    private SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles> unbaked_tinted_builder(
            Item item,
            String prefix,
            String parent,
            DeadbeatCrossbowItem.Projectiles projectile,
            ItemModelGenerator itemModelGenerator,
            TintSource tintSource
    )
    {

        String[] layers = projectile.getLayers();
        Identifier[] identifiers = new Identifier[layers.length];
        for (int j = 0; j < layers.length; j++) {
            identifiers[j] = ModelIds.getItemSubModelId(item, prefix + projectile.getName() + "_" + layers[j]);
        }

        ItemModel.Unbaked unbaked = ItemModels.tinted(
                uploadLayered(
                        item,
                        prefix + projectile.getName(),
                        parent,
                        itemModelGenerator,
                        identifiers
                ),
                tintSource
        );

        return ItemModels.switchCase(projectile, unbaked);

    }

    public String itemName(Item item){
        return (item.toString().split(":"))[1];
    }

    public enum tintSourceEnum{
        POTION(new MainChargePotionTintSource(), new SecondChargePotionTintSource(), new ThirdChargePotionTintSource());

        final TintSource mainTintSource;
        final TintSource secondTintSource;
        final TintSource thirdTintSource;

        tintSourceEnum(TintSource mainTintSource, TintSource secondTintSource, TintSource thirdTintSource){
            this.mainTintSource = mainTintSource;
            this.secondTintSource = secondTintSource;
            this.thirdTintSource = thirdTintSource;
        }

        public TintSource getMainTintSource() {
            return mainTintSource;
        }

        public TintSource getSecondTintSource() {
            return secondTintSource;
        }

        public TintSource getThirdTintSource() {
            return thirdTintSource;
        }

    }

}
