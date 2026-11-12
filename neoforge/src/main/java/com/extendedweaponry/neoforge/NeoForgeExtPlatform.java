package com.extendedweaponry.neoforge;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Version NeoForge de ce qui diffère d'un chargeur à l'autre. */
final class NeoForgeExtPlatform implements ExtPlatform {
    private final IEventBus modBus;
    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new LinkedHashMap<>();

    NeoForgeExtPlatform(IEventBus modBus) {
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
        DeferredHolder<R, T> holder = register.register(name, factory);
        return new Registered<>() {
            @Override
            public T get() {
                return holder.get();
            }

            @Override
            public Holder<R> holder() {
                return holder;
            }
        };
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT);
    }
}
