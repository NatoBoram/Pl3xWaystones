package com.natoboram.pl3xwaystones;

import static net.fabricmc.api.EnvType.SERVER;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.html.HtmlEscapers;

import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTypes;
import net.blay09.mods.waystones.api.WaystoneVisibility;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.pl3x.map.core.markers.layer.WorldLayer;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.world.World;

/** Shows the waystones, sharestones and warp plates of a world on Pl3xMap. */
@Environment(value = SERVER)
public class WaystonesLayer extends WorldLayer {
	private static final int ICON_SIZE = 16;

	/** Reads the blocks of waystones in loaded chunks and remembers the others. */
	private static Map<UUID, BlockState> blocks(final ServerLevel level, final List<Waystone> waystones) {
		final Map<UUID, BlockState> lastSeen = level.getAttachedOrElse(Main.WAYSTONE_BLOCKS, Map.of());
		final Map<UUID, BlockState> blocks = new HashMap<>();

		for (final Waystone waystone : waystones) {
			final BlockPos pos = waystone.getPos();
			final BlockState block = level.isLoaded(pos) ? level.getBlockState(pos) : lastSeen.get(waystone.getWaystoneUid());
			if (block != null)
				blocks.put(waystone.getWaystoneUid(), block);
		}

		return blocks;
	}

	/** Activated warp plates have an attuned shard and aren't disabled by redstone. */
	private static boolean isActivated(@Nullable final BlockState block) {
		if (block == null || !block.hasProperty(WarpPlateBlock.STATUS))
			return false;

		return switch (block.getValue(WarpPlateBlock.STATUS)) {
			case IDLE, WARPING, WARPING_INVALID -> true;
			default -> false;
		};
	}

	/** Waystones on a team are private. */
	private static boolean isPublic(final WaystoneVisibility visibility) {
		return switch (visibility) {
			case ACTIVATION, GLOBAL -> true;
			default -> false;
		};
	}

	/** Waystones get a name when they're discovered. */
	private static boolean isShown(final Waystone waystone, @Nullable final BlockState block) {
		if (waystone.isTransient() || !waystone.hasName())
			return false;

		final ResourceLocation type = waystone.getWaystoneType();
		if (WaystoneTypes.isSharestone(type))
			return true;
		if (type.equals(WaystoneTypes.WARP_PLATE))
			return isActivated(block);
		if (type.equals(WaystoneTypes.WAYSTONE))
			return isPublic(waystone.getVisibility());
		return false;
	}

	private static Marker<?> marker(final Waystone waystone, @Nullable final BlockState block) {
		final BlockPos pos = waystone.getPos();
		final String name = HtmlEscapers.htmlEscaper().escape(waystone.getName().getString());

		return Marker
			.icon(waystone.getWaystoneUid().toString(), pos.getX(), pos.getZ(), Icons.resolve(waystone, block), ICON_SIZE)
			.setOptions(Options.builder().tooltipContent(name));
	}

	private static List<Marker<?>> markers(final ServerLevel level) {
		final List<Waystone> waystones = WaystonesAPI.getAllWaystones(level.getServer())
			.filter(waystone -> waystone.getDimension().equals(level.dimension()))
			.toList();

		final Map<UUID, BlockState> blocks = blocks(level, waystones);
		level.setAttached(Main.WAYSTONE_BLOCKS, blocks);

		return waystones.stream()
			.filter(waystone -> isShown(waystone, blocks.get(waystone.getWaystoneUid())))
			.<Marker<?>>map(waystone -> marker(waystone, blocks.get(waystone.getWaystoneUid())))
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
