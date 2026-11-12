package com.extendedweaponry.registry;

import com.extendedweaponry.ExtendedWeaponry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

/** Tags du mod. Un datapack peut les compléter (ex : rendre fragile l'objet d'un autre mod). */
public final class ExtTags {
    /** Objets en améthyste : Solidité et Raccommodage sans effet, jamais incassables, rendent des éclats en cassant. */
    public static final TagKey<Item> FRAGILE = item("fragile");
    /** Armes de mêlée en améthyste : leurs coups font résonner les ennemis proches. */
    public static final TagKey<Item> RESONANT = item("resonant_weapons");
    /** Armes du Warden : une partie des dégâts traverse l'armure, et les coups ne déclenchent pas les capteurs de sculk. */
    public static final TagKey<Item> SONIC = item("sonic_weapons");
    /** Objets qui réparent l'équipement du Warden à l'enclume. */
    public static final TagKey<Item> WARDEN_REPAIR = item("repairs_warden_gear");

    /**
     * Ingrédients des matériaux. En 1.20.1, Forge et Fabric nomment différemment leurs tags communs : ces tags du mod
     * contiennent l'objet vanilla et, en facultatif, le tag commun de chaque chargeur.
     */
    public static final TagKey<Item> COPPER_INGOTS = item("ingredients/copper");
    public static final TagKey<Item> EMERALD_GEMS = item("ingredients/emerald");
    public static final TagKey<Item> AMETHYST_GEMS = item("ingredients/amethyst");

    /** Enchantements qui ménagent ou réparent la durabilité (Solidité, Raccommodage), interdits sur les objets fragiles. */
    public static final TagKey<Enchantment> PROTECTS_DURABILITY = TagKey.create(Registries.ENCHANTMENT, ExtendedWeaponry.id("protects_durability"));

    private ExtTags() {
    }

    public static TagKey<Item> item(String path) {
        return TagKey.create(Registries.ITEM, ExtendedWeaponry.id(path));
    }
}
