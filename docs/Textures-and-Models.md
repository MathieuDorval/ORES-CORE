# Textures and Models

Ores Core utilizes a sophisticated dynamic asset engine to generate thousands of unique items and blocks without bloating the game's memory or installation size.

## 🎨 Dynamic Texture Generation
The vast majority of textures in Ores Core are not static image files for every single material. Instead, they are **dynamically generated** at runtime.
- **The Process:** The engine takes a grayscale "template" (the model) and applies a specific color palette defined for each material in the `MaterialsDatabase`.
- **Consistency:** This ensures that all items of the same material (e.g., Tin Ingot, Tin Plate, Tin Gear) have perfectly matching hues and shading.

## 📐 Models and Inspirations
The models used as bases for our dynamic textures are carefully selected to provide a familiar yet premium aesthetic:
- **Popular Mod Inspiration:** Many item shapes (like gears, plates, and crushed ores) are inspired by industry standards from mods like **Mekanism**, **Create**, **Thermal Series**, and **Industrial Foregoing**. This ensures Ores Core feels right at home in any major technological or magical modpack.
- **Unique Creations:** Several models are entirely original to Ores Core, designed to fill gaps in the standard Minecraft resource ecosystem and offer a sleek, modern look.

## 💡 How to Contribute?
We are always looking to improve our visual library! If you are an artist or a developer and want to:
- **Propose a New Model:** Suggest a better base texture for an existing item type (e.g., a cooler-looking Gear or a more detailed Ingot).
- **Custom Textures:** Propose unique textures for specific materials that shouldn't follow the dynamic rules.
- **New Item Types:** Suggest entirely new categories of items with their own models.

You can submit your visual proposals here:
👉 **[Propose a Texture or Content](https://github.com/MathieuDorval/ORES-CORE/issues/new?template=proposition_contenu.yml)**

By contributing a model, you help standardize the look of Minecraft modding for everyone!
