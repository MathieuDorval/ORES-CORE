//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Enum defining all dynamically generated item variants
// along with their default base structures and generic naming conventions.
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

public enum ItemType {
    INGOT("ingot", new ItemOverride()
        .setNamingPattern("%s_ingot")
        .setTagCategory("ingots")
        .setNameEN("%s Ingot").setNameFR("Lingot de %s").setNameES("Lingote de %s").setNameIT("Lingotto di %s").setNameDE("%s-Barren").setNamePT("Lingote de %s").setNameRU("Слиток %s").setNameZH("%s锭").setNameJP("%sのインゴット")
        .setRarity(Rarity.COMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(1.0f)
        .setCanBeTrimmable(true)
        .setCanBeFireproof(true)
        .setCanBePiglinLoved(true)
        .addCompatiblePattern("ingot_%s")
    ),
    SELF("self", new ItemOverride()
        .setNamingPattern("%s")
        .setTagCategory("gems")
        .setNameEN("%s").setNameFR("%s").setNameES("%s").setNameIT("%s").setNameDE("%s").setNamePT("%s").setNameRU("%s").setNameZH("%s").setNameJP("%s")
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
        .setNameEN("%s Gem").setNameFR("Gemme de %s").setNameES("Gema de %s").setNameIT("Gemma di %s").setNameDE("%s-Edelstein").setNamePT("Gema de %s").setNameRU("Драгоценный камень %s").setNameZH("%s宝石").setNameJP("%sの宝石")
        .setRarity(Rarity.COMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(1.0f)
        .setCanBeTrimmable(true)
        .setCanBeFireproof(true)
        .setCanBePiglinLoved(true)
        .addCompatiblePattern("gem_%s")
    ),
    NUGGET("nugget", new ItemOverride()
        .setNamingPattern("%s_nugget")
        .setTagCategory("nuggets")
        .setNameEN("%s Nugget").setNameFR("Pépite de %s").setNameES("Pepita de %s").setNameIT("Pepita di %s").setNameDE("%s-Klumpen").setNamePT("Pepita de %s").setNameRU("Самородок %s").setNameZH("%s粒").setNameJP("%sの塊")
        .setSmeltingMultiplier(0.111f)
        .setXpMultiplier(0.111f)
        .setFuelFactor(0.111f)
        .addCompatiblePattern("nugget_%s")
    ),
    DUST("dust", new ItemOverride()
        .setNamingPattern("%s_dust")
        .setTagCategory("dusts")
        .setNameEN("%s Dust").setNameFR("Poudre de %s").setNameES("Polvo de %s").setNameIT("Polvere di %s").setNameDE("%s-Staub").setNamePT("Pó de %s").setNameRU("Пыль %s").setNameZH("%s粉").setNameJP("%sの粉")
        .setFuelFactor(0.0f)
        .addCompatiblePattern("dust_%s")
    ),
    POWDER("powder", new ItemOverride()
        .setNamingPattern("%s_powder")
        .setTagCategory("powders")
        .setNameEN("%s Powder").setNameFR("Poudre de %s").setNameES("Polvo de %s").setNameIT("Polvere di %s").setNameDE("%s-Pulver").setNamePT("Pó de %s").setNameRU("Порошок %s").setNameZH("%s粉末").setNameJP("%sの粉末")
        .setFuelFactor(0.0f)
        .setUseRawColor(true)
    ),
    RAW("raw", new ItemOverride()
        .setNamingPattern("raw_%s")
        .setTagCategory("raw_materials")
        .setNameEN("Raw %s").setNameFR("%s Brut").setNameES("%s en Bruto").setNameIT("%s Grezzo").setNameDE("Rohes %s").setNamePT("%s Bruto").setNameRU("Сырой %s").setNameZH("粗%s").setNameJP("生%s")
        .setFuelFactor(0.0f)
        .setCanBeTrimmable(false)
        .setUseRawColor(true)
        .addCompatiblePattern("%s_raw")
        .addCompatiblePattern("%s_raw_material")
    ),
    CRUSHED_RAW("crushed_raw", new ItemOverride()
        .setNamingPattern("crushed_%s")
        .setTagCategory("crushed_raw_materials")
        .setNameEN("Crushed %s").setNameFR("%s Broyé").setNameES("%s Triturado").setNameIT("%s Triturato").setNameDE("Zerkleinertes %s").setNamePT("%s Triturado").setNameRU("Дробленый %s").setNameZH("粉碎%s").setNameJP("砕かれた%s")
        .setFuelFactor(0.0f)
        .setCanBeTrimmable(false)
        .setUseRawColor(true)
    ),
    ROD("rod", new ItemOverride()
        .setNamingPattern("%s_rod")
        .setTagCategory("rods")
        .setNameEN("%s Rod").setNameFR("Tige de %s").setNameES("Vara de %s").setNameIT("Barra di %s").setNameDE("%s-Stab").setNamePT("Vara de %s").setNameRU("Стержень %s").setNameZH("%s棒").setNameJP("%sの棒")
        .setFuelFactor(0.0f)
        .addCompatiblePattern("rod_%s")
    ),
    GEAR("gear", new ItemOverride()
        .setNamingPattern("%s_gear")
        .setTagCategory("gears")
        .setNameEN("%s Gear").setNameFR("Engrenage de %s").setNameES("Engranaje de %s").setNameIT("Ingranaggio di %s").setNameDE("%s-Zahnrad").setNamePT("Engrenagem de %s").setNameRU("Шестерня %s").setNameZH("%s齿轮").setNameJP("%sの歯車")
        .setFuelFactor(0.0f)
        .addCompatiblePattern("gear_%s")
    ),
    PLATE("plate", new ItemOverride()
        .setNamingPattern("%s_plate")
        .setTagCategory("plates")
        .setNameEN("%s Plate").setNameFR("Plaque de %s").setNameES("Placa de %s").setNameIT("Piastra di %s").setNameDE("%s-Platte").setNamePT("Placa de %s").setNameRU("Пластина %s").setNameZH("%s板").setNameJP("%sの板")
        .setFuelFactor(0.0f)
        .setCanBeTrimmable(true)
        .addCompatiblePattern("plate_%s")
    ),
    SHEET("sheet", new ItemOverride()
        .setNamingPattern("%s_sheet")
        .setTagCategory("sheets")
        .setNameEN("%s Sheet").setNameFR("Feuille de %s").setNameES("Lámina de %s").setNameIT("Lamina di %s").setNameDE("%s-Blech").setNamePT("Folha de %s").setNameRU("Лист %s").setNameZH("%s薄板").setNameJP("%sのシート")
        .setFuelFactor(0.0f)
    ),
    CRYSTAL("crystal", new ItemOverride()
        .setNamingPattern("%s_crystal")
        .setTagCategory("crystals")
        .setNameEN("%s Crystal").setNameFR("Cristal de %s").setNameES("Cristal de %s").setNameIT("Cristallo di %s").setNameDE("%s-Kristall").setNamePT("Cristal de %s").setNameRU("Кристалл %s").setNameZH("%s水晶").setNameJP("%sのクリスタル")
        .setRarity(Rarity.COMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(0.0f)
    ),
    SHARD("shard", new ItemOverride()
        .setNamingPattern("%s_shard")
        .setTagCategory("shards")
        .setNameEN("%s Shard").setNameFR("Éclat de %s").setNameES("Fragmento de %s").setNameIT("Frammento di %s").setNameDE("%s-Scherbe").setNamePT("Fragmento de %s").setNameRU("Осколок %s").setNameZH("%s碎片").setNameJP("%sの破片")
        .setFuelFactor(0.0f)
    ),
    CLUMP("clump", new ItemOverride()
        .setNamingPattern("%s_clump")
        .setTagCategory("clumps")
        .setNameEN("%s Clump").setNameFR("Agrégat de %s").setNameES("Grumo de %s").setNameIT("Grumo di %s").setNameDE("%s-Klumpen").setNamePT("Grumo de %s").setNameRU("Сгусток %s").setNameZH("%s块").setNameJP("%sの塊")
        .setFuelFactor(0.0f)
    ),
    DIRTY_DUST("dirty_dust", new ItemOverride()
        .setNamingPattern("dirty_%s_dust")
        .setTagCategory("dusts")
        .setNameEN("Dirty %s Dust").setNameFR("Poussière de %s Sale").setNameES("Polvo de %s Sucio").setNameIT("Polvere di %s Sporca").setNameDE("Schmutziger %s-Staub").setNamePT("Pó de %s Sujo").setNameRU("Грязная пыль %s").setNameZH("脏%s粉").setNameJP("汚れた%sの粉")
        .setFuelFactor(0.0f)
        .setUseRawColor(true)
    ),
    SCRAP("scrap", new ItemOverride()
        .setNamingPattern("%s_scrap")
        .setTagCategory("scraps")
        .setNameEN("%s Scrap").setNameFR("Fragment de %s").setNameES("Fragmento de %s").setNameIT("Frammento di %s").setNameDE("%s-Schrott").setNamePT("Fragmento de %s").setNameRU("Обломок %s").setNameZH("%s碎屑").setNameJP("%sのスクラップ")
        .setFuelFactor(0.0f)
        .setUseRawColor(true)
    ),
    RING("ring", new ItemOverride()
        .setNamingPattern("%s_ring")
        .setTagCategory("rings")
        .setNameEN("%s Ring").setNameFR("Anneau de %s").setNameES("Anillo de %s").setNameIT("Anello di %s").setNameDE("%s-Ring").setNamePT("Anel de %s").setNameRU("Кольцо %s").setNameZH("%s戒指").setNameJP("%sのリング")
        .setFuelFactor(0.0f)
    ),
    LARGE_PLATE("large_plate", new ItemOverride()
        .setNamingPattern("%s_large_plate")
        .setTagCategory("plates")
        .setNameEN("%s Large Plate").setNameFR("Grande Plaque de %s").setNameES("Placa Grande de %s").setNameIT("Piastra Grande di %s").setNameDE("Große %s-Platte").setNamePT("Placa Grande de %s").setNameRU("Большая пластина %s").setNameZH("%s大板").setNameJP("%sの大きな板")
        .setRarity(Rarity.UNCOMMON)
        .setFuelFactor(0.0f)
    ),
    DOUBLE_INGOT("double_ingot", new ItemOverride()
        .setNamingPattern("%s_double_ingot")
        .setTagCategory("double_ingots")
        .setNameEN("%s Double Ingot").setNameFR("Double Lingot de %s").setNameES("Lingote Doble de %s").setNameIT("Doppio Lingotto di %s").setNameDE("Doppel-Ingot %s").setNamePT("Lingote Duplo de %s").setNameRU("Двойной слиток %s").setNameZH("%s双锭").setNameJP("%sのダブルインゴット")
        .setRarity(Rarity.UNCOMMON)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(2.0f)
    ),
    SMALL_CLUMP("small_clump", new ItemOverride()
        .setNamingPattern("small_%s_clump")
        .setTagCategory("small_clumps")
        .setNameEN("Small %s Clump").setNameFR("Petit Agrégat de %s").setNameES("Grumo de %s Pequeño").setNameIT("Grumo di %s Piccolo").setNameDE("Kleiner %s-Klumpen").setNamePT("Grumo de %s Pequeno").setNameRU("Малый сгусток %s").setNameZH("小%s块").setNameJP("小さな%sの塊")
        .setSmeltingMultiplier(0.33f)
        .setXpMultiplier(0.33f)
        .setFuelFactor(0.0f)
    ),
    SMALL_DUST("small_dust", new ItemOverride()
        .setNamingPattern("small_%s_dust")
        .setTagCategory("small_dusts")
        .setNameEN("Small %s Dust").setNameFR("Petite Poudre de %s").setNameES("Polvo de %s Pequeño").setNameIT("Polvere di %s Piccola").setNameDE("Kleiner %s-Staub").setNamePT("Pó de %s Pequeno").setNameRU("Малая пыль %s").setNameZH("小%s粉").setNameJP("小さな%sの粉")
        .setSmeltingMultiplier(0.111f)
        .setXpMultiplier(0.111f)
        .setFuelFactor(0.0f)
    ),
    COIN("coin", new ItemOverride()
        .setNamingPattern("%s_coin")
        .setTagCategory("coins")
        .setNameEN("%s Coin").setNameFR("Pièce de %s").setNameES("Moneda de %s").setNameIT("Moneta di %s").setNameDE("%s-Münze").setNamePT("Moeda de %s").setNameRU("Монета %s").setNameZH("%s硬币").setNameJP("%sの硬貨")
        .setRarity(Rarity.COMMON)
        .setSmeltingMultiplier(0.0f)
        .setXpMultiplier(0.0f)
        .setFuelFactor(0.0f)
    ),
    ENRICHED("enriched", new ItemOverride()
        .setNamingPattern("enriched_%s")
        .setTagCategory("enriched")
        .setNameEN("Enriched %s").setNameFR("%s Enrichi").setNameES("%s Enriquecido").setNameIT("%s Arricchito").setNameDE("Angereichertes %s").setNamePT("%s Enriquecido").setNameRU("Обогащенный %s").setNameZH("富集%s").setNameJP("濃縮された%s")
        .setRarity(Rarity.UNCOMMON)
        .setFuelFactor(0.0f)
    ),
    REFINED_INGOT("refined_ingot", new ItemOverride()
        .setNamingPattern("refined_%s_ingot")
        .setTagCategory("refined_ingots")
        .setNameEN("Refined %s Ingot").setNameFR("Lingot de %s Raffiné").setNameES("Lingote de %s Refinado").setNameIT("Lingotto di %s Raffinato").setNameDE("Raffinerter %s-Barren").setNamePT("Lingote de %s Refinado").setNameRU("Очищенный слиток %s").setNameZH("精炼%s锭").setNameJP("洗練された%sのインゴット")
        .setRarity(Rarity.RARE)
        .setCanBeBeaconPayment(true)
        .setFuelFactor(0.0f)
    ),
    CHARGED_CRYSTAL("charged_crystal", new ItemOverride()
        .setNamingPattern("charged_%s_crystal")
        .setTagCategory("charged_crystals")
        .setNameEN("Charged %s Crystal").setNameFR("Cristal de %s Chargé").setNameES("Cristal de %s Cargado").setNameIT("Cristallo di %s Carico").setNameDE("Geladener %s-Kristall").setNamePT("Cristal de %s Carregado").setNameRU("Заряженный кристалл %s").setNameZH("充能%s水晶").setNameJP("充填された%sのクリスタル")
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
        return suffix.isEmpty() ? "self" : suffix;
    }
    public ItemOverride getDefaultOverride() {
        return defaultOverride;
    }

    public static ItemType forName(String name) {
        if (name == null || name.isEmpty()) return null;
        for (ItemType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.suffix.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
