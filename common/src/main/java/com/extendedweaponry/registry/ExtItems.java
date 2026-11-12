package com.extendedweaponry.registry;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.item.ExtendedTiers;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.SwordItem;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Les objets propres au mod : un set complet (épée, outils, armure) par matériau, le cœur du Warden et le modèle
 * d'amélioration. Les armes de Better Weaponry dans ces matériaux sont enregistrées par Better Weaponry.
 */
public final class ExtItems {

    /** Les neuf pièces d'un set, dans l'ordre de l'onglet créatif. */
    public enum Piece {
        SWORD, SHOVEL, PICKAXE, AXE, HOE, HELMET, CHESTPLATE, LEGGINGS, BOOTS;

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean isArmor() {
            return ordinal() >= HELMET.ordinal();
        }

        public ArmorItem.Type armorType() {
            return switch (this) {
                case HELMET -> ArmorItem.Type.HELMET;
                case CHESTPLATE -> ArmorItem.Type.CHESTPLATE;
                case LEGGINGS -> ArmorItem.Type.LEGGINGS;
                case BOOTS -> ArmorItem.Type.BOOTS;
                default -> throw new IllegalStateException(this + " is not armor");
            };
        }
    }

    private static final Map<ExtendedTiers, Map<Piece, ExtPlatform.Registered<Item, Item>>> SETS = new EnumMap<>(ExtendedTiers.class);

    public static final ExtPlatform.Registered<Item, Item> WARDEN_HEART = register("warden_heart",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    public static final ExtPlatform.Registered<Item, Item> WARDEN_UPGRADE_SMITHING_TEMPLATE = register("warden_upgrade_smithing_template",
            ExtItems::wardenTemplate);

    static {
        for (ExtendedTiers tier : ExtendedTiers.values()) {
            ExtArmorMaterials armor = ExtArmorMaterials.valueOf(tier.name());
            Map<Piece, ExtPlatform.Registered<Item, Item>> set = new EnumMap<>(Piece.class);
            for (Piece piece : Piece.values()) {
                set.put(piece, register(tier.id() + "_" + piece.id(), () -> create(tier, armor, piece)));
            }
            SETS.put(tier, set);
        }
    }

    private ExtItems() {
    }

    /** Force la déclaration des objets. */
    public static void init() {
    }

    public static Item get(ExtendedTiers tier, Piece piece) {
        return SETS.get(tier).get(piece).get();
    }

    /** Toutes les pièces de tous les sets, matériau par matériau. */
    public static List<Item> allGear() {
        List<Item> items = new ArrayList<>();
        SETS.values().forEach(set -> set.values().forEach(item -> items.add(item.get())));
        return items;
    }

    private static ExtPlatform.Registered<Item, Item> register(String name, java.util.function.Supplier<Item> factory) {
        return ExtPlatform.get().register(Registries.ITEM, name, factory);
    }

    /** Mêmes valeurs d'attaque que les outils vanilla ; seuls la hache et la houe changent selon le matériau. */
    private static Item create(ExtendedTiers tier, ExtArmorMaterials armor, Piece piece) {
        Item.Properties properties = new Item.Properties();
        if (tier == ExtendedTiers.WARDEN) properties.fireResistant();
        return switch (piece) {
            case SWORD -> new SwordItem(tier, 3, -2.4F, properties);
            case SHOVEL -> new ShovelItem(tier, 1.5F, -3.0F, properties);
            // Constructeurs protégés en 1.20.1 : une sous-classe anonyme y a accès.
            case PICKAXE -> new PickaxeItem(tier, 1, -2.8F, properties) {
            };
            case AXE -> new AxeItem(tier, tier.axeDamage(), tier.axeSpeed(), properties) {
            };
            case HOE -> new HoeItem(tier, tier.hoeDamage(), tier.hoeSpeed(), properties) {
            };
            default -> new ArmorItem(armor, piece.armorType(), properties);
        };
    }

    /** Même présentation que le modèle d'amélioration en netherite, avec le cœur du Warden comme ingrédient. */
    private static Item wardenTemplate() {
        Function<String, Component> text = key -> Component.translatable(Util.key("item", "smithing_template.warden_upgrade." + key));
        return new SmithingTemplateItem(
                text.apply("applies_to").copy().withStyle(ChatFormatting.BLUE),
                text.apply("ingredients").copy().withStyle(ChatFormatting.BLUE),
                text.apply("title").copy().withStyle(ChatFormatting.GRAY),
                text.apply("base_slot_description"),
                text.apply("additions_slot_description"),
                List.of(vanilla("item/empty_armor_slot_helmet"), vanilla("item/empty_slot_sword"), vanilla("item/empty_armor_slot_chestplate"),
                        vanilla("item/empty_slot_pickaxe"), vanilla("item/empty_armor_slot_leggings"), vanilla("item/empty_slot_axe"),
                        vanilla("item/empty_armor_slot_boots"), vanilla("item/empty_slot_hoe"), vanilla("item/empty_slot_shovel")),
                List.of(ExtendedWeaponry.id("item/empty_slot_warden_heart")));
    }

    private static ResourceLocation vanilla(String path) {
        return new ResourceLocation(path);
    }

    /** Clés de traduction du mod. */
    public static final class Util {
        private Util() {
        }

        public static String key(String category, String path) {
            return category + "." + ExtendedWeaponry.MODID + "." + path;
        }
    }
}
