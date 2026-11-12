package com.extendedweaponry.datagen;

import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.item.WeaponMaterials;
import com.betterweaponry.item.WeaponRecipe;
import com.betterweaponry.item.WeaponType;
import com.betterweaponry.registry.ModItems;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.item.ExtendedMaterials;
import com.extendedweaponry.item.ExtendedTiers;
import com.extendedweaponry.registry.ExtItems;
import com.extendedweaponry.registry.ExtItems.Piece;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Recettes du mod :
 * <ul>
 *   <li>outils et armures en cuivre, émeraude et améthyste : mêmes formes que les vanilla ;</li>
 *   <li>armes de Better Weaponry dans ces matériaux : mêmes motifs que dans Better Weaponry ;</li>
 *   <li>Warden : objet en netherite + modèle d'amélioration + cœur du Warden, à la table de forgeron, et duplication
 *       du modèle (7 débris de netherite, 1 éclat d'écho). Sans effet quand Deeper and Darker est installé.</li>
 * </ul>
 */
public class ExtRecipeProvider extends RecipeProvider {
    private static final Map<Piece, List<String>> VANILLA_SHAPES = Map.of(
            Piece.SWORD, List.of("M", "M", "S"),
            Piece.SHOVEL, List.of("M", "S", "S"),
            Piece.PICKAXE, List.of("MMM", " S ", " S "),
            Piece.AXE, List.of("MM", "MS", " S"),
            Piece.HOE, List.of("MM", " S", " S"),
            Piece.HELMET, List.of("MMM", "M M"),
            Piece.CHESTPLATE, List.of("M M", "MMM", "MMM"),
            Piece.LEGGINGS, List.of("MMM", "M M", "M M"),
            Piece.BOOTS, List.of("M M", "M M"));
    private static final Map<Piece, Item> NETHERITE = Map.of(
            Piece.SWORD, Items.NETHERITE_SWORD, Piece.SHOVEL, Items.NETHERITE_SHOVEL, Piece.PICKAXE, Items.NETHERITE_PICKAXE,
            Piece.AXE, Items.NETHERITE_AXE, Piece.HOE, Items.NETHERITE_HOE, Piece.HELMET, Items.NETHERITE_HELMET,
            Piece.CHESTPLATE, Items.NETHERITE_CHESTPLATE, Piece.LEGGINGS, Items.NETHERITE_LEGGINGS, Piece.BOOTS, Items.NETHERITE_BOOTS);

    // Formes des équipements de Better Weaponry (mêmes motifs que dans son propre générateur de recettes).
    private static final WeaponRecipe SHIELD_RECIPE = WeaponRecipe.of("PMP", "PPP", " P ").with('P', ItemTags.PLANKS);
    private static final WeaponRecipe BOW_RECIPE = WeaponRecipe.of(" MX", "M X", " MX").with('X', Tags.Items.STRINGS);
    private static final WeaponRecipe CROSSBOW_RECIPE = WeaponRecipe.of("MIM", "XHX", " M ")
            .with('I', Tags.Items.INGOTS_IRON).with('X', Tags.Items.STRINGS).withItem('H', () -> Items.TRIPWIRE_HOOK);
    private static final WeaponRecipe SHURIKEN_RECIPE = WeaponRecipe.of("M M", " S ", "M M");

    public ExtRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        RecipeOutput warden = output.withConditions(new NotCondition(new ModLoadedCondition(ExtendedWeaponry.DEEPER_DARKER)));
        Item template = ExtItems.WARDEN_UPGRADE_SMITHING_TEMPLATE.get();
        Item heart = ExtItems.WARDEN_HEART.get();

        // Sets vanilla
        for (ExtendedTiers tier : ExtendedTiers.values()) {
            for (Piece piece : Piece.values()) {
                Item result = ExtItems.get(tier, piece);
                String name = tier.id() + "_" + piece.id();
                if (tier == ExtendedTiers.WARDEN) {
                    smithing(warden, NETHERITE.get(piece), result, name, template, heart);
                } else {
                    ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(piece.isArmor() ? RecipeCategory.COMBAT : RecipeCategory.TOOLS, result);
                    VANILLA_SHAPES.get(piece).forEach(builder::pattern);
                    TagKey<Item> material = ingredient(tier);
                    builder.define('M', material);
                    if (!piece.isArmor()) builder.define('S', Tags.Items.RODS_WOODEN);
                    builder.unlockedBy("has_material", has(material)).save(output, id(name));
                }
            }
        }

        // Armes et équipements de Better Weaponry
        for (WeaponMaterial material : ExtendedMaterials.ALL) {
            for (WeaponType type : WeaponType.values()) {
                if (type.recipe() == null) continue;
                Item result = ModItems.get(type, material).get();
                if (material == ExtendedMaterials.WARDEN) {
                    smithing(warden, ModItems.get(type, WeaponMaterials.NETHERITE).get(), result, material.itemName(type), template, heart);
                } else {
                    shaped(output, result, material.itemName(type), type.recipe(), material.craftingIngredient().tag());
                }
            }
            gear(output, warden, material, SHIELD_RECIPE, ModItems.shield(material).get(), ModItems.shield(WeaponMaterials.NETHERITE).get(), material.id() + "_shield", template, heart);
            gear(output, warden, material, BOW_RECIPE, ModItems.bow(material).get(), ModItems.bow(WeaponMaterials.NETHERITE).get(), material.id() + "_bow", template, heart);
            gear(output, warden, material, CROSSBOW_RECIPE, ModItems.crossbow(material).get(), ModItems.crossbow(WeaponMaterials.NETHERITE).get(), material.id() + "_crossbow", template, heart);
            gear(output, warden, material, SHURIKEN_RECIPE, ModItems.shuriken(material).get(), ModItems.shuriken(WeaponMaterials.NETHERITE).get(), material.id() + "_shuriken", template, heart);
        }

        // Duplication du modèle, comme celui du netherite : débris de netherite à la place des diamants, éclat d'écho à la place de la netherrack.
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template, 2)
                .pattern("#T#").pattern("#E#").pattern("###")
                .define('#', Items.NETHERITE_SCRAP).define('E', Items.ECHO_SHARD).define('T', template)
                .unlockedBy("has_template", has(template))
                .save(warden, id("warden_upgrade_smithing_template"));
    }

    private static TagKey<Item> ingredient(ExtendedTiers tier) {
        return switch (tier) {
            case COPPER -> ExtendedMaterials.COPPER.craftingIngredient().tag();
            case EMERALD -> ExtendedMaterials.EMERALD.craftingIngredient().tag();
            case AMETHYST -> ExtendedMaterials.AMETHYST.craftingIngredient().tag();
            case WARDEN -> throw new IllegalArgumentException("Warden gear is made at the smithing table");
        };
    }

    private void gear(RecipeOutput output, RecipeOutput warden, WeaponMaterial material, WeaponRecipe recipe, Item result, Item netherite,
                      String name, Item template, Item heart) {
        if (material == ExtendedMaterials.WARDEN) smithing(warden, netherite, result, name, template, heart);
        else shaped(output, result, name, recipe, material.craftingIngredient().tag());
    }

    private void shaped(RecipeOutput output, Item result, String name, WeaponRecipe recipe, TagKey<Item> materialTag) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result);
        recipe.pattern().forEach(builder::pattern);
        if (recipe.uses(WeaponRecipe.MATERIAL)) builder.define(WeaponRecipe.MATERIAL, materialTag);
        if (recipe.uses(WeaponRecipe.STICK)) builder.define(WeaponRecipe.STICK, Tags.Items.RODS_WOODEN);
        recipe.extraIngredients().forEach((symbol, tag) -> {
            if (recipe.uses(symbol)) builder.define(symbol, tag);
        });
        recipe.extraItems().forEach((symbol, item) -> builder.define(symbol, item.get()));
        builder.unlockedBy("has_material", has(materialTag)).save(output, id(name));
    }

    private void smithing(RecipeOutput output, Item base, Item result, String name, Item template, Item heart) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(template), Ingredient.of(base), Ingredient.of(heart), RecipeCategory.COMBAT, result)
                .unlocks("has_warden_heart", has(heart))
                .save(output, id(name + "_smithing"));
    }

    private static ResourceLocation id(String path) {
        return ExtendedWeaponry.id(path);
    }
}
