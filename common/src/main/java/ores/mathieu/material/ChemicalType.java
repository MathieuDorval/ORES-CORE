//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Enum defining slurry and chemical types for Mekanism integration.
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
public enum ChemicalType {
    CLEAN("clean_slurry", new ChemicalOverride()
        .setNamingPattern("clean_%s_slurry")
        .setTagCategory("clean_slurries")
        .setTranslation("Clean %s Slurry", "Bouillie de %s Propre", "Lechada de %s Limpia", "Slurry di %s Pulito", "Saubere %s-Aufschlämmung", "Lodo de %s Limpo", "Чистая суспензия %s", "清洁%s浆料", "きれいな%sのスラリー")
    ),
    DIRTY("dirty_slurry", new ChemicalOverride()
        .setNamingPattern("dirty_%s_slurry")
        .setTagCategory("dirty_slurries")
        .setTranslation("Dirty %s Slurry", "Bouillie de %s Sale", "Lechada de %s Sucia", "Slurry di %s Sporco", "Dreckige %s-Aufschlämmung", "Lodo de %s Sujo", "Грязная суспензия %s", "脏%s浆料", "汚れた%sのスラリー")
    ),
    SLURRY("slurry", new ChemicalOverride()
        .setNamingPattern("%s_slurry")
        .setTagCategory("slurries")
        .setTranslation("%s Slurry", "Bouillie de %s", "Lechada de %s", "Slurry di %s", "%s-Aufschlämmung", "Lodo de %s", "Суспензия %s", "%s浆料", "%sのスラリー")
    );

    private final String suffix;
    private final ChemicalOverride defaultOverride;
    ChemicalType(String suffix, ChemicalOverride defaultOverride) {
        this.suffix = suffix;
        this.defaultOverride = defaultOverride;
    }
    public String getSuffix() {
        return suffix;
    }
    public ChemicalOverride getDefaultOverride() {
        return defaultOverride;
    }
}
