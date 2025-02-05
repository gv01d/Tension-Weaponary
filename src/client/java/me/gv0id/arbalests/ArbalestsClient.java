package me.gv0id.arbalests;

import me.gv0id.arbalests.client.property.bool.ModBoolProperties;
import me.gv0id.arbalests.client.property.numeric.ModNumericProperties;
import me.gv0id.arbalests.client.property.select.ModSelectProperties;
import me.gv0id.arbalests.client.particles.ModParticles;
import me.gv0id.arbalests.client.render.item.tint.ModTintSource;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ArbalestsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModNumericProperties.initialization();
		ModBoolProperties.initialization();
		ModSelectProperties.initialization();
		ModParticles.initialization();
		ModTintSource.initialization();
		/*
		HudRenderCallback.EVENT.register(((drawContext, renderTickCounter) -> {

		}));
		*/
	}
}