# 🤝 Contributing Guide: Ores Core & Ores Mods

Thank you for your interest and welcome! 
If you wish to propose new additions, bug fixes, or changes to **Ores Core** or **Ores Mods**, this guide is for you.

**Important Note:** Everyone is welcome to propose modifications! However, please be aware that all proposals are subject to review, and only the repository owner can validate, approve, or merge them.

---

## 📌 1. How to Contribute?

You can contribute in two main ways on GitHub:

1. **If you are not familiar with coding or Pull Requests:**
   Use the **Issues** tab > **New Issue**. We have specialized templates:
   - 💎 **[Bug Report (Ores Core)](https://github.com/MathieuDorval/ORES-CORE/issues/new?template=bug_report_ores_core.md)**: For asset, API, or core registration issues.
   - 🧩 **[Bug Report (Ores Mods)](https://github.com/MathieuDorval/ORES-CORE/issues/new?template=bug_report_ores_mods.md)**: For unification or infinite ore generation issues.
   - 🛠️ **[Content Proposal](https://github.com/MathieuDorval/ORES-CORE/issues/new?template=proposition_contenu.yml)**: To suggest new materials, items, or textures.
   - ✨ **[Feature Request](https://github.com/MathieuDorval/ORES-CORE/issues/new?template=feature_request.md)**: To propose new engine functionalities.

2. **If you are a developer (Pull Request):**
   Create a fork, work on a branch, and open a *Pull Request*. 
   A pre-filled template will appear to help you structure your proposal. Please ensure your code follows the existing structure.

---

## 🛠️ 2. Key Files to Modify

The core logic and data are mainly located in `common/src/main/java/ores/mathieu/`.

### 🪨 Materials & Properties
- **File:** `common/src/main/java/ores/mathieu/material/MaterialsDatabase.java`
- **Action:** Add/Modify base materials, colors, and default properties.
- **Tip:** Adding reaching support for a new material is often just **a single line of code** here!

### ⛏️ Items & Blocks
- **File:** `common/src/main/java/ores/mathieu/material/ItemType.java` or `BlockType.java`
- **Action:** Define new dynamic item/block variations that the engine can generate.

### ⚙️ Ore Generation Logic
- **File:** `common/src/main/java/ores/mathieu/config/ConfigManager.java`
- **Action:** Modify the default generation rules, vein shapes, or distribution algorithms.

---

## 🎨 3. Texture Contributions

All base textures and dynamic palettes are located in: `common/src/main/resources/assets/ores/textures/`

1. **Unique Textures**: Place your `*.png` in the appropriate category (block, item, ore_overlays).
2. **Dynamic Palettes**: If modifying the global style (e.g., all ingots or dusts), update:
   `common/src/main/resources/assets/ores/textures/dynamics/palettes/base_palette.png`

---

## 🏆 4. Proposal Criteria

To make the review easier, please follow these guidelines:
- 🔖 **Name Format**: Tag your issues/PRs clearly: `[CORE-BUG]`, `[MODS-BUG]`, `[Material]`, `[Texture]`.
- 💻 **Proposed Implementation**: Provide clear code values or attached files.
- 🏷️ **Rationale**: Briefly explain why this addition or change is beneficial for the community.

## 📖 5. Need More Info?
Check out our **[Wiki](docs/Home.md)** for detailed guides on how the engine works, how to use the API, and advanced configuration.

Thank you for helping make **Ores Core** a universal tool for everyone! 🚀
