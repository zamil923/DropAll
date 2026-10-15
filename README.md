# DropAll — Fabric 1.21.11

Name: DropAll  
Package: official.zamil.dropall  
Version: 1.0.0+1.21.11  
Client-only. Java 21. Fabric Loader 0.18.4 or newer. Fabric API is not required.

## Install

Copy `mod/DropAll-1.0.0+1.21.11.jar` from the release ZIP into the `mods` folder
of your Minecraft 1.21.11 Fabric installation. Restart Minecraft.
Do not put the ZIP or the sources JAR in `mods`.

## Use

1. Open your inventory, chest, shulker box, ender chest, barrel, or another normal inventory screen.
2. Put down any stack attached to your mouse cursor.
3. Hover the mouse over the item you want to drop. No mouse click is needed.
4. Hold either Alt key and press Q once.

All stacks of that item type in the open screen, including the player inventory
shown underneath a container, are queued for dropping onto the ground.
For example, cobblestone in a chest AND your inventory is dropped together.
One stack is dropped each game tick (normally 20 stacks per second).
Release Q before triggering it again. Closing the screen cancels remaining drops.
Ordinary Q and Ctrl+Q behavior is preserved.

Matching is by item type: custom names, enchantments, durability and other
components do NOT distinguish items. For example, all diamond swords match,
including enchanted ones. Each queued stack is rechecked for unchanged contents
before its drop is sent. A changed stack is skipped.

## Scope

- Works on accessible slots of the currently open inventory/container.
- Closed chests, closed ender chests and items packed inside unopened shulker
  boxes/bundles cannot be searched or emptied by this client mod.
- Opening a shulker box lets you drop its contents. Hovering a shulker-box ITEM
  drops matching box items themselves, with their contents still inside.
- Creative inventory is intentionally unsupported; use survival inventory or a
  regular container. Crafting/trading/output-only slots are skipped to avoid
  performing recipes or trades. Disabled/locked slots are skipped.
- A second shortcut while the queue is active is ignored. Moving an item onto
  the mouse cursor cancels the remaining queue.
- Uses normal server-validated slot actions. Server restrictions still apply;
  plugin shop GUIs and custom virtual menus are not supported.
- Dropped items can be picked up again normally. There is no undo operation.
- Alt+Q is fixed, independent of Minecraft's rebindable Drop key.

## Build from source (Windows)

Install a Java 21 JDK, extract this ZIP, then run in the `source` folder:

```bat
gradlew.bat clean build
```

Or double-click `build-windows.bat`. The compiled mod is placed in `build/libs`.
Use `DropAll-1.0.0+1.21.11.jar`, not the `-sources.jar`.
An internet connection is needed for the first build to download Gradle,
Minecraft and Fabric dependencies. A preinstalled Gradle is not needed.

## Build from source (Linux/macOS)

```sh
chmod +x gradlew
./gradlew clean build
```

## Manual gameplay checks

- Inventory: multiple cobblestone stacks plus dirt; Alt+Q drops only cobblestone.
- Chest: cobblestone in chest and inventory; verify both are emptied.
- Repeat with a double chest, barrel, shulker box and ender chest.
- Hold Q: ensure the shortcut does not create a second job.
- Close the screen mid-job: verify remaining items stay in place.
- Move a pending stack: verify the replacement is skipped.
- Pick up an item onto the cursor: verify the job stops.
- Check Q/Ctrl+Q without Alt retain vanilla behavior.
- Check renamed/enchanted copies match by item type as documented.

See BUILD-STATUS.txt for what was actually verified in the build environment.
