package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MobGear;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.monster.Vindicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Les vindicateurs, marchands d'émeraudes déchus, portent parfois une hache en émeraude. */
@Mixin(Vindicator.class)
public abstract class VindicatorMixin {

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void extendedweaponry$emeraldAxe(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        MobGear.vindicator((Vindicator) (Object) this, random);
    }
}
