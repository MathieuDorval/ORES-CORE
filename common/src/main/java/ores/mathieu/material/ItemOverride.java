//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Data structure storing overriding parameters for Item types.
// Defines properties like stack size, rarity, multipliers, and fuel factors.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("null")
public class ItemOverride {
    private String namingPattern;
    private final List<String> compatiblePatterns = new ArrayList<>();
    private String tagCategory;
    private String tagPattern = "%s";
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
    private String nameEN;
    private String nameFR;
    private String nameES;
    private String nameIT;
    private String nameDE;
    private String namePT;
    private String nameRU;
    private String nameZH;
    private String nameJP;

    public ItemOverride() {}

    public String getNamingPattern() { return namingPattern; }
    public ItemOverride setNamingPattern(String namingPattern) { this.namingPattern = namingPattern; return this; }

    public List<String> getCompatiblePatterns() { return Collections.unmodifiableList(compatiblePatterns); }
    public ItemOverride addCompatiblePattern(String pattern) { this.compatiblePatterns.add(pattern); return this; }

    public String getTagCategory() { return tagCategory; }
    public ItemOverride setTagCategory(String tagCategory) { this.tagCategory = tagCategory; return this; }

    public String getTagPattern() { return tagPattern; }
    public ItemOverride setTagPattern(String tagPattern) { this.tagPattern = tagPattern; return this; }

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

    public String getNameEN() { return nameEN; }
    public ItemOverride setNameEN(String nameEN) { this.nameEN = nameEN; return this; }

    public String getNameFR() { return nameFR; }
    public ItemOverride setNameFR(String nameFR) { this.nameFR = nameFR; return this; }

    public String getNameES() { return nameES; }
    public ItemOverride setNameES(String nameES) { this.nameES = nameES; return this; }

    public String getNameIT() { return nameIT; }
    public ItemOverride setNameIT(String nameIT) { this.nameIT = nameIT; return this; }

    public String getNameDE() { return nameDE; }
    public ItemOverride setNameDE(String nameDE) { this.nameDE = nameDE; return this; }

    public String getNamePT() { return namePT; }
    public ItemOverride setNamePT(String namePT) { this.namePT = namePT; return this; }

    public String getNameRU() { return nameRU; }
    public ItemOverride setNameRU(String nameRU) { this.nameRU = nameRU; return this; }

    public String getNameZH() { return nameZH; }
    public ItemOverride setNameZH(String nameZH) { this.nameZH = nameZH; return this; }

    public String getNameJP() { return nameJP; }
    public ItemOverride setNameJP(String nameJP) { this.nameJP = nameJP; return this; }



    public void applyOverrides(java.util.Map<String, String> props, java.util.Set<String> protectedKeys) {
        for (java.util.Map.Entry<String, String> entry : props.entrySet()) {
            String key = entry.getKey();
            if (protectedKeys.contains(key)) continue;
            String val = entry.getValue();
            try {
                switch (key) {
                    case "rarity": this.rarity = Rarity.valueOf(String.valueOf(val).toUpperCase()); break;
                    case "maxStackSize": this.maxStackSize = Integer.parseInt(val); break;
                    case "smeltingMultiplier": this.smeltingMultiplier = Float.parseFloat(val); break;
                    case "xpMultiplier": this.xpMultiplier = Float.parseFloat(val); break;
                    case "canBeFireproof": this.canBeFireproof = Boolean.parseBoolean(val); break;
                    case "canBeBeaconPayment": this.canBeBeaconPayment = Boolean.parseBoolean(val); break;
                    case "canBePiglinLoved": this.canBePiglinLoved = Boolean.parseBoolean(val); break;
                    case "canBeTrimmable": this.canBeTrimmable = Boolean.parseBoolean(val); break;
                    case "useRawColor": this.useRawColor = Boolean.parseBoolean(val); break;
                    case "fuelFactor": this.fuelFactor = Float.parseFloat(val); break;
                    case "nameEN": this.nameEN = val; break;
                    case "nameFR": this.nameFR = val; break;
                    case "nameES": this.nameES = val; break;
                    case "nameIT": this.nameIT = val; break;
                    case "nameDE": this.nameDE = val; break;
                    case "namePT": this.namePT = val; break;
                    case "nameRU": this.nameRU = val; break;
                    case "nameZH": this.nameZH = val; break;
                    case "nameJP": this.nameJP = val; break;
                }
            } catch (Exception e) {
                ores.mathieu.OresCoreCommon.LOGGER.error("[ORES CORE] Failed to apply override '{}={}' for ItemOverride", key, val, e);
            }
        }
    }
}
