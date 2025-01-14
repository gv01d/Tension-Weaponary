package me.gv0id.arbalests.client.data;

import me.gv0id.arbalests.Arbalests;
import me.gv0id.arbalests.client.property.bool.CopperDiscChargedProperty;
import me.gv0id.arbalests.client.property.bool.DeadbeatCrossbowChargingProperty;
import me.gv0id.arbalests.client.property.numeric.DeadbeatCrossbowPullProperty;
import me.gv0id.arbalests.client.property.select.*;
import me.gv0id.arbalests.client.render.item.tint.MainChargePotionTintSource;
import me.gv0id.arbalests.client.render.item.tint.SecondChargePotionTintSource;
import me.gv0id.arbalests.client.render.item.tint.ThirdChargePotionTintSource;
import me.gv0id.arbalests.item.ModItems;
import me.gv0id.arbalests.item.custom.CopperDiscItem;
import me.gv0id.arbalests.item.custom.DeadbeatCrossbowItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

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

        ArrayList<SelectItemModel.SwitchCase<CopperDiscItem.Music>> varList = new ArrayList<>();

        CopperDiscItem.Music[] projArray = CopperDiscItem.Music.values();

        for (CopperDiscItem.Music music : projArray){
                varList.add(
                        ItemModels.switchCase(music, ItemModels
                                .basic(itemModelGenerator.registerSubModel(item,"_" + music.asString(), ModModels.COPPER_DISC)))
                );
        }

        itemModelGenerator.output.accept(item,
                ItemModels.condition(
                        new CopperDiscChargedProperty(),
                        ItemModels.select(
                                new CopperDiscVariationTypeProperty(),
                                unbaked,
                                varList
                        ),
                        unbaked
                )

        );
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
            if (i > 0 && !projectile.isCollection() && !projectile.isTinted() && !projectile.isVariation()) {
                Arbalests.LOGGER.info("<> Generated charge name: _{}", projectile.getName());

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
                Arbalests.LOGGER.info("<Tinted> Generated charge name: _{}", projectile.getName());

                mainChargeList.add(unbaked_tinted_builder(item, "_", "deadbeat_main_charge", projectile ,itemModelGenerator, tintSourceEnum.valueOf(projectile.getEnumName()).mainTintSource ));
                secondChargeList.add(unbaked_tinted_builder(item, "_second_", "deadbeat_charge", projectile ,itemModelGenerator, tintSourceEnum.valueOf(projectile.getEnumName()).secondTintSource));
                thirdChargeList.add(unbaked_tinted_builder(item, "_third_", "deadbeat_charge", projectile ,itemModelGenerator, tintSourceEnum.valueOf(projectile.getEnumName()).thirdTintSource));

            }
            else if (projectile.isVariation()){
                Arbalests.LOGGER.info("<Var> Generated charge name: _{}", projectile.getName());

                mainChargeList.add(unbaked_variation_builder(item, "_", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE, projectile, itemModelGenerator, VarSourceEnum.valueOf(projectile.getEnumName())));
                secondChargeList.add(unbaked_variation_builder(item, "_second_", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE, projectile, itemModelGenerator, VarSourceEnum.valueOf(projectile.getEnumName())));
                thirdChargeList.add(unbaked_variation_builder(item, "_third_", ModModels.DEADBEAT_CROSSBOW_MAIN_CHARGE, projectile, itemModelGenerator, VarSourceEnum.valueOf(projectile.getEnumName())));
            }
            i++;
        }
        // - - -

        Arbalests.LOGGER.info("Creating output...");

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

        Arbalests.LOGGER.info("Finished output for < registerDeadbeatCrossbow2 >");

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

    private SelectItemModel.SwitchCase<DeadbeatCrossbowItem.Projectiles> unbaked_variation_builder(
            Item item,
            String prefix,
            Model parent,
            DeadbeatCrossbowItem.Projectiles projectile,
            ItemModelGenerator itemModelGenerator,
            VarSourceEnum varSourceEnum
    ) {
        ArrayList<SelectItemModel.SwitchCase<ItemVariation>> variationList = new ArrayList<>();

        Arbalests.LOGGER.info("     Name : {}", prefix + projectile.asString() );
        ItemModel.Unbaked unbaked = ItemModels.basic(itemModelGenerator.registerSubModel(item, prefix + projectile.asString(), parent));

        for (ItemVariation itemVariation : varSourceEnum.variationItems){
            Arbalests.LOGGER.info("         Variation Name : {}", prefix + projectile.asString() + "_" + itemVariation.asString() );
            variationList.add(ItemModels.switchCase(
                    itemVariation, ItemModels.basic(itemModelGenerator.registerSubModel(item, prefix + "" + projectile.asString() + "_" + itemVariation.asString(), parent)))
            );
        }


        return ItemModels.switchCase(
                projectile,
                ItemModels.select(
                        varSourceEnum.getVariationSelectProperty(),
                        unbaked,
                        variationList
                )
        );
    }

    public String itemName(Item item){
        return (item.toString().split(":"))[1];
    }

    public static ItemVariation getItemVariation(ItemStack stack){
        for(ItemVariation itemVariation : ItemVariation.values()){
            if (stack.isOf(itemVariation.item)) return itemVariation;
        }
        return ItemVariation.NONE;
    }

    public static enum ItemVariation implements StringIdentifiable {
        NONE(null),
        D13(Items.MUSIC_DISC_13),
        D11(Items.MUSIC_DISC_11),
        D_BLOCKS(Items.MUSIC_DISC_BLOCKS),
        D_CAT(Items.MUSIC_DISC_CAT),
        D_5(Items.MUSIC_DISC_5),
        D_CHIRP(Items.MUSIC_DISC_CHIRP),
        D_CREATOR(Items.MUSIC_DISC_CREATOR),
        D_CREATOR_MUSIC_BOX(Items.MUSIC_DISC_CREATOR_MUSIC_BOX),
        D_FAR(Items.MUSIC_DISC_FAR),
        D_MALL(Items.MUSIC_DISC_MALL),
        D_MELLOHI(Items.MUSIC_DISC_MELLOHI),
        D_OTHERSIDE(Items.MUSIC_DISC_OTHERSIDE),
        D_PIGSTEP(Items.MUSIC_DISC_PIGSTEP),
        D_PRECIPICE(Items.MUSIC_DISC_PRECIPICE),
        D_RELIC(Items.MUSIC_DISC_RELIC),
        D_STAL(Items.MUSIC_DISC_STAL),
        D_STRAD(Items.MUSIC_DISC_STRAD),
        D_WAIT(Items.MUSIC_DISC_WAIT),
        D_WARD(Items.MUSIC_DISC_WARD);


        public static final EnumCodec<ModModelProvider.ItemVariation> CODEC = StringIdentifiable.createCodec(ModModelProvider.ItemVariation::values);
        Item item = null;

        ItemVariation(Item item){
            this.item = item;
        }

        @Override
        public String asString(){
            if (this.item == null)
                return "none";
            return (item.toString().split(":"))[1];
        }

    }

    public enum VarSourceEnum{
        MUSIC(ModItems.COPPER_DISC, new CopperDiscProjectileVariationTypeProperty(),
                ItemVariation.D13,
                ItemVariation.D11,
                ItemVariation.D_BLOCKS,
                ItemVariation.D_CAT,
                ItemVariation.D_5,
                ItemVariation.D_CHIRP,
                ItemVariation.D_CREATOR,
                ItemVariation.D_CREATOR_MUSIC_BOX,
                ItemVariation.D_FAR,
                ItemVariation.D_MALL,
                ItemVariation.D_MELLOHI,
                ItemVariation.D_OTHERSIDE,
                ItemVariation.D_PIGSTEP,
                ItemVariation.D_PRECIPICE,
                ItemVariation.D_RELIC,
                ItemVariation.D_STAL,
                ItemVariation.D_STRAD,
                ItemVariation.D_WAIT,
                ItemVariation.D_WARD
        );

        Item item = null;
        ItemVariation[] variationItems;
        SelectProperty<?> variationSelectProperty;

        VarSourceEnum( Item item, SelectProperty<?> variationSelectProperty, ItemVariation ...variationItems){
            this.item = item;
            this.variationSelectProperty = variationSelectProperty;
            this.variationItems = variationItems;
        }

        public <T> SelectProperty<T> getVariationSelectProperty() {
            return (SelectProperty<T>) variationSelectProperty;
        }
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
