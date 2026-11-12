package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Effets des matériaux sur les créatures : éclats d'améthyste, Résonance, Frappe sonique et coups silencieux. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /** Un objet porté qui casse (il est encore dans son emplacement à ce moment) : s'il est en améthyste, il vole en éclats. */
    @Inject(method = "broadcastBreakEvent(Lnet/minecraft/world/entity/EquipmentSlot;)V", at = @At("HEAD"))
    private void extendedweaponry$shatter(EquipmentSlot slot, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        MaterialEffects.onFragileBroken(self, self.getItemBySlot(slot).getItem());
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"))
    private void extendedweaponry$rememberDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir,
                                                 @Share("beforeArmor") LocalFloatRef beforeArmor) {
        beforeArmor.set(amount);
    }

    /** Frappe sonique : une partie de ce que l'armure aurait retiré passe quand même. */
    @ModifyReturnValue(method = "getDamageAfterArmorAbsorb", at = @At("RETURN"))
    private float extendedweaponry$pierceArmor(float afterArmor, @Local(argsOnly = true) DamageSource source,
                                               @Share("beforeArmor") LocalFloatRef beforeArmor) {
        return MaterialEffects.pierceArmor(source, beforeArmor.get(), afterArmor);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void extendedweaponry$startHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir,
                                           @Share("health") LocalFloatRef health, @Share("silent") LocalBooleanRef previousSilence) {
        health.set(((LivingEntity) (Object) this).getHealth());
        previousSilence.set(MaterialEffects.startHit(source));
    }

    /** Résonance, d'après la vie réellement perdue par la cible. */
    @Inject(method = "hurt", at = @At("RETURN"))
    private void extendedweaponry$endHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir,
                                         @Share("health") LocalFloatRef health, @Share("silent") LocalBooleanRef previousSilence) {
        MaterialEffects.endHit(previousSilence.get());
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide() && cir.getReturnValueZ()) {
            MaterialEffects.resonate(self, source, health.get() - self.getHealth());
        }
    }
}
