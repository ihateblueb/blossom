# Blossom

A (work in progress) Hytale mod for essential player & admin utilities, written by a real person and
not a LLM.

This mod uses a custom placeholder system. Placeholders start with `$` and the placeholder's name follows
(e.g. `%playerUsername%`). It also has a custom color system. There's a few predefined colors available in the
configuration, and you can also use hex codes (e.g. `<blue>` or `<#000000>` or `<bold>`). Hex codes must be six characters
long and start with a #.

If you would like to use Blossom's placeholder system or color code system, you can use the `blossom-formatter` module
in your mod.

Currently, Blossom has the following implemented:

- tpask (ask to teleport to another player, who can then accept or deny)
  - tpaccept
  - tpdeny
- msg (Privately message other players)
- reply (Reply to last person who messaged you)
- Discord bridge
  - Server startup and shutdown
  - Join and leaves
  - Messages from Hytale to Discord
  - Messages from Discord to Hytale
- Chat formatting

More is coming soon, including:

- Improved join server messages
- Per-user custom join messages
- User-equipable chat prefixes called tags
- Discord account linking
- Giving Discord roles to players based on permissions
- tpahere (Teleport another player to you)
- tpcancel (Cancel all outgoing teleportation requests)
- AFK system
- Waypoint system
  - Waypoints on the map and compass
  - Potentially visuals for when someone teleports to a warp

## Building

Add a `libs` folder, and add the HytaleServer.jar into that folder. 
Afterward, Gradle will be able to resolve the Hytale APIs and the mod will be buildable.