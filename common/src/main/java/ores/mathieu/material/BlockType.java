//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Enum defining all dynamically generated block variants
// along with their fallback default overrides.
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public enum BlockType {
    BLOCK("block", new BlockOverride()
        .setNamingPattern("%s_block")
        .setTagCategory("storage_blocks")
        .setNameEN("Block of %s").setNameFR("Bloc de %s").setNameES("Bloque de %s").setNameIT("Blocco di %s").setNameDE("%s-Block").setNamePT("Bloco de %s").setNameRU("Блок %s").setNameZH("%s块").setNameJP("%sのブロック")
        .setRarity(Rarity.COMMON)
        .setSmeltingMultiplier(9.0f)
        .setXpMultiplier(9.0f)
        .setFuelFactor(9.0f)
        .setSound(SoundType.METAL)
        .setHardness(5.0f)
        .setResistance(6.0f)
        .setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setSlipperiness(true)
        .setSpeedFactor(true)
        .setJumpFactor(true)
        .setLightMode(true)
        .setParticleMode(true)
        .setRedstonePower(true)
        .addCompatiblePattern("block_%s")
    ),
    RAW_BLOCK("raw_block", new BlockOverride()
        .setNamingPattern("raw_%s_block")
        .setTagCategory("storage_blocks")
        .setNameEN("Block of Raw %s").setNameFR("Bloc de %s Brut").setNameES("Bloque de %s en Bruto").setNameIT("Blocco di %s Grezzo").setNameDE("Roher %s-Block").setNamePT("Bloco de %s Bruto").setNameRU("Блок сырого %s").setNameZH("粗%s块").setNameJP("生%sのブロック")
        .setSmeltingMultiplier(9.0f)
        .setXpMultiplier(9.0f)
        .setFuelFactor(0.0f)
        .setSound(SoundType.STONE)
        .setHardness(5.0f)
        .setResistance(6.0f)
        .setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setSlipperiness(false)
        .setSpeedFactor(false)
        .setJumpFactor(false)
        .setLightMode(false)
        .setParticleMode(false)
        .setRedstonePower(false)
        .setUseRawColor(true)
        .setTagPattern("raw_%s")
        .addCompatiblePattern("%s_raw_block")
        .addCompatiblePattern("%s_raw_storage_block")
    ),
    DUST_BLOCK("dust_block", new BlockOverride()
        .setNamingPattern("%s_dust_block")
        .setTagCategory("storage_blocks")
        .setNameEN("%s Dust Block").setNameFR("Bloc de Poudre de %s").setNameES("Bloque de Polvo de %s").setNameIT("Blocco di Polvere di %s").setNameDE("%s-Staubblock").setNamePT("Bloco de Pó de %s").setNameRU("Блок пыли %s").setNameZH("%s粉块").setNameJP("%sの粉ブロック")
        .setSmeltingMultiplier(9.0f)
        .setXpMultiplier(9.0f)
        .setFuelFactor(0.0f)
        .setSound(SoundType.SAND)
        .setHardness(0.5f)
        .setResistance(0.5f)
        .setRequiredTool("shovel")
        .setGravityMode("FALLING")
        .setMapColor(MapColor.SAND)
        .setShouldDropWhenFallingHitTorch(true)
        .setCanBeBeaconBase(false)
        .setSlipperiness(true)
        .setSpeedFactor(true)
        .setJumpFactor(true)
        .setLightMode(true)
        .setParticleMode(true)
        .setRedstonePower(true)
    ),
    POWDER_BLOCK("powder_block", new BlockOverride()
        .setNamingPattern("%s_powder_block")
        .setTagCategory("storage_blocks")
        .setNameEN("%s Powder Block").setNameFR("Bloc de Poudre de %s").setNameES("Bloque de Polvo de %s").setNameIT("Blocco di Polvere di %s").setNameDE("%s-Pulverblock").setNamePT("Bloco de Pó de %s").setNameRU("Блок порошка %s").setNameZH("%s粉末块").setNameJP("%sの粉末ブロック")
        .setSmeltingMultiplier(9.0f)
        .setXpMultiplier(9.0f)
        .setFuelFactor(0.0f)
        .setSound(SoundType.SAND)
        .setHardness(0.5f)
        .setResistance(0.5f)
        .setRequiredTool("shovel")
        .setGravityMode("FALLING")
        .setMapColor(MapColor.SAND)
        .setShouldDropWhenFallingHitTorch(true)
        .setCanBeBeaconBase(false)
        .setSlipperiness(true)
        .setSpeedFactor(true)
        .setJumpFactor(true)
        .setLightMode(true)
        .setParticleMode(true)
        .setRedstonePower(true)
        .setUseRawColor(true)
    ),
    GLASS("glass", new BlockOverride()
        .setNamingPattern("%s_glass")
        .setTagCategory("glass")
        .setNameEN("%s Glass").setNameFR("Verre de %s").setNameES("Cristal de %s").setNameIT("Vetro di %s").setNameDE("%s-Glas").setNamePT("Vidro de %s").setNameRU("Стекло %s").setNameZH("%s玻璃").setNameJP("%sのガラス")
        .setFuelFactor(0.0f)
        .setSound(SoundType.GLASS)
        .setHardness(0.3f)
        .setResistance(0.3f)
        .setTranslucent(true)
        .setMapColor(MapColor.NONE)
        .setCanBeBeaconBase(false)
        .setSlipperiness(true)
        .setSpeedFactor(true)
        .setJumpFactor(true)
        .setLightMode(true)
        .setParticleMode(true)
        .setRedstonePower(false)

    ),
    LAMP("lamp", new BlockOverride()
        .setNamingPattern("%s_lamp")
        .setTagCategory("lamps")
        .setNameEN("%s Lamp").setNameFR("Lampe de %s").setNameES("Lámpara de %s").setNameIT("Lampada di %s").setNameDE("%s-Lampe").setNamePT("Lâmpada de %s").setNameRU("Лампа %s").setNameZH("%s灯").setNameJP("%sのランプ")
        .setFuelFactor(0.0f)
        .setSound(SoundType.GLASS)
        .setHardness(0.3f)
        .setResistance(0.3f)
        .setLightLevel(15)
        .setLightMode("ALWAYS")
        .setTranslucent(true)
        .setMapColor(MapColor.NONE)
        .setCanBeBeaconBase(false)
        .setSlipperiness(true)
        .setSpeedFactor(true)
        .setJumpFactor(true)
        .setParticleMode(true)
        .setRedstonePower(true)
    ),
    ORE("ore", new BlockOverride()
        .setNamingPattern("%s_ore")
        .setTagCategory("ores")
        .setNameEN("%s Ore").setNameFR("Minerai de %s").setNameES("Mineral de %s").setNameIT("Minerale di %s").setNameDE("%s-Erz").setNamePT("Minério de %s").setNameRU("Руда %s").setNameZH("%s矿石").setNameJP("%sの鉱石")
        .setShouldDropWhenFallingHitTorch(false)
    ),

    COMPRESSED_BLOCK("compressed_block", new BlockOverride()
        .setNamingPattern("compressed_%s_block").setTagCategory("storage_blocks/compressed/level_1")
        .setNameEN("Compressed Block of %s").setNameFR("Bloc de %s Compressé").setNameES("Bloque de %s Comprimido").setNameIT("Blocco di %s Compresso").setNameDE("Komprimierter %s-Block").setNamePT("Bloco de %s Compactado").setNameRU("Сжатый блок %s").setNameZH("压缩%s块").setNameJP("圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setSmeltingMultiplier(81.0f).setXpMultiplier(81.0f).setFuelFactor(81.0f)
        .setSound(SoundType.METAL).setHardness(10.0f).setResistance(12.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(BLOCK).setCompressionLevel(1)
    ),
    COMPRESSED_RAW_BLOCK("compressed_raw_block", new BlockOverride()
        .setNamingPattern("compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_1")
        .setNameEN("Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Compressé").setNameES("Bloque de %s en Bruto Comprimido").setNameIT("Blocco di %s Grezzo Compresso").setNameDE("Komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Compactado").setNameRU("Сжатый блок сырого %s").setNameZH("压缩粗%s块").setNameJP("圧縮された生%sのブロック")
        .setSmeltingMultiplier(81.0f).setXpMultiplier(81.0f)
        .setSound(SoundType.STONE).setHardness(10.0f).setResistance(12.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(RAW_BLOCK).setCompressionLevel(1)
        .setUseRawColor(true)
    ),
    COMPRESSED_DUST_BLOCK("compressed_dust_block", new BlockOverride()
        .setNamingPattern("compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_1")
        .setNameEN("Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Compressé").setNameES("Bloque de Polvo de %s Comprimido").setNameIT("Blocco di Polvere di %s Compresso").setNameDE("Komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Compactado").setNameRU("Сжатый блок пыли %s").setNameZH("压缩%s粉块").setNameJP("圧縮された%sの粉ブロック")
        .setSmeltingMultiplier(81.0f).setXpMultiplier(81.0f)
        .setSound(SoundType.SAND).setHardness(2.0f).setResistance(2.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(DUST_BLOCK).setCompressionLevel(1)
    ),

    DOUBLE_COMPRESSED_BLOCK("double_compressed_block", new BlockOverride()
        .setNamingPattern("double_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_2")
        .setNameEN("Double Compressed Block of %s").setNameFR("Bloc de %s Doublement Compressé").setNameES("Bloque de %s Doblemente Comprimido").setNameIT("Blocco di %s Doppio Compresso").setNameDE("Doppelt komprimierter %s-Block").setNamePT("Bloco de %s Duplamente Compactado").setNameRU("Двойной сжатый блок %s").setNameZH("双重压缩%s块").setNameJP("二重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(729.0f)
        .setSound(SoundType.METAL).setHardness(20.0f).setResistance(25.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(COMPRESSED_BLOCK).setCompressionLevel(2)
    ),
    DOUBLE_COMPRESSED_RAW_BLOCK("double_compressed_raw_block", new BlockOverride()
        .setNamingPattern("double_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_2")
        .setNameEN("Double Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Doublement Compressé").setNameES("Bloque de %s en Bruto Doblemente Comprimido").setNameIT("Blocco di %s Grezzo Doppio Compresso").setNameDE("Doppelt komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Duplamente Compactado").setNameRU("Двойной сжатый блок сырого %s").setNameZH("双重压缩粗%s块").setNameJP("二重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(20.0f).setResistance(25.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(COMPRESSED_RAW_BLOCK).setCompressionLevel(2)
        .setUseRawColor(true)
    ),
    DOUBLE_COMPRESSED_DUST_BLOCK("double_compressed_dust_block", new BlockOverride()
        .setNamingPattern("double_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_2")
        .setNameEN("Double Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Doublement Compressé").setNameES("Bloque de Polvo de %s Doblemente Comprimido").setNameIT("Blocco di Polvere di %s Doppio Compresso").setNameDE("Doppelt komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Duplamente Compactado").setNameRU("Двойной сжатый блок пыли %s").setNameZH("双重压缩%s粉块").setNameJP("二重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(4.0f).setResistance(4.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(COMPRESSED_DUST_BLOCK).setCompressionLevel(2)
    ),

    TRIPLE_COMPRESSED_BLOCK("triple_compressed_block", new BlockOverride()
        .setNamingPattern("triple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_3")
        .setNameEN("Triple Compressed Block of %s").setNameFR("Bloc de %s Triplement Compressé").setNameES("Bloque de %s Triplemente Comprimido").setNameIT("Blocco di %s Triplo Compresso").setNameDE("Dreifach komprimierter %s-Block").setNamePT("Bloco de %s Triplamente Compactado").setNameRU("Тройной сжатый блок %s").setNameZH("三重压缩%s块").setNameJP("三重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(6561.0f)
        .setSound(SoundType.METAL).setHardness(40.0f).setResistance(50.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(DOUBLE_COMPRESSED_BLOCK).setCompressionLevel(3)
    ),
    TRIPLE_COMPRESSED_RAW_BLOCK("triple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("triple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_3")
        .setNameEN("Triple Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Triplement Compressé").setNameES("Bloque de %s en Bruto Triplemente Comprimido").setNameIT("Blocco di %s Grezzo Triplo Compresso").setNameDE("Dreifach komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Triplamente Compactado").setNameRU("Тройной сжатый блок сырого %s").setNameZH("三重压缩粗%s块").setNameJP("三重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(40.0f).setResistance(50.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(DOUBLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(3)
        .setUseRawColor(true)
    ),
    TRIPLE_COMPRESSED_DUST_BLOCK("triple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("triple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_3")
        .setNameEN("Triple Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Triplement Compressé").setNameES("Bloque de Polvo de %s Triplemente Comprimido").setNameIT("Blocco di Polvere di %s Triplo Compresso").setNameDE("Dreifach komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Triplamente Compactado").setNameRU("Тройной сжатый блок пыли %s").setNameZH("三重压缩%s粉块").setNameJP("三重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(8.0f).setResistance(8.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(DOUBLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(3)
    ),

    QUADRUPLE_COMPRESSED_BLOCK("quadruple_compressed_block", new BlockOverride()
        .setNamingPattern("quadruple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_4")
        .setNameEN("Quadruple Compressed Block of %s").setNameFR("Bloc de %s Quadruplement Compressé").setNameES("Bloque de %s Cuádruplemente Comprimido").setNameIT("Blocco di %s Quadruplo Compresso").setNameDE("Vierfach komprimierter %s-Block").setNamePT("Bloco de %s Quadruplamente Compactado").setNameRU("Четверной сжатый блок %s").setNameZH("四重压缩%s块").setNameJP("四重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(59049.0f)
        .setSound(SoundType.METAL).setHardness(80.0f).setResistance(100.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(TRIPLE_COMPRESSED_BLOCK).setCompressionLevel(4)
    ),
    QUADRUPLE_COMPRESSED_RAW_BLOCK("quadruple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("quadruple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_4")
        .setNameEN("Quadruple Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Quadruplement Compressé").setNameES("Bloque de %s en Bruto Cuádruplemente Comprimido").setNameIT("Blocco di %s Grezzo Quadruplo Compresso").setNameDE("Vierfach komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Quadruplamente Compactado").setNameRU("Четверной сжатый блок сырого %s").setNameZH("四重压缩粗%s块").setNameJP("四重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(80.0f).setResistance(100.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(TRIPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(4)
        .setUseRawColor(true)
    ),
    QUADRUPLE_COMPRESSED_DUST_BLOCK("quadruple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("quadruple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_4")
        .setNameEN("Quadruple Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Quadruplement Compressé").setNameES("Bloque de Polvo de %s Cuádruplemente Comprimido").setNameIT("Blocco di Polvere di %s Quadruplo Compresso").setNameDE("Vierfach komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Quadruplamente Compactado").setNameRU("Четверной сжатый блок пыли %s").setNameZH("四重压缩%s粉块").setNameJP("四重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(16.0f).setResistance(16.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(TRIPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(4)
    ),

    QUINTUPLE_COMPRESSED_BLOCK("quintuple_compressed_block", new BlockOverride()
        .setNamingPattern("quintuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_5")
        .setNameEN("Quintuple Compressed Block of %s").setNameFR("Bloc de %s Quintuplement Compressé").setNameES("Bloque de %s Quíntuplemente Comprimido").setNameIT("Blocco di %s Quintuplo Compresso").setNameDE("Fünffach komprimierter %s-Block").setNamePT("Bloco de %s Quintuplamente Compactado").setNameRU("Пятерной сжатый блок %s").setNameZH("五重压缩%s块").setNameJP("五重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(150.0f).setResistance(200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(QUADRUPLE_COMPRESSED_BLOCK).setCompressionLevel(5)
    ),
    QUINTUPLE_COMPRESSED_RAW_BLOCK("quintuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("quintuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_5")
        .setNameEN("Quintuple Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Quintuplement Compressé").setNameES("Bloque de %s en Bruto Quíntuplemente Comprimido").setNameIT("Blocco di %s Grezzo Quintuplo Compresso").setNameDE("Fünffach komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Quintuplamente Compactado").setNameRU("Пятерной сжатый блок сырого %s").setNameZH("五重压缩粗%s块").setNameJP("五重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(150.0f).setResistance(200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(QUADRUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(5)
        .setUseRawColor(true)
    ),
    QUINTUPLE_COMPRESSED_DUST_BLOCK("quintuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("quintuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_5")
        .setNameEN("Quintuple Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Quintuplement Compressé").setNameES("Bloque de Polvo de %s Quíntuplemente Comprimido").setNameIT("Blocco di Polvere di %s Quintuplo Compresso").setNameDE("Fünffach komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Quintuplamente Compactado").setNameRU("Пятерной сжатый блок пыли %s").setNameZH("五重压缩%s粉块").setNameJP("五重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(32.0f).setResistance(32.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(QUADRUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(5)
    ),

    SEXTUPLE_COMPRESSED_BLOCK("sextuple_compressed_block", new BlockOverride()
        .setNamingPattern("sextuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_6")
        .setNameEN("Sextuple Compressed Block of %s").setNameFR("Bloc de %s Sextuplement Compressé").setNameES("Bloque de %s Sextuplemente Comprimido").setNameIT("Blocco di %s Sestuplo Compresso").setNameDE("Sechsfach komprimierter %s-Block").setNamePT("Bloco de %s Sextuplamente Compactado").setNameRU("Шестерной сжатый блок %s").setNameZH("六重压缩%s块").setNameJP("六重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(300.0f).setResistance(400.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(QUINTUPLE_COMPRESSED_BLOCK).setCompressionLevel(6)
    ),
    SEXTUPLE_COMPRESSED_RAW_BLOCK("sextuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("sextuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_6")
        .setNameEN("Sextuple Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Sextuplement Compressé").setNameES("Bloque de %s en Bruto Sextuplemente Comprimido").setNameIT("Blocco di %s Grezzo Sestuplo Compresso").setNameDE("Sechsfach komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Sextuplamente Compactado").setNameRU("Шестерной сжатый блок сырого %s").setNameZH("六重压缩粗%s块").setNameJP("六重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(300.0f).setResistance(400.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(QUINTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(6)
        .setUseRawColor(true)
    ),
    SEXTUPLE_COMPRESSED_DUST_BLOCK("sextuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("sextuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_6")
        .setNameEN("Sextuple Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Sextuplement Compressé").setNameES("Bloque de Polvo de %s Sextuplemente Comprimido").setNameIT("Blocco di Polvere di %s Sestuplo Compresso").setNameDE("Sechsfach komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Sextuplamente Compactado").setNameRU("Шестерной сжатый блок пыли %s").setNameZH("六重压缩%s粉块").setNameJP("六重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(64.0f).setResistance(64.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(QUINTUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(6)
    ),

    SEPTUPLE_COMPRESSED_BLOCK("septuple_compressed_block", new BlockOverride()
        .setNamingPattern("septuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_7")
        .setNameEN("Septuple Compressed Block of %s").setNameFR("Bloc de %s Septuplement Compressé").setNameES("Bloque de %s Septuplemente Comprimido").setNameIT("Blocco di %s Settuplo Compresso").setNameDE("Siebenfach komprimierter %s-Block").setNamePT("Bloco de %s Septuplamente Compactado").setNameRU("Семикратный сжатый блок %s").setNameZH("七重压缩%s块").setNameJP("七重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(600.0f).setResistance(800.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(SEXTUPLE_COMPRESSED_BLOCK).setCompressionLevel(7)
    ),
    SEPTUPLE_COMPRESSED_RAW_BLOCK("septuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("septuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_7")
        .setNameEN("Septuple Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Septuplement Compressé").setNameES("Bloque de %s en Bruto Septuplemente Comprimido").setNameIT("Blocco di %s Grezzo Settuplo Compresso").setNameDE("Siebenfach komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Septuplamente Compactado").setNameRU("Семикратный сжатый блок сырого %s").setNameZH("七重压缩粗%s块").setNameJP("七重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(600.0f).setResistance(800.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(SEXTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(7)
        .setUseRawColor(true)
    ),
    SEPTUPLE_COMPRESSED_DUST_BLOCK("septuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("septuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_7")
        .setNameEN("Septuple Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Septuplement Compressé").setNameES("Bloque de Polvo de %s Septuplemente Comprimido").setNameIT("Blocco di Polvere di %s Settuplo Compresso").setNameDE("Siebenfach komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Septuplamente Compactado").setNameRU("Семикратный сжатый блок пыли %s").setNameZH("七重压缩%s粉块").setNameJP("七重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(128.0f).setResistance(128.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(SEXTUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(7)
    ),

    OCTUPLE_COMPRESSED_BLOCK("octuple_compressed_block", new BlockOverride()
        .setNamingPattern("octuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_8")
        .setNameEN("Octuple Compressed Block of %s").setNameFR("Bloc de %s Octuplement Compressé").setNameES("Bloque de %s Octuplemente Comprimido").setNameIT("Blocco di %s Ottuplo Compresso").setNameDE("Achtfach komprimierter %s-Block").setNamePT("Bloco de %s Octuplamente Compactado").setNameRU("Восьмикратный сжатый блок %s").setNameZH("八重压缩%s块").setNameJP("八重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(1200.0f).setResistance(1600.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(SEPTUPLE_COMPRESSED_BLOCK).setCompressionLevel(8)
    ),
    OCTUPLE_COMPRESSED_RAW_BLOCK("octuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("octuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_8")
        .setNameEN("Octuple Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Octuplement Compressé").setNameES("Bloque de %s en Bruto Octuplemente Comprimido").setNameIT("Blocco di %s Grezzo Ottuplo Compresso").setNameDE("Achtfach komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Octuplamente Compactado").setNameRU("Восьмикратный сжатый блок сырого %s").setNameZH("八重压缩粗%s块").setNameJP("八重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(1200.0f).setResistance(1600.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(SEPTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(8)
        .setUseRawColor(true)
    ),
    OCTUPLE_COMPRESSED_DUST_BLOCK("octuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("octuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_8")
        .setNameEN("Octuple Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Octuplement Compressé").setNameES("Bloque de Polvo de %s Octuplemente Comprimido").setNameIT("Blocco di Polvere di %s Ottuplo Compresso").setNameDE("Achtfach komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Octuplamente Compactado").setNameRU("Восьмикратный сжатый блок пыли %s").setNameZH("八重压缩%s粉块").setNameJP("八重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(256.0f).setResistance(256.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(SEPTUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(8)
    ),

    NONUPLE_COMPRESSED_BLOCK("nonuple_compressed_block", new BlockOverride()
        .setNamingPattern("nonuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_9")
        .setNameEN("Nonuple Compressed Block of %s").setNameFR("Bloc de %s Nonuplement Compressé").setNameES("Bloque de %s Nonuplemente Comprimido").setNameIT("Blocco di %s Nonuplo Compresso").setNameDE("Neunfach komprimierter %s-Block").setNamePT("Bloco de %s Nonuplamente Compactado").setNameRU("Девятикратный сжатый блок %s").setNameZH("九重压缩%s块").setNameJP("九重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(2400.0f).setResistance(3200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(OCTUPLE_COMPRESSED_BLOCK).setCompressionLevel(9)
    ),
    NONUPLE_COMPRESSED_RAW_BLOCK("nonuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("nonuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_9")
        .setNameEN("Nonuple Compressed Block of Raw %s").setNameFR("Bloc de %s Brut Nonuplement Compressé").setNameES("Bloque de %s en Bruto Nonuplemente Comprimido").setNameIT("Blocco di %s Grezzo Nonuplo Compresso").setNameDE("Neunfach komprimierter roher %s-Block").setNamePT("Bloco de %s Bruto Nonuplamente Compactado").setNameRU("Девятикратный сжатый блок сырого %s").setNameZH("九重压缩粗%s块").setNameJP("九重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(2400.0f).setResistance(3200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(OCTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(9)
        .setUseRawColor(true)
    ),
    NONUPLE_COMPRESSED_DUST_BLOCK("nonuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("nonuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_9")
        .setNameEN("Nonuple Compressed %s Dust Block").setNameFR("Bloc de Poudre de %s Nonuplement Compressé").setNameES("Bloque de Polvo de %s Nonuplemente Comprimido").setNameIT("Blocco di Polvere di %s Nonuplo Compresso").setNameDE("Neunfach komprimierter %s-Staubblock").setNamePT("Bloco de Pó de %s Nonuplamente Compactado").setNameRU("Девятикратный сжатый блок пыли %s").setNameZH("九重压缩%s粉块").setNameJP("九重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(512.0f).setResistance(512.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(OCTUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(9)
    );
    private final String suffix;
    private final BlockOverride defaultOverride;
    BlockType(String suffix, BlockOverride defaultOverride) {
        this.suffix = suffix;
        this.defaultOverride = defaultOverride;
    }
    public String getSuffix() {
        return suffix;
    }
    public BlockOverride getDefaultOverride() {
        return defaultOverride;
    }
    public BlockType getParentType() {
        return defaultOverride.getParentType();
    }
    public int getCompressionLevel() {
        Integer lvl = defaultOverride.getCompressionLevel();
        return lvl != null ? lvl : 0;
    }

    public static BlockType forName(String name) {
        if (name == null || name.isEmpty()) return null;
        for (BlockType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.suffix.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
