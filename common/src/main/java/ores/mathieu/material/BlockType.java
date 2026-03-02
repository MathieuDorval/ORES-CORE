//    ___    ____    _____   ____       ____    ___    ____    _____ 
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|  
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___ 
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Enum defining various block categories.
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public enum BlockType {
    BLOCK("block", new BlockOverride()
        .setNamingPattern("%s_block")
        .setTagCategory("storage_blocks")
        .setTranslations("Block of %s", "Bloc de %s", "Bloque de %s", "Blocco di %s", "%s-Block", "Bloco de %s", "Блок %s", "%s块", "%sのブロック")
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
    ),
    RAW_BLOCK("raw_block", new BlockOverride()
        .setNamingPattern("raw_%s_block")
        .setTagCategory("storage_blocks")
        .setTranslations("Block of Raw %s", "Bloc de %s Brut", "Bloque de %s en Bruto", "Blocco di %s Grezzo", "Roher %s-Block", "Bloco de %s Bruto", "Блок сырого %s", "粗%s块", "生%sのブロック")
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
    ),
    DUST_BLOCK("dust_block", new BlockOverride()
        .setNamingPattern("%s_dust_block")
        .setTagCategory("storage_blocks")
        .setTranslations("%s Dust Block", "Bloc de Poudre de %s", "Bloque de Polvo de %s", "Blocco di Polvere di %s", "%s-Staubblock", "Bloco de Pó de %s", "Блок пыли %s", "%s粉块", "%sの粉ブロック")
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
        .setTranslations("%s Powder Block", "Bloc de Poudre de %s", "Bloque de Polvo de %s", "Blocco di Polvere di %s", "%s-Pulverblock", "Bloco de Pó de %s", "Блок порошка %s", "%s粉末块", "%sの粉末ブロック")
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
        .setTranslations("%s Glass", "Verre de %s", "Cristal de %s", "Vetro di %s", "%s-Glas", "Vidro de %s", "Стекло %s", "%s玻璃", "%sのガラス")
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
        .setTranslations("%s Lamp", "Lampe de %s", "Lámpara de %s", "Lampada di %s", "%s-Lampe", "Lâmpada de %s", "Лампа %s", "%s灯", "%sのランプ")
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
        .setTranslations("%s Ore", "Minerai de %s", "Mineral de %s", "Minerale di %s", "%s-Erz", "Minério de %s", "Руда %s", "%s矿石", "%sの鉱石")
        .setShouldDropWhenFallingHitTorch(false)
    ),

    COMPRESSED_BLOCK("compressed_block", new BlockOverride()
        .setNamingPattern("compressed_%s_block").setTagCategory("storage_blocks/compressed/level_1")
        .setTranslations("Compressed Block of %s", "Bloc de %s Compressé", "Bloque de %s Comprimido", "Blocco di %s Compresso", "Komprimierter %s-Block", "Bloco de %s Compactado", "Сжатый блок %s", "压缩%s块", "圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setSmeltingMultiplier(81.0f).setXpMultiplier(81.0f).setFuelFactor(81.0f)
        .setSound(SoundType.METAL).setHardness(10.0f).setResistance(12.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(BLOCK).setCompressionLevel(1)
    ),
    COMPRESSED_RAW_BLOCK("compressed_raw_block", new BlockOverride()
        .setNamingPattern("compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_1")
        .setTranslations("Compressed Block of Raw %s", "Bloc de %s Brut Compressé", "Bloque de %s en Bruto Comprimido", "Blocco di %s Grezzo Compresso", "Komprimierter roher %s-Block", "Bloco de %s Bruto Compactado", "Сжатый блок сырого %s", "压缩粗%s块", "圧縮された生%sのブロック")
        .setSmeltingMultiplier(81.0f).setXpMultiplier(81.0f)
        .setSound(SoundType.STONE).setHardness(10.0f).setResistance(12.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(RAW_BLOCK).setCompressionLevel(1)
        .setUseRawColor(true)
    ),
    COMPRESSED_DUST_BLOCK("compressed_dust_block", new BlockOverride()
        .setNamingPattern("compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_1")
        .setTranslations("Compressed %s Dust Block", "Bloc de Poudre de %s Compressé", "Bloque de Polvo de %s Comprimido", "Blocco di Polvere di %s Compresso", "Komprimierter %s-Staubblock", "Bloco de Pó de %s Compactado", "Сжатый блок пыли %s", "压缩%s粉块", "圧縮された%sの粉ブロック")
        .setSmeltingMultiplier(81.0f).setXpMultiplier(81.0f)
        .setSound(SoundType.SAND).setHardness(2.0f).setResistance(2.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(DUST_BLOCK).setCompressionLevel(1)
    ),

    DOUBLE_COMPRESSED_BLOCK("double_compressed_block", new BlockOverride()
        .setNamingPattern("double_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_2")
        .setTranslations("Double Compressed Block of %s", "Bloc de %s Doublement Compressé", "Bloque de %s Doblemente Comprimido", "Blocco di %s Doppio Compresso", "Doppelt komprimierter %s-Block", "Bloco de %s Duplamente Compactado", "Двойной сжатый блок %s", "双重压缩%s块", "二重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(729.0f)
        .setSound(SoundType.METAL).setHardness(20.0f).setResistance(25.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(COMPRESSED_BLOCK).setCompressionLevel(2)
    ),
    DOUBLE_COMPRESSED_RAW_BLOCK("double_compressed_raw_block", new BlockOverride()
        .setNamingPattern("double_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_2")
        .setTranslations("Double Compressed Block of Raw %s", "Bloc de %s Brut Doublement Compressé", "Bloque de %s en Bruto Doblemente Comprimido", "Blocco di %s Grezzo Doppio Compresso", "Doppelt komprimierter roher %s-Block", "Bloco de %s Bruto Duplamente Compactado", "Двойной сжатый блок сырого %s", "双重压缩粗%s块", "二重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(20.0f).setResistance(25.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(COMPRESSED_RAW_BLOCK).setCompressionLevel(2)
        .setUseRawColor(true)
    ),
    DOUBLE_COMPRESSED_DUST_BLOCK("double_compressed_dust_block", new BlockOverride()
        .setNamingPattern("double_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_2")
        .setTranslations("Double Compressed %s Dust Block", "Bloc de Poudre de %s Doublement Compressé", "Bloque de Polvo de %s Doblemente Comprimido", "Blocco di Polvere di %s Doppio Compresso", "Doppelt komprimierter %s-Staubblock", "Bloco de Pó de %s Duplamente Compactado", "Двойной сжатый блок пыли %s", "双重压缩%s粉块", "二重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(4.0f).setResistance(4.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(COMPRESSED_DUST_BLOCK).setCompressionLevel(2)
    ),

    TRIPLE_COMPRESSED_BLOCK("triple_compressed_block", new BlockOverride()
        .setNamingPattern("triple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_3")
        .setTranslations("Triple Compressed Block of %s", "Bloc de %s Triplement Compressé", "Bloque de %s Triplemente Comprimido", "Blocco di %s Triplo Compresso", "Dreifach komprimierter %s-Block", "Bloco de %s Triplamente Compactado", "Тройной сжатый блок %s", "三重压缩%s块", "三重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(6561.0f)
        .setSound(SoundType.METAL).setHardness(40.0f).setResistance(50.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(DOUBLE_COMPRESSED_BLOCK).setCompressionLevel(3)
    ),
    TRIPLE_COMPRESSED_RAW_BLOCK("triple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("triple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_3")
        .setTranslations("Triple Compressed Block of Raw %s", "Bloc de %s Brut Triplement Compressé", "Bloque de %s en Bruto Triplemente Comprimido", "Blocco di %s Grezzo Triplo Compresso", "Dreifach komprimierter roher %s-Block", "Bloco de %s Bruto Triplamente Compactado", "Тройной сжатый блок сырого %s", "三重压缩粗%s块", "三重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(40.0f).setResistance(50.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(DOUBLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(3)
        .setUseRawColor(true)
    ),
    TRIPLE_COMPRESSED_DUST_BLOCK("triple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("triple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_3")
        .setTranslations("Triple Compressed %s Dust Block", "Bloc de Poudre de %s Triplement Compressé", "Bloque de Polvo de %s Triplemente Comprimido", "Blocco di Polvere di %s Triplo Compresso", "Dreifach komprimierter %s-Staubblock", "Bloco de Pó de %s Triplamente Compactado", "Тройной сжатый блок пыли %s", "三重压缩%s粉块", "三重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(8.0f).setResistance(8.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(DOUBLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(3)
    ),

    QUADRUPLE_COMPRESSED_BLOCK("quadruple_compressed_block", new BlockOverride()
        .setNamingPattern("quadruple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_4")
        .setTranslations("Quadruple Compressed Block of %s", "Bloc de %s Quadruplement Compressé", "Bloque de %s Cuádruplemente Comprimido", "Blocco di %s Quadruplo Compresso", "Vierfach komprimierter %s-Block", "Bloco de %s Quadruplamente Compactado", "Четверной сжатый блок %s", "四重压缩%s块", "四重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(59049.0f)
        .setSound(SoundType.METAL).setHardness(80.0f).setResistance(100.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(TRIPLE_COMPRESSED_BLOCK).setCompressionLevel(4)
    ),
    QUADRUPLE_COMPRESSED_RAW_BLOCK("quadruple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("quadruple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_4")
        .setTranslations("Quadruple Compressed Block of Raw %s", "Bloc de %s Brut Quadruplement Compressé", "Bloque de %s en Bruto Cuádruplemente Comprimido", "Blocco di %s Grezzo Quadruplo Compresso", "Vierfach komprimierter roher %s-Block", "Bloco de %s Bruto Quadruplamente Compactado", "Четверной сжатый блок сырого %s", "四重压缩粗%s块", "四重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(80.0f).setResistance(100.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(TRIPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(4)
        .setUseRawColor(true)
    ),
    QUADRUPLE_COMPRESSED_DUST_BLOCK("quadruple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("quadruple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_4")
        .setTranslations("Quadruple Compressed %s Dust Block", "Bloc de Poudre de %s Quadruplement Compressé", "Bloque de Polvo de %s Cuádruplemente Comprimido", "Blocco di Polvere di %s Quadruplo Compresso", "Vierfach komprimierter %s-Staubblock", "Bloco de Pó de %s Quadruplamente Compactado", "Четверной сжатый блок пыли %s", "四重压缩%s粉块", "四重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(16.0f).setResistance(16.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(TRIPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(4)
    ),

    QUINTUPLE_COMPRESSED_BLOCK("quintuple_compressed_block", new BlockOverride()
        .setNamingPattern("quintuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_5")
        .setTranslations("Quintuple Compressed Block of %s", "Bloc de %s Quintuplement Compressé", "Bloque de %s Quíntuplemente Comprimido", "Blocco di %s Quintuplo Compresso", "Fünffach komprimierter %s-Block", "Bloco de %s Quintuplamente Compactado", "Пятерной сжатый блок %s", "五重压缩%s块", "五重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(150.0f).setResistance(200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(QUADRUPLE_COMPRESSED_BLOCK).setCompressionLevel(5)
    ),
    QUINTUPLE_COMPRESSED_RAW_BLOCK("quintuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("quintuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_5")
        .setTranslations("Quintuple Compressed Block of Raw %s", "Bloc de %s Brut Quintuplement Compressé", "Bloque de %s en Bruto Quíntuplemente Comprimido", "Blocco di %s Grezzo Quintuplo Compresso", "Fünffach komprimierter roher %s-Block", "Bloco de %s Bruto Quintuplamente Compactado", "Пятерной сжатый блок сырого %s", "五重压缩粗%s块", "五重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(150.0f).setResistance(200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(QUADRUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(5)
        .setUseRawColor(true)
    ),
    QUINTUPLE_COMPRESSED_DUST_BLOCK("quintuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("quintuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_5")
        .setTranslations("Quintuple Compressed %s Dust Block", "Bloc de Poudre de %s Quintuplement Compressé", "Bloque de Polvo de %s Quíntuplemente Comprimido", "Blocco di Polvere di %s Quintuplo Compresso", "Fünffach komprimierter %s-Staubblock", "Bloco de Pó de %s Quintuplamente Compactado", "Пятерной сжатый блок пыли %s", "五重压缩%s粉块", "五重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(32.0f).setResistance(32.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(QUADRUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(5)
    ),

    SEXTUPLE_COMPRESSED_BLOCK("sextuple_compressed_block", new BlockOverride()
        .setNamingPattern("sextuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_6")
        .setTranslations("Sextuple Compressed Block of %s", "Bloc de %s Sextuplement Compressé", "Bloque de %s Sextuplemente Comprimido", "Blocco di %s Sestuplo Compresso", "Sechsfach komprimierter %s-Block", "Bloco de %s Sextuplamente Compactado", "Шестерной сжатый блок %s", "六重压缩%s块", "六重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(300.0f).setResistance(400.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(QUINTUPLE_COMPRESSED_BLOCK).setCompressionLevel(6)
    ),
    SEXTUPLE_COMPRESSED_RAW_BLOCK("sextuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("sextuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_6")
        .setTranslations("Sextuple Compressed Block of Raw %s", "Bloc de %s Brut Sextuplement Compressé", "Bloque de %s en Bruto Sextuplemente Comprimido", "Blocco di %s Grezzo Sestuplo Compresso", "Sechsfach komprimierter roher %s-Block", "Bloco de %s Bruto Sextuplamente Compactado", "Шестерной сжатый блок сырого %s", "六重压缩粗%s块", "六重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(300.0f).setResistance(400.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(QUINTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(6)
        .setUseRawColor(true)
    ),
    SEXTUPLE_COMPRESSED_DUST_BLOCK("sextuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("sextuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_6")
        .setTranslations("Sextuple Compressed %s Dust Block", "Bloc de Poudre de %s Sextuplement Compressé", "Bloque de Polvo de %s Sextuplemente Comprimido", "Blocco di Polvere di %s Sestuplo Compresso", "Sechsfach komprimierter %s-Staubblock", "Bloco de Pó de %s Sextuplamente Compactado", "Шестерной сжатый блок пыли %s", "六重压缩%s粉块", "六重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(64.0f).setResistance(64.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(QUINTUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(6)
    ),

    SEPTUPLE_COMPRESSED_BLOCK("septuple_compressed_block", new BlockOverride()
        .setNamingPattern("septuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_7")
        .setTranslations("Septuple Compressed Block of %s", "Bloc de %s Septuplement Compressé", "Bloque de %s Septuplemente Comprimido", "Blocco di %s Settuplo Compresso", "Siebenfach komprimierter %s-Block", "Bloco de %s Septuplamente Compactado", "Семикратный сжатый блок %s", "七重压缩%s块", "七重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(600.0f).setResistance(800.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(SEXTUPLE_COMPRESSED_BLOCK).setCompressionLevel(7)
    ),
    SEPTUPLE_COMPRESSED_RAW_BLOCK("septuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("septuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_7")
        .setTranslations("Septuple Compressed Block of Raw %s", "Bloc de %s Brut Septuplement Compressé", "Bloque de %s en Bruto Septuplemente Comprimido", "Blocco di %s Grezzo Settuplo Compresso", "Siebenfach komprimierter roher %s-Block", "Bloco de %s Bruto Septuplamente Compactado", "Семикратный сжатый блок сырого %s", "七重压缩粗%s块", "七重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(600.0f).setResistance(800.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(SEXTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(7)
        .setUseRawColor(true)
    ),
    SEPTUPLE_COMPRESSED_DUST_BLOCK("septuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("septuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_7")
        .setTranslations("Septuple Compressed %s Dust Block", "Bloc de Poudre de %s Septuplement Compressé", "Bloque de Polvo de %s Septuplemente Comprimido", "Blocco di Polvere di %s Settuplo Compresso", "Siebenfach komprimierter %s-Staubblock", "Bloco de Pó de %s Septuplamente Compactado", "Семикратный сжатый блок пыли %s", "七重压缩%s粉块", "七重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(128.0f).setResistance(128.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(SEXTUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(7)
    ),

    OCTUPLE_COMPRESSED_BLOCK("octuple_compressed_block", new BlockOverride()
        .setNamingPattern("octuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_8")
        .setTranslations("Octuple Compressed Block of %s", "Bloc de %s Octuplement Compressé", "Bloque de %s Octuplemente Comprimido", "Blocco di %s Ottuplo Compresso", "Achtfach komprimierter %s-Block", "Bloco de %s Octuplamente Compactado", "Восьмикратный сжатый блок %s", "八重压缩%s块", "八重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(1200.0f).setResistance(1600.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(SEPTUPLE_COMPRESSED_BLOCK).setCompressionLevel(8)
    ),
    OCTUPLE_COMPRESSED_RAW_BLOCK("octuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("octuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_8")
        .setTranslations("Octuple Compressed Block of Raw %s", "Bloc de %s Brut Octuplement Compressé", "Bloque de %s en Bruto Octuplemente Comprimido", "Blocco di %s Grezzo Ottuplo Compresso", "Achtfach komprimierter roher %s-Block", "Bloco de %s Bruto Octuplamente Compactado", "Восьмикратный сжатый блок сырого %s", "八重压缩粗%s块", "八重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(1200.0f).setResistance(1600.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(SEPTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(8)
        .setUseRawColor(true)
    ),
    OCTUPLE_COMPRESSED_DUST_BLOCK("octuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("octuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_8")
        .setTranslations("Octuple Compressed %s Dust Block", "Bloc de Poudre de %s Octuplement Compressé", "Bloque de Polvo de %s Octuplemente Comprimido", "Blocco di Polvere di %s Ottuplo Compresso", "Achtfach komprimierter %s-Staubblock", "Bloco de Pó de %s Octuplamente Compactado", "Восьмикратный сжатый блок пыли %s", "八重压缩%s粉块", "八重に圧縮された%sの粉ブロック")
        .setSound(SoundType.SAND).setHardness(256.0f).setResistance(256.0f).setRequiredTool("shovel")
        .setGravityMode("FALLING").setMapColor(MapColor.SAND)
        .setCanBeBeaconBase(false)
        .setParentType(SEPTUPLE_COMPRESSED_DUST_BLOCK).setCompressionLevel(8)
    ),

    NONUPLE_COMPRESSED_BLOCK("nonuple_compressed_block", new BlockOverride()
        .setNamingPattern("nonuple_compressed_%s_block").setTagCategory("storage_blocks/compressed/level_9")
        .setTranslations("Nonuple Compressed Block of %s", "Bloc de %s Nonuplement Compressé", "Bloque de %s Nonuplemente Comprimido", "Blocco di %s Nonuplo Compresso", "Neunfach komprimierter %s-Block", "Bloco de %s Nonuplamente Compactado", "Девятикратный сжатый блок %s", "九重压缩%s块", "九重に圧縮された%sのブロック")
        .setRarity(Rarity.COMMON)
        .setFuelFactor(531441.0f)
        .setSound(SoundType.METAL).setHardness(2400.0f).setResistance(3200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.METAL)
        .setCanBeBeaconBase(true)
        .setParentType(OCTUPLE_COMPRESSED_BLOCK).setCompressionLevel(9)
    ),
    NONUPLE_COMPRESSED_RAW_BLOCK("nonuple_compressed_raw_block", new BlockOverride()
        .setNamingPattern("nonuple_compressed_raw_%s_block").setTagCategory("storage_blocks/compressed/level_9")
        .setTranslations("Nonuple Compressed Block of Raw %s", "Bloc de %s Brut Nonuplement Compressé", "Bloque de %s en Bruto Nonuplemente Comprimido", "Blocco di %s Grezzo Nonuplo Compresso", "Neunfach komprimierter roher %s-Block", "Bloco de %s Bruto Nonuplamente Compactado", "Девятикратный сжатый блок сырого %s", "九重压缩粗%s块", "九重に圧縮された生%sのブロック")
        .setSound(SoundType.STONE).setHardness(2400.0f).setResistance(3200.0f).setRequiredTool("pickaxe")
        .setMapColor(MapColor.RAW_IRON)
        .setCanBeBeaconBase(false)
        .setParentType(OCTUPLE_COMPRESSED_RAW_BLOCK).setCompressionLevel(9)
        .setUseRawColor(true)
    ),
    NONUPLE_COMPRESSED_DUST_BLOCK("nonuple_compressed_dust_block", new BlockOverride()
        .setNamingPattern("nonuple_compressed_%s_dust_block").setTagCategory("storage_blocks/compressed/level_9")
        .setTranslations("Nonuple Compressed %s Dust Block", "Bloc de Poudre de %s Nonuplement Compressé", "Bloque de Polvo de %s Nonuplemente Comprimido", "Blocco di Polvere di %s Nonuplo Compresso", "Neunfach komprimierter %s-Staubblock", "Bloco de Pó de %s Nonuplamente Compactado", "Девятикратный сжатый блок пыли %s", "九重压缩%s粉块", "九重に圧縮された%sの粉ブロック")
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
}
