package com.natoboram.pl3xwaystones;

import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import static net.fabricmc.api.EnvType.SERVER;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value = SERVER)
public class Main implements ModInitializer {
	public static final String MOD_ID = "pl3xwaystones";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Loaded Pl3xWaystones.");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
