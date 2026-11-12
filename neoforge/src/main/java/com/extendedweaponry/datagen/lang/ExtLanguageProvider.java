package com.extendedweaponry.datagen.lang;

import com.betterweaponry.item.GearType;
import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.item.WeaponType;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.item.ExtendedMaterials;
import com.extendedweaponry.registry.ExtItems;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Fichiers de langue ({@code assets/extended_weaponry/lang/<langue>.json}), construits depuis {@code common/src/main/lang/}.
 * Chaque langue doit avoir exactement les mêmes textes que l'anglais, et un nom pour chaque type et chaque matériau :
 * sinon la génération échoue avec la liste des manques.
 */
public final class ExtLanguageProvider implements DataProvider {
    private static final String REFERENCE = "en_us";
    private final PackOutput output;

    public ExtLanguageProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<LanguageSource> sources = readSources();
        LanguageSource reference = sources.stream().filter(source -> source.locale().equals(REFERENCE)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing the " + REFERENCE + " language source"));
        List<String> problems = new ArrayList<>();
        List<CompletableFuture<?>> writes = new ArrayList<>();
        Path folder = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(ExtendedWeaponry.MODID).resolve("lang");
        for (LanguageSource source : sources) {
            checkStrings(reference, source, problems);
            JsonObject json = new JsonObject();
            try {
                addItemNames(source, json);
            } catch (IllegalArgumentException e) {
                problems.add(e.getMessage());
                continue;
            }
            source.strings().forEach(json::addProperty);
            writes.add(DataProvider.saveStable(cache, json, folder.resolve(source.locale() + ".json")));
        }
        if (!problems.isEmpty()) throw new IllegalStateException("Language sources:\n - " + String.join("\n - ", problems));
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    private static void addItemNames(LanguageSource source, JsonObject json) {
        for (WeaponMaterial material : ExtendedMaterials.ALL) {
            for (ExtItems.Piece piece : ExtItems.Piece.values()) {
                json.addProperty(itemKey(material.id() + "_" + piece.id()), source.itemName(piece.id(), material.id()));
            }
            for (WeaponType type : WeaponType.values()) {
                json.addProperty(itemKey(material.itemName(type)), source.itemName(type.id(), material.id()));
            }
            for (GearType gear : GearType.values()) {
                json.addProperty(itemKey(material.itemName(gear)), source.itemName(gear.id(), material.id()));
            }
        }
    }

    private static String itemKey(String name) {
        return "item." + ExtendedWeaponry.MODID + "." + name;
    }

    private static void checkStrings(LanguageSource reference, LanguageSource source, List<String> problems) {
        for (String key : reference.strings().keySet()) {
            if (!source.strings().containsKey(key)) problems.add(source.locale() + ": missing text \"" + key + "\"");
        }
        for (String key : source.strings().keySet()) {
            if (!reference.strings().containsKey(key)) problems.add(source.locale() + ": unknown text \"" + key + "\"");
        }
    }

    private static List<LanguageSource> readSources() {
        Path folder = Path.of(System.getProperty("extendedweaponry.langSources"));
        try (Stream<Path> files = Files.list(folder)) {
            List<LanguageSource> sources = new ArrayList<>();
            for (Path file : files.filter(path -> path.toString().endsWith(".json")).sorted().toList()) sources.add(LanguageSource.read(file));
            return sources;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String getName() {
        return "Extended Weaponry - languages";
    }
}
