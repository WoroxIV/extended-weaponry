package com.extendedweaponry.datagen;

import com.betterweaponry.item.GearType;
import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.item.WeaponType;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.item.ExtendedMaterials;
import com.extendedweaponry.item.ExtendedTiers;
import com.extendedweaponry.registry.ExtItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Modèles des objets du mod :
 * <ul>
 *   <li>outils et armures : textures dessinées, {@code textures/item/<nom>.png} ;</li>
 *   <li>armes de Better Weaponry dans les matériaux du mod : mêmes modèles que celles de Better Weaponry, avec une texture
 *       fabriquée au chargement par Better Weaponry à partir de la palette {@code textures/palettes/<matériau>.png}.</li>
 * </ul>
 */
public class ExtItemModelProvider extends ItemModelProvider {
    private static final String BETTER_WEAPONRY = "better_weaponry";

    public ExtItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExtendedWeaponry.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (ExtendedTiers tier : ExtendedTiers.values()) {
            for (ExtItems.Piece piece : ExtItems.Piece.values()) {
                String name = tier.id() + "_" + piece.id();
                withExistingParent(name, mcLoc(piece.isArmor() ? "item/generated" : "item/handheld")).texture("layer0", modLoc("item/" + name));
            }
        }
        withExistingParent("warden_heart", mcLoc("item/generated")).texture("layer0", modLoc("item/warden_heart"));
        withExistingParent("warden_upgrade_smithing_template", mcLoc("item/generated")).texture("layer0", modLoc("item/warden_upgrade_smithing_template"));

        for (WeaponMaterial material : ExtendedMaterials.ALL) {
            for (WeaponType type : WeaponType.values()) {
                ResourceLocation base = ResourceLocation.fromNamespaceAndPath(BETTER_WEAPONRY, "item/" + type.id() + "_base");
                ResourceLocation parent = existingFileHelper.exists(base, MODEL) ? base : mcLoc("item/handheld");
                withExistingParent(material.itemName(type), parent).texture("layer0", generated(material.itemName(type)));
            }
            addShieldModel(material);
            addBowModels(material.id() + "_");
            addCrossbowModels(material.id() + "_");
            String shuriken = material.itemName(GearType.SHURIKEN);
            withExistingParent(shuriken, mcLoc("item/handheld")).texture("layer0", generated(shuriken));
        }
    }

    /** Texture fabriquée au chargement par Better Weaponry : elle n'existe pas encore comme fichier. */
    private ResourceLocation generated(String name) {
        ResourceLocation texture = modLoc("item/" + name);
        existingFileHelper.trackGenerated(texture, ModelProvider.TEXTURE);
        return texture;
    }

    private ModelFile.UncheckedModelFile model(String name) {
        return new ModelFile.UncheckedModelFile(modLoc("item/" + name));
    }

    /** Même modèle que le bouclier vanilla ; la texture est choisie par le rendu du bouclier de Better Weaponry. */
    private void addShieldModel(WeaponMaterial material) {
        withExistingParent(material.itemName(GearType.SHIELD), mcLoc("item/shield"))
                .override().predicate(mcLoc("blocking"), 1.0F).model(new ModelFile.UncheckedModelFile(mcLoc("item/shield_blocking"))).end();
    }

    private void addBowModels(String prefix) {
        String name = prefix + "bow";
        for (int stage = 0; stage < 3; stage++) {
            withExistingParent(name + "_pulling_" + stage, mcLoc("item/bow")).texture("layer0", generated(name + "_pulling_" + stage));
        }
        withExistingParent(name, mcLoc("item/bow")).texture("layer0", generated(name))
                .override().predicate(mcLoc("pulling"), 1.0F).model(model(name + "_pulling_0")).end()
                .override().predicate(mcLoc("pulling"), 1.0F).predicate(mcLoc("pull"), 0.65F).model(model(name + "_pulling_1")).end()
                .override().predicate(mcLoc("pulling"), 1.0F).predicate(mcLoc("pull"), 0.9F).model(model(name + "_pulling_2")).end();
    }

    private void addCrossbowModels(String prefix) {
        String name = prefix + "crossbow";
        for (int stage = 0; stage < 3; stage++) {
            withExistingParent(name + "_pulling_" + stage, mcLoc("item/crossbow")).texture("layer0", generated(name + "_pulling_" + stage));
        }
        for (String loaded : new String[]{name + "_arrow", name + "_firework"}) {
            withExistingParent(loaded, mcLoc("item/crossbow")).texture("layer0", generated(loaded));
        }
        withExistingParent(name, mcLoc("item/crossbow")).texture("layer0", generated(name))
                .override().predicate(mcLoc("pulling"), 1.0F).model(model(name + "_pulling_0")).end()
                .override().predicate(mcLoc("pulling"), 1.0F).predicate(mcLoc("pull"), 0.58F).model(model(name + "_pulling_1")).end()
                .override().predicate(mcLoc("pulling"), 1.0F).predicate(mcLoc("pull"), 1.0F).model(model(name + "_pulling_2")).end()
                .override().predicate(mcLoc("charged"), 1.0F).model(model(name + "_arrow")).end()
                .override().predicate(mcLoc("charged"), 1.0F).predicate(mcLoc("firework"), 1.0F).model(model(name + "_firework")).end();
    }
}
