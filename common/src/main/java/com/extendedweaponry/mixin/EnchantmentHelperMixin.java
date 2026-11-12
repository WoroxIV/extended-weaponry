package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Même si un objet fragile a reçu Solidité d'une autre façon, l'usure n'est jamais réduite. */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "processDurabilityChange", at = @At("HEAD"), cancellable = true)
    private static void extendedweaponry$fullWear(ServerLevel level, ItemStack stack, int damage, CallbackInfoReturnable<Integer> cir) {
        if (MaterialEffects.isFragile(stack)) cir.setReturnValue(damage);
    }
}
