package com.extendedweaponry.registry;

import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.platform.ExtPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Les armures des quatre matériaux. Références vanilla (facteur de durabilité / protection casque-plastron-jambières-bottes /
 * robustesse / enchantabilité) : fer 15 / 2-6-5-2 / 0 / 9, diamant 33 / 3-8-6-3 / 2 / 10, netherite 37 / 3-8-6-3 / 3 / 15.
 */
public enum ExtArmorMaterials {
    /** Valeurs exactes de l'armure en cuivre de Minecraft 1.21.9. */
    COPPER(11, 2, 4, 3, 1, 4, 8, () -> SoundEvents.ARMOR_EQUIP_IRON, ExtTags.COPPER_INGOTS, 0.0F, 0.0F, false),
    /** Protection du fer, un peu plus durable, un peu de robustesse, très enchantable. */
    EMERALD(17, 2, 6, 5, 2, 5, 20, () -> SoundEvents.ARMOR_EQUIP_DIAMOND, ExtTags.EMERALD_GEMS, 0.5F, 0.0F, false),
    /** Protection du diamant, mais très fragile (environ 3 fois moins durable que l'or). */
    AMETHYST(3, 3, 8, 6, 3, 11, 18, () -> BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_BLOCK_CHIME), ExtTags.AMETHYST_GEMS, 2.0F, 0.0F, false),
    /** Au-dessus du netherite. */
    WARDEN(42, 3, 8, 6, 3, 11, 18, () -> SoundEvents.ARMOR_EQUIP_NETHERITE, ExtTags.WARDEN_REPAIR, 4.0F, 0.15F, true);

    private final int durabilityMultiplier;
    private final boolean fireResistant;
    private final ExtPlatform.Registered<ArmorMaterial, ArmorMaterial> material;

    ExtArmorMaterials(int durabilityMultiplier, int helmet, int chestplate, int leggings, int boots, int body, int enchantability,
                      Supplier<Holder<SoundEvent>> sound, TagKey<Item> repair, float toughness, float knockbackResistance, boolean fireResistant) {
        this.durabilityMultiplier = durabilityMultiplier;
        this.fireResistant = fireResistant;
        String name = name().toLowerCase(Locale.ROOT);
        this.material = ExtPlatform.get().register(Registries.ARMOR_MATERIAL, name, () -> {
            Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
            defense.put(ArmorItem.Type.HELMET, helmet);
            defense.put(ArmorItem.Type.CHESTPLATE, chestplate);
            defense.put(ArmorItem.Type.LEGGINGS, leggings);
            defense.put(ArmorItem.Type.BOOTS, boots);
            defense.put(ArmorItem.Type.BODY, body);
            // Textures : textures/models/armor/<matériau>_layer_1.png et _layer_2.png dans le namespace du mod.
            return new ArmorMaterial(defense, enchantability, sound.get(), () -> Ingredient.of(repair),
                    List.of(new ArmorMaterial.Layer(ExtendedWeaponry.id(name))), toughness, knockbackResistance);
        });
    }

    /** Force la déclaration des matériaux d'armure. */
    public static void init() {
    }

    public Holder<ArmorMaterial> holder() {
        return material.holder();
    }

    public int durability(ArmorItem.Type type) {
        return type.getDurability(durabilityMultiplier);
    }

    public boolean isFireResistant() {
        return fireResistant;
    }
}
