"""Regenerates Extended Weaponry tool/armor textures from tools/textures/templates + tools/textures/palettes.

  templates/<piece>.png         item icons, painted with the 8 colors of palettes/key.png (copper source)
  templates/armor_layer_N.png   worn armor, same key colors (gold source)
  palettes/<mat>.png            8 colors for item icons
  palettes/armor_<mat>.png      8 colors for worn armor

Run from anywhere: python tools/apply_palettes.py
Weapon textures are not made here: Better Weaponry recolors them at load time from
assets/extended_weaponry/textures/palettes/<mat>.png (7 colors, Better Weaponry key).
"""
import os
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
T = os.path.join(HERE, "textures")
ASSETS = os.path.join(HERE, "..", "common", "src", "main", "resources", "assets")
ITEMS = os.path.join(ASSETS, "extended_weaponry", "textures", "item")
PIECES = ["axe", "hoe", "pickaxe", "shovel", "sword", "boots", "chestplate", "helmet", "leggings"]
MATS = ["copper", "emerald", "amethyst"]  # warden: custom Retro Diffusion textures (tools/rd_gen.py), not palette-made


def armor_path(mat, layer):
    # 1.21.1: extended_weaponry:textures/models/armor/<mat>_layer_N.png
    # 1.20.1: minecraft:textures/models/armor/extended_weaponry_<mat>_layer_N.png
    legacy = os.path.join(ASSETS, "minecraft", "textures", "models", "armor")
    if os.path.isdir(legacy):
        return os.path.join(legacy, f"extended_weaponry_{mat}_layer_{layer}.png")
    return os.path.join(ASSETS, "extended_weaponry", "textures", "models", "armor", f"{mat}_layer_{layer}.png")


def strip(name):
    return [p[:3] for p in Image.open(os.path.join(T, "palettes", name + ".png")).convert("RGBA").getdata()]


def recolor(template, palette):
    mapping = dict(zip(strip("key"), strip(palette)))
    im = Image.open(os.path.join(T, "templates", template + ".png")).convert("RGBA")
    im.putdata([mapping.get(px[:3], px[:3]) + (px[3],) if px[3] else px for px in im.getdata()])
    return im


sheet = Image.new("RGBA", (9 * 144 + 2 * 256 + 24, len(MATS) * 144), (198, 198, 198, 255))
for r, mat in enumerate(MATS):
    for c, piece in enumerate(PIECES):
        im = recolor(piece, mat)
        im.save(os.path.join(ITEMS, f"{mat}_{piece}.png"))
        sheet.alpha_composite(im.resize((128, 128), Image.NEAREST), (c * 144 + 8, r * 144 + 8))
    for layer in (1, 2):
        im = recolor(f"armor_layer_{layer}", "armor_" + mat)
        path = armor_path(mat, layer)
        os.makedirs(os.path.dirname(path), exist_ok=True)
        im.save(path)
        sheet.alpha_composite(im.resize((256, 128), Image.NEAREST), (9 * 144 + 8 + (layer - 1) * 264, r * 144 + 8))
sheet.save(os.path.join(T, "preview.png"))
print("done:", len(MATS) * (len(PIECES) + 2), "textures")
