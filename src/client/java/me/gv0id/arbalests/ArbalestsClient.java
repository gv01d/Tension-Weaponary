package me.gv0id.arbalests;

import me.gv0id.arbalests.client.property.bool.ModBoolProperties;
import me.gv0id.arbalests.client.property.numeric.ModNumericProperties;
import net.fabricmc.api.ClientModInitializer;

public class ArbalestsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModNumericProperties.inicialization();
		ModBoolProperties.inicialization();
	}
}