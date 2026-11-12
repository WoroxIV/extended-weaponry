package com.extendedweaponry.loot;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.registry.ExtLootConditions;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/** Faux quand Deeper and Darker est installé : son propre équipement du Warden remplace celui de ce mod. */
public final class WardenGearEnabledCondition implements LootItemCondition {
    public static final WardenGearEnabledCondition INSTANCE = new WardenGearEnabledCondition();
    public static final MapCodec<WardenGearEnabledCondition> CODEC = MapCodec.unit(INSTANCE);

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
}
