//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Enum defining fluid/gas/slurry states used to dynamically 
// generate Mekanism-compatible processing elements.
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
public enum ChemicalType {
    CLEAN("clean_slurry", new ChemicalOverride()
        .setNamingPattern("clean_%s_slurry")
        .setTagCategory("clean_slurries")
        .setNameEN("Clean %s Slurry").setNameFR("Bouillie de %s Propre").setNameES("Lechada de %s Limpia").setNameIT("Slurry di %s Pulito").setNameDE("Saubere %s-Aufschlämmung").setNamePT("Lodo de %s Limpo").setNameRU("Чистая суспензия %s").setNameZH("清洁%s浆料").setNameJP("きれいな%sのスラリー")
    ),
    DIRTY("dirty_slurry", new ChemicalOverride()
        .setNamingPattern("dirty_%s_slurry")
        .setTagCategory("dirty_slurries")
        .setNameEN("Dirty %s Slurry").setNameFR("Bouillie de %s Sale").setNameES("Lechada de %s Sucia").setNameIT("Slurry di %s Sporco").setNameDE("Dreckige %s-Aufschlämmung").setNamePT("Lodo de %s Sujo").setNameRU("Грязная суспензия %s").setNameZH("脏%s浆料").setNameJP("汚れた%sのスラリー")
    ),
    SLURRY("slurry", new ChemicalOverride()
        .setNamingPattern("%s_slurry")
        .setTagCategory("slurries")
        .setNameEN("%s Slurry").setNameFR("Bouillie de %s").setNameES("Lechada de %s").setNameIT("Slurry di %s").setNameDE("%s-Aufschlämmung").setNamePT("Lodo de %s").setNameRU("Суспензия %s").setNameZH("%s浆料").setNameJP("%sのスラリー")
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

    public static ChemicalType forName(String name) {
        if (name == null || name.isEmpty()) return null;
        for (ChemicalType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.suffix.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
