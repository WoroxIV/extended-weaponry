package com.extendedweaponry.forge;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.loot.LootInjections;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Point d'entrée Forge (client et serveur). */
@Mod(ExtendedWeaponry.MODID)
public final class ExtendedWeaponryForge {

    @SuppressWarnings("removal")
    public ExtendedWeaponryForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ExtPlatform.set(new ForgeExtPlatform(modEventBus));
        ExtendedWeaponry.init();
        MinecraftForge.EVENT_BUS.addListener(ExtendedWeaponryForge::addLoot);
    }

    /** La table vanilla tire aussi la table du mod qui lui correspond (voir {@link LootInjections}). */
    private static void addLoot(LootTableLoadEvent event) {
        ResourceLocation injected = LootInjections.INJECTIONS.get(event.getName());
        if (injected == null) return;
        event.getTable().addPool(LootPool.lootPool()
                .name(ExtendedWeaponry.MODID + ":" + injected.getPath())
                .setRolls(ConstantValue.exactly(1))
                .add(LootTableReference.lootTableReference(injected))
                .build());
    }
}
