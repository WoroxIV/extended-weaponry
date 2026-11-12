package com.extendedweaponry.registry;

import com.extendedweaponry.ExtendedWeaponry;
import com.google.common.base.Suppliers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Les armures des quatre matériaux. Références vanilla (facteur de durabilité / protection casque-plastron-jambières-bottes /
 * robustesse / enchantabilité) : fer 15 / 2-6-5-2 / 0 / 9, diamant 33 / 3-8-6-3 / 2 / 10, netherite 37 / 3-8-6-3 / 3 / 15.
 * <p>
 * Textures : {@code assets/minecraft/textures/models/armor/extended_weaponry_<matériau>_layer_1.png} et {@code _layer_2.png}.
 * En 1.20.1, le jeu de base (donc Fabric) ne lit pas de namespace dans le nom d'un matériau d'armure.
 */
public enum ExtArmorMaterials implements ArmorMaterial {
    /** Valeurs exactes de l'armure en cuivre de Minecraft 1.21.9. */
    COPPER(11, 2, 4, 3, 1, 8, SoundEvents.ARMOR_EQUIP_IRON, ExtTags.COPPER_INGOTS, 0.0F, 0.0F),
    /** Protection du fer, un peu plus durable, un peu de robustesse, très enchantable. */
    EMERALD(17, 2, 6, 5, 2, 20, SoundEvents.ARMOR_EQUIP_DIAMOND, ExtTags.EMERALD_GEMS, 0.5F, 0.0F),
    /** Protection du diamant, mais très fragile (environ 3 fois moins durable que l'or). */
    AMETHYST(3, 3, 8, 6, 3, 18, SoundEvents.AMETHYST_BLOCK_CHIME, ExtTags.AMETHYST_GEMS, 2.0F, 0.0F),
    /** Au-dessus du netherite. */
    WARDEN(42, 3, 8, 6, 3, 18, SoundEvents.ARMOR_EQUIP_NETHERITE, ExtTags.WARDEN_REPAIR, 4.0F, 0.15F);

    private final int durabilityMultiplier;
    private final int helmet;
    private final int chestplate;
    private final int leggings;
    private final int boots;
    private final int enchantability;
    private final SoundEvent sound;
    private final Supplier<Ingredient> repair;
    private final float toughness;
    private final float knockbackResistance;

    ExtArmorMaterials(int durabilityMultiplier, int helmet, int chestplate, int leggings, int boots, int enchantability,
                      SoundEvent sound, TagKey<Item> repair, float toughness, float knockbackResistance) {
        this.durabilityMultiplier = durabilityMultiplier;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.enchantability = enchantability;
        this.sound = sound;
        this.repair = Suppliers.memoize(() -> Ingredient.of(repair));
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    /** Rien à déclarer en 1.20.1 : les matériaux d'armure ne sont pas un registre. */
    public static void init() {
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        // Mêmes durabilités de base que le vanilla (casque 11, plastron 16, jambières 15, bottes 13).
        int base = switch (type) {
            case HELMET -> 11;
            case CHESTPLATE -> 16;
            case LEGGINGS -> 15;
            case BOOTS -> 13;
        };
        return base * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> helmet;
            case CHESTPLATE -> chestplate;
            case LEGGINGS -> leggings;
            case BOOTS -> boots;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repair.get();
    }

    @Override
    public String getName() {
        return ExtendedWeaponry.MODID + "_" + name().toLowerCase(Locale.ROOT);
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
