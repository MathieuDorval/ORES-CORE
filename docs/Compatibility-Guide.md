# Compatibility Guide

Ores Core is designed with a "Universal Bridge" philosophy. Our goal is to ensure that a modpack feels like a single, cohesive game rather than a collection of separate mods.

## 🤝 Universal Tag Compatibility
Any mod is **instantly compatible** with Ores Core if it follows standard Minecraft tagging conventions. 
- Ores Core automatically registers every generated item, block, and chemical into the common `#c:` tags (e.g., `#c:ingots/tin`, `#c:ores/copper`).
- If your mod uses these tags in its recipes, machines, or loot tables, it will work perfectly with Ores Core right out of the box.

## 🛠️ Modpack Creator & Player Power
If you are a player or a modpack creator using a mod that *doesn't* natively support Ores Core or common tags, you can use our companion mod **Ores Mods**.

**Download Ores Mods:** [Modrinth](https://modrinth.com/project/ores-mods) | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ores-mods)

- **Ores Mods** can force compatibility by unifying duplicate items and recipes.
- It can replace non-standard items with Ores Core equivalents in existing mod inventories and machine outputs, ensuring a 100% unified experience even for legacy mods.

## Automated Recipes (Core, Mekanism & Create)
You don't need to manually write recipe JSONs for standard processing. Ores Core **dynamically generates** full processing chains for:

- **Core Recipes:** 
    - Full compression/decompression cycles: **Nuggets ↔ Base Items ↔ Blocks ↔ Compressed Blocks** (supporting up to Tier 9).
    - Crafting recipes for all **Raw materials** and **Dusts**.
- **Smelting & Blasting:** 
    - High-efficiency smelting for all **Ores, Raw Materials, and Dusts**.
    - Bulk smelting support: smelt **Raw Blocks, Dust Blocks**, and even **Compressed forms** directly into their final item or block equivalents.
- **Mekanism:** Dynamic Chemical Dissolution, Washing, Crystallizing, and regular crushing/enriching.
- **Create:** Automated Milling and Crushing Wheel recipes for all registered materials.

## 💡 Custom Recipe Integration
For mods that utilize unique custom machines or non-standard recipe types:

- **Universal Tags:** If your machine or system already accepts standard common tags (`#c:`), it is **instantly compatible** with all Ores Core materials without any further action.
- **Native Integration:** If your mod adds a recipe type that is frequently used across many materials (like specialized furnaces, grinders, or magic altars), **we can integrate it directly into Ores Core**. This means you won't have to write hundreds of recipe JSONs—Ores Core will dynamically generate them for all supported materials.

## ❓ Missing a Material or Special Item?
Ores Core is built to be a universal tool crafted *for* the community. Our goal is to make it useful for every single mod and modpack.

- **One Line to Integrate:** Adding a new base material or a specific item type (like a new tool head, a specific gear variant, etc.) usually takes us **only a single line of code**.
- **Community Driven:** If you don't find the resource you need for your mod, please **don't hesitate to make a proposal**. We will add it to the core generation engine, making it instantly available and globally unified for the entire community!

👉 **[Submit a Content Proposal](https://github.com/MathieuDorval/ORES-CORE/issues/new?template=proposition_contenu.yml)**

Ores Core is designed to grow with you—whenever a new popular mod emerges or a new resource is needed, we aim to integrate it into our dynamic generation engine.
