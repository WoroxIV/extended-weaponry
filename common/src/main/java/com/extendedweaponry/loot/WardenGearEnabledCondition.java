package com.extendedweaponry.loot;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.registry.ExtLootConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/** Faux quand Deeper and Darker est installé : son propre équipement du Warden remplace celui de ce mod. */
public final class WardenGearEnabledCondition implements LootItemCondition {
    public static final WardenGearEnabledCondition INSTANCE = new WardenGearEnabledCondition();

    private WardenGearEnabledCondition() {
    }

    @Override
    public LootItemConditionType getType() {
        return ExtLootConditions.WARDEN_GEAR_ENABLED.get();
    }

    @Override
    public boolean test(LootContext context) {
        return ExtendedWeaponry.wardenGearEnabled();
    }

    public static final class Serializer implements net.minecraft.world.level.storage.loot.Serializer<WardenGearEnabledCondition> {
        @Override
        public void serialize(JsonObject json, WardenGearEnabledCondition condition, JsonSerializationContext context) {
        }

        @Override
        public WardenGearEnabledCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return INSTANCE;
        }
    }
}
