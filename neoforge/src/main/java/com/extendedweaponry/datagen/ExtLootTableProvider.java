package com.extendedweaponry.datagen;

import com.extendedweaponry.loot.LootInjections;
import com.extendedweaponry.loot.PlayerDamageShareCondition;
import com.extendedweaponry.loot.WardenGearEnabledCondition;
import com.extendedweaponry.registry.ExtItems;
import com.extendedweaponry.registry.ExtTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Tables tirées en plus des tables vanilla (voir {@link LootInjections}) :
 * <ul>
 *   <li>cœur du Warden : toujours, si au moins 60 % des dégâts reçus par le Warden venaient de joueurs ;</li>
 *   <li>modèle d'amélioration du Warden : 15 % des coffres des cités antiques ;</li>
 *   <li>un objet usé en cuivre, émeraude ou améthyste dans quelques coffres.</li>
 * </ul>
 */
public class ExtLootTableProvider extends LootTableProvider {

    public ExtLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(provider -> ExtLootTableProvider::entities, LootContextParamSets.ENTITY),
                new SubProviderEntry(provider -> ExtLootTableProvider::chests, LootContextParamSets.CHEST)), registries);
    }

    private static void entities(java.util.function.BiConsumer<net.minecraft.resources.ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(LootInjections.WARDEN_HEART, LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ExtItems.WARDEN_HEART.get()))
                .when(() -> WardenGearEnabledCondition.INSTANCE)
                .when(() -> new PlayerDamageShareCondition(PlayerDamageShareCondition.DEFAULT_SHARE))));
    }

    private static void chests(java.util.function.BiConsumer<net.minecraft.resources.ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(LootInjections.ANCIENT_CITY, LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ExtItems.WARDEN_UPGRADE_SMITHING_TEMPLATE.get()))
                .when(() -> WardenGearEnabledCondition.INSTANCE)
                .when(LootItemRandomChanceCondition.randomChance(0.15F))));
        output.accept(LootInjections.COPPER_GEAR, wornGear(ExtTags.item("loot/copper_gear"), 0.12F));
        output.accept(LootInjections.EMERALD_GEAR, wornGear(ExtTags.item("loot/emerald_gear"), 0.08F));
        output.accept(LootInjections.AMETHYST_GEAR, wornGear(ExtTags.item("loot/amethyst_gear"), 0.10F));
    }

    /** Un objet tiré au hasard dans le tag, usé entre 20 et 80 %. */
    private static LootTable.Builder wornGear(TagKey<Item> items, float chance) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(TagEntry.expandTag(items).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.2F, 0.8F))))
                .when(LootItemRandomChanceCondition.randomChance(chance)));
    }
}
