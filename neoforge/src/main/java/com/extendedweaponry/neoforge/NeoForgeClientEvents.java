package com.extendedweaponry.neoforge;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.client.ExtTooltips;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Événements côté client. */
@EventBusSubscriber(modid = ExtendedWeaponry.MODID, value = Dist.CLIENT)
public final class NeoForgeClientEvents {

    private NeoForgeClientEvents() {
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        ExtTooltips.addLines(event.getItemStack(), event.getToolTip());
    }
}
