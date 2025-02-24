package me.gv0id.arbalests.client.data.models;

import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CustomModel extends Model {
    public CustomModel(Optional<Identifier> parent, Optional<String> variant, TextureKey... requiredTextureKeys) {
        super(parent, variant, requiredTextureKeys);
    }

}
