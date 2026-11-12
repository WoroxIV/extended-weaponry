package com.extendedweaponry.registry;

import com.extendedweaponry.loot.PlayerDamageShareCondition;
import com.extendedweaponry.loot.WardenGearEnabledCondition;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/** Conditions de butin du mod, utilisables dans les tables de butin et les datapacks. */
public final class ExtLootConditions {
    /** {@code extended_weaponry:player_damage_share} : part minimale des dégâts infligés par des joueurs à la créature. */
    public static final ExtPlatform.Registered<LootItemConditionType, LootItemConditionType> PLAYER_DAMAGE_SHARE = ExtPlatform.get().register(
            Registries.LOOT_CONDITION_TYPE, "player_damage_share", () -> new LootItemConditionType(new PlayerDamageShareCondition.Serializer()));
    /** {@code extended_weaponry:warden_gear_enabled} : faux quand Deeper and Darker est installé. */
    public static final ExtPlatform.Registered<LootItemConditionType, LootItemConditionType> WARDEN_GEAR_ENABLED = ExtPlatform.get().register(
            Registries.LOOT_CONDITION_TYPE, "warden_gear_enabled", () -> new LootItemConditionType(new WardenGearEnabledCondition.Serializer()));

    private ExtLootConditions() {
    }

    /** Force la déclaration des conditions. */
    public static void init() {
    }
}
