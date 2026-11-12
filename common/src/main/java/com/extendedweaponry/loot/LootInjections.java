package com.extendedweaponry.loot;

import com.extendedweaponry.ExtendedWeaponry;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Butin ajouté aux tables vanilla : chaque table du jeu reçoit, en plus de son butin, un tirage d'une table du mod
 * ({@code data/extended_weaponry/loot_tables/...}). Ajouté au chargement des tables, par chaque chargeur.
 */
public final class LootInjections {
    public static final ResourceLocation WARDEN_HEART = ExtendedWeaponry.id("entities/warden_heart");
    public static final ResourceLocation ANCIENT_CITY = ExtendedWeaponry.id("chests/ancient_city");
    public static final ResourceLocation COPPER_GEAR = ExtendedWeaponry.id("chests/copper_gear");
    public static final ResourceLocation EMERALD_GEAR = ExtendedWeaponry.id("chests/emerald_gear");
    public static final ResourceLocation AMETHYST_GEAR = ExtendedWeaponry.id("chests/amethyst_gear");

    /** Table du jeu → table du mod tirée en plus. */
    public static final Map<ResourceLocation, ResourceLocation> INJECTIONS = build();

    private LootInjections() {
    }

    private static Map<ResourceLocation, ResourceLocation> build() {
        Map<ResourceLocation, ResourceLocation> map = new LinkedHashMap<>();
        map.put(vanilla("entities/warden"), WARDEN_HEART);
        map.put(vanilla("chests/ancient_city"), ANCIENT_CITY);
        map.put(vanilla("chests/underwater_ruin_big"), COPPER_GEAR);
        map.put(vanilla("chests/simple_dungeon"), COPPER_GEAR);
        map.put(vanilla("chests/village/village_weaponsmith"), EMERALD_GEAR);
        map.put(vanilla("chests/village/village_armorer"), EMERALD_GEAR);
        map.put(vanilla("chests/village/village_toolsmith"), EMERALD_GEAR);
        map.put(vanilla("chests/pillager_outpost"), EMERALD_GEAR);
        map.put(vanilla("chests/shipwreck_treasure"), AMETHYST_GEAR);
        return Collections.unmodifiableMap(map);
    }

    private static ResourceLocation vanilla(String path) {
        return new ResourceLocation(path);
    }
}
