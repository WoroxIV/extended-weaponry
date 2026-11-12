package com.extendedweaponry.forge;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Version Forge de ce qui diffère d'un chargeur à l'autre. */
final class ForgeExtPlatform implements ExtPlatform {
    private final IEventBus modBus;
    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new LinkedHashMap<>();

    ForgeExtPlatform(IEventBus modBus) {
        this.modBus = modBus;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDataGen() {
        return DatagenModLoader.isRunningDataGen();
    }

    @Override
    public boolean isFakePlayer(Entity entity) {
        return entity instanceof FakePlayer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, T extends R> Registered<R, T> register(ResourceKey<? extends Registry<R>> registry, String name, Supplier<T> factory) {
        DeferredRegister<R> register = (DeferredRegister<R>) registers.computeIfAbsent(registry, key -> {
            DeferredRegister<R> created = DeferredRegister.create(registry, ExtendedWeaponry.MODID);
            created.register(modBus);
            return created;
        });
        RegistryObject<T> object = register.register(name, factory);
        return object::get;
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        // Après le dernier onglet vanilla (sinon le tri par nom le place avant Nourriture, Ingrédients et Œufs d'apparition).
        return CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
    }
}
