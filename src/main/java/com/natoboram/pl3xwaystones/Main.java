package com.natoboram.pl3xwaystones;

import static net.fabricmc.api.EnvType.SERVER;

import com.natoboram.pl3xwaystones.attachments.WaystoneBlocks;

import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.pl3x.map.core.Pl3xMap;

@Environment(value = SERVER)
public class Main implements ModInitializer {

	/**
	 * This code runs as soon as Minecraft is in a mod-load-ready state. However, some things (like resources) may still
	 * be uninitialized. Proceed with mild caution.
	 */
	@Override
	public void onInitialize() {

		// Pl3xMap's API is available after its own entrypoint and it enables itself on `SERVER_STARTED`.
		ServerLifecycleEvents.SERVER_STARTING
			.register(server -> {
				final Pl3xMapListener listener = new Pl3xMapListener(new WaystoneBlocks());
				Pl3xMap.api().getEventRegistry().register(listener);
			});

		Mod.LOGGER.info("Loaded Pl3xWaystones.");
	}
}
