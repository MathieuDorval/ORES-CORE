# Ore Generation Guide

Ores Core features a powerful and highly flexible ore generation engine. It allows you to create everything from standard underground deposits to rare, exotic veins in any dimension.

## ⛰️ Host Block Flexibility
One of the core strengths of Ores Core is its ability to generate ores inside **any solid, full block** (excluding complex blocks like chests, machines, or tile entities).
- **Vanilla & Modded:** You can inject ores into Stone, Deepslate, Netherrack, End Stone, or any custom block from other mods (e.g., custom marble, basalt, etc.).
- **Automatic Consistency:** The generated ore will automatically match the background texture of the host block to maintain visual consistency.

## 🛠️ Advanced Ore Properties
Ores are not just chunks of rock; they can have unique physical and visual properties defined in `properties.toml`:
- **Luminous:** Ores can emit light (e.g., a glowing Radioactive Uranite).
- **Particles:** Add custom periodic particles to ores for visual feedback.
- **Gravity:** Ores can be subject to gravity, falling like sand or gravel.
- **Custom Tools:** You can decide if an ore requires a **Pickaxe**, **Shovel**, **Axe**, or even more specific tools.
- **Tier Overrides:** You can decouple the mining level of an ore from its base material (e.g., an Iron Ore that requires a Diamond Pickaxe).

## ⚙️ Configuration Parameters (`ores-generation.toml`)

### Basic Settings
- **Target Material:** The material to be generated (e.g., `tin`).
- **Rarity:** How many times the engine attempts to spawn a vein per chunk.
- **Vein Size:** The maximum number of ore blocks in a single vein.

### 📊 Shapes & Distributions
- **Shapes:** 
    - `BLOB`: The standard Minecraft ore cluster.
    - `PLATE`: Flat, horizontal layers.
    - `HORIZONTAL`: Tube-like horizontal structures.
    - `VERTICAL`: Tall, column-like structures.
    - `SCATTERED`: Small, scattered clusters over a wide area.
- **Distributions:**
    - `UNIFORM`: Equal chance across all heights.
    - `TRAPEZOID`: Higher concentration in the center of the range.
    - `GAUSSIAN`: Bell-curve distribution.
    - `TRIANGLE_HIGH`: Concentration at the top of the range.
    - `TRIANGLE_LOW`: Concentration at the bottom of the range.

### 🧠 Advanced Logic
- **Giant Veins:** Define massive, rare geological formations similar to Iron-Tuff veins.
- **Multi-Material Veins:** Mix different ores together in one vein with weighted ratios.
- **Unique Rules:**
    - `AIR_ONLY`: Generates veins floating in the air (Floating Islands).
    - `CAVE_ONLY`: Generates only on exposed cave walls as a flat layer.
    - `WATER_ONLY`: Generates only inside water-logged blocks.
    - `LAVA_ONLY`: Generates only at the contact of lava.
- **Filtering:** Use **Whitelists** and **Blacklists** for both **Biomes** and **Dimensions** to restrict generation precisely where you want it.

> 💡 **Tip:** Use the **[Ore Gen Manager](https://mathieudorval.github.io/ORES-CORE/tools/ore_gen_manager.html)** to generate your configuration files visually and avoid errors in your TOML syntax!
