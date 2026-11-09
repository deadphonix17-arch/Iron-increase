package com.example.zombieiron;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ZombieIron implements ModInitializer {
    // ---- Tweak these, then rebuild ----
    // Chance (0.0 - 1.0) that a zombie drops extra iron. Vanilla is about 0.025 (2.5%).
    private static final float EXTRA_IRON_CHANCE = 0.30f;
    // Amount dropped when the chance succeeds (min / max, inclusive).
    private static final float MIN_COUNT = 1f;
    private static final float MAX_COUNT = 2f;
    // If true, only drops when a player (or player's tamed wolf etc.) kills it, like vanilla.
    private static final boolean REQUIRE_PLAYER_KILL = true;
    // -----------------------------------

    private static final Identifier ZOMBIE_LOOT = Identifier.of("minecraft", "entities/zombie");

    @Override
    public void onInitialize() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin() || !ZOMBIE_LOOT.equals(key.getValue())) {
                return;
            }

            LootPool.Builder pool = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(RandomChanceLootCondition.builder(EXTRA_IRON_CHANCE))
                    .with(ItemEntry.builder(Items.IRON_INGOT)
                            .apply(SetCountLootFunction.builder(
                                    UniformLootNumberProvider.create(MIN_COUNT, MAX_COUNT))));

            if (REQUIRE_PLAYER_KILL) {
                pool.conditionally(KilledByPlayerLootCondition.builder());
            }

            tableBuilder.pool(pool.build());
        });
    }
}
