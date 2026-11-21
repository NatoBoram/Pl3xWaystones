package com.natoboram.pl3xwaystones;

import static net.fabricmc.api.EnvType.SERVER;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.google.common.io.MoreFiles;

import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.event.EventHandler;
import net.pl3x.map.core.event.EventListener;
import net.pl3x.map.core.event.server.Pl3xMapEnabledEvent;
import net.pl3x.map.core.event.world.WorldLoadedEvent;
import net.pl3x.map.core.image.IconImage;

/** Adds the waystones layer to every world loaded by Pl3xMap. */
@Environment(value = SERVER)
public class Pl3xMapListener implements EventListener {

	private static void registerIcon(final Path file) throws IOException {
		final String icon = WaystonesLayer.icon(MoreFiles.getNameWithoutExtension(file));

		try (InputStream image = Files.newInputStream(file)) {
			Pl3xMap.api().getIconRegistry().register(new IconImage(icon, ImageIO.read(image), "png"));
		}
	}

	/** Pl3xMap clears its icons every time it's enabled, including on reload. */
	@EventHandler
	public void onPl3xMapEnabled(final Pl3xMapEnabledEvent event) throws IOException {
		final Path icons = FabricLoader.getInstance()
			.getModContainer(Main.MOD_ID)
			.flatMap(mod -> mod.findPath("assets/pl3xwaystones/icons"))
			.orElseThrow();

		try (DirectoryStream<Path> files = Files.newDirectoryStream(icons, "*.png")) {
			for (final Path file : files)
				registerIcon(file);
		}
	}

	@EventHandler
	public void onWorldLoaded(final WorldLoadedEvent event) {
		event.getWorld().getLayerRegistry().register(new WaystonesLayer(event.getWorld()));
	}
}
