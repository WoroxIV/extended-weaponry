package com.extendedweaponry.client;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.registry.ExtItems;
import com.extendedweaponry.registry.ExtTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Lignes ajoutées aux infobulles des objets du mod (et des armes de Better Weaponry dans ses matériaux), par les
 * événements d'infobulle de chaque chargeur : les infobulles de Better Weaponry elles-mêmes ne sont pas modifiées.
 */
public final class ExtTooltips {
    private ExtTooltips() {
    }

    public static void addLines(ItemStack stack, List<Component> lines) {
        // Juste sous le nom de l'objet, avant les statistiques.
        int under = Math.min(1, lines.size());
        if (stack.is(ExtTags.FRAGILE)) lines.add(under++, line("fragile", ChatFormatting.LIGHT_PURPLE));
        if (stack.is(ExtTags.SONIC)) lines.add(under, line("sonic", ChatFormatting.DARK_AQUA));
        if (stack.is(ExtItems.WARDEN_HEART.get())) lines.add(line("warden_heart", ChatFormatting.GRAY));
    }

    private static Component line(String key, ChatFormatting color) {
        return Component.translatable("tooltip." + ExtendedWeaponry.MODID + "." + key).withStyle(color);
    }
}
