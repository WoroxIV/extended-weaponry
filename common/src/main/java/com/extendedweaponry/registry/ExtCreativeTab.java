package com.extendedweaponry.registry;

import com.betterweaponry.item.WeaponItem;
import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.registry.ModCreativeTabs;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.item.ExtendedMaterials;
import com.extendedweaponry.item.ExtendedTiers;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Onglet du mod : les sets (épée, outils, armure) matériau par matériau, le cœur et le modèle du Warden, puis les armes
 * de Better Weaponry dans ces matériaux, dans le même ordre que l'onglet de Better Weaponry.
 * Avec Deeper and Darker, l'équipement du Warden n'y apparaît pas.
 */
public final class ExtCreativeTab {
    public static final ExtPlatform.Registered<CreativeModeTab, CreativeModeTab> TAB = ExtPlatform.get().register(Registries.CREATIVE_MODE_TAB, "gear",
            () -> ExtPlatform.get().creativeTabBuilder()
                    .title(Component.translatable("itemGroup." + ExtendedWeaponry.MODID + ".gear"))
                    .icon(() -> new ItemStack(ExtItems.get(ExtendedTiers.AMETHYST, ExtItems.Piece.SWORD)))
                    .displayItems((parameters, output) -> displayOrder().forEach(output::accept))
                    .build());

    private ExtCreativeTab() {
    }

    /** Force la déclaration de l'onglet. */
    public static void init() {
    }

    public static List<ItemStack> displayOrder() {
        boolean warden = ExtendedWeaponry.wardenGearEnabled();
        List<ItemStack> stacks = new ArrayList<>();
        for (ExtendedTiers tier : ExtendedTiers.values()) {
            if (tier == ExtendedTiers.WARDEN && !warden) continue;
            for (ExtItems.Piece piece : ExtItems.Piece.values()) stacks.add(new ItemStack(ExtItems.get(tier, piece)));
        }
        if (warden) {
            stacks.add(new ItemStack(ExtItems.WARDEN_HEART.get()));
            stacks.add(new ItemStack(ExtItems.WARDEN_UPGRADE_SMITHING_TEMPLATE.get()));
        }
        stacks.addAll(ModCreativeTabs.displayOrder(material -> isShown(material, warden)));
        return stacks;
    }

    private static boolean isShown(WeaponMaterial material, boolean warden) {
        return material.namespace().equals(ExtendedWeaponry.MODID) && (warden || material != ExtendedMaterials.WARDEN);
    }

    /** Vrai pour une arme de Better Weaponry créée dans un matériau de ce mod. */
    public static boolean isOwnWeapon(ItemStack stack) {
        return stack.getItem() instanceof WeaponItem weapon && weapon.getMaterial().namespace().equals(ExtendedWeaponry.MODID);
    }
}
