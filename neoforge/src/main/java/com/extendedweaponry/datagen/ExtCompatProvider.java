package com.extendedweaponry.datagen;

import com.betterweaponry.item.GearType;
import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.item.WeaponType;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.item.ExtendedMaterials;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.ModList;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Fichiers qui font reconnaître les armes par Better Combat ({@code data/extended_weaponry/weapon_attributes/}) : ce sont
 * ceux de Better Weaponry pour l'arme en fer du même type, recopiés (ils ne dépendent que du type d'arme).
 * Les fichiers d'Epic Fight sont écrits au lancement par Better Weaponry pour tous les matériaux d'addons.
 */
public final class ExtCompatProvider implements DataProvider {
    private final PackOutput output;

    public ExtCompatProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path folder = output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(ExtendedWeaponry.MODID).resolve("weapon_attributes");
        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (WeaponMaterial material : ExtendedMaterials.ALL) {
            for (WeaponType type : WeaponType.values()) {
                writes.add(DataProvider.saveStable(cache, betterWeaponryFile("iron_" + type.id()), folder.resolve(material.itemName(type) + ".json")));
            }
            for (GearType gear : List.of(GearType.BOW, GearType.CROSSBOW)) {
                writes.add(DataProvider.saveStable(cache, betterWeaponryFile("iron_" + gear.id()), folder.resolve(material.itemName(gear) + ".json")));
            }
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    /** Fichier Better Combat de Better Weaponry, lu dans son jar (chargé pendant la génération). */
    private static JsonElement betterWeaponryFile(String name) {
        Path file = ModList.get().getModFileById("better_weaponry").getFile().findResource("data", "better_weaponry", "weapon_attributes", name + ".json");
        try {
            return JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Better Weaponry file missing: " + file, e);
        }
    }

    @Override
    public String getName() {
        return "Extended Weaponry - Better Combat files";
    }
}
