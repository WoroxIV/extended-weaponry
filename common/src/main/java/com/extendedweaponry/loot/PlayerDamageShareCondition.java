package com.extendedweaponry.loot;

import com.extendedweaponry.combat.DamageLog;
import com.extendedweaponry.registry.ExtLootConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;
import java.util.Set;

/**
 * Vrai si au moins {@code min_share} (0.6 par défaut) des dégâts reçus par la créature venaient de joueurs, un ou
 * plusieurs : coups au corps à corps et projectiles tirés par un joueur. Les explosions, le feu, la magie, les épines
 * et les dégâts sans joueur (stalactite, chute, autres créatures) ne comptent pas comme dégâts de joueur.
 * Seules les créatures qui tiennent ce compte (le Warden) peuvent remplir la condition.
 */
public record PlayerDamageShareCondition(float minShare) implements LootItemCondition {
    public static final float DEFAULT_SHARE = 0.6F;
    /** "min_share" est facultatif à la lecture, mais toujours écrit : les fichiers générés montrent le seuil à modifier. */
    public static final MapCodec<PlayerDamageShareCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_share").xmap(share -> share.orElse(DEFAULT_SHARE), Optional::of)
                    .forGetter(PlayerDamageShareCondition::minShare)
    ).apply(instance, PlayerDamageShareCondition::new));

    @Override
    public LootItemConditionType getType() {
        return ExtLootConditions.PLAYER_DAMAGE_SHARE.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        return entity instanceof DamageLog log && log.extendedweaponry$playerDamageShare() >= minShare;
    }
}
