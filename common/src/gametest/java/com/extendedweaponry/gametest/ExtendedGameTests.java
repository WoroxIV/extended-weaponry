package com.extendedweaponry.gametest;

import com.betterweaponry.item.GearType;
import com.betterweaponry.item.WeaponMaterial;
import com.betterweaponry.item.WeaponMaterials;
import com.betterweaponry.item.WeaponType;
import com.betterweaponry.registry.ModItems;
import com.extendedweaponry.ExtendedWeaponry;
import com.extendedweaponry.combat.MaterialEffects;
import com.extendedweaponry.item.ExtendedMaterials;
import com.extendedweaponry.item.ExtendedTiers;
import com.extendedweaponry.registry.ExtCreativeTab;
import com.extendedweaponry.registry.ExtItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

/** Tests automatiques d'Extended Weaponry, lancés avec les vrais jars de Better Weaponry. */
//@LOADER_TEST_ANNOTATIONS@
public final class ExtendedGameTests {
    private static final String ARENA = "@ARENA@";

    /** Fabric crée une instance de la classe de tests. */
    public ExtendedGameTests() {
    }

    // --- Objets ---

    /** Better Weaponry a créé toutes ses armes dans les quatre matériaux, sous le namespace du mod. */
    @GameTest(template = ARENA)
    public static void betterWeaponryItemsExist(GameTestHelper helper) {
        for (WeaponMaterial material : ExtendedMaterials.ALL) {
            for (WeaponType type : WeaponType.values()) {
                ResourceLocation id = ExtendedWeaponry.id(material.itemName(type));
                helper.assertTrue(BuiltInRegistries.ITEM.containsKey(id), "missing " + id);
            }
            for (GearType gear : GearType.values()) {
                ResourceLocation id = ExtendedWeaponry.id(material.itemName(gear));
                helper.assertTrue(BuiltInRegistries.ITEM.containsKey(id), "missing " + id);
            }
        }
        helper.assertTrue(WeaponMaterials.all().containsAll(ExtendedMaterials.ALL), "Better Weaponry lists the materials");
        helper.succeed();
    }

    /** Valeurs voulues : cuivre = Copper Age, améthyste à 16 de durabilité, Warden au-dessus du netherite. */
    @GameTest(template = ARENA)
    public static void statsMatchTheDesign(GameTestHelper helper) {
        helper.assertTrue(Objects.equals(attackDamage(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.SWORD)), 5.0D), "copper sword damage" + ": " + attackDamage(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.SWORD)));
        helper.assertTrue(Objects.equals(attackDamage(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.AXE)), 9.0D), "copper axe damage" + ": " + attackDamage(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.AXE)));
        helper.assertTrue(Objects.equals(new ItemStack(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.SWORD)).getMaxDamage(), 190), "copper durability" + ": " + new ItemStack(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.SWORD)).getMaxDamage());
        helper.assertTrue(Objects.equals(new ItemStack(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.HELMET)).getMaxDamage(), 121), "copper helmet durability" + ": " + new ItemStack(ExtItems.get(ExtendedTiers.COPPER, ExtItems.Piece.HELMET)).getMaxDamage());
        helper.assertTrue(Objects.equals(new ItemStack(ExtItems.get(ExtendedTiers.EMERALD, ExtItems.Piece.SWORD)).getMaxDamage(), 320), "emerald durability" + ": " + new ItemStack(ExtItems.get(ExtendedTiers.EMERALD, ExtItems.Piece.SWORD)).getMaxDamage());
        helper.assertTrue(Objects.equals(attackDamage(ExtItems.get(ExtendedTiers.AMETHYST, ExtItems.Piece.SWORD)), 7.5D), "amethyst sword damage" + ": " + attackDamage(ExtItems.get(ExtendedTiers.AMETHYST, ExtItems.Piece.SWORD)));
        helper.assertTrue(Objects.equals(new ItemStack(ModItems.get(WeaponType.KATANA, ExtendedMaterials.AMETHYST).get()).getMaxDamage(), 16), "amethyst katana durability" + ": " + new ItemStack(ModItems.get(WeaponType.KATANA, ExtendedMaterials.AMETHYST).get()).getMaxDamage());
        helper.assertTrue(Objects.equals(new ItemStack(ModItems.shield(ExtendedMaterials.AMETHYST).get()).getMaxDamage(), 64), "amethyst shield durability" + ": " + new ItemStack(ModItems.shield(ExtendedMaterials.AMETHYST).get()).getMaxDamage());
        helper.assertTrue(ModItems.get(WeaponType.KATANA, ExtendedMaterials.WARDEN).get().isFireResistant(), "warden weapons resist fire");
        helper.assertTrue(attackDamage(ExtItems.get(ExtendedTiers.WARDEN, ExtItems.Piece.SWORD)) > 8.0D, "warden sword beats netherite");
        helper.succeed();
    }

    private static double attackDamage(Item item) {
        double total = 1.0D;
        for (AttributeModifier modifier : new ItemStack(item).getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
            total += modifier.getAmount();
        }
        return total;
    }

    // --- Cœur du Warden ---

    @GameTest(template = ARENA, timeoutTicks = 100)
    public static void wardenHeartDropsAtSixtyPercent(GameTestHelper helper) {
        killWarden(helper, new BlockPos(2, 2, 2), false, 190.0F, 310.0F, true);
    }

    @GameTest(template = ARENA, timeoutTicks = 100)
    public static void noWardenHeartBelowSixtyPercent(GameTestHelper helper) {
        killWarden(helper, new BlockPos(2, 2, 2), false, 220.0F, 280.0F, false);
    }

    /** Une explosion allumée par un joueur ne compte pas comme dégâts de joueur. */
    @GameTest(template = ARENA, timeoutTicks = 100)
    public static void explosionsAreNotPlayerDamage(GameTestHelper helper) {
        killWarden(helper, new BlockPos(2, 2, 2), true, 310.0F, 190.0F, false);
    }

    /**
     * Le Warden reçoit d'abord {@code otherDamage} (dégâts sans joueur, ou explosion d'un joueur), puis le joueur l'achève
     * à l'épée avec {@code playerDamage}.
     */
    private static void killWarden(GameTestHelper helper, BlockPos pos, boolean playerExplosion, float otherDamage, float playerDamage, boolean heartExpected) {
        Warden warden = helper.spawn(EntityType.WARDEN, pos);
        Player player = helper.makeMockPlayer();
        ServerLevel level = helper.getLevel();
        DamageSource other = playerExplosion ? source(level, DamageTypes.PLAYER_EXPLOSION, player) : level.damageSources().generic();
        warden.hurt(other, otherDamage);
        warden.invulnerableTime = 0;
        warden.hurt(level.damageSources().playerAttack(player), playerDamage);
        helper.assertTrue(warden.isDeadOrDying(), "the warden should be dead");
        helper.runAfterDelay(2, () -> {
            AABB around = new AABB(warden.blockPosition()).inflate(4.0D);
            boolean heart = level.getEntitiesOfClass(ItemEntity.class, around).stream().anyMatch(item -> item.getItem().is(ExtItems.WARDEN_HEART.get()));
            helper.assertTrue(Objects.equals(heart, heartExpected), "warden heart dropped" + ": " + heart);
            helper.succeed();
        });
    }

    private static DamageSource source(ServerLevel level, ResourceKey<DamageType> type, Player player) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), player);
    }

    // --- Améthyste ---

    @GameTest(template = ARENA)
    public static void amethystRefusesDurabilityEnchantments(GameTestHelper helper) {
        ItemStack amethyst = new ItemStack(ModItems.get(WeaponType.KATANA, ExtendedMaterials.AMETHYST).get());
        ItemStack emerald = new ItemStack(ModItems.get(WeaponType.KATANA, ExtendedMaterials.EMERALD).get());
        ItemStack helmet = new ItemStack(ExtItems.get(ExtendedTiers.AMETHYST, ExtItems.Piece.HELMET));

        helper.assertFalse(Enchantments.UNBREAKING.canEnchant(amethyst), "unbreaking on amethyst");
        helper.assertFalse(Enchantments.MENDING.canEnchant(amethyst), "mending on amethyst");
        helper.assertFalse(Enchantments.UNBREAKING.canEnchant(helmet), "unbreaking on amethyst armor");
        helper.assertTrue(Enchantments.SHARPNESS.canEnchant(amethyst), "sharpness still works on amethyst");
        helper.assertTrue(Enchantments.UNBREAKING.canEnchant(emerald), "unbreaking on emerald");
        for (EnchantmentInstance offer : EnchantmentHelper.getAvailableEnchantmentResults(30, amethyst, true)) {
            helper.assertFalse(MaterialEffects.protectsDurability(offer.enchantment), "the enchanting table offers " + offer.enchantment.getDescriptionId());
        }
        helper.succeed();
    }

    /** Même enchanté de force et marqué incassable, un objet en améthyste s'use à chaque coup. */
    @GameTest(template = ARENA)
    public static void amethystCannotBeMadeUnbreakable(GameTestHelper helper) {
        ItemStack stack = new ItemStack(ExtItems.get(ExtendedTiers.AMETHYST, ExtItems.Piece.PICKAXE));
        stack.enchant(Enchantments.UNBREAKING, 3);
        stack.getOrCreateTag().putBoolean("Unbreakable", true);
        helper.assertTrue(stack.isDamageableItem(), "an unbreakable amethyst item still wears");
        for (int i = 0; i < 20; i++) {
            helper.assertFalse(DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(stack, 3, helper.getLevel().random), "unbreaking ignored");
        }
        MaterialEffects.enforceFragility(stack);
        helper.assertFalse(stack.getOrCreateTag().contains("Unbreakable"), "the unbreakable mark is removed");
        helper.succeed();
    }

    // --- Warden ---

    @GameTest(template = ARENA)
    public static void wardenWeaponsPierceArmor(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        DamageSource hit = helper.getLevel().damageSources().playerAttack(player);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.get(WeaponType.KATANA, ExtendedMaterials.WARDEN).get()));
        helper.assertTrue(Math.abs(MaterialEffects.pierceArmor(hit, 10.0F, 4.0F) - (4.0F + 6.0F * MaterialEffects.SONIC_ARMOR_PIERCING)) < 1.0E-4F, "warden piercing");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.get(WeaponType.KATANA, WeaponMaterials.NETHERITE).get()));
        helper.assertTrue(Objects.equals(MaterialEffects.pierceArmor(hit, 10.0F, 4.0F), 4.0F), "no piercing with netherite" + ": " + MaterialEffects.pierceArmor(hit, 10.0F, 4.0F));
        helper.succeed();
    }

    // --- Recettes et onglet ---

    @GameTest(template = ARENA)
    public static void recipesAreLoaded(GameTestHelper helper) {
        for (String recipe : List.of("copper_sword", "emerald_chestplate", "amethyst_pickaxe", "emerald_katana", "copper_shield",
                "amethyst_shuriken", "warden_sword_smithing", "warden_katana_smithing", "warden_shield_smithing", "warden_upgrade_smithing_template")) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(ExtendedWeaponry.id(recipe)).isPresent(), "missing recipe " + recipe);
        }
        helper.succeed();
    }

    /** Sans Deeper and Darker : 4 sets de 9 pièces, le cœur, le modèle, et les 17 objets de Better Weaponry par matériau. */
    @GameTest(template = ARENA)
    public static void creativeTabListsEverything(GameTestHelper helper) {
        helper.assertTrue(Objects.equals(ExtCreativeTab.displayOrder().size(), 4 * 9 + 2 + 4 * 17), "items in the tab" + ": " + ExtCreativeTab.displayOrder().size());
        helper.succeed();
    }

}
