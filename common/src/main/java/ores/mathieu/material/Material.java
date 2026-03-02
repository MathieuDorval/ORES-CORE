//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Data model representing a material and its properties.
//
// Author: __mathieu
// Version: 26.1.001
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
}
