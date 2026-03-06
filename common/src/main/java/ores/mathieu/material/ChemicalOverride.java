//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Data structure storing overriding parameters for 
// Chemical types (Mekanism compat). Defines naming patterns and 
// slurry rendering colors.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChemicalOverride {
    private String namingPattern;
    private final List<String> compatiblePatterns = new ArrayList<>();
    private String tagCategory;
    private String tagPattern = "%s";
    private Integer chemicalCleanColor;
    private Integer chemicalDirtyColor;
    private Integer chemicalSlurryColor;
    private String nameEN;
    private String nameFR;
    private String nameES;
    private String nameIT;
    private String nameDE;
    private String namePT;
    private String nameRU;
    private String nameZH;
    private String nameJP;

    public ChemicalOverride() {}

    public String getNamingPattern() { return namingPattern; }
    public ChemicalOverride setNamingPattern(String namingPattern) { this.namingPattern = namingPattern; return this; }

    public List<String> getCompatiblePatterns() { return Collections.unmodifiableList(compatiblePatterns); }
    public ChemicalOverride addCompatiblePattern(String pattern) { this.compatiblePatterns.add(pattern); return this; }

    public String getTagCategory() { return tagCategory; }
    public ChemicalOverride setTagCategory(String tagCategory) { this.tagCategory = tagCategory; return this; }

    public String getTagPattern() { return tagPattern; }
    public ChemicalOverride setTagPattern(String tagPattern) { this.tagPattern = tagPattern; return this; }

    public String getNameEN() { return nameEN; }
    public ChemicalOverride setNameEN(String nameEN) { this.nameEN = nameEN; return this; }

    public String getNameFR() { return nameFR; }
    public ChemicalOverride setNameFR(String nameFR) { this.nameFR = nameFR; return this; }

    public String getNameES() { return nameES; }
    public ChemicalOverride setNameES(String nameES) { this.nameES = nameES; return this; }

    public String getNameIT() { return nameIT; }
    public ChemicalOverride setNameIT(String nameIT) { this.nameIT = nameIT; return this; }

    public String getNameDE() { return nameDE; }
    public ChemicalOverride setNameDE(String nameDE) { this.nameDE = nameDE; return this; }

    public String getNamePT() { return namePT; }
    public ChemicalOverride setNamePT(String namePT) { this.namePT = namePT; return this; }

    public String getNameRU() { return nameRU; }
    public ChemicalOverride setNameRU(String nameRU) { this.nameRU = nameRU; return this; }

    public String getNameZH() { return nameZH; }
    public ChemicalOverride setNameZH(String nameZH) { this.nameZH = nameZH; return this; }

    public String getNameJP() { return nameJP; }
    public ChemicalOverride setNameJP(String nameJP) { this.nameJP = nameJP; return this; }

    public Integer getChemicalCleanColor() { return chemicalCleanColor; }
    public ChemicalOverride setChemicalCleanColor(Integer chemicalCleanColor) { this.chemicalCleanColor = chemicalCleanColor; return this; }

    public Integer getChemicalDirtyColor() { return chemicalDirtyColor; }
    public ChemicalOverride setChemicalDirtyColor(Integer chemicalDirtyColor) { this.chemicalDirtyColor = chemicalDirtyColor; return this; }

    public Integer getChemicalSlurryColor() { return chemicalSlurryColor; }
    public ChemicalOverride setChemicalSlurryColor(Integer chemicalSlurryColor) { this.chemicalSlurryColor = chemicalSlurryColor; return this; }



    public void applyOverrides(java.util.Map<String, String> props, java.util.Set<String> protectedKeys) {
        for (java.util.Map.Entry<String, String> entry : props.entrySet()) {
            String key = entry.getKey();
            if (protectedKeys.contains(key)) continue;
            String val = entry.getValue();
            try {
                switch (key) {
                    case "chemicalCleanColor": this.chemicalCleanColor = Integer.decode(val); break;
                    case "chemicalDirtyColor": this.chemicalDirtyColor = Integer.decode(val); break;
                    case "chemicalSlurryColor": this.chemicalSlurryColor = Integer.decode(val); break;
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
                ores.mathieu.OresCoreCommon.LOGGER.error("[ORES CORE] Failed to apply chemical override '{}={}'", key, val, e);
            }
        }
    }
}
