package com.extendedweaponry.combat;

import com.extendedweaponry.registry.ExtTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

/**
 * Effets propres aux matériaux, appelés par les mixins :
 * <ul>
 *   <li>améthyste ({@link ExtTags#FRAGILE}) : aucune protection de durabilité possible, éclats rendus à la casse ;</li>
 *   <li>Warden ({@link ExtTags#SONIC}) : Frappe sonique (une partie de l'armure est ignorée) et coups silencieux.</li>
 * </ul>
 */
public final class MaterialEffects {
    /** Part de la réduction d'armure ignorée par les armes du Warden. */
    public static final float SONIC_ARMOR_PIERCING = 0.35F;

    /** Vrai pendant les dégâts d'un coup silencieux : l'événement de dégâts n'est alors pas émis (capteurs de sculk). */
    private static final ThreadLocal<Boolean> SILENT_HIT = ThreadLocal.withInitial(() -> false);

    private MaterialEffects() {
    }

    // --- Améthyste : fragilité ---

    public static boolean isFragile(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ExtTags.FRAGILE);
    }

    public static boolean isFragile(Item item) {
        return item.builtInRegistryHolder().is(ExtTags.FRAGILE);
    }

    /** Solidité, Raccommodage et tout enchantement qui ménage ou répare la durabilité ne s'appliquent pas aux objets fragiles. */
    public static boolean protectsDurability(Enchantment enchantment) {
        return enchantment.effects().has(EnchantmentEffectComponents.ITEM_DAMAGE)
                || enchantment.effects().has(EnchantmentEffectComponents.REPAIR_WITH_XP);
    }

    /**
     * Un objet fragile ne peut pas devenir incassable : on retire le composant "incassable" et on rétablit sa durabilité
     * maximale d'origine si quelque chose l'a changée.
     */
    public static void enforceFragility(ItemStack stack) {
        if (!isFragile(stack)) return;
        if (stack.has(DataComponents.UNBREAKABLE)) stack.remove(DataComponents.UNBREAKABLE);
        Integer original = stack.getItem().components().get(DataComponents.MAX_DAMAGE);
        if (original != null && !original.equals(stack.get(DataComponents.MAX_DAMAGE))) {
            stack.set(DataComponents.MAX_DAMAGE, original);
            if (stack.getDamageValue() >= original) stack.setDamageValue(original - 1);
        }
    }

    /** Un objet en améthyste qui casse vole en éclats : son de cristal et un ou deux éclats d'améthyste rendus. */
    public static void onFragileBroken(LivingEntity entity, Item item) {
        if (!isFragile(item) || !(entity.level() instanceof ServerLevel level)) return;
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, entity.getSoundSource(), 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.AMETHYST_CLUSTER.defaultBlockState()),
                entity.getX(), entity.getY(0.6D), entity.getZ(), 20, 0.3D, 0.3D, 0.3D, 0.1D);
        ItemEntity shards = new ItemEntity(level, entity.getX(), entity.getY(0.5D), entity.getZ(), new ItemStack(Items.AMETHYST_SHARD, 1 + level.random.nextInt(2)));
        shards.setDefaultPickUpDelay();
        level.addFreshEntity(shards);
    }

    // --- Warden : Frappe sonique et silence ---

    /** L'arme qui porte ce coup (au corps à corps, ou celle qui a tiré le projectile) est une arme du Warden. */
    public static boolean isSonic(DamageSource source) {
        ItemStack weapon = source.getWeaponItem();
        return weapon != null && weapon.is(ExtTags.SONIC) && source.getEntity() != null;
    }

    /** Dégâts après l'armure : une partie de ce que l'armure a retiré est rendue pour les armes du Warden. */
    public static float pierceArmor(DamageSource source, float beforeArmor, float afterArmor) {
        if (afterArmor >= beforeArmor || !isSonic(source)) return afterArmor;
        return afterArmor + (beforeArmor - afterArmor) * SONIC_ARMOR_PIERCING;
    }

    /** Marque le début des dégâts d'un coup ; renvoie l'état précédent, à rendre à {@link #endHit(boolean)}. */
    public static boolean startHit(DamageSource source) {
        boolean previous = SILENT_HIT.get();
        SILENT_HIT.set(isSonic(source));
        return previous;
    }

    public static void endHit(boolean previous) {
        SILENT_HIT.set(previous);
    }

    /** Pendant un coup silencieux, la créature touchée n'émet pas l'événement de dégâts que perçoivent les capteurs de sculk. */
    public static boolean isSilenced(@Nullable Entity emitter) {
        return emitter instanceof LivingEntity && SILENT_HIT.get();
    }
}
