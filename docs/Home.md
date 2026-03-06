# Welcome to Ores Core Wiki

**Ores Core** is a high-performance, dynamic library designed to solve material fragmentation and repetitive resource creation in modded Minecraft.

## General Functioning
At its heart, Ores Core manages a massive database of materials and standardizes them. Depending on user configurations or mod request via `registry.json`, Ores Core dynamically generates:
- **Items:** Ingots, nuggets, dusts, gears, plates, raw materials, shards, clumps, crystals, dirty dusts, rods, and more...
- **Blocks:** Storage blocks, raw blocks, and extremely compressed variants up to tier 9...
- **Ores:** Endless combinations of materials within any host stone (Stone, Deepslate, Netherrack, End Stone, or any custom modded stone).
- **Chemicals (Mekanism compat):** Dynamic Slurries (both clean and dirty versions) for every supported material.
- **Recipes & Tags:** Automatic `#c:` item tagging, smelting, blasting, crushing, milling (Create), and full Mekanism processing chains.

It does all of this completely dynamically at runtime. Only the items and variants actively requested are loaded into the game's memory, ensuring a **zero-dependency, blazing-fast** experience.

## Navigating the Wiki
- **[For Players](For-Players.md)**: A simple guide on how to install and what to expect as a player.
- **[For Mod Creators](For-Mod-Creators.md)**: Learn how to offload your material registration to Ores Core and integrate custom terrain.
- **[For Modpack Creators](For-Modpack-Creators.md)**: Learn how to manage the global economy, unify your pack, and master ore generation with Ores Mods.
- **[Ore Generation Guide](Ore-Generation.md)**: Detailed documentation on vein shapes, distributions, and custom ore properties.
- **[Textures and Models](Textures-and-Models.md)**: How Ores Core creates its visual items dynamically.
- **[Compatibility Guide](Compatibility-Guide.md)**: How Ores Core unifies mods, standardizes tags, and handles dynamic recipes.
- **[API Reference](API-Reference.md)**: Documentation on interacting with Ores Core programmatically.
