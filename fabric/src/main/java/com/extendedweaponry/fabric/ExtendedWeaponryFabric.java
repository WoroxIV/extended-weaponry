package com.extendedweaponry.fabric;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.loot.LootInjections;
import com.extendedweaponry.platform.ExtPlatform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/** Point d'entrée Fabric (client et serveur). */
public final class ExtendedWeaponryFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ExtPlatform.set(new FabricExtPlatform());
        ExtendedWeaponry.init();
        // Comme les modificateurs de butin de NeoForge : la table vanilla tire aussi la table du mod.
        LootTableEvents.MODIFY.register((key, builder, source, registries) -> {
            ResourceKey<LootTable> injected = LootInjections.INJECTIONS.get(key);
            if (injected != null && source.isBuiltin()) {
                builder.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(NestedLootTable.lootTableReference(injected)));
            }
        });
    }
}
