package com.natoboram.pl3xwaystones;

import static net.fabricmc.api.EnvType.SERVER;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.fabricmc.api.Environment;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.event.EventHandler;
import net.pl3x.map.core.event.EventListener;
import net.pl3x.map.core.event.server.Pl3xMapEnabledEvent;
import net.pl3x.map.core.event.world.WorldLoadedEvent;
import net.pl3x.map.core.image.IconImage;

/** Adds the waystones layer to every world loaded by Pl3xMap. */
@Environment(value = SERVER)
public class Pl3xMapListener implements EventListener {

	/** Pl3xMap clears its icons every time it's enabled, including on reload. */
	@EventHandler
	public void onPl3xMapEnabled(final Pl3xMapEnabledEvent event) throws IOException {
		final BufferedImage image = ImageIO.read(Pl3xMapListener.class.getResource("/assets/pl3xwaystones/waystone.png"));
		Pl3xMap.api().getIconRegistry().register(new IconImage(WaystonesLayer.ICON, image, "png"));
	}

	@EventHandler
	public void onWorldLoaded(final WorldLoadedEvent event) {
		event.getWorld().getLayerRegistry().register(new WaystonesLayer(event.getWorld()));
	}
}
