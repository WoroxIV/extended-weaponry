# Extended Weaponry

Addon for [Better Weaponry](https://modrinth.com/mod/better-weaponry) (3.1.0 or newer). It adds four materials, each with every Better Weaponry weapon (13 weapon types, shield, bow, crossbow, shuriken) and a full vanilla-style set (sword, shovel, pickaxe, axe, hoe, helmet, chestplate, leggings, boots). That is 26 items per material, 106 items in all, in their own creative tab.

Minecraft 1.21.1 (NeoForge, Fabric) and 1.20.1 (Forge, Fabric).

## Materials

| Material | Durability | Damage bonus | Mining level | Enchantability | Obtained |
|---|---|---|---|---|---|
| Copper | 190 | +1 (same as stone) | stone | 13 | crafting table, copper ingots |
| Emerald | 320 | +2.5 | iron | 20 | crafting table, emeralds |
| Amethyst | 16 | +3.5 | diamond | 18 | crafting table, amethyst shards |
| Warden | 2800 | +5 | netherite | 18 | smithing table, from netherite |

- **Copper** uses the exact values of the copper tools and armor of Minecraft 1.21.9, with no extra mechanic. Armor: 2/4/3/1 protection, durability factor 11.
- **Emerald** is slightly more durable than iron and hits between iron and diamond. Armor: iron protection, durability factor 17, 0.5 toughness.
- **Amethyst** breaks very fast but hits harder than diamond. Armor: diamond protection, durability factor 3.
  - Unbreaking and Mending cannot be applied (table, anvil, loot, commands), and have no effect if the item gets them anyway.
  - The "Unbreakable" mark is removed, and the item always wears.
  - A broken amethyst item shatters and gives back one or two shards.
  - The shield, bow and crossbow have 64 durability, and the shuriken has 8.
- **Warden** is an upgrade of netherite.
  - Recipe: netherite item + Warden upgrade smithing template + one Warden Heart. Repaired with a Warden Heart.
  - Resists fire.
  - Pierces armor: 35% of what armor would block still goes through.
  - Silent hits: the hit (or killed) creature sends no vibration to sculk sensors.

## Warden Heart and template

- The **Warden Heart** drops from a Warden when at least 60% of the damage it took came from players: one or several, in melee or with projectiles they shot. Explosions (TNT, fireworks), fire, magic, thorns and damage without a player (dripstone, falls, other mobs) do not count as player damage. Players simulated by mods (Create deployers...) do not count either. The threshold is the `min_share` field of the `extended_weaponry:player_damage_share` loot condition, in `data/extended_weaponry/loot_table/entities/warden_heart.json`.
- The **Warden upgrade smithing template** is found in 15% of ancient city chests. It is duplicated like the netherite one: 7 netherite scraps, 1 echo shard and the template give 2 templates.
- With **Deeper and Darker** installed, the Warden gear of this mod is hidden. It is not in the tab, has no recipes, and neither the heart nor the template drops. The items stay registered, so the ones already in a world are not lost. Deeper and Darker brings its own Warden gear.

## Other content

- Zombies that spawn with an iron sword or shovel have it in copper 30% of the time. Vindicators have an emerald axe 10% of the time.
- Worn copper, emerald or amethyst gear can be found in a few chests: trial chamber corridors (dungeons in 1.20.1), big ocean ruins, village smiths, pillager outposts and shipwreck treasure.
- The Better Weaponry weapons in these materials are recognized by Combat Ascended (through the `better_weaponry:<type>s` tags), Better Combat and Epic Fight, like the other Better Weaponry weapons.
- Data packs can extend the tags `extended_weaponry:fragile` and `extended_weaponry:sonic_weapons`.
- In Better Weaponry's data files, these materials are written `extended_weaponry:copper`, `extended_weaponry:emerald`, and so on.

## How it plugs into Better Weaponry

Better Weaponry 3.1.0 has a material API (`com.betterweaponry.api.WeaponMaterialProvider`). This mod declares `com.extendedweaponry.item.ExtendedMaterials` in `neoforge.mods.toml` (`[modproperties.extended_weaponry] better_weaponry_materials`) and in `fabric.mod.json` (`better_weaponry_materials` entrypoint). Better Weaponry then creates the weapons itself under the `extended_weaponry` namespace. Their textures are made at load time from Better Weaponry's color templates and this mod's palettes (`textures/palettes/<material>.png`).

## Differences in 1.20.1

- Armor piercing and silent hits only work in melee: in 1.20.1, an arrow does not remember the bow that shot it.
- Copper gear is found in dungeons instead of trial chambers, which do not exist yet.
- Material ingredients go through the mod's own tags (`extended_weaponry:ingredients/copper`, `emerald`, `amethyst`), because Forge and Fabric name their common tags differently in 1.20.1.
- Worn armor textures are in `assets/minecraft/textures/models/armor/extended_weaponry_<material>_layer_<1|2>.png`, because the vanilla 1.20.1 renderer (used on Fabric) does not read a namespace in an armor material name.

## Development

(These commands are for the 1.21.1 project. In 1.20.1 the loader is `forge`, and there is no `runData`.)

Better Weaponry 3.1.0 must be built first in the folder set by `better_weaponry_dir` in `gradle.properties`. Its jar is used to compile, and it is copied into the `mods` folder of every run.

```
./gradlew build                     # jars in neoforge/build/libs and fabric/build/libs
./gradlew :neoforge:runData         # models, translations, tags, recipes, loot into common/src/generated/resources
./gradlew :neoforge:runGameTestServer
./gradlew :fabric:runGameTestServer
```

Translations are built from `common/src/main/lang/<language>.json`, with the same format as Better Weaponry. Weapon names reuse Better Weaponry's type names. `runData` fails if a language misses a text, a type or a material.

The 1.20.1 project (`ExtendedWeaponry-1.20.1`) has no data generator. Its data is converted from this project with `python tools/convert_data_1_20.py ../ExtendedWeaponry-1.21.1`.
