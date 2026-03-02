//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Property overrides for chemical types.
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

public class ChemicalOverride {
    private String namingPattern;
    private String tagCategory;
    private Integer chemicalCleanColor;
    private Integer chemicalDirtyColor;
    private Integer chemicalSlurryColor;
    private String translationEN;
    private String translationFR;
    private String translationES;
    private String translationIT;
    private String translationDE;
    private String translationPT;
    private String translationRU;
    private String translationZH;
    private String translationJP;

    public ChemicalOverride() {}

    public String getNamingPattern() { return namingPattern; }
    public ChemicalOverride setNamingPattern(String namingPattern) { this.namingPattern = namingPattern; return this; }

    public String getTagCategory() { return tagCategory; }
    public ChemicalOverride setTagCategory(String tagCategory) { this.tagCategory = tagCategory; return this; }

    public String getTranslationEN() { return translationEN; }
    public ChemicalOverride setTranslationEN(String translationEN) { this.translationEN = translationEN; return this; }

    public String getTranslationFR() { return translationFR; }
    public ChemicalOverride setTranslationFR(String translationFR) { this.translationFR = translationFR; return this; }

    public String getTranslationES() { return translationES; }
    public ChemicalOverride setTranslationES(String translationES) { this.translationES = translationES; return this; }

    public String getTranslationIT() { return translationIT; }
    public ChemicalOverride setTranslationIT(String translationIT) { this.translationIT = translationIT; return this; }

    public String getTranslationDE() { return translationDE; }
    public ChemicalOverride setTranslationDE(String translationDE) { this.translationDE = translationDE; return this; }

    public String getTranslationPT() { return translationPT; }
    public ChemicalOverride setTranslationPT(String translationPT) { this.translationPT = translationPT; return this; }

    public String getTranslationRU() { return translationRU; }
    public ChemicalOverride setTranslationRU(String translationRU) { this.translationRU = translationRU; return this; }

    public String getTranslationZH() { return translationZH; }
    public ChemicalOverride setTranslationZH(String translationZH) { this.translationZH = translationZH; return this; }

    public String getTranslationJP() { return translationJP; }
    public ChemicalOverride setTranslationJP(String translationJP) { this.translationJP = translationJP; return this; }

    public Integer getChemicalCleanColor() { return chemicalCleanColor; }
    public ChemicalOverride setChemicalCleanColor(Integer chemicalCleanColor) { this.chemicalCleanColor = chemicalCleanColor; return this; }

    public Integer getChemicalDirtyColor() { return chemicalDirtyColor; }
    public ChemicalOverride setChemicalDirtyColor(Integer chemicalDirtyColor) { this.chemicalDirtyColor = chemicalDirtyColor; return this; }

    public Integer getChemicalSlurryColor() { return chemicalSlurryColor; }
    public ChemicalOverride setChemicalSlurryColor(Integer chemicalSlurryColor) { this.chemicalSlurryColor = chemicalSlurryColor; return this; }

    public ChemicalOverride setTranslation(String en, String fr) {
        this.translationEN = en;
        this.translationFR = fr;
        return this;
    }

    public ChemicalOverride setTranslation(String en, String fr, String es, String it, String de) {
        this.translationEN = en;
        this.translationFR = fr;
        this.translationES = es;
        this.translationIT = it;
        this.translationDE = de;
        return this;
    }

    public ChemicalOverride setTranslation(String en, String fr, String es, String it, String de, String pt, String ru, String zh, String jp) {
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
