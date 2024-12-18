package me.gv0id.arbalests.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.UnaryOperator;

@Mixin(DataComponentTypes.class)
public interface ModDataComponentTypesMx {

    @Invoker("register")
    public static <T> ComponentType<T> invokeRegister(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return null;
    }
}
