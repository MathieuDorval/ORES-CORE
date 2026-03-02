//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Property overrides for block types.
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

public class BlockOverride extends ItemOverride {

    private SoundType sound;
    private Integer color;
    private Float hardness;
    private Float resistance;
    private String requiredTool;
    private Integer lightLevel;
    private String lightMode;
    private Boolean useMaterialLight;
    private String particleMode;
    private Boolean useMaterialParticles;
    private Float particleIntensity;
    private Boolean useMaterialParticleIntensity;
    private Boolean translucent;
    private PushReaction pushReaction;
    private Float redstoneFactor;
    private Integer redstonePower;
    private Boolean useMaterialRedstone;
    private String gravityMode;
    private MapColor mapColor;
    private Float slipperiness;
    private Boolean useMaterialSlipperiness;
    private Float speedFactor;
    private Boolean useMaterialSpeedFactor;
    private Float jumpFactor;
    private Boolean useMaterialJumpFactor;
    private Boolean isFlammable;
    private NoteBlockInstrument instrument;
    private Boolean ignitedByLava;
    private Boolean isReplaceable;
    private Boolean noCollision;
    private Boolean shouldDropWhenFallingHitTorch;

    private Boolean canBeBeaconBase;

    private BlockType parentType;
    private Integer compressionLevel;

    public BlockOverride() {}

    public SoundType getSound() { return sound; }
    public BlockOverride setSound(SoundType sound) { this.sound = sound; return this; }

    public Integer getColor() { return color; }
    public BlockOverride setColor(Integer color) { this.color = color; return this; }

    public Float getHardness() { return hardness; }
    public BlockOverride setHardness(Float hardness) { this.hardness = hardness; return this; }

    public Float getResistance() { return resistance; }
    public BlockOverride setResistance(Float resistance) { this.resistance = resistance; return this; }

    public String getRequiredTool() { return requiredTool; }
    public BlockOverride setRequiredTool(String requiredTool) { this.requiredTool = requiredTool; return this; }

    public Integer getLightLevel() { return lightLevel; }
    public BlockOverride setLightLevel(Integer lightLevel) { this.lightLevel = lightLevel; return this; }
    public BlockOverride setLightLevel() { this.lightLevel = -1; return this; }

    public String getLightMode() { return lightMode; }
    public BlockOverride setLightMode(String lightMode) { this.lightMode = lightMode; return this; }
    public BlockOverride setLightMode(boolean useMaterial) { this.useMaterialLight = useMaterial; return this; }
    public Boolean getUseMaterialLight() { return useMaterialLight; }
    public BlockOverride setUseMaterialLight(Boolean useMaterialLight) { this.useMaterialLight = useMaterialLight; return this; }

    public String getParticleMode() { return particleMode; }
    public BlockOverride setParticleMode(String particleMode) { this.particleMode = particleMode; return this; }
    public BlockOverride setParticleMode(boolean useMaterial) { this.useMaterialParticles = useMaterial; return this; }
    public Boolean getUseMaterialParticles() { return useMaterialParticles; }
    public BlockOverride setUseMaterialParticles(Boolean useMaterialParticles) { this.useMaterialParticles = useMaterialParticles; return this; }

    public Float getParticleIntensity() { return particleIntensity; }
    public BlockOverride setParticleIntensity(Float particleIntensity) { this.particleIntensity = particleIntensity; return this; }
    public BlockOverride setParticleIntensity(boolean useMaterial) { this.useMaterialParticleIntensity = useMaterial; return this; }
    public Boolean getUseMaterialParticleIntensity() { return useMaterialParticleIntensity; }
    public BlockOverride setUseMaterialParticleIntensity(Boolean useMaterialParticleIntensity) { this.useMaterialParticleIntensity = useMaterialParticleIntensity; return this; }

    public Boolean getTranslucent() { return translucent; }
    public BlockOverride setTranslucent(Boolean translucent) { this.translucent = translucent; return this; }

    public PushReaction getPushReaction() { return pushReaction; }
    public BlockOverride setPushReaction(PushReaction pushReaction) { this.pushReaction = pushReaction; return this; }

    public Float getRedstoneFactor() { return redstoneFactor; }
    public BlockOverride setRedstoneFactor(Float redstoneFactor) { this.redstoneFactor = redstoneFactor; return this; }

    public String getGravityMode() { return gravityMode; }
    public BlockOverride setGravityMode(String gravityMode) { this.gravityMode = gravityMode; return this; }
    public BlockOverride setGravityMode() { this.gravityMode = "AUTO"; return this; }

    public MapColor getMapColor() { return mapColor; }
    public BlockOverride setMapColor(MapColor mapColor) { this.mapColor = mapColor; return this; }

    public Float getSlipperiness() { return slipperiness; }
    public BlockOverride setSlipperiness(Float slipperiness) { this.slipperiness = slipperiness; return this; }
    public BlockOverride setSlipperiness(boolean useMaterial) { this.useMaterialSlipperiness = useMaterial; return this; }
    public Boolean getUseMaterialSlipperiness() { return useMaterialSlipperiness; }
    public BlockOverride setUseMaterialSlipperiness(Boolean useMaterialSlipperiness) { this.useMaterialSlipperiness = useMaterialSlipperiness; return this; }

    public Float getSpeedFactor() { return speedFactor; }
    public BlockOverride setSpeedFactor(Float speedFactor) { this.speedFactor = speedFactor; return this; }
    public BlockOverride setSpeedFactor(boolean useMaterial) { this.useMaterialSpeedFactor = useMaterial; return this; }
    public Boolean getUseMaterialSpeedFactor() { return useMaterialSpeedFactor; }
    public BlockOverride setUseMaterialSpeedFactor(Boolean useMaterialSpeedFactor) { this.useMaterialSpeedFactor = useMaterialSpeedFactor; return this; }

    public Float getJumpFactor() { return jumpFactor; }
    public BlockOverride setJumpFactor(Float jumpFactor) { this.jumpFactor = jumpFactor; return this; }
    public BlockOverride setJumpFactor(boolean useMaterial) { this.useMaterialJumpFactor = useMaterial; return this; }
    public Boolean getUseMaterialJumpFactor() { return useMaterialJumpFactor; }
    public BlockOverride setUseMaterialJumpFactor(Boolean useMaterialJumpFactor) { this.useMaterialJumpFactor = useMaterialJumpFactor; return this; }

    public Boolean getIsFlammable() { return isFlammable; }
    public BlockOverride setIsFlammable(Boolean isFlammable) { this.isFlammable = isFlammable; return this; }
    public BlockOverride setIsFlammable() { this.isFlammable = false; return this; }

    public NoteBlockInstrument getInstrument() { return instrument; }
    public BlockOverride setInstrument(NoteBlockInstrument instrument) { this.instrument = instrument; return this; }
    public BlockOverride setInstrument() { this.instrument = null; return this; }

    public Boolean getIgnitedByLava() { return ignitedByLava; }
    public BlockOverride setIgnitedByLava(Boolean ignitedByLava) { this.ignitedByLava = ignitedByLava; return this; }
    public BlockOverride setIgnitedByLava() { this.ignitedByLava = false; return this; }

    public Boolean getIsReplaceable() { return isReplaceable; }
    public BlockOverride setIsReplaceable(Boolean isReplaceable) { this.isReplaceable = isReplaceable; return this; }

    public Boolean getNoCollision() { return noCollision; }
    public BlockOverride setNoCollision(Boolean noCollision) { this.noCollision = noCollision; return this; }

    public Integer getRedstonePower() { return redstonePower; }
    public BlockOverride setRedstonePower(Integer redstonePower) { this.redstonePower = redstonePower; return this; }
    public BlockOverride setRedstonePower(boolean useMaterial) { this.useMaterialRedstone = useMaterial; return this; }
    public Boolean getUseMaterialRedstone() { return useMaterialRedstone; }
    public BlockOverride setUseMaterialRedstone(Boolean useMaterialRedstone) { this.useMaterialRedstone = useMaterialRedstone; return this; }

    public Boolean getShouldDropWhenFallingHitTorch() { return shouldDropWhenFallingHitTorch; }
    public BlockOverride setShouldDropWhenFallingHitTorch(Boolean shouldDropWhenFallingHitTorch) { this.shouldDropWhenFallingHitTorch = shouldDropWhenFallingHitTorch; return this; }

    public Boolean getCanBeBeaconBase() { return canBeBeaconBase; }
    public BlockOverride setCanBeBeaconBase(Boolean canBeBeaconBase) { this.canBeBeaconBase = canBeBeaconBase; return this; }

    public BlockType getParentType() { return parentType; }
    public BlockOverride setParentType(BlockType parentType) { this.parentType = parentType; return this; }

    public Integer getCompressionLevel() { return compressionLevel; }
    public BlockOverride setCompressionLevel(Integer compressionLevel) { this.compressionLevel = compressionLevel; return this; }

    @Override
    public BlockOverride setNamingPattern(String namingPattern) {
        super.setNamingPattern(namingPattern);
        return this;
    }

    @Override
    public BlockOverride setTagCategory(String tagCategory) {
        super.setTagCategory(tagCategory);
        return this;
    }

    @Override
    public BlockOverride setRarity(Rarity rarity) {
        super.setRarity(rarity);
        return this;
    }

    @Override
    public BlockOverride setMaxStackSize(Integer maxStackSize) {
        super.setMaxStackSize(maxStackSize);
        return this;
    }

    @Override
    public BlockOverride setSmeltingMultiplier(Float smeltingMultiplier) {
        super.setSmeltingMultiplier(smeltingMultiplier);
        return this;
    }

    @Override
    public BlockOverride setXpMultiplier(Float xpMultiplier) {
        super.setXpMultiplier(xpMultiplier);
        return this;
    }

    @Override
    public BlockOverride setCanBeFireproof(Boolean canBeFireproof) {
        super.setCanBeFireproof(canBeFireproof);
        return this;
    }

    @Override
    public BlockOverride setCanBeFireproof() {
        super.setCanBeFireproof();
        return this;
    }

    @Override
    public BlockOverride setCanBeBeaconPayment(Boolean canBeBeaconPayment) {
        super.setCanBeBeaconPayment(canBeBeaconPayment);
        return this;
    }

    @Override
    public BlockOverride setCanBeBeaconPayment() {
        super.setCanBeBeaconPayment();
        return this;
    }

    @Override
    public BlockOverride setCanBePiglinLoved(Boolean canBePiglinLoved) {
        super.setCanBePiglinLoved(canBePiglinLoved);
        return this;
    }

    @Override
    public BlockOverride setCanBePiglinLoved() {
        super.setCanBePiglinLoved();
        return this;
    }

    @Override
    public BlockOverride setFuelFactor(Float fuelFactor) {
        super.setFuelFactor(fuelFactor);
        return this;
    }

    @Override
    public BlockOverride setUseRawColor(Boolean useRawColor) {
        super.setUseRawColor(useRawColor);
        return this;
    }

    @Override
    public BlockOverride setTranslationEN(String translationEN) {
        super.setTranslationEN(translationEN);
        return this;
    }

    @Override
    public BlockOverride setTranslationFR(String translationFR) {
        super.setTranslationFR(translationFR);
        return this;
    }

    @Override
    public BlockOverride setTranslationES(String translationES) {
        super.setTranslationES(translationES);
        return this;
    }

    @Override
    public BlockOverride setTranslationIT(String translationIT) {
        super.setTranslationIT(translationIT);
        return this;
    }

    @Override
    public BlockOverride setTranslationDE(String translationDE) {
        super.setTranslationDE(translationDE);
        return this;
    }

    @Override
    public BlockOverride setTranslationPT(String translationPT) {
        super.setTranslationPT(translationPT);
        return this;
    }

    @Override
    public BlockOverride setTranslationRU(String translationRU) {
        super.setTranslationRU(translationRU);
        return this;
    }

    @Override
    public BlockOverride setTranslationZH(String translationZH) {
        super.setTranslationZH(translationZH);
        return this;
    }

    @Override
    public BlockOverride setTranslationJP(String translationJP) {
        super.setTranslationJP(translationJP);
        return this;
    }

    @Override
    public BlockOverride setTranslations(String en, String fr, String es, String it, String de) {
        super.setTranslations(en, fr, es, it, de);
        return this;
    }

    public BlockOverride setTranslations(String en, String fr, String es, String it, String de, String pt, String ru, String zh, String jp) {
        super.setTranslations(en, fr, es, it, de, pt, ru, zh, jp);
        return this;
    }
}
