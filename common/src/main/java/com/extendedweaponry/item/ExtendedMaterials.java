package com.extendedweaponry.item;

import com.betterweaponry.api.WeaponMaterialProvider;
import com.betterweaponry.item.GearType;
import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.item.WeaponMaterials;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.registry.ExtTags;

import java.util.List;

/**
 * Les matériaux donnés à Better Weaponry, qui crée pour chacun ses 13 armes, un bouclier, un arc, une arbalète et un
 * shuriken ({@code extended_weaponry:emerald_katana}...). Déclaré dans neoforge.mods.toml et fabric.mod.json.
 * <p>
 * Chargé par Better Weaponry très tôt, avant le point d'entrée de ce mod : cette classe ne doit rien utiliser d'autre que
 * Better Weaponry et les classes de données du mod (pas de plateforme, pas de registre).
 */
public final class ExtendedMaterials implements WeaponMaterialProvider {
    /** Durabilité de l'équipement en améthyste : celle de ses outils (16) le rendrait inutilisable. */
    private static final int AMETHYST_GEAR_DURABILITY = 64;
    private static final int AMETHYST_SHURIKEN_DURABILITY = 8;

    public static final WeaponMaterial COPPER = WeaponMaterial.builder(ExtendedWeaponry.MODID, "copper", ExtendedTiers.COPPER)
            .craftedWith(ExtTags.COPPER_INGOTS)
            .build();

    public static final WeaponMaterial EMERALD = WeaponMaterial.builder(ExtendedWeaponry.MODID, "emerald", ExtendedTiers.EMERALD)
            .craftedWith(ExtTags.EMERALD_GEMS)
            .build();

    public static final WeaponMaterial AMETHYST = WeaponMaterial.builder(ExtendedWeaponry.MODID, "amethyst", ExtendedTiers.AMETHYST)
            .craftedWith(ExtTags.AMETHYST_GEMS)
            .gearDurability(GearType.SHIELD, AMETHYST_GEAR_DURABILITY)
            .gearDurability(GearType.BOW, AMETHYST_GEAR_DURABILITY)
            .gearDurability(GearType.CROSSBOW, AMETHYST_GEAR_DURABILITY)
            .gearDurability(GearType.SHURIKEN, AMETHYST_SHURIKEN_DURABILITY)
            .build();

    /** Comme le netherite vanilla : arme en netherite + modèle d'amélioration + cœur du Warden. */
    public static final WeaponMaterial WARDEN = WeaponMaterial.builder(ExtendedWeaponry.MODID, "warden", ExtendedTiers.WARDEN)
            .smithingUpgrade(WeaponMaterials.NETHERITE,
                    ExtendedWeaponry.MODID + ":warden_upgrade_smithing_template", ExtendedWeaponry.MODID + ":warden_heart")
            .fireResistant()
            .build();

    /** Dans l'ordre de l'onglet créatif. */
    public static final List<WeaponMaterial> ALL = List.of(COPPER, EMERALD, AMETHYST, WARDEN);

    /** Créé par Better Weaponry (constructeur public sans argument). */
    public ExtendedMaterials() {
    }

    @Override
    public List<WeaponMaterial> materials() {
        return ALL;
    }
}
