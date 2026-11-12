package com.extendedweaponry.loot;

import com.extendedweaponry.combat.DamageLog;
import com.extendedweaponry.registry.ExtLootConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

/**
 * Vrai si au moins {@code min_share} (0.6 par défaut) des dégâts reçus par la créature venaient de joueurs, un ou
 * plusieurs : coups au corps à corps et projectiles tirés par un joueur. Les explosions, le feu, la magie, les épines
 * et les dégâts sans joueur (stalactite, chute, autres créatures) ne comptent pas comme dégâts de joueur.
 * Seules les créatures qui tiennent ce compte (le Warden) peuvent remplir la condition.
 */
public record PlayerDamageShareCondition(float minShare) implements LootItemCondition {
    public static final float DEFAULT_SHARE = 0.6F;

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

    /** "min_share" est facultatif à la lecture, mais toujours écrit. */
    public static final class Serializer implements net.minecraft.world.level.storage.loot.Serializer<PlayerDamageShareCondition> {
        @Override
        public void serialize(JsonObject json, PlayerDamageShareCondition condition, JsonSerializationContext context) {
            json.addProperty("min_share", condition.minShare());
        }

        @Override
        public PlayerDamageShareCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new PlayerDamageShareCondition(Mth.clamp(GsonHelper.getAsFloat(json, "min_share", DEFAULT_SHARE), 0.0F, 1.0F));
        }
    }
}
