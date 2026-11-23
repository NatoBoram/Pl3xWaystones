# Copilot instructions

Server-side Fabric mod that displays Waystones, Sharestones and Warp Plates on Pl3xMap. Targets Minecraft 1.21.1 with official Mojang mappings, Java 21 bytecode and JDK 27 for Gradle/CI.

## Commands

```sh
./gradlew build
./gradlew spotlessApply
./gradlew runServer
```

CI runs `build`, `spotlessCheck` and `javadoc`.

## Architecture

- **Lifecycle and registration order**: `Main.onInitialize` defers registration to `ServerLifecycleEvents.SERVER_STARTING` because Pl3xMap's API is only available after its own entrypoint and Pl3xMap enables itself on `SERVER_STARTED`. Instantiating `WaystoneBlocks` there ensures its persistent Fabric Attachment (`pl3xwaystones:waystone_blocks`) is registered before worlds load.
- **Pl3xMap events**: `Pl3xMapListener` handles `Pl3xMapEnabledEvent` (re-registering all `*.png` files in `assets/pl3xwaystones/icons` on every enable or reload because Pl3xMap clears its icon registry) and `WorldLoadedEvent` (registering a `WaystonesLayer` per world).
- **Threading and chunk-unloaded state**: Pl3xMap invokes `WaystonesLayer.getMarkers()` off the server thread, so it dispatches work via `level.getServer().submit(...).join()`. Because Waystones does not expose block variants or warp plate status for unloaded chunks, `WaystonesLayer` reads `BlockState` from loaded chunks, falls back to the last seen `BlockState` stored in the world's `WaystoneBlocks` persistent attachment and writes the updated map back to the attachment. Until a chunk is loaded with this mod installed, waystones use the default icon and warp plates stay hidden.
- **Visibility rules**: `WaystonesLayer` shows named, non-transient entries only: public waystones (`ACTIVATION` or `GLOBAL`, excluding team-only), all sharestones and warp plates whose `WarpPlateBlock.STATUS` is `IDLE`, `WARPING` or `WARPING_INVALID`. Tooltip text is HTML-escaped with Guava's `HtmlEscapers`.

## Conventions

- **Icons**: Add PNGs to `src/main/resources/assets/pl3xwaystones/icons/` named after the block ID (such as `mossy_waystone.png`, `red_sharestone.png` or `warp_plate.png`). `Icons` registers each under `pl3xwaystones_<filename>`, resolves by block ID (or waystone type when the block has not been seen) and falls back to `waystone.png`.
- **Pl3xMap nested runtime dependencies**: Loom strips jars nested inside Pl3xMap, so `cloud-fabric` and `adventure-platform-fabric` are declared as `modLocalRuntime` in `build.gradle`. Keep their versions in `gradle.properties` matched to the jars nested in `pl3xmap_version`.
- **Jar packaging**: Add `LICENSE.md` and `README.md` only to `org.gradle.api.tasks.bundling.Jar` tasks (Loom propagates them into remapped jars without duplicates) and configure `archiveVersion` on `org.gradle.jvm.tasks.Jar` so output filenames include `+${minecraft_version}` without changing `project.version` or `fabric.mod.json`.
