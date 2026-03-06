# For Mod Creators

Tired of manually creating dozens of basic items, blocks, registries, and world generation rules for every new material you introduce? 

With **Ores Core**, you no longer need to handle the tedious work of registering basic resources. Let the core handle all the heavy lifting of material generation, item registration, and ore placement, so you can focus 100% of your time on what matters most: **creating your mod's unique features and mechanics.**

## 💎 Using Ores Core for Your Materials

Instead of creating `TinIngot.java` and writing JSON files for textures and models, simply declare your material needs.

### 1. Add Repository and Dependency
Add **JitPack** and Ores Core to your `build.gradle`.

```gradle
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    // For developers working in a 'Common' module:
    compileOnly "com.github.MathieuDorval:ORES-CORE:common:v26.1.100"

    // Implementation for specific loaders:
    // Fabric
    implementation "com.github.MathieuDorval:ORES-CORE:fabric:v26.1.100"
    // NeoForge
    implementation "com.github.MathieuDorval:ORES-CORE:neoforge:v26.1.100"
}
```

### 2. Create `registry.json`
Place this configuration file in `src/main/resources/data/ores/registry.json`.

> 💡 **Tip:** Use the **[Interactive Registry Generator](https://mathieudorval.github.io/ORES-CORE/tools/registry_generator.html)** to generate this file without writing a single line of JSON!

```json
{
  "materials_registry": ["tin_ingot", "tin_nugget", "raw_tin", "tin_block"],
  "ore_generation": ["tin"]
}
```
*That's it.* Ores Core will read your request, and automatically generate the Tin items, the Tin Block, the Raw Tin, dynamically construct their textures and models in memory, and inject Tin into the world generation based on intelligent defaults.

## ⛰️ Integrating Custom Stones

If you create a mod that adds custom terrain or new underground stone types (like a custom marble, basalt, etc.), you can ensure that Ores Core naturally generates its ores directly inside your blocks!

Add your custom stone to the `stones_replacement` array in your `registry.json`:

```json
{
  "stones_replacement": ["your_mod:custom_stone", "your_mod:custom_rock"]
}
```

Ores Core will seamlessly inherit all physical properties of your custom host block (hardness, mining properties, sounds, gravity, push reactions) and mix it dynamically with the generated ores to create the perfect cohesive blend for your terrain.
