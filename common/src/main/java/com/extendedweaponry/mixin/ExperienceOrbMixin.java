package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

/**
 * Raccommodage ne choisit jamais un objet fragile : l'expérience va aux autres objets réparables ou au joueur, au lieu
 * d'être perdue.
 */
@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {

    @ModifyArg(method = "repairPlayerItems", index = 2, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRandomItemWith(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/Map$Entry;"))
    private Predicate<ItemStack> extendedweaponry$skipFragile(Predicate<ItemStack> filter) {
        return filter.and(stack -> !MaterialEffects.isFragile(stack));
    }
}
