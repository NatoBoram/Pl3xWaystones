package com.natoboram.pl3xwaystones;

import static net.fabricmc.api.EnvType.SERVER;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.jetbrains.annotations.Nullable;

import com.google.common.io.MoreFiles;

import net.blay09.mods.waystones.api.Waystone;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.image.IconImage;

@Environment(value = SERVER)
public class Icons {
	private static final String DEFAULT_KEY = Icons.key("waystone");

	/** Registers an icon from the given path. */
	public static void register(final Path path) throws IOException {
		final String key = Icons.key(MoreFiles.getNameWithoutExtension(path));

		try (InputStream file = Files.newInputStream(path)) {
			final IconImage icon = new IconImage(key, ImageIO.read(file), "png");
			Pl3xMap.api().getIconRegistry().register(icon);
		}
	}

	/**
	 * Uses the icon of the block or of the waystone type, if there's one.
	 *
	 * @return the key of the icon to use for the given waystone.
	 */
	public static String resolve(final Waystone waystone, @Nullable final BlockState block) {
		final ResourceLocation id = block == null ? waystone.getWaystoneType()
			: BuiltInRegistries.BLOCK.getKey(block.getBlock());

		final String key = Icons.key(id.getPath());
		return Pl3xMap.api().getIconRegistry().has(key) ? key : DEFAULT_KEY;
	}

	/**
	 * Icons are named after their image in `assets/pl3xwaystones/icons/`.
	 *
	 * @returns the key of the icon, such as `pl3xwaystones_waystone`.
	 */
	private static String key(final String name) {
		return Mod.ID + "_" + name;
	}
}
