//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Fundamental representation of a raw material containing
// all its intrinsic default physical properties, colors, base values,
// translations and behaviors.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.material;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

@SuppressWarnings("null")
public class Material {

    private String name;
    private String nameEN;
    private String nameFR;
    private String nameES;
    private String nameIT;
    private String nameDE;
    private String namePT;
    private String nameRU;
    private String nameZH;
    private String nameJP;

    private String baseItemName = null;
    private Integer baseColorHigh = null;
    private Integer baseColorLow = null;
    private Integer rawColorHigh = null;
    private Integer rawColorLow = null;
    private String baseType = "ingot";

    private int maxStackSize = 64;
    private Rarity rarity = Rarity.COMMON;
    private boolean fireproof = false;
    private boolean beacon = false;
    private boolean piglinLoved = false;
    private int fuelTime = 0;
    private boolean trimmable = false;

    private float blockHardnessFactor = 1.0f;
    private float blockResistanceFactor = 1.0f;
    private int blockRedstonePower = 0;
    private int blockLightLevel = 0;
    private String blockLightMode = "NONE";
    private String blockParticleMode = "NONE";
    private float blockParticleIntensity = 1.0f;
    private String blockGravityMode = "NONE";
    private SoundType sound = SoundType.STONE;
    private int blockColor = -1;
    private PushReaction blockPushReaction = PushReaction.NORMAL;
    private NoteBlockInstrument instrument = NoteBlockInstrument.BASEDRUM;
    private float slipperiness = 0.6f;
    private float speedFactor = 1.0f;
    private float jumpFactor = 1.0f;
    private MapColor mapColor = MapColor.STONE;
    private boolean ignitedByLava = false;
    private int compressionRatio = 9;

    private float oreHardnessFactor = 1.0f;
    private float oreResistanceFactor = 1.0f;
    private int miningLevel = 1;
    private String oreDropItem = null;
    private int oreDropMin = 1;
    private int oreDropMax = 1;
    private int oreDropXPMin = 0;
    private int oreDropXPMax = 0;
    private int oreLightLevel = 0;
    private String oreLightMode = "NONE";
    private String oreParticleMode = "NONE";
    private float oreParticleIntensity = 1.0f;
    private String oreGravityMode = "NONE";
    private PushReaction orePushReaction = PushReaction.NORMAL;
    private int oreSmeltingTime = 200;
    private float oreSmeltingXP = 0.1f;
    private String debrisName = null;

    private int chemicalCleanColor = -1;
    private int chemicalDirtyColor = -1;
    private int chemicalSlurryColor = -1;

    public Material() {}

    public String getName() { return name; }
    public Material setName(String name) { this.name = name; return this; }

    public String getNameEN() { return nameEN != null ? nameEN : capitalize(name); }
    public Material setNameEN(String nameEN) { this.nameEN = nameEN; return this; }

    public String getNameFR() { return nameFR != null ? nameFR : getNameEN(); }
    public Material setNameFR(String nameFR) { this.nameFR = nameFR; return this; }

    public String getNameES() { return nameES != null ? nameES : getNameEN(); }
    public Material setNameES(String nameES) { this.nameES = nameES; return this; }

    public String getNameIT() { return nameIT != null ? nameIT : getNameEN(); }
    public Material setNameIT(String nameIT) { this.nameIT = nameIT; return this; }

    public String getNameDE() { return nameDE != null ? nameDE : getNameEN(); }
    public Material setNameDE(String nameDE) { this.nameDE = nameDE; return this; }

    public String getNamePT() { return namePT != null ? namePT : getNameEN(); }
    public Material setNamePT(String namePT) { this.namePT = namePT; return this; }

    public String getNameRU() { return nameRU != null ? nameRU : getNameEN(); }
    public Material setNameRU(String nameRU) { this.nameRU = nameRU; return this; }

    public String getNameZH() { return nameZH != null ? nameZH : getNameEN(); }
    public Material setNameZH(String nameZH) { this.nameZH = nameZH; return this; }

    public String getNameJP() { return nameJP != null ? nameJP : getNameEN(); }
    public Material setNameJP(String nameJP) { this.nameJP = nameJP; return this; }

    public String getBaseItemName() { return baseItemName != null ? baseItemName : name; }
    public Material setBaseItemName(String baseItemName) { this.baseItemName = baseItemName; return this; }
    public Integer getBaseColorHigh() { return baseColorHigh; }
    public Material setBaseColorHigh(Integer baseColorHigh) { this.baseColorHigh = baseColorHigh; return this; }
    public Integer getBaseColorLow() { return baseColorLow; }
    public Material setBaseColorLow(Integer baseColorLow) { this.baseColorLow = baseColorLow; return this; }
    public Integer getRawColorHigh() { return rawColorHigh; }
    public Material setRawColorHigh(Integer rawColorHigh) { this.rawColorHigh = rawColorHigh; return this; }
    public Integer getRawColorLow() { return rawColorLow; }
    public Material setRawColorLow(Integer rawColorLow) { this.rawColorLow = rawColorLow; return this; }

    public int getMaterialColor() { return baseColorHigh != null ? baseColorHigh : 0xFFFFFF; }

    public String getBaseType() { return baseType; }
    public Material setBaseType(String baseType) { this.baseType = baseType; return this; }

    public int getMaxStackSize() { return maxStackSize; }
    public Material setMaxStackSize(int maxStackSize) { this.maxStackSize = maxStackSize; return this; }
    public Rarity getRarity() { return rarity; }
    public Material setRarity(Rarity rarity) { this.rarity = rarity; return this; }
    public boolean isFireproof() { return fireproof; }
    public Material setFireproof(boolean fireproof) { this.fireproof = fireproof; return this; }
    public boolean isBeacon() { return beacon; }
    public Material setBeacon(boolean beacon) { this.beacon = beacon; return this; }
    public boolean isPiglinLoved() { return piglinLoved; }
    public Material setPiglinLoved(boolean piglinLoved) { this.piglinLoved = piglinLoved; return this; }
    public int getFuelTime() { return fuelTime; }
    public Material setFuelTime(int fuelTime) { this.fuelTime = fuelTime; return this; }
    public boolean isTrimmable() { return trimmable; }
    public Material setTrimmable(boolean trimmable) { this.trimmable = trimmable; return this; }

    public float getBlockHardnessFactor() { return blockHardnessFactor; }
    public Material setBlockHardnessFactor(float blockHardnessFactor) { this.blockHardnessFactor = blockHardnessFactor; return this; }
    public float getBlockResistanceFactor() { return blockResistanceFactor; }
    public Material setBlockResistanceFactor(float blockResistanceFactor) { this.blockResistanceFactor = blockResistanceFactor; return this; }
    public int getBlockRedstonePower() { return blockRedstonePower; }
    public Material setBlockRedstonePower(int blockRedstonePower) { this.blockRedstonePower = blockRedstonePower; return this; }
    public int getBlockLightLevel() { return blockLightLevel; }
    public Material setBlockLightLevel(int blockLightLevel) { this.blockLightLevel = blockLightLevel; return this; }
    public String getBlockLightMode() { return blockLightMode; }
    public Material setBlockLightMode(String blockLightMode) { this.blockLightMode = blockLightMode; return this; }
    public String getBlockParticleMode() { return blockParticleMode; }
    public Material setBlockParticleMode(String blockParticleMode) { this.blockParticleMode = blockParticleMode; return this; }
    public float getBlockParticleIntensity() { return blockParticleIntensity; }
    public Material setBlockParticleIntensity(float blockParticleIntensity) { this.blockParticleIntensity = blockParticleIntensity; return this; }
    public String getBlockGravityMode() { return blockGravityMode; }
    public Material setBlockGravityMode(String blockGravityMode) { this.blockGravityMode = blockGravityMode; return this; }
    public SoundType getSound() { return sound; }
    public Material setSound(SoundType sound) { this.sound = sound; return this; }
    public int getBlockColor() { return blockColor; }
    public Material setBlockColor(int blockColor) { this.blockColor = blockColor; return this; }
    public PushReaction getBlockPushReaction() { return blockPushReaction; }
    public Material setBlockPushReaction(PushReaction blockPushReaction) { this.blockPushReaction = blockPushReaction; return this; }
    public NoteBlockInstrument getInstrument() { return instrument; }
    public Material setInstrument(NoteBlockInstrument instrument) { this.instrument = instrument; return this; }
    public float getSlipperiness() { return slipperiness; }
    public Material setSlipperiness(float slipperiness) { this.slipperiness = slipperiness; return this; }
    public float getSpeedFactor() { return speedFactor; }
    public Material setSpeedFactor(float speedFactor) { this.speedFactor = speedFactor; return this; }
    public float getJumpFactor() { return jumpFactor; }
    public Material setJumpFactor(float jumpFactor) { this.jumpFactor = jumpFactor; return this; }
    public MapColor getMapColor() { return mapColor; }
    public Material setMapColor(MapColor mapColor) { this.mapColor = mapColor; return this; }
    public boolean isIgnitedByLava() { return ignitedByLava; }
    public Material setIgnitedByLava(boolean ignitedByLava) { this.ignitedByLava = ignitedByLava; return this; }
    public int getCompressionRatio() { return compressionRatio; }
    public Material setCompressionRatio(int compressionRatio) { this.compressionRatio = compressionRatio; return this; }

    public float getOreHardnessFactor() { return oreHardnessFactor; }
    public Material setOreHardnessFactor(float oreHardnessFactor) { this.oreHardnessFactor = oreHardnessFactor; return this; }
    public float getOreResistanceFactor() { return oreResistanceFactor; }
    public Material setOreResistanceFactor(float oreResistanceFactor) { this.oreResistanceFactor = oreResistanceFactor; return this; }
    public int getMiningLevel() { return miningLevel; }
    public Material setMiningLevel(int miningLevel) { this.miningLevel = miningLevel; return this; }
    public String getOreDropItem() { return oreDropItem; }
    public Material setOreDropItem(String oreDropItem) { this.oreDropItem = oreDropItem; return this; }
    public int getOreDropMin() { return oreDropMin; }
    public Material setOreDropMin(int oreDropMin) { this.oreDropMin = oreDropMin; return this; }
    public int getOreDropMax() { return oreDropMax; }
    public Material setOreDropMax(int oreDropMax) { this.oreDropMax = oreDropMax; return this; }
    public int getOreDropXPMin() { return oreDropXPMin; }
    public Material setOreDropXPMin(int oreDropXPMin) { this.oreDropXPMin = oreDropXPMin; return this; }
    public int getOreDropXPMax() { return oreDropXPMax; }
    public Material setOreDropXPMax(int oreDropXPMax) { this.oreDropXPMax = oreDropXPMax; return this; }
    public int getOreLightLevel() { return oreLightLevel; }
    public Material setOreLightLevel(int oreLightLevel) { this.oreLightLevel = oreLightLevel; return this; }
    public String getOreLightMode() { return oreLightMode; }
    public Material setOreLightMode(String oreLightMode) { this.oreLightMode = oreLightMode; return this; }
    public String getOreParticleMode() { return oreParticleMode; }
    public Material setOreParticleMode(String oreParticleMode) { this.oreParticleMode = oreParticleMode; return this; }
    public float getOreParticleIntensity() { return oreParticleIntensity; }
    public Material setOreParticleIntensity(float oreParticleIntensity) { this.oreParticleIntensity = oreParticleIntensity; return this; }
    public String getOreGravityMode() { return oreGravityMode; }
    public Material setOreGravityMode(String oreGravityMode) { this.oreGravityMode = oreGravityMode; return this; }
    public PushReaction getOrePushReaction() { return orePushReaction; }
    public Material setOrePushReaction(PushReaction orePushReaction) { this.orePushReaction = orePushReaction; return this; }
    public int getOreSmeltingTime() { return oreSmeltingTime; }
    public Material setOreSmeltingTime(int oreSmeltingTime) { this.oreSmeltingTime = oreSmeltingTime; return this; }
    public float getOreSmeltingXP() { return oreSmeltingXP; }
    public Material setOreSmeltingXP(float oreSmeltingXP) { this.oreSmeltingXP = oreSmeltingXP; return this; }
    public String getDebrisName() { return debrisName; }
    public Material setDebrisName(String debrisName) { this.debrisName = debrisName; return this; }

    public int getChemicalCleanColor() { return chemicalCleanColor; }
    public Material setChemicalCleanColor(int chemicalCleanColor) { this.chemicalCleanColor = chemicalCleanColor; return this; }
    public int getChemicalDirtyColor() { return chemicalDirtyColor; }
    public Material setChemicalDirtyColor(int chemicalDirtyColor) { this.chemicalDirtyColor = chemicalDirtyColor; return this; }
    public int getChemicalSlurryColor() { return chemicalSlurryColor; }
    public Material setChemicalSlurryColor(int chemicalSlurryColor) { this.chemicalSlurryColor = chemicalSlurryColor; return this; }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public void applyOverrides(java.util.Map<String, String> props, java.util.Set<String> protectedKeys) {
        for (java.util.Map.Entry<String, String> entry : props.entrySet()) {
            String key = entry.getKey();
            if (protectedKeys.contains(key)) continue;
            String val = entry.getValue();
            try {
                switch (key) {
                    case "nameEN": this.nameEN = val; break;
                    case "nameFR": this.nameFR = val; break;
                    case "nameES": this.nameES = val; break;
                    case "nameIT": this.nameIT = val; break;
                    case "nameDE": this.nameDE = val; break;
                    case "namePT": this.namePT = val; break;
                    case "nameRU": this.nameRU = val; break;
                    case "nameZH": this.nameZH = val; break;
                    case "nameJP": this.nameJP = val; break;
                    case "baseItemName": this.baseItemName = val; break;
                    case "baseColorHigh": this.baseColorHigh = Integer.decode(val); break;
                    case "baseColorLow": this.baseColorLow = Integer.decode(val); break;
                    case "rawColorHigh": this.rawColorHigh = Integer.decode(val); break;
                    case "rawColorLow": this.rawColorLow = Integer.decode(val); break;
                    case "baseType": this.baseType = val; break;
                    case "maxStackSize": this.maxStackSize = Integer.parseInt(val); break;
                    case "rarity": this.rarity = Rarity.valueOf(String.valueOf(val).toUpperCase()); break;
                    case "fireproof": this.fireproof = Boolean.parseBoolean(val); break;
                    case "beacon": this.beacon = Boolean.parseBoolean(val); break;
                    case "piglinLoved": this.piglinLoved = Boolean.parseBoolean(val); break;
                    case "fuelTime": this.fuelTime = Integer.parseInt(val); break;
                    case "trimmable": this.trimmable = Boolean.parseBoolean(val); break;
                    case "blockHardnessFactor": this.blockHardnessFactor = Float.parseFloat(val); break;
                    case "blockResistanceFactor": this.blockResistanceFactor = Float.parseFloat(val); break;
                    case "blockRedstonePower": this.blockRedstonePower = Integer.parseInt(val); break;
                    case "blockLightLevel": this.blockLightLevel = Integer.parseInt(val); break;
                    case "blockLightMode": this.blockLightMode = val; break;
                    case "blockParticleMode": this.blockParticleMode = val; break;
                    case "blockParticleIntensity": this.blockParticleIntensity = Float.parseFloat(val); break;
                    case "blockGravityMode": this.blockGravityMode = val; break;
                    case "blockColor": this.blockColor = Integer.decode(val); break;
                    case "blockPushReaction": this.blockPushReaction = PushReaction.valueOf(String.valueOf(val).toUpperCase()); break;
                    case "instrument": this.instrument = NoteBlockInstrument.valueOf(String.valueOf(val).toUpperCase()); break;
                    case "slipperiness": this.slipperiness = Float.parseFloat(val); break;
                    case "speedFactor": this.speedFactor = Float.parseFloat(val); break;
                    case "jumpFactor": this.jumpFactor = Float.parseFloat(val); break;
                    case "ignitedByLava": this.ignitedByLava = Boolean.parseBoolean(val); break;
                    case "compressionRatio": this.compressionRatio = Integer.parseInt(val); break;
                    case "oreHardnessFactor": this.oreHardnessFactor = Float.parseFloat(val); break;
                    case "oreResistanceFactor": this.oreResistanceFactor = Float.parseFloat(val); break;
                    case "miningLevel": this.miningLevel = Integer.parseInt(val); break;
                    case "oreDropItem": this.oreDropItem = val; break;
                    case "oreDropMin": this.oreDropMin = Integer.parseInt(val); break;
                    case "oreDropMax": this.oreDropMax = Integer.parseInt(val); break;
                    case "oreDropXPMin": this.oreDropXPMin = Integer.parseInt(val); break;
                    case "oreDropXPMax": this.oreDropXPMax = Integer.parseInt(val); break;
                    case "oreLightLevel": this.oreLightLevel = Integer.parseInt(val); break;
                    case "oreLightMode": this.oreLightMode = val; break;
                    case "oreParticleMode": this.oreParticleMode = val; break;
                    case "oreParticleIntensity": this.oreParticleIntensity = Float.parseFloat(val); break;
                    case "oreGravityMode": this.oreGravityMode = val; break;
                    case "orePushReaction": this.orePushReaction = PushReaction.valueOf(String.valueOf(val).toUpperCase()); break;
                    case "oreSmeltingTime": this.oreSmeltingTime = Integer.parseInt(val); break;
                    case "oreSmeltingXP": this.oreSmeltingXP = Float.parseFloat(val); break;
                    case "chemicalCleanColor": this.chemicalCleanColor = Integer.decode(val); break;
                    case "chemicalDirtyColor": this.chemicalDirtyColor = Integer.decode(val); break;
                    case "chemicalSlurryColor": this.chemicalSlurryColor = Integer.decode(val); break;
                }
                ores.mathieu.OresCoreCommon.LOGGER.debug("[ORES CORE] Material '{}' : property '{}' set to '{}'", name, key, val);
            } catch (Exception e) {
                 ores.mathieu.OresCoreCommon.LOGGER.error("[ORES CORE] Failed to apply override '{}={}' for material {}", key, val, name, e);
            }
        }
    }
}
