package me.gv0id.arbalests.components.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;

public final class MultiChargedProjectilesComponent {
    public static final MultiChargedProjectilesComponent DEFAULT = new MultiChargedProjectilesComponent(List.of());

    // Codec (WTF IS A CODEC)
    public static final Codec<MultiChargedProjectilesComponent> CODEC = ItemStack.CODEC
            .listOf()
            .xmap(MultiChargedProjectilesComponent::new, MultiChargedProjectilesComponent::RetList);


    // Packet Codec (WTF IS A PACKET CODEC)
    public static final PacketCodec<RegistryByteBuf, MultiChargedProjectilesComponent> PACKET_CODEC = ItemStack.PACKET_CODEC
            .collect(PacketCodecs.toList())
            .xmap(MultiChargedProjectilesComponent::new, MultiChargedProjectilesComponent::RetList);

    private final List<List<ItemStack>> projectileChamber;

    private final List<ItemStack> projectiles;

    private List<ItemStack> RetList(){
        List<ItemStack> temp = this.projectileChamber.getFirst();

        int i = 0;
        for (List<ItemStack> l : this.projectileChamber){
            if (i > 0){
                temp.addAll(l);
            }
            i++;
        }

        return temp;
    }

    public static MultiChargedProjectilesComponent removeAmount(int amount, MultiChargedProjectilesComponent stack){
        MultiChargedProjectilesComponent temp = stack;
        for (int i = 0; (i < amount) && (i < temp.projectiles.size()); i++) {
            temp.projectiles.remove(i);
        }

        return  temp;
    }

    private MultiChargedProjectilesComponent(List<ItemStack> projectiles) {
        this.projectiles = projectiles;
        this.projectileChamber = List.of(projectiles);
    }

    public static MultiChargedProjectilesComponent of(ItemStack projectile) {
        return new MultiChargedProjectilesComponent(List.of(projectile.copy()));
    }

    public static MultiChargedProjectilesComponent ofList(List<ItemStack> projectiles) {
        return new MultiChargedProjectilesComponent(List.copyOf(Lists.<ItemStack, ItemStack>transform(projectiles, ItemStack::copy)));
    }

    public boolean contains(Item item) {
        for (ItemStack itemStack : this.projectiles) {
            if (itemStack.isOf(item)) {
                return true;
            }
        }

        return false;
    }

    public List<ItemStack> getProjectiles() {
        return this.projectiles;
    }

    public boolean isEmpty() {
        return this.projectiles.isEmpty();
    }

    public boolean equals(Object o) {

        if (this == o) {
            return true;
        } else {
            return o instanceof MultiChargedProjectilesComponent multiChargedProjectilesComponent && ItemStack.stacksEqual(this.projectiles, multiChargedProjectilesComponent.projectiles);
        }
    }

    public int hashCode() {
        return ItemStack.listHashCode(this.projectiles);
    }

    public String toString() {
        return "MultiChargedProjectiles[items=" + this.projectiles + "]";
    }
}
