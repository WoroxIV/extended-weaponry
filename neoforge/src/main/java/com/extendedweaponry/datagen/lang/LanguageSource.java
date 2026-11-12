package com.extendedweaponry.datagen.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Une source de traduction : {@code common/src/main/lang/<langue>.json}, au même format que celles de Better Weaponry.
 * Les noms d'objets sont construits avec {@code names.pattern} ({@code {type}} et {@code {material}}, avec
 * {@code |lower} / {@code |upper}) ; un type peut avoir un genre, un matériau une forme par genre et son propre motif ;
 * {@code names.overrides} impose le nom complet d'un objet. Les autres textes sont dans {@code strings}.
 */
public record LanguageSource(String locale, String pattern, Map<String, TypeName> types, Map<String, JsonElement> materials,
                             Map<String, String> overrides, Map<String, String> strings) {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(type|material)(?:\\|(lower|upper))?}");

    public record TypeName(String name, @Nullable String gender) {
    }

    public static LanguageSource read(Path file) throws IOException {
        String fileName = file.getFileName().toString();
        String locale = fileName.substring(0, fileName.length() - ".json".length());
        JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        JsonObject names = root.getAsJsonObject("names");

        Map<String, TypeName> types = new LinkedHashMap<>();
        for (var entry : names.getAsJsonObject("types").entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive()) {
                types.put(entry.getKey(), new TypeName(value.getAsString(), null));
            } else {
                JsonObject object = value.getAsJsonObject();
                JsonElement gender = object.get("gender");
                types.put(entry.getKey(), new TypeName(object.get("name").getAsString(), gender == null ? null : gender.getAsString()));
            }
        }
        Map<String, JsonElement> materials = new LinkedHashMap<>();
        names.getAsJsonObject("materials").entrySet().forEach(entry -> materials.put(entry.getKey(), entry.getValue()));
        Map<String, String> overrides = new LinkedHashMap<>();
        if (names.has("overrides")) names.getAsJsonObject("overrides").entrySet().forEach(entry -> overrides.put(entry.getKey(), entry.getValue().getAsString()));
        Map<String, String> strings = new LinkedHashMap<>();
        root.getAsJsonObject("strings").entrySet().forEach(entry -> strings.put(entry.getKey(), entry.getValue().getAsString()));
        return new LanguageSource(locale, names.get("pattern").getAsString(), types, materials, overrides, strings);
    }

    /** Nom traduit de l'objet {@code typeId} dans le matériau {@code materialId}. */
    public String itemName(String typeId, String materialId) {
        String override = overrides.get(materialId + "_" + typeId);
        if (override != null) return override;
        TypeName type = types.get(typeId);
        JsonElement material = materials.get(materialId);
        if (type == null) throw new IllegalArgumentException(locale + ": no name for the type \"" + typeId + "\"");
        if (material == null) throw new IllegalArgumentException(locale + ": no name for the material \"" + materialId + "\"");

        String usedPattern = pattern;
        String form;
        if (material.isJsonPrimitive()) {
            form = material.getAsString();
        } else {
            JsonObject forms = material.getAsJsonObject();
            JsonElement gendered = type.gender() != null ? forms.get(type.gender()) : null;
            if (gendered == null) gendered = forms.get("name");
            if (gendered == null) {
                throw new IllegalArgumentException(locale + ": the material \"" + materialId + "\" has no form for the gender \"" + type.gender() + "\" of \"" + typeId + "\"");
            }
            form = gendered.getAsString();
            if (forms.has("pattern")) usedPattern = forms.get("pattern").getAsString();
        }
        Matcher matcher = PLACEHOLDER.matcher(usedPattern);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String value = matcher.group(1).equals("type") ? type.name() : form;
            matcher.appendReplacement(result, Matcher.quoteReplacement(changeFirstLetter(value, matcher.group(2))));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String changeFirstLetter(String text, @Nullable String mode) {
        if (mode == null || text.isEmpty()) return text;
        String first = text.substring(0, 1);
        return (mode.equals("lower") ? first.toLowerCase(Locale.ROOT) : first.toUpperCase(Locale.ROOT)) + text.substring(1);
    }
}
