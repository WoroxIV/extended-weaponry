package com.extendedweaponry.datagen;

import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.item.WeaponType;
import com.betterweaponry.registry.ModItems;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.item.ExtendedMaterials;
import com.extendedweaponry.item.ExtendedTiers;
import com.extendedweaponry.registry.ExtItems;
import com.extendedweaponry.registry.ExtItems.Piece;
import com.extendedweaponry.registry.ExtTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Tags des objets du mod :
 * <ul>
 *   <li>tags vanilla des outils et armures (enchantements, garnitures, récolte des amas d'améthyste...) ;</li>
 *   <li>tags de Better Weaponry pour ses armes dans nos matériaux (c'est par eux que Combat Reforged les reconnaît) ;</li>
 *   <li>tags du mod : fragile, résonance, sonique, réparation du Warden, butin ;</li>
 *   <li>tags d'ingrédients de l'amélioration du Warden, générés pour Better Weaponry.</li>
 * </ul>
 */
public class ExtItemTagsProvider extends ItemTagsProvider {
    private static final String BETTER_WEAPONRY = "better_weaponry";
    private static final Map<Piece, TagKey<Item>> VANILLA_TAGS = Map.of(
            Piece.SWORD, ItemTags.SWORDS, Piece.SHOVEL, ItemTags.SHOVELS, Piece.PICKAXE, ItemTags.PICKAXES, Piece.AXE, ItemTags.AXES,
            Piece.HOE, ItemTags.HOES, Piece.HELMET, ItemTags.HEAD_ARMOR, Piece.CHESTPLATE, ItemTags.CHEST_ARMOR,
            Piece.LEGGINGS, ItemTags.LEG_ARMOR, Piece.BOOTS, ItemTags.FOOT_ARMOR);
    /** Armes de Better Weaponry qui acceptent Hémorragie, comme dans Better Weaponry. */
    private static final List<WeaponType> BLEEDING = List.of(WeaponType.SICKLE, WeaponType.SCYTHE, WeaponType.KATANA);

    public ExtItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ExtendedWeaponry.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        addVanillaGearTags();
        addBetterWeaponryTags();
        addMaterialTags();

        tag(ExtTags.WARDEN_REPAIR).add(ExtItems.WARDEN_HEART.get());
        WeaponMaterial.SmithingUpgrade upgrade = ExtendedMaterials.WARDEN.smithingUpgrade();
        tag(upgrade.template().tag()).add(ExtItems.WARDEN_UPGRADE_SMITHING_TEMPLATE.get());
        tag(upgrade.addition().tag()).add(ExtItems.WARDEN_HEART.get());
    }

    private void addVanillaGearTags() {
        for (ExtendedTiers tier : ExtendedTiers.values()) {
            for (Piece piece : Piece.values()) {
                Item item = ExtItems.get(tier, piece);
                tag(VANILLA_TAGS.get(piece)).add(item);
                if (piece.isArmor()) tag(ItemTags.TRIMMABLE_ARMOR).add(item);
                if (piece == Piece.PICKAXE) tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(item);
            }
        }
    }

    private void addBetterWeaponryTags() {
        for (WeaponMaterial material : ExtendedMaterials.ALL) {
            for (WeaponType type : WeaponType.values()) {
                Item weapon = ModItems.get(type, material).get();
                tag(ItemTags.SWORDS).add(weapon);
                tag(betterWeaponry(type.id() + "s")).add(weapon);
                if (BLEEDING.contains(type)) tag(betterWeaponry("enchantable/bleeding")).add(weapon);
            }
            tag(betterWeaponry("shields")).add(ModItems.shield(material).get());
            tag(betterWeaponry("bows")).add(ModItems.bow(material).get());
            tag(betterWeaponry("crossbows")).add(ModItems.crossbow(material).get());
            tag(betterWeaponry("shurikens")).add(ModItems.shuriken(material).get());
        }
    }

    /** Effets propres aux matériaux, et objets de chaque matériau pour le butin des coffres. */
    private void addMaterialTags() {
        var fragile = tag(ExtTags.FRAGILE);
        var resonant = tag(ExtTags.RESONANT);
        var sonic = tag(ExtTags.SONIC);
        for (Piece piece : Piece.values()) fragile.add(ExtItems.get(ExtendedTiers.AMETHYST, piece));
        for (Item item : allBetterWeaponry(ExtendedMaterials.AMETHYST)) fragile.add(item);
        for (Piece piece : List.of(Piece.SWORD, Piece.AXE)) {
            resonant.add(ExtItems.get(ExtendedTiers.AMETHYST, piece));
            sonic.add(ExtItems.get(ExtendedTiers.WARDEN, piece));
        }
        for (WeaponType type : WeaponType.values()) {
            resonant.add(ModItems.get(type, ExtendedMaterials.AMETHYST).get());
            sonic.add(ModItems.get(type, ExtendedMaterials.WARDEN).get());
        }
        sonic.add(ModItems.bow(ExtendedMaterials.WARDEN).get(), ModItems.crossbow(ExtendedMaterials.WARDEN).get(), ModItems.shuriken(ExtendedMaterials.WARDEN).get());

        for (WeaponMaterial material : List.of(ExtendedMaterials.COPPER, ExtendedMaterials.EMERALD, ExtendedMaterials.AMETHYST)) {
            var loot = tag(ExtTags.item("loot/" + material.id() + "_gear"));
            ExtendedTiers tier = (ExtendedTiers) material.tier();
            for (Piece piece : Piece.values()) loot.add(ExtItems.get(tier, piece));
            for (Item item : allBetterWeaponry(material)) loot.add(item);
        }
    }

    private static List<Item> allBetterWeaponry(WeaponMaterial material) {
        List<Item> items = new java.util.ArrayList<>();
        for (WeaponType type : WeaponType.values()) items.add(ModItems.get(type, material).get());
        items.add(ModItems.shield(material).get());
        items.add(ModItems.bow(material).get());
        items.add(ModItems.crossbow(material).get());
        items.add(ModItems.shuriken(material).get());
        return items;
    }

    private static TagKey<Item> betterWeaponry(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BETTER_WEAPONRY, path));
    }
}
