//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Enum defining various item categories.
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

public enum ItemType {
    INGOT("ingot", new ItemOverride()
        .setNamingPattern("%s_ingot")
        .setTagCategory("ingots")
        .setTranslations("%s Ingot", "Lingot de %s", "Lingote de %s", "Lingotto di %s", "%s-Barren", "Lingote de %s", "Слиток %s", "%s锭", "%sのインゴット")
        .setRarity(Rarity.COMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(1.0f)
        .setCanBeTrimmable(true)
        .setCanBeFireproof(true)
        .setCanBePiglinLoved(true)
    ),
    SELF("", new ItemOverride()
        .setNamingPattern("%s")
        .setTagCategory("gems")
        .setTranslations("%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s", "%s")
        .setRarity(Rarity.COMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(1.0f)
        .setCanBeTrimmable(true)
        .setCanBeFireproof(true)
        .setCanBePiglinLoved(true)
    ),
    GEM("gem", new ItemOverride()
        .setNamingPattern("%s_gem")
        .setTagCategory("gems")
        .setTranslations("%s Gem", "Gemme de %s", "Gema de %s", "Gemma di %s", "%s-Edelstein", "Gema de %s", "Драгоценный камень %s", "%s宝石", "%sの宝石")
        .setRarity(Rarity.COMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(1.0f)
        .setCanBeTrimmable(true)
        .setCanBeFireproof(true)
        .setCanBePiglinLoved(true)
    ),
    NUGGET("nugget", new ItemOverride()
        .setNamingPattern("%s_nugget")
        .setTagCategory("nuggets")
        .setTranslations("%s Nugget", "Pépite de %s", "Pepita de %s", "Pepita di %s", "%s-Klumpen", "Pepita de %s", "Самородок %s", "%s粒", "%sの塊")
        .setSmeltingMultiplier(0.111f)
        .setXpMultiplier(0.111f)
        .setFuelFactor(0.111f)
    ),
    DUST("dust", new ItemOverride()
        .setNamingPattern("%s_dust")
        .setTagCategory("dusts")
        .setTranslations("%s Dust", "Poudre de %s", "Polvo de %s", "Polvere di %s", "%s-Staub", "Pó de %s", "Пыль %s", "%s粉", "%sの粉")
        .setFuelFactor(0.0f)
    ),
    POWDER("powder", new ItemOverride()
        .setNamingPattern("%s_powder")
        .setTagCategory("powders")
        .setTranslations("%s Powder", "Poudre de %s", "Polvo de %s", "Polvere di %s", "%s-Pulver", "Pó de %s", "Порошок %s", "%s粉末", "%sの粉末")
        .setFuelFactor(0.0f)
        .setUseRawColor(true)
    ),
    RAW("raw", new ItemOverride()
        .setNamingPattern("raw_%s")
        .setTagCategory("raw_materials")
        .setTranslations("Raw %s", "%s Brut", "%s en Bruto", "%s Grezzo", "Rohes %s", "%s Bruto", "Сырой %s", "粗%s", "生%s")
        .setFuelFactor(0.0f)
        .setCanBeTrimmable(false)
        .setUseRawColor(true)
    ),
    CRUSHED_RAW("crushed_raw", new ItemOverride()
        .setNamingPattern("crushed_%s")
        .setTagCategory("crushed_raw_materials")
        .setTranslations("Crushed %s", "%s Broyé", "%s Triturado", "%s Triturato", "Zerkleinertes %s", "%s Triturado", "Дробленый %s", "粉碎%s", "砕かれた%s")
        .setFuelFactor(0.0f)
        .setCanBeTrimmable(false)
        .setUseRawColor(true)
    ),
    ROD("rod", new ItemOverride()
        .setNamingPattern("%s_rod")
        .setTagCategory("rods")
        .setTranslations("%s Rod", "Tige de %s", "Vara de %s", "Barra di %s", "%s-Stab", "Vara de %s", "Стержень %s", "%s棒", "%sの棒")
        .setFuelFactor(0.0f)
    ),
    GEAR("gear", new ItemOverride()
        .setNamingPattern("%s_gear")
        .setTagCategory("gears")
        .setTranslations("%s Gear", "Engrenage de %s", "Engranaje de %s", "Ingranaggio di %s", "%s-Zahnrad", "Engrenagem de %s", "Шестерня %s", "%s齿轮", "%sの歯車")
        .setFuelFactor(0.0f)
    ),
    PLATE("plate", new ItemOverride()
        .setNamingPattern("%s_plate")
        .setTagCategory("plates")
        .setTranslations("%s Plate", "Plaque de %s", "Placa de %s", "Piastra di %s", "%s-Platte", "Placa de %s", "Пластина %s", "%s板", "%sの板")
        .setFuelFactor(0.0f)
        .setCanBeTrimmable(true)
    ),
    SHEET("sheet", new ItemOverride()
        .setNamingPattern("%s_sheet")
        .setTagCategory("sheets")
        .setTranslations("%s Sheet", "Feuille de %s", "Lámina de %s", "Lamina di %s", "%s-Blech", "Folha de %s", "Лист %s", "%s薄板", "%sのシート")
        .setFuelFactor(0.0f)
    ),
    CRYSTAL("crystal", new ItemOverride()
        .setNamingPattern("%s_crystal")
        .setTagCategory("crystals")
        .setTranslations("%s Crystal", "Cristal de %s", "Cristal de %s", "Cristallo di %s", "%s-Kristall", "Cristal de %s", "Кристалл %s", "%s水晶", "%sのクリスタル")
        .setRarity(Rarity.COMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(0.0f)
    ),
    SHARD("shard", new ItemOverride()
        .setNamingPattern("%s_shard")
        .setTagCategory("shards")
        .setTranslations("%s Shard", "Éclat de %s", "Fragmento de %s", "Frammento di %s", "%s-Scherbe", "Fragmento de %s", "Осколок %s", "%s碎片", "%sの破片")
        .setFuelFactor(0.0f)
    ),
    CLUMP("clump", new ItemOverride()
        .setNamingPattern("%s_clump")
        .setTagCategory("clumps")
        .setTranslations("%s Clump", "Agrégat de %s", "Grumo de %s", "Grumo di %s", "%s-Klumpen", "Grumo de %s", "Сгусток %s", "%s块", "%sの塊")
        .setFuelFactor(0.0f)
    ),
    DIRTY_DUST("dirty_dust", new ItemOverride()
        .setNamingPattern("dirty_%s_dust")
        .setTagCategory("dusts")
        .setTranslations("Dirty %s Dust", "Poussière de %s Sale", "Polvo de %s Sucio", "Polvere di %s Sporca", "Schmutziger %s-Staub", "Pó de %s Sujo", "Грязная пыль %s", "脏%s粉", "汚れた%sの粉")
        .setFuelFactor(0.0f)
        .setUseRawColor(true)
    ),
    SCRAP("scrap", new ItemOverride()
        .setNamingPattern("%s_scrap")
        .setTagCategory("scraps")
        .setTranslations("%s Scrap", "Fragment de %s", "Fragmento de %s", "Frammento di %s", "%s-Schrott", "Fragmento de %s", "Обломок %s", "%s碎屑", "%sのスクラップ")
        .setFuelFactor(0.0f)
        .setUseRawColor(true)
    ),
    RING("ring", new ItemOverride()
        .setNamingPattern("%s_ring")
        .setTagCategory("rings")
        .setTranslations("%s Ring", "Anneau de %s", "Anillo de %s", "Anello di %s", "%s-Ring", "Anel de %s", "Кольцо %s", "%s戒指", "%sのリング")
        .setFuelFactor(0.0f)
    ),
    LARGE_PLATE("large_plate", new ItemOverride()
        .setNamingPattern("%s_large_plate")
        .setTagCategory("plates")
        .setTranslations("%s Large Plate", "Grande Plaque de %s", "Placa Grande de %s", "Piastra Grande di %s", "Große %s-Platte", "Placa Grande de %s", "Большая пластина %s", "%s大板", "%sの大きな板")
        .setRarity(Rarity.UNCOMMON)
        .setFuelFactor(0.0f)
    ),
    DOUBLE_INGOT("double_ingot", new ItemOverride()
        .setNamingPattern("%s_double_ingot")
        .setTagCategory("double_ingots")
        .setTranslations("%s Double Ingot", "Double Lingot de %s", "Lingote Doble de %s", "Doppio Lingotto di %s", "Doppel-Ingot %s", "Lingote Duplo de %s", "Двойной слиток %s", "%s双锭", "%sのダブルインゴット")
        .setRarity(Rarity.UNCOMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(2.0f)
    ),
    SMALL_CLUMP("small_clump", new ItemOverride()
        .setNamingPattern("small_%s_clump")
        .setTagCategory("small_clumps")
        .setTranslations("Small %s Clump", "Petit Agrégat de %s", "Grumo de %s Pequeño", "Grumo di %s Piccolo", "Kleiner %s-Klumpen", "Grumo de %s Pequeno", "Малый сгусток %s", "小%s块", "小さな%sの塊")
        .setSmeltingMultiplier(0.33f)
        .setXpMultiplier(0.33f)
        .setFuelFactor(0.0f)
    ),
    SMALL_DUST("small_dust", new ItemOverride()
        .setNamingPattern("small_%s_dust")
        .setTagCategory("small_dusts")
        .setTranslations("Small %s Dust", "Petite Poudre de %s", "Polvo de %s Pequeño", "Polvere di %s Piccola", "Kleiner %s-Staub", "Pó de %s Pequeno", "Малая пыль %s", "小%s粉", "小さな%sの粉")
        .setSmeltingMultiplier(0.111f)
        .setXpMultiplier(0.111f)
        .setFuelFactor(0.0f)
    ),
    COIN("coin", new ItemOverride()
        .setNamingPattern("%s_coin")
        .setTagCategory("coins")
        .setTranslations("%s Coin", "Pièce de %s", "Moneda de %s", "Moneta di %s", "%s-Münze", "Moeda de %s", "Монета %s", "%s硬币", "%sの硬貨")
        .setRarity(Rarity.COMMON)
        .setSmeltingMultiplier(0.0f)
        .setXpMultiplier(0.0f)
        .setFuelFactor(0.0f)
    ),
    ENRICHED("enriched", new ItemOverride()
        .setNamingPattern("enriched_%s")
        .setTagCategory("enriched")
        .setTranslations("Enriched %s", "%s Enrichi", "%s Enriquecido", "%s Arricchito", "Angereichertes %s", "%s Enriquecido", "Обогащенный %s", "富集%s", "濃縮された%s")
        .setRarity(Rarity.UNCOMMON)
        .setFuelFactor(0.0f)
    ),
    REFINED_INGOT("refined_ingot", new ItemOverride()
        .setNamingPattern("refined_%s_ingot")
        .setTagCategory("refined_ingots")
        .setTranslations("Refined %s Ingot", "Lingot de %s Raffiné", "Lingote de %s Refinado", "Lingotto di %s Raffinato", "Raffinerter %s-Barren", "Lingote de %s Refinado", "Очищенный слиток %s", "精炼%s锭", "洗練された%sのインゴット")
        .setRarity(Rarity.RARE)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(0.0f)
    ),
    CHARGED_CRYSTAL("charged_crystal", new ItemOverride()
        .setNamingPattern("charged_%s_crystal")
        .setTagCategory("charged_crystals")
        .setTranslations("Charged %s Crystal", "Cristal de %s Chargé", "Cristal de %s Cargado", "Cristallo di %s Carico", "Geladener %s-Kristall", "Cristal de %s Carregado", "Заряженный кристалл %s", "充能%s水晶", "充填された%sのクリスタル")
        .setRarity(Rarity.RARE)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(0.0f)
    );
    private final String suffix;
    private final ItemOverride defaultOverride;
    ItemType(String suffix, ItemOverride defaultOverride) {
        this.suffix = suffix;
        this.defaultOverride = defaultOverride;
    }
    public String getSuffix() {
        return suffix;
    }
    public ItemOverride getDefaultOverride() {
        return defaultOverride;
    }
}
