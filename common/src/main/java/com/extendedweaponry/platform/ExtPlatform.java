package com.extendedweaponry.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;
import java.util.function.Supplier;

/** Ce qui diffère d'un chargeur à l'autre. Chaque chargeur installe sa version au tout début de son point d'entrée. */
public interface ExtPlatform {

    static ExtPlatform get() {
        return Objects.requireNonNull(Installed.instance, "Extended Weaponry platform not installed yet");
    }

    static void set(ExtPlatform platform) {
        Installed.instance = platform;
    }

    boolean isModLoaded(String modId);

    /** Vrai pendant la génération des fichiers de données. */
    boolean isDataGen();

    /** Joueur simulé par un mod (déployeur de Create...) : il ne compte pas comme un joueur. */
    boolean isFakePlayer(Entity entity);

    /** Enregistre un objet du jeu sous le namespace du mod. Utilisable une fois les registres remplis. */
    <R, T extends R> Registered<R, T> register(ResourceKey<? extends Registry<R>> registry, String name, Supplier<T> factory);

    /** Début de la construction d'un onglet créatif. */
    CreativeModeTab.Builder creativeTabBuilder();

    /** Un objet enregistré. */
    interface Registered<R, T extends R> extends Supplier<T> {
    }

    final class Installed {
        private static volatile ExtPlatform instance;

        private Installed() {
        }
    }
}
