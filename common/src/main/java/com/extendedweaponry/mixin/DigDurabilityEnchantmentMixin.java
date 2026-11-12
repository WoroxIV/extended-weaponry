package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Même si un objet fragile a reçu Solidité d'une autre façon, l'usure n'est jamais évitée. */
@Mixin(DigDurabilityEnchantment.class)
public abstract class DigDurabilityEnchantmentMixin {

    /** Solidité a sa propre règle d'application (tout objet qui s'use) : elle ne vaut pas pour un objet fragile. */
    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void extendedweaponry$notOnFragile(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (MaterialEffects.isFragile(stack)) cir.setReturnValue(false);
    }

    @Inject(method = "shouldIgnoreDurabilityDrop", at = @At("HEAD"), cancellable = true)
    private static void extendedweaponry$fullWear(ItemStack stack, int level, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (MaterialEffects.isFragile(stack)) cir.setReturnValue(false);
    }
}
