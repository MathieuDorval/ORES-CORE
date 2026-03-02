//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Configuration model for ore veins.
//
// Author: __mathieu
// Version: 26.1.001
//
// License: CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike)
// This code is free to be copied, shared, and adapted under the terms 
// of the Creative Commons NC-SA license. 
// Commercial use is strictly prohibited.
//

package ores.mathieu.worldgen;

import java.util.List;

public class VeinConfig {
    public List<String> materials;
    public List<Integer> materialsRatio;
    public String veinType;

    public String shape = "BLOB";
    public String distribution = "TRAPEZOID";
    public int rarity = 10;
    public int density = 8;
    public float airExposureChance = 0.5f;

    public String associatedBlock;
    public float oreDensity = 0.1f;
    public String bonusBlock;
    public float bonusBlockChance = 0.05f;

    public int minY = -64;
    public int maxY = 64;
    public String specifics = "NONE";
    public List<String> dimensionsWhitelist;
    public List<String> dimensionsBlacklist;
    public List<String> biomesWhitelist;
    public List<String> biomesBlacklist;
    
    public String getMaterialWeighted(net.minecraft.util.RandomSource random) {
        if (materials == null || materials.isEmpty()) return null;
        if (materialsRatio == null || materialsRatio.isEmpty() || materialsRatio.size() != materials.size()) {
            return materials.get(random.nextInt(materials.size()));
        }
        
        int totalWeight = 0;
        for (int weight : materialsRatio) {
            totalWeight += Math.max(0, weight);
        }
        if (totalWeight <= 0) return materials.get(random.nextInt(materials.size()));
        
        int roll = random.nextInt(totalWeight);
        int currentWeightSum = 0;
        for (int i = 0; i < materials.size(); i++) {
            currentWeightSum += Math.max(0, materialsRatio.get(i));
            if (roll < currentWeightSum) {
                return materials.get(i);
            }
        }
        return materials.get(0);
    }
}
