package com.extendedweaponry.fabric;

import com.extendedweaponry.client.ExtTooltips;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

/** Point d'entrée Fabric côté client. */
public final class ExtendedWeaponryFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> ExtTooltips.addLines(stack, lines));
    }
}
