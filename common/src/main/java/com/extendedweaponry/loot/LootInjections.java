package com.extendedweaponry.loot;

import com.extendedweaponry.ExtendedWeaponry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Butin ajouté aux tables vanilla : chaque table du jeu reçoit, en plus de son butin, un tirage d'une table du mod
 * ({@code data/extended_weaponry/loot_table/...}). Sur NeoForge, par des modificateurs de butin générés ; sur Fabric,
 * au chargement des tables.
 */
public final class LootInjections {
    public static final ResourceKey<LootTable> WARDEN_HEART = own("entities/warden_heart");
    public static final ResourceKey<LootTable> ANCIENT_CITY = own("chests/ancient_city");
    public static final ResourceKey<LootTable> COPPER_GEAR = own("chests/copper_gear");
    public static final ResourceKey<LootTable> EMERALD_GEAR = own("chests/emerald_gear");
    public static final ResourceKey<LootTable> AMETHYST_GEAR = own("chests/amethyst_gear");

    /** Table du jeu → table du mod tirée en plus. */
    public static final Map<ResourceKey<LootTable>, ResourceKey<LootTable>> INJECTIONS = build();

    private LootInjections() {
    }

    private static Map<ResourceKey<LootTable>, ResourceKey<LootTable>> build() {
        Map<ResourceKey<LootTable>, ResourceKey<LootTable>> map = new LinkedHashMap<>();
        map.put(vanilla("entities/warden"), WARDEN_HEART);
        map.put(vanilla("chests/ancient_city"), ANCIENT_CITY);
        map.put(vanilla("chests/trial_chambers/corridor"), COPPER_GEAR);
        map.put(vanilla("chests/underwater_ruin_big"), COPPER_GEAR);
        map.put(vanilla("chests/village/village_weaponsmith"), EMERALD_GEAR);
        map.put(vanilla("chests/village/village_armorer"), EMERALD_GEAR);
        map.put(vanilla("chests/village/village_toolsmith"), EMERALD_GEAR);
        map.put(vanilla("chests/pillager_outpost"), EMERALD_GEAR);
        map.put(vanilla("chests/shipwreck_treasure"), AMETHYST_GEAR);
        return Collections.unmodifiableMap(map);
    }

    private static ResourceKey<LootTable> own(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ExtendedWeaponry.id(path));
    }

    private static ResourceKey<LootTable> vanilla(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace(path));
    }
}
