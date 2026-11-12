package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.DamageLog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Le Warden compte les dégâts reçus, et la part venue des joueurs : elle décide si son cœur tombe. Sauvegardé avec lui. */
@Mixin(Warden.class)
public abstract class WardenMixin implements DamageLog {
    @Unique
    private static final String PLAYER_DAMAGE_KEY = "extended_weaponry:player_damage";
    @Unique
    private static final String TOTAL_DAMAGE_KEY = "extended_weaponry:total_damage";

    @Unique
    private float extendedweaponry$playerDamage;
    @Unique
    private float extendedweaponry$totalDamage;

    @Override
    public void extendedweaponry$record(DamageSource source, float damage) {
        if (!(damage > 0.0F)) return;
        extendedweaponry$totalDamage += damage;
        if (DamageLog.isPlayerDamage(source)) extendedweaponry$playerDamage += damage;
    }

    @Override
    public float extendedweaponry$playerDamageShare() {
        return extendedweaponry$totalDamage <= 0.0F ? 0.0F : extendedweaponry$playerDamage / extendedweaponry$totalDamage;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void extendedweaponry$save(CompoundTag tag, CallbackInfo ci) {
        if (extendedweaponry$totalDamage <= 0.0F) return;
        tag.putFloat(PLAYER_DAMAGE_KEY, extendedweaponry$playerDamage);
        tag.putFloat(TOTAL_DAMAGE_KEY, extendedweaponry$totalDamage);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void extendedweaponry$load(CompoundTag tag, CallbackInfo ci) {
        extendedweaponry$playerDamage = tag.getFloat(PLAYER_DAMAGE_KEY);
        extendedweaponry$totalDamage = tag.getFloat(TOTAL_DAMAGE_KEY);
    }
}
