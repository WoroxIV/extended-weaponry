package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MobGear;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Les zombies armés prennent parfois une épée ou une pelle en cuivre au lieu du fer. */
@Mixin(Zombie.class)
public abstract class ZombieMixin {

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void extendedweaponry$copperGear(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        MobGear.zombie((Zombie) (Object) this, random);
    }
}
