package com.natoboram.pl3xwaystones;

import static net.fabricmc.api.EnvType.SERVER;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.natoboram.pl3xwaystones.attachments.WaystoneBlocks;

import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.pl3x.map.core.event.EventHandler;
import net.pl3x.map.core.event.EventListener;
import net.pl3x.map.core.event.server.Pl3xMapEnabledEvent;
import net.pl3x.map.core.event.world.WorldLoadedEvent;

/** Adds the waystones layer to every world loaded by Pl3xMap. */
@Environment(value = SERVER)
public class Pl3xMapListener implements EventListener {
	private final WaystoneBlocks WAYSTONE_BLOCKS;

	public Pl3xMapListener(final WaystoneBlocks attachments) {
		WAYSTONE_BLOCKS = attachments;
	}

	/** Pl3xMap clears its icons every time it's enabled, including on reload. */
	@EventHandler
	public void onPl3xMapEnabled(final Pl3xMapEnabledEvent event) throws IOException {
		final Path paths = FabricLoader.getInstance()
			.getModContainer(Mod.ID)
			.flatMap(mod -> mod.findPath("assets/pl3xwaystones/icons"))
			.orElseThrow();

		try (DirectoryStream<Path> streams = Files.newDirectoryStream(paths, "*.png")) {
			for (final Path path : streams)
				Icons.register(path);
		}
	}

	@EventHandler
	public void onWorldLoaded(final WorldLoadedEvent event) {
		final WaystonesLayer layer = new WaystonesLayer(event.getWorld(), WAYSTONE_BLOCKS);
		event.getWorld().getLayerRegistry().register(layer);
	}
}
