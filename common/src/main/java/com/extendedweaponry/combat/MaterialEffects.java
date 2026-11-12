package com.extendedweaponry.combat;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.registry.ExtTags;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

/**
 * Effets propres aux matériaux, appelés par les mixins :
 * <ul>
 *   <li>améthyste ({@link ExtTags#FRAGILE}) : aucune protection de durabilité possible, éclats rendus à la casse ;</li>
 *   <li>améthyste ({@link ExtTags#RESONANT}) : Résonance, une partie des dégâts touche les ennemis proches ;</li>
 *   <li>Warden ({@link ExtTags#SONIC}) : Frappe sonique (une partie de l'armure est ignorée) et coups silencieux.</li>
 * </ul>
 */
public final class MaterialEffects {
    /** Part des dégâts reçus par la cible qui touche aussi chaque ennemi proche. */
    public static final float RESONANCE_SHARE = 0.25F;
    public static final double RESONANCE_RADIUS = 2.5D;
    /** Part de la réduction d'armure ignorée par les armes du Warden. */
    public static final float SONIC_ARMOR_PIERCING = 0.35F;

    public static final ResourceKey<DamageType> RESONANCE = ResourceKey.create(Registries.DAMAGE_TYPE, ExtendedWeaponry.id("resonance"));

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

    /**
     * Solidité, Raccommodage et tout enchantement qui ménage ou répare la durabilité ne s'appliquent pas aux objets fragiles.
     * Un datapack peut en ajouter d'autres mods au tag {@link ExtTags#PROTECTS_DURABILITY}.
     */
    public static boolean protectsDurability(Enchantment enchantment) {
        if (enchantment instanceof DigDurabilityEnchantment || enchantment instanceof MendingEnchantment) return true;
        return BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment)
                .flatMap(BuiltInRegistries.ENCHANTMENT::getHolder)
                .map(holder -> holder.is(ExtTags.PROTECTS_DURABILITY))
                .orElse(false);
    }

    /** Un objet fragile ne peut pas devenir incassable : la marque "Unbreakable" est retirée. */
    public static void enforceFragility(ItemStack stack) {
        if (!isFragile(stack)) return;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Unbreakable")) tag.remove("Unbreakable");
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

    // --- Améthyste : Résonance ---

    /**
     * Après un coup au corps à corps porté avec une arme résonante : chaque ennemi proche de la cible subit une part des
     * dégâts réellement infligés. Les dégâts de Résonance ne relancent pas la Résonance.
     */
    public static void resonate(LivingEntity target, DamageSource source, float healthLost) {
        if (healthLost <= 0.0F || !source.is(DamageTypes.PLAYER_ATTACK) && !source.is(DamageTypes.MOB_ATTACK)) return;
        if (!(source.getEntity() instanceof LivingEntity attacker) || source.getDirectEntity() != attacker) return;
        if (!attacker.getMainHandItem().is(ExtTags.RESONANT) || !(target.level() instanceof ServerLevel level)) return;

        DamageSource resonance = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(RESONANCE), attacker);
        float damage = healthLost * RESONANCE_SHARE;
        boolean hitSomething = false;
        for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(RESONANCE_RADIUS),
                entity -> isResonanceTarget(entity, target, attacker))) {
            hitSomething |= nearby.hurt(resonance, damage);
        }
        if (hitSomething) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 1.2F);
            level.sendParticles(ParticleTypes.END_ROD, target.getX(), target.getY(0.5D), target.getZ(), 8, 0.6D, 0.4D, 0.6D, 0.05D);
        }
    }

    private static boolean isResonanceTarget(LivingEntity entity, LivingEntity target, LivingEntity attacker) {
        if (entity == target || entity == attacker || !entity.isAlive() || entity.isSpectator()) return false;
        if (entity.isAlliedTo(attacker)) return false;
        return !(entity instanceof OwnableEntity pet && pet.getOwner() == attacker);
    }

    // --- Warden : Frappe sonique et silence ---

    /**
     * Coup au corps à corps porté avec une arme du Warden. En 1.20.1, une flèche ne garde pas l'arc qui l'a tirée :
     * seuls les coups directs comptent.
     */
    public static boolean isSonic(DamageSource source) {
        return source.getEntity() instanceof LivingEntity attacker && source.getDirectEntity() == attacker
                && attacker.getMainHandItem().is(ExtTags.SONIC);
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
