package com.extendedweaponry;

import com.extendedweaponry.platform.ExtPlatform;
import com.extendedweaponry.registry.ExtArmorMaterials;
import com.extendedweaponry.registry.ExtCreativeTab;
import com.extendedweaponry.registry.ExtItems;
import com.extendedweaponry.registry.ExtLootConditions;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * Le cœur d'Extended Weaponry, commun à tous les chargeurs.
 * <p>
 * Les armes de Better Weaponry dans les nouveaux matériaux sont créées par Better Weaponry lui-même, à partir de
 * {@link com.extendedweaponry.item.ExtendedMaterials} (déclaré comme fournisseur de matériaux). Ce mod enregistre le
 * reste : outils, armures, cœur du Warden et modèle d'amélioration.
 */
public final class ExtendedWeaponry {
    public static final String MODID = "extended_weaponry";
    public static final Logger LOGGER = LogUtils.getLogger();
    /** Deeper and Darker a déjà son équipement du Warden : le nôtre est alors caché (mais reste enregistré). */
    public static final String DEEPER_DARKER = "deeperdarker";

    private ExtendedWeaponry() {
    }

    /** Déclare tout le contenu du mod. */
    public static void init() {
        ExtArmorMaterials.init();
        ExtItems.init();
        ExtLootConditions.init();
        ExtCreativeTab.init();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    /** L'équipement du Warden s'obtient (recettes, butin, onglet) seulement sans Deeper and Darker. */
    public static boolean wardenGearEnabled() {
        return !ExtPlatform.get().isModLoaded(DEEPER_DARKER);
    }
}
