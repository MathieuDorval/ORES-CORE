//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: System component for Ores Core.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.registry;

import java.util.HashSet;
import java.util.Set;

public class OresRegistryData {
    private final Set<String> materialsRegistry = new HashSet<>();
    private final Set<String> oreGeneration = new HashSet<>();
    private final Set<String> stonesReplacement = new HashSet<>();

    public void addMaterial(String material) {
        materialsRegistry.add(stripNamespace(material));
    }

    public void addOreGeneration(String material) {
        oreGeneration.add(stripNamespace(material));
    }

    public void addStoneReplacement(String stone, String defaultNamespace) {
        if (!stone.contains(":")) {
            stonesReplacement.add(defaultNamespace + ":" + stone);
        } else {
            stonesReplacement.add(stone);
        }
    }

    private String stripNamespace(String id) {
        if (id.contains(":")) {
            return id.split(":")[1];
        }
        return id;
    }

    public Set<String> getMaterialsRegistry() {
        return materialsRegistry;
    }

    public Set<String> getOreGeneration() {
        return oreGeneration;
    }

    public Set<String> getStonesReplacement() {
        return stonesReplacement;
    }
}
