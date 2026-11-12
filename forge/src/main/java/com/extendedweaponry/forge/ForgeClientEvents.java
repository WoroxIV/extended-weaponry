package com.extendedweaponry.forge;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.client.ExtTooltips;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Événements côté client. */
@Mod.EventBusSubscriber(modid = ExtendedWeaponry.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeClientEvents {

    private ForgeClientEvents() {
    }

    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event) {
        ExtTooltips.addLines(event.getItemStack(), event.getToolTip());
    }
}
