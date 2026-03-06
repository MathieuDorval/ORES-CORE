//    ___    ____    _____   ____       ____    ___    ____    _____
//   / _ \  |  _ \  | ____| / ___|     / ___|  / _ \  |  _ \  | ____|
//  | | | | | |_) | |  _|   \___ \    | |     | | | | | |_) | |  _|
//  | |_| | |  _ <  | |___   ___) |   | |___  | |_| | |  _ <  | |___
//   \___/  |_| \_\ |_____| |____/     \____|  \___/  |_| \_\ |_____|
//
// [ ORES CORE ] - Common Module
//
// Description: Manages deferred property patching for combinatorial ores
// whose base block was not yet registered at creation time.
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

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ores.mathieu.OresCoreCommon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("null")
public class PendingPatchManager {

    private record DeferredOrePatch(
        Block oreBlock,
        String baseBlockId,
        float hardnessFactor,
        float resistanceFactor
    ) {}

    private static final List<DeferredOrePatch> PENDING = new ArrayList<>();

    private static Field FIELD_PROPERTIES;

    private static Field[] ALL_PROP_FIELDS;

    private static Field FIELD_DESTROY_TIME;

    private static Field FIELD_EXPLOSION_RESISTANCE;

    private static boolean fieldsInit = false;

    private static void initFields() {
        if (fieldsInit) return;
        try {
            for (Field f : BlockBehaviour.class.getDeclaredFields()) {
                if (f.getType() == BlockBehaviour.Properties.class && FIELD_PROPERTIES == null) {
                    f.setAccessible(true);
                    FIELD_PROPERTIES = f;
                }
            }
            if (FIELD_PROPERTIES == null) {
                throw new NoSuchFieldException("BlockBehaviour.Properties field not found in BlockBehaviour");
            }

            List<Field> instanceFields = new ArrayList<>();
            int floatCount = 0;
            for (Field f : BlockBehaviour.Properties.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                instanceFields.add(f);
                if (f.getType() == float.class) {
                    if (floatCount == 0) FIELD_DESTROY_TIME = f;
                    else if (floatCount == 1) FIELD_EXPLOSION_RESISTANCE = f;
                    floatCount++;
                }
            }
            ALL_PROP_FIELDS = instanceFields.toArray(new Field[0]);

            if (FIELD_DESTROY_TIME == null || FIELD_EXPLOSION_RESISTANCE == null) {
                throw new NoSuchFieldException(
                    "Could not find two float fields in BlockBehaviour.Properties (destroyTime / explosionResistance). Found: " + floatCount);
            }

            fieldsInit = true;
            OresCoreCommon.LOGGER.debug("[ORES CORE] PendingPatchManager: reflection ready ({} Properties fields, destroy={}, resistance={}).",
                ALL_PROP_FIELDS.length, FIELD_DESTROY_TIME.getName(), FIELD_EXPLOSION_RESISTANCE.getName());

        } catch (Exception e) {
            OresCoreCommon.LOGGER.error("[ORES CORE] PendingPatchManager: reflection init failed!", e);
        }
    }

    public static void enqueue(Block oreBlock, String baseBlockId, float hardnessFactor, float resistanceFactor) {
        PENDING.add(new DeferredOrePatch(oreBlock, baseBlockId, hardnessFactor, resistanceFactor));
        OresCoreCommon.LOGGER.debug("[ORES CORE] Queued deferred patch for {} (base: {})", oreBlock, baseBlockId);
    }

    public static void applyAll() {
        if (PENDING.isEmpty()) return;

        initFields();
        if (!fieldsInit) {
            OresCoreCommon.LOGGER.error("[ORES CORE] PendingPatchManager: reflection init failed — deferred patches cannot be applied.");
            PENDING.clear();
            return;
        }

        int applied = 0, skipped = 0;

        for (DeferredOrePatch patch : PENDING) {
            Identifier baseId = Identifier.tryParse(patch.baseBlockId());
            if (baseId == null) {
                OresCoreCommon.LOGGER.warn("[ORES CORE] Invalid base block ID '{}' — skipping.", patch.baseBlockId());
                skipped++;
                continue;
            }

            Block baseBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(baseId);
            if (baseBlock == null || baseBlock == net.minecraft.world.level.block.Blocks.AIR) {
                OresCoreCommon.LOGGER.warn("[ORES CORE] Base block '{}' not found after registry freeze — ore {} left with stone fallback.",
                    patch.baseBlockId(), patch.oreBlock());
                skipped++;
                continue;
            }

            try {
                BlockBehaviour.Properties baseProps = (BlockBehaviour.Properties) FIELD_PROPERTIES.get(baseBlock);
                BlockBehaviour.Properties oreProps  = (BlockBehaviour.Properties) FIELD_PROPERTIES.get(patch.oreBlock());

                for (Field f : ALL_PROP_FIELDS) {
                    f.set(oreProps, f.get(baseProps));
                }

                float baseDestroyTime = FIELD_DESTROY_TIME.getFloat(baseProps);
                float baseExplosionRes = FIELD_EXPLOSION_RESISTANCE.getFloat(baseProps);

                float newHardness   = baseDestroyTime * patch.hardnessFactor();
                float newResistance = baseExplosionRes * patch.resistanceFactor();
                
                FIELD_DESTROY_TIME.set(oreProps, newHardness);
                FIELD_EXPLOSION_RESISTANCE.set(oreProps, newResistance);

                for (Field f : BlockBehaviour.class.getDeclaredFields()) {
                    if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                    if (f.getType() == BlockBehaviour.Properties.class) continue;
                    
                    f.setAccessible(true);
                    Object baseVal = f.get(baseBlock);
                    
                    if (f.getType() == float.class && baseVal != null) {
                        float fVal = (Float) baseVal;
                        if (fVal == baseDestroyTime) baseVal = newHardness;
                        else if (fVal == baseExplosionRes) baseVal = newResistance;
                    }
                    f.set(patch.oreBlock(), baseVal);
                }

                net.minecraft.world.level.block.state.BlockState baseState = baseBlock.defaultBlockState();
                for (net.minecraft.world.level.block.state.BlockState oreState : patch.oreBlock().getStateDefinition().getPossibleStates()) {
                    Class<?> stateClass = oreState.getClass();
                    while (stateClass != Object.class && stateClass != null) {
                        for (Field f : stateClass.getDeclaredFields()) {
                            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                            if (f.getType() == Block.class) continue;
                            
                            f.setAccessible(true);
                            try {
                                Object baseVal = f.get(baseState);
                                if (f.getType() == float.class && baseVal != null) {
                                    float fVal = (Float) baseVal;
                                    if (fVal == baseDestroyTime) baseVal = newHardness;
                                    else if (fVal == baseExplosionRes) baseVal = newResistance;
                                }
                                f.set(oreState, baseVal);
                            } catch (Exception ignored) {
                            }
                        }
                        stateClass = stateClass.getSuperclass();
                    }
                }

                OresCoreCommon.LOGGER.debug("[ORES CORE] Deeply applied deferred patch {} → {} (hardness={}, resistance={})",
                    patch.oreBlock(), patch.baseBlockId(), newHardness, newResistance);
                applied++;

            } catch (IllegalAccessException e) {
                OresCoreCommon.LOGGER.error("[ORES CORE] Failed to apply deferred patch {} → {}",
                    patch.oreBlock(), patch.baseBlockId(), e);
                skipped++;
            }
        }

        OresCoreCommon.LOGGER.debug("[ORES CORE] Deferred patches complete: {} applied, {} skipped.", applied, skipped);
        PENDING.clear();
    }
}
