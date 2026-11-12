package com.extendedweaponry.neoforge;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.datagen.ExtBlockTagsProvider;
import com.extendedweaponry.datagen.ExtCompatProvider;
import com.extendedweaponry.datagen.ExtItemModelProvider;
import com.extendedweaponry.datagen.ExtItemTagsProvider;
import com.extendedweaponry.datagen.ExtLootModifierProvider;
import com.extendedweaponry.datagen.ExtLootTableProvider;
import com.extendedweaponry.datagen.ExtRecipeProvider;
import com.extendedweaponry.datagen.lang.ExtLanguageProvider;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** Point d'entrée NeoForge (client et serveur). */
@Mod(ExtendedWeaponry.MODID)
public final class ExtendedWeaponryNeoForge {

    public ExtendedWeaponryNeoForge(IEventBus modEventBus) {
        ExtPlatform.set(new NeoForgeExtPlatform(modEventBus));
        ExtendedWeaponry.init();
        modEventBus.addListener(this::gatherData);
    }

    /** Lancé par "gradlew :neoforge:runData" : modèles, traductions, tags, recettes, butin et fichiers de compatibilité. */
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new ExtItemModelProvider(output, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ExtLanguageProvider(output));

        generator.addProvider(event.includeServer(), new ExtRecipeProvider(output, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new ExtLootTableProvider(output, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new ExtLootModifierProvider(output, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new ExtCompatProvider(output));
        ExtBlockTagsProvider blockTags = generator.addProvider(event.includeServer(),
                new ExtBlockTagsProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(),
                new ExtItemTagsProvider(output, event.getLookupProvider(), blockTags.contentsGetter(), event.getExistingFileHelper()));
    }
}
