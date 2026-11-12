package com.extendedweaponry.combat;

import com.extendedweaponry.item.ExtendedTiers;
import com.extendedweaponry.registry.ExtItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Équipement des créatures à leur apparition. Jamais d'améthyste ni de Warden : ce butin serait trop facile. */
public final class MobGear {
    /** Chance qu'une arme en fer de zombie soit en cuivre. */
    public static final float ZOMBIE_COPPER_CHANCE = 0.3F;
    /** Chance que la hache en fer d'un vindicateur soit en émeraude. */
    public static final float VINDICATOR_EMERALD_CHANCE = 0.1F;

    private MobGear() {
    }

    public static void zombie(Mob mob, RandomSource random) {
        ItemStack held = mob.getMainHandItem();
        if (held.is(Items.IRON_SWORD)) replace(mob, random, ZOMBIE_COPPER_CHANCE, ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.SWORD));
        else if (held.is(Items.IRON_SHOVEL)) replace(mob, random, ZOMBIE_COPPER_CHANCE, ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.SHOVEL));
    }

    public static void vindicator(Mob mob, RandomSource random) {
        if (mob.getMainHandItem().is(Items.IRON_AXE)) replace(mob, random, VINDICATOR_EMERALD_CHANCE, ExtItems.get(ExtendedTiers.EMERALD, ExtItems.Piece.AXE));
    }

    private static void replace(Mob mob, RandomSource random, float chance, Item replacement) {
        if (random.nextFloat() < chance) mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(replacement));
    }
}
