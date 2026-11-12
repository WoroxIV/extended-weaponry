package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Un objet fragile s'use toujours, même marqué "incassable" (commande, autre mod). */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    private void extendedweaponry$alwaysWears(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (MaterialEffects.isFragile(self)) cir.setReturnValue(self.has(DataComponents.MAX_DAMAGE) && self.has(DataComponents.DAMAGE));
    }

    /** Dans l'inventaire d'un joueur, ce qui le rendait incassable est retiré (et sa durabilité maximale rétablie). */
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void extendedweaponry$enforceFragility(Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (!level.isClientSide()) MaterialEffects.enforceFragility((ItemStack) (Object) this);
    }
}
