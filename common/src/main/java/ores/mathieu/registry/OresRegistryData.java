//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Internal state wrapper handling parsed material metadata,
// generation rules, and stones replacer mappings extracted directly 
// from decoded registry configurations.
//
// Author: __mathieu
// Version: 26.1.100
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OresRegistryData {
    private final Set<String> hardcodedMaterials = new HashSet<>();
    private final Set<String> configMaterials = new HashSet<>();
    private final Set<String> oreGeneration = new HashSet<>();
    private final Map<String, Boolean> stonesReplacement = new HashMap<>();

    private final Set<String> blacklist = new HashSet<>();

    public void addMaterial(String material) {
        addMaterial(material, false);
    }

    public void addMaterial(String material, boolean isConfig) {
        String stripped = stripNamespace(material);
        if (isConfig) {
            configMaterials.add(stripped);
        } else {
            hardcodedMaterials.add(stripped);
        }
    }

    public void addOreGeneration(String material) {
        oreGeneration.add(stripNamespace(material));
    }

    public void addStoneReplacement(String stone, String defaultNamespace, boolean isConfig) {
        if (stone == null || !stone.contains(":")) {
            return;
        }
        
        String finalStone = stone;
        
        Boolean existing = stonesReplacement.get(finalStone);
        if (existing == null || (!existing && isConfig)) {
            stonesReplacement.put(finalStone, isConfig);
        }
    }

    public void addBlacklist(String id) {
        blacklist.add(stripNamespace(id));
    }

    private String stripNamespace(String id) {
        if (id.contains(":")) {
            return id.split(":")[1];
        }
        return id;
    }

    public Set<String> getMaterialsRegistry() {
        Set<String> all = new HashSet<>(hardcodedMaterials);
        all.addAll(configMaterials);
        return all;
    }

    public Set<String> getHardcodedMaterials() {
        return hardcodedMaterials;
    }

    public Set<String> getConfigMaterials() {
        return configMaterials;
    }

    public Set<String> getOreGeneration() {
        return oreGeneration;
    }

    public Map<String, Boolean> getStonesReplacement() {
        return stonesReplacement;
    }

    public Set<String> getBlacklist() {
        return blacklist;
    }

    public boolean isBlacklisted(String id) {
        return blacklist.contains(stripNamespace(id));
    }
}
