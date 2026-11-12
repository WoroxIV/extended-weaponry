package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.DamageLog;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Le jeu note ici chaque dégât qui retire de la vie, juste avant de la retirer (et avant la mort, donc avant le butin) :
 * c'est le bon endroit pour le compte du Warden. Ce qui dépasse la vie restante n'est pas compté.
 */
@Mixin(CombatTracker.class)
public abstract class CombatTrackerMixin {
    @Shadow
    @Final
    private LivingEntity mob;

    @Inject(method = "recordDamage", at = @At("HEAD"))
    private void extendedweaponry$logDamage(DamageSource source, float damage, CallbackInfo ci) {
        if (mob instanceof DamageLog log && !mob.level().isClientSide()) {
            log.extendedweaponry$record(source, Math.min(damage, mob.getHealth()));
        }
    }
}
