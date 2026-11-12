package com.extendedweaponry.registry;

import com.extendedweaponry.ExtendedWeaponry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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

    // Ingrédients (tags communs, remplis par les chargeurs)
    public static final TagKey<Item> COPPER_INGOTS = common("ingots/copper");
    public static final TagKey<Item> EMERALD_GEMS = common("gems/emerald");
    public static final TagKey<Item> AMETHYST_GEMS = common("gems/amethyst");

    // Ce que chaque matériau ne peut pas miner (même échelle que les outils vanilla)
    public static final TagKey<Block> INCORRECT_FOR_COPPER_TOOL = block("incorrect_for_copper_tool");
    public static final TagKey<Block> INCORRECT_FOR_EMERALD_TOOL = block("incorrect_for_emerald_tool");
    public static final TagKey<Block> INCORRECT_FOR_AMETHYST_TOOL = block("incorrect_for_amethyst_tool");
    public static final TagKey<Block> INCORRECT_FOR_WARDEN_TOOL = block("incorrect_for_warden_tool");

    private ExtTags() {
    }

    public static TagKey<Item> item(String path) {
        return TagKey.create(Registries.ITEM, ExtendedWeaponry.id(path));
    }

    private static TagKey<Item> common(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Block> block(String path) {
        return TagKey.create(Registries.BLOCK, ExtendedWeaponry.id(path));
    }
}
