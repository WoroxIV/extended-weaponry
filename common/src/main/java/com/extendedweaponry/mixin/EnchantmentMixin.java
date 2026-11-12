package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Solidité, Raccommodage (et tout enchantement qui ménage ou répare la durabilité) ne se posent pas sur un objet fragile :
 * ni table d'enchantement, ni enclume, ni butin, ni commande. NeoForge passe aussi par {@code isSupportedItem}.
 */
@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Inject(method = {"isSupportedItem", "canEnchant"}, at = @At("HEAD"), cancellable = true)
    private void extendedweaponry$notOnFragile(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (MaterialEffects.isFragile(stack) && MaterialEffects.protectsDurability((Enchantment) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
