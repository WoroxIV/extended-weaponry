package com.extendedweaponry.item;

import com.extendedweaponry.registry.ExtTags;
import com.google.common.base.Suppliers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Les quatre matériaux, pour les outils du mod comme pour les armes de Better Weaponry.
 * Références vanilla (durabilité / vitesse de minage / bonus de dégâts / enchantabilité) :
 * pierre 131/4/1/5, fer 250/6/2/14, diamant 1561/8/3/10, netherite 2031/9/4/15.
 */
public enum ExtendedTiers implements Tier {
    /** Valeurs exactes des outils en cuivre de Minecraft 1.21.9 (entre la pierre et le fer). */
    COPPER(ExtTags.INCORRECT_FOR_COPPER_TOOL, 190, 5.0F, 1.0F, 13, ExtTags.COPPER_INGOTS,
            7.0F, -3.2F, -1.0F, -2.0F),
    /** Un peu plus durable que le fer, dégâts entre le fer et le diamant, très enchantable. */
    EMERALD(ExtTags.INCORRECT_FOR_EMERALD_TOOL, 320, 7.0F, 2.5F, 20, ExtTags.EMERALD_GEMS,
            5.5F, -3.1F, -2.5F, -0.5F),
    /** Se casse très vite (moitié de l'or), mais frappe et mine mieux que le diamant. */
    AMETHYST(ExtTags.INCORRECT_FOR_AMETHYST_TOOL, 16, 9.0F, 3.5F, 18, ExtTags.AMETHYST_GEMS,
            5.0F, -3.0F, -3.5F, 0.0F),
    /** Amélioration du netherite avec un cœur du Warden. */
    WARDEN(ExtTags.INCORRECT_FOR_WARDEN_TOOL, 2800, 10.0F, 5.0F, 18, ExtTags.WARDEN_REPAIR,
            5.0F, -3.0F, -5.0F, 0.0F);

    private final TagKey<Block> incorrectBlocks;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;
    // Hache et houe : leurs valeurs changent d'un matériau à l'autre en vanilla (l'épée, la pioche et la pelle non).
    private final float axeDamage;
    private final float axeSpeed;
    private final float hoeDamage;
    private final float hoeSpeed;

    ExtendedTiers(TagKey<Block> incorrectBlocks, int uses, float speed, float attackDamageBonus, int enchantmentValue,
                  TagKey<Item> repair, float axeDamage, float axeSpeed, float hoeDamage, float hoeSpeed) {
        this.incorrectBlocks = incorrectBlocks;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        // Les tags ne sont pas encore chargés au démarrage : l'ingrédient est créé à la première réparation.
        this.repairIngredient = Suppliers.memoize(() -> Ingredient.of(repair));
        this.axeDamage = axeDamage;
        this.axeSpeed = axeSpeed;
        this.hoeDamage = hoeDamage;
        this.hoeSpeed = hoeSpeed;
    }

    /** Nom du matériau dans les noms d'objets ("copper" pour "copper_sword"). */
    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocks;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    public float axeDamage() {
        return axeDamage;
    }

    public float axeSpeed() {
        return axeSpeed;
    }

    public float hoeDamage() {
        return hoeDamage;
    }

    public float hoeSpeed() {
        return hoeSpeed;
    }
}
