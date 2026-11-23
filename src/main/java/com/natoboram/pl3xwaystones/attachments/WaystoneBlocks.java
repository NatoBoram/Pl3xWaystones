package com.natoboram.pl3xwaystones.attachments;

import java.util.Map;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.natoboram.pl3xwaystones.Mod;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class WaystoneBlocks {
	/**
	 * The block last seen at each waystone of a world, since their chunk isn't always loaded. It must be registered
	 * before worlds load.
	 */
	private static final AttachmentType<Map<UUID, BlockState>> WAYSTONE_BLOCKS = AttachmentRegistry
		.createPersistent(id("waystone_blocks"), Codec.unboundedMap(UUIDUtil.STRING_CODEC, BlockState.CODEC));

	private static ResourceLocation id(final String path) {
		return ResourceLocation.fromNamespaceAndPath(Mod.ID, path);
	}

	public Map<UUID, BlockState> getAttached(final ServerLevel level) {
		return level.getAttachedOrElse(WAYSTONE_BLOCKS, Map.of());
	}

	public void setAttached(final ServerLevel level, final Map<UUID, BlockState> blocks) {
		level.setAttached(WAYSTONE_BLOCKS, blocks);
	}
}
