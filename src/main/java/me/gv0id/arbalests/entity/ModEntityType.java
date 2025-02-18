package me.gv0id.arbalests.entity;

import me.gv0id.arbalests.entity.projectile.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;


public class ModEntityType {
    public static final EntityType<WindGaleEntity> WIND_GALE = register(
            "wind_gale",
            EntityType.Builder.<WindGaleEntity>create(WindGaleEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.3125F, 0.3125F)
                    .eyeHeight(0.0F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );
    public static final EntityType<MusicDiscEntity> MUSIC_DISC = register(
            "music_disc_entity",
            EntityType.Builder.<MusicDiscEntity>create(MusicDiscEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.3F, 0.3F)
                    .eyeHeight(0.2F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );
    public static final  EntityType<EndCrystalProjectileEntity> END_CRYSTAL_PROJECTILE = register(
            "end_crystal_projectile",
            EntityType.Builder.<EndCrystalProjectileEntity>create(EndCrystalProjectileEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(1.2F,1.2F)
                    .eyeHeight(0.6F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );


    public static final EntityType<CustomEnderPearlEntity> CUSTOM_ENDER_PEARL = register(
            "custom_ender_pearl",
            EntityType.Builder.<CustomEnderPearlEntity>create(CustomEnderPearlEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.25F, 0.25F)
                    .eyeHeight(0.125F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );

    public static final EntityType<SnowProjectileEntity> CUSTOM_SNOWBALL = register(
            "custom_snowball",
            EntityType.Builder.<SnowProjectileEntity>create(SnowProjectileEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.25F, 0.25F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );

    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("arbalests",id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }


}
