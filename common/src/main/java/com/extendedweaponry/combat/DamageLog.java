package com.extendedweaponry.combat;

import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

/**
 * Compte des dégâts reçus par une créature, séparant ceux des joueurs. Ajouté au Warden par mixin, sauvegardé avec lui :
 * c'est lui qui décide si le cœur du Warden tombe.
 */
public interface DamageLog {

    /** Ajoute des dégâts réellement retirés à la vie de la créature (sans ce qui dépasse sa vie restante). */
    void extendedweaponry$record(DamageSource source, float damage);

    /** Part des dégâts reçus qui venaient de joueurs, entre 0 et 1 (0 si elle n'a rien reçu). */
    float extendedweaponry$playerDamageShare();

    /**
     * Dégâts infligés par un joueur, au corps à corps ou avec un projectile qu'il a tiré (flèche, trident, shuriken...).
     * Explosions (TNT, fusées), feu, magie et épines ne comptent pas, même déclenchés par un joueur ; les joueurs simulés
     * par les mods non plus.
     */
    static boolean isPlayerDamage(DamageSource source) {
        Entity attacker = source.getEntity();
        if (!(attacker instanceof Player) || ExtPlatform.get().isFakePlayer(attacker)) return false;
        if (source.is(DamageTypeTags.IS_EXPLOSION) || source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.WITCH_RESISTANT_TO)
                || source.is(DamageTypes.THORNS)) {
            return false;
        }
        Entity direct = source.getDirectEntity();
        return direct == attacker || direct instanceof Projectile;
    }
}
