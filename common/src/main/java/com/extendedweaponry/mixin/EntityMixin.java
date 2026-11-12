package com.extendedweaponry.mixin;

import com.extendedweaponry.combat.MaterialEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Coups silencieux des armes du Warden : la créature touchée (ou tuée) n'émet pas de vibration. */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void extendedweaponry$silence(Holder<GameEvent> event, @Nullable Entity entity, CallbackInfo ci) {
        GameEvent value = event.value();
        if ((value == GameEvent.ENTITY_DAMAGE.value() || value == GameEvent.ENTITY_DIE.value()) && MaterialEffects.isSilenced((Entity) (Object) this)) {
            ci.cancel();
        }
    }
}
