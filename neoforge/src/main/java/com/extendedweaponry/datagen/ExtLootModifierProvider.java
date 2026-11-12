package com.extendedweaponry.datagen;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.loot.LootInjections;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

/** NeoForge : chaque table vanilla de {@link LootInjections} tire aussi la table du mod qui lui correspond. */
public class ExtLootModifierProvider extends GlobalLootModifierProvider {

    public ExtLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ExtendedWeaponry.MODID);
    }

    @Override
    protected void start() {
        LootInjections.INJECTIONS.forEach((target, injected) -> add(
                target.location().getPath().replace('/', '_'),
                new AddTableLootModifier(new LootItemCondition[]{LootTableIdCondition.builder(target.location()).build()}, injected)));
    }
}
