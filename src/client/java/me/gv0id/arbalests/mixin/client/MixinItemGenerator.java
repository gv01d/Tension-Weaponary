package me.gv0id.arbalests.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Environment(EnvType.CLIENT)
@Mixin(ItemModelGenerator)
public interface MixinItemGenerator {

    @Accessor()

    @Invoker("register")
}
