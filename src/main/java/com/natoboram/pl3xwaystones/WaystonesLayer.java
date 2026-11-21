package com.natoboram.pl3xwaystones;

import static net.fabricmc.api.EnvType.SERVER;

import java.util.Collection;
import java.util.List;

import com.google.common.html.HtmlEscapers;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.pl3x.map.core.markers.layer.WorldLayer;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.world.World;

/** Shows the waystones of a world on Pl3xMap. */
@Environment(value = SERVER)
public class WaystonesLayer extends WorldLayer {

	public static final String ICON = Main.MOD_ID + "_waystone";
	private static final int ICON_SIZE = 16;

	private static Marker<?> marker(final Waystone waystone) {
		final BlockPos pos = waystone.getPos();
		final String name = HtmlEscapers.htmlEscaper().escape(waystone.getName().getString());

		return Marker.icon(waystone.getWaystoneUid().toString(), pos.getX(), pos.getZ(), ICON, ICON_SIZE)
			.setOptions(Options.builder().tooltipContent(name));
	}

	private static List<Marker<?>> markers(final ServerLevel level) {
		return WaystonesAPI.getWaystonesByType(level.getServer(), WaystoneTypes.WAYSTONE)
			.filter(waystone -> waystone.getDimension().equals(level.dimension()))
			.map(WaystonesLayer::marker)
			.toList();
	}

	WaystonesLayer(final World world) {
		super(Main.MOD_ID, world, () -> "Waystones");
	}

	/** Pl3xMap calls this from its own thread, so waystones are read on the server thread. */
	@Override
	public Collection<Marker<?>> getMarkers() {
		final ServerLevel level = getWorld().getLevel();
		return level.getServer().submit(() -> markers(level)).join();
	}
}
