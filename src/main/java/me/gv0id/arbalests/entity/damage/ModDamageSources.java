package me.gv0id.arbalests.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.DynamicRegistryManager;
import org.jetbrains.annotations.Nullable;

public class ModDamageSources extends DamageSources{

    public ModDamageSources(DynamicRegistryManager registryManager) {
        super(registryManager);
    }

    public DamageSource COPPER_DISC(Entity source, @Nullable Entity attacker) {
        return this.create(ModDamageTypes.SONIC_BOOM, source, attacker);
    }

    public DamageSource SONIC_BOOM(Entity source, @Nullable Entity attacker) {
        return this.create(ModDamageTypes.SONIC_BOOM, source, attacker);
    }


}
