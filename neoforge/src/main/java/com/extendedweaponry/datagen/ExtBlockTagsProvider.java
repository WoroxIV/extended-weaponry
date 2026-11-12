package com.extendedweaponry.datagen;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.registry.ExtTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Ce que chaque matériau ne peut pas miner, sur l'échelle vanilla : cuivre = pierre (comme en 1.21.9), émeraude = fer,
 * améthyste = diamant, Warden = netherite. Un datapack peut changer ces tags.
 */
public class ExtBlockTagsProvider extends BlockTagsProvider {

    public ExtBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExtendedWeaponry.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ExtTags.INCORRECT_FOR_COPPER_TOOL).addTag(BlockTags.INCORRECT_FOR_STONE_TOOL);
        tag(ExtTags.INCORRECT_FOR_EMERALD_TOOL).addTag(BlockTags.INCORRECT_FOR_IRON_TOOL);
        tag(ExtTags.INCORRECT_FOR_AMETHYST_TOOL).addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
        tag(ExtTags.INCORRECT_FOR_WARDEN_TOOL).addTag(BlockTags.INCORRECT_FOR_NETHERITE_TOOL);
    }
}
