package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/** La table d'enchantement (et le butin enchanté au hasard) ne propose jamais Solidité ni Raccommodage pour un objet fragile. */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"), cancellable = true)
    private static void extendedweaponry$notOnFragile(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (!MaterialEffects.isFragile(stack)) return;
        List<EnchantmentInstance> results = new ArrayList<>(cir.getReturnValue());
        if (results.removeIf(instance -> MaterialEffects.protectsDurability(instance.enchantment))) cir.setReturnValue(results);
    }
}
