# For Modpack Creators

Managing a modpack's progression and keeping the player's inventory clean is one of the hardest challenges. **Ores Core**, combined with its official companion mod **Ores Mods**, solves this completely.

## 🧩 Ores Mods: The Ultimate Companion

**Ores Mods** is a powerful companion to Ores Core specifically designed to solve the common issue of cluttered and duplicated resources in significantly heavily modded environments.

### 1. Total Material Unification
Say goodbye to having 5 different types of Copper or Lead in your inventory. Ores Mods automatically unifies identical resources (ores, ingots, nuggets, raw materials, plates, gears, etc.) from different mods into a single type, hiding duplicates from JEI/EMI. This keeps your inventory clean and crafting recipes seamless.

### 2. Infinite Ore Generation
Generate an infinite variety of ores combining any material type with *any* stone or block type from *any* installed mod. You are no longer limited by what mods provide out of the box. Manage **all ore generation completely from scratch** in a highly customized and granular way.

**Download Ores Mods:** [Modrinth](https://modrinth.com/project/ores-mods) | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ores-mods)

## ⚙️ Understanding the Configuration

Ores Mods and Ores Core supply unparalleled control via configuration files, **all of which are also fully accessible and editable in-game** through a dedicated configuration screen. This allows for instant feedback and visual management of your world's economy.

You can manage the totality of items, blocks, ores, generation rules, and unique properties directly:

### `ores-generation.toml`
This file completely dictates how ores spawn in the world. It provides fine-tuned control over vein sizes, shapes, heights, and distribution. Apply custom rules to *any mod*, *any biome*, and *any dimension*.

- **Target Material:** Select which material to generate.
- **Vein Shapes:** Choose from `BLOB` (classic blob), `PLATE` (flat/sheet-like), `HORIZONTAL` (horizontal tubes), `VERTICAL` (vertical columns), or `SCATTERED`.
- **Distributions:** Advanced vertical control with `UNIFORM`, `TRAPEZOID`, `GAUSSIAN`, `TRIANGLE_HIGH`, or `TRIANGLE_LOW`.
- **Giant Veins:** Create massive custom deposits (like vanilla **Iron-Tuff** or **Copper-Granite** veins) by replacing specific blocks and adding rare "Bonus Blocks" (like Raw Blocks).
- **Multi-Material Veins:** Mix multiple materials in a single vein with customizable ratio weights.
- **Unique Specifics:** Apply specialized generation rules like `AIR_ONLY`, `CAVE_ONLY`, `WATER_ONLY`, `LAVA_ONLY`, or `UNIQUE`.
- **Filtering:** Full control via **Whitelist/Blacklist** for both **Dimensions** and **Biomes** (vanilla or modded).
- **Rarity & Altitude:** Precise control over Min/Max Y and spawn frequency.

### `properties.toml`
Allows overriding the default statistics for *every single material* (hardness, resistance, mining level, light emitted, item drops, colors, and much more). For example, you can create a unique Uranium Ore that emits glowing green particles or a specific light level to warn players of its presence, or simply adjust Silver to drop more XP.

> 💡 **Tip:** Use the online **[Ore Gen Manager](https://mathieudorval.github.io/ORES-CORE/tools/ore_gen_manager.html)**, **[Registry Generator](https://mathieudorval.github.io/ORES-CORE/tools/registry_generator.html)** and **[Material Reference](https://mathieudorval.github.io/ORES-CORE/tools/material_reference.html)** to generate these config files instantly.

### 🪨 Custom Host Stones (Blocks Replacement)
Modpack creators can define **any block from any mod** to serve as a host stone for ores. 
- You are not limited to Stone, Deepslate, or Netherrack. If your pack adds custom marbles, granites, or even mystical blocks, you can register them as "Stones Replacement".
- Ores Core will automatically generate and spawn the corresponding ore variations inside these custom blocks, ensuring visual and physical consistency throughout your world.

### 🪙 Independent Item Creation
One of the most powerful features for modpack authors is the ability to generate items **even if no other mod provides them**.
- **Example - Economy System:** You can create "Coins" for every single material (Iron Coin, Gold Coin, Tin Coin, etc.) simply by declaring them in your config. This is perfect for setting up a unified currency system or advanced quest rewards without needing to install separate "economy" mods.

### 🚫 Item Blacklisting
If you find that Ores Core is generating items that you don't want in your pack (to simplify the progression or reduce clutter), you can use the **Global Blacklist**.
- You can blacklist specific items (like gears or plates for certain materials) to prevent them from being registered and showing up in-game.
- This gives you total control over exactly which resources are available to the player.

> 💡 **Tip:** Use the **[Registry Generator](https://mathieudorval.github.io/ORES-CORE/tools/registry_generator.html)** to easily generate the JSON configuration for custom host stones and independent items.
