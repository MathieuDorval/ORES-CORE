//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Property overrides for item types.
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

public class ItemOverride {
    private String namingPattern;
    private String tagCategory;
    private Rarity rarity;
    private Integer maxStackSize = 64;
    private Float smeltingMultiplier = 0.0f;
    private Float xpMultiplier = 0.0f;
    private Boolean canBeFireproof = false;
    private Boolean canBeBeaconPayment = false;
    private Boolean canBePiglinLoved = false;
    private Boolean canBeTrimmable = false;
    private Boolean useRawColor = false;
    private Float fuelFactor = 0.0f;
    private String translationEN;
    private String translationFR;
    private String translationES;
    private String translationIT;
    private String translationDE;
    private String translationPT;
    private String translationRU;
    private String translationZH;
    private String translationJP;

    public ItemOverride() {}

    public String getNamingPattern() { return namingPattern; }
    public ItemOverride setNamingPattern(String namingPattern) { this.namingPattern = namingPattern; return this; }

    public String getTagCategory() { return tagCategory; }
    public ItemOverride setTagCategory(String tagCategory) { this.tagCategory = tagCategory; return this; }

    public Rarity getRarity() { return rarity; }
    public ItemOverride setRarity(Rarity rarity) { this.rarity = rarity; return this; }

    public Integer getMaxStackSize() { return maxStackSize; }
    public ItemOverride setMaxStackSize(Integer maxStackSize) { this.maxStackSize = maxStackSize; return this; }

    public Float getSmeltingMultiplier() { return smeltingMultiplier; }
    public ItemOverride setSmeltingMultiplier(Float smeltingMultiplier) { this.smeltingMultiplier = smeltingMultiplier; return this; }

    public Float getXpMultiplier() { return xpMultiplier; }
    public ItemOverride setXpMultiplier(Float xpMultiplier) { this.xpMultiplier = xpMultiplier; return this; }

    public Boolean getCanBeFireproof() { return canBeFireproof; }
    public ItemOverride setCanBeFireproof(Boolean canBeFireproof) { this.canBeFireproof = canBeFireproof; return this; }
    public ItemOverride setCanBeFireproof() { this.canBeFireproof = false; return this; }

    public Boolean getCanBeBeaconPayment() { return canBeBeaconPayment; }
    public ItemOverride setCanBeBeaconPayment(Boolean canBeBeaconPayment) { this.canBeBeaconPayment = canBeBeaconPayment; return this; }
    public ItemOverride setCanBeBeaconPayment() { this.canBeBeaconPayment = false; return this; }

    public Boolean getCanBePiglinLoved() { return canBePiglinLoved; }
    public ItemOverride setCanBePiglinLoved(Boolean canBePiglinLoved) { this.canBePiglinLoved = canBePiglinLoved; return this; }
    public ItemOverride setCanBePiglinLoved() { this.canBePiglinLoved = false; return this; }

    public Boolean getCanBeTrimmable() { return canBeTrimmable; }
    public ItemOverride setCanBeTrimmable(Boolean canBeTrimmable) { this.canBeTrimmable = canBeTrimmable; return this; }
    public ItemOverride setCanBeTrimmable() { this.canBeTrimmable = false; return this; }

    public Boolean getUseRawColor() { return useRawColor; }
    public ItemOverride setUseRawColor(Boolean useRawColor) { this.useRawColor = useRawColor; return this; }

    public Float getFuelFactor() { return fuelFactor; }
    public ItemOverride setFuelFactor(Float fuelFactor) { this.fuelFactor = fuelFactor; return this; }

    public String getTranslationEN() { return translationEN; }
    public ItemOverride setTranslationEN(String translationEN) { this.translationEN = translationEN; return this; }

    public String getTranslationFR() { return translationFR; }
    public ItemOverride setTranslationFR(String translationFR) { this.translationFR = translationFR; return this; }

    public String getTranslationES() { return translationES; }
    public ItemOverride setTranslationES(String translationES) { this.translationES = translationES; return this; }

    public String getTranslationIT() { return translationIT; }
    public ItemOverride setTranslationIT(String translationIT) { this.translationIT = translationIT; return this; }

    public String getTranslationDE() { return translationDE; }
    public ItemOverride setTranslationDE(String translationDE) { this.translationDE = translationDE; return this; }

    public String getTranslationPT() { return translationPT; }
    public ItemOverride setTranslationPT(String translationPT) { this.translationPT = translationPT; return this; }

    public String getTranslationRU() { return translationRU; }
    public ItemOverride setTranslationRU(String translationRU) { this.translationRU = translationRU; return this; }

    public String getTranslationZH() { return translationZH; }
    public ItemOverride setTranslationZH(String translationZH) { this.translationZH = translationZH; return this; }

    public String getTranslationJP() { return translationJP; }
    public ItemOverride setTranslationJP(String translationJP) { this.translationJP = translationJP; return this; }

    public ItemOverride setTranslations(String en, String fr, String es, String it, String de) {
        this.translationEN = en;
        this.translationFR = fr;
        this.translationES = es;
        this.translationIT = it;
        this.translationDE = de;
        return this;
    }

    public ItemOverride setTranslations(String en, String fr, String es, String it, String de, String pt, String ru, String zh, String jp) {
        this.translationEN = en;
        this.translationFR = fr;
        this.translationES = es;
        this.translationIT = it;
        this.translationDE = de;
        this.translationPT = pt;
        this.translationRU = ru;
        this.translationZH = zh;
        this.translationJP = jp;
        return this;
    }

}
