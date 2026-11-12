"""Retro Diffusion img2img on 16x16 netherite textures -> echo/warden versions.

usage: python rd_gen.py <out_dir> <strength> <seed> <name>=<path> [...]
Writes <out_dir>/<name>_raw.png (RD output) and <name>.png (silhouette = base alpha).
"""
import base64, io, json, os, sys, time, uuid, urllib.request
from PIL import Image

KEY = open(os.path.expanduser("~/.config/retro-diffusion/api_key")).read().strip()
API = "https://api.retrodiffusion.ai/v2"
STYLE = os.environ.get("RD_STYLE", "rd_plus__mc_item")

PROMPT = ("{item} made of dark echo metal from the deep dark, deep navy and black teal metal with "
          "glowing cyan sculk veins and echo shard crystals, warden themed, minecraft item")


def call(method, path, body=None):
    req = urllib.request.Request(API + path, method=method,
                                 data=json.dumps(body).encode() if body else None,
                                 headers={"X-RD-Token": KEY, "Content-Type": "application/json",
                                          **({"Idempotency-Key": str(uuid.uuid4())} if body else {})})
    with urllib.request.urlopen(req, timeout=120) as r:
        return json.load(r)


def b64(im):
    buf = io.BytesIO(); im.save(buf, "PNG"); return base64.b64encode(buf.getvalue()).decode()


RAMP = [(5, 8, 12), (10, 18, 26), (14, 30, 40), (16, 44, 56), (14, 62, 74), (8, 92, 104), (20, 150, 160), (60, 220, 225)]


def lum(c):
    return 0.299 * c[0] + 0.587 * c[1] + 0.114 * c[2]


KEYP = "C:/Users/thema/Desktop/Better Weaponry/BetterWeaponry-1.21.1/common/src/main/resources/assets/better_weaponry/textures/palettes/key.png"


def material_mask(base, template):
    """True where the pixel is weapon material (template colour is one of BW's key colours)."""
    if not template:
        return [p[3] > 0 for p in base.getdata()]
    key = {p[:3] for p in Image.open(KEYP).convert("RGBA").getdata() if p[3]}
    t = Image.open(template).convert("RGBA")
    return [tp[3] > 0 and tp[:3] in key and bp[3] > 0 for tp, bp in zip(t.getdata(), base.getdata())]


MASK = None


def prerecolor(base):
    """Map netherite colours by luminance rank onto the echo ramp (material pixels only)."""
    mask = MASK or [p[3] > 0 for p in base.getdata()]
    cols = sorted({p[:3] for p, k in zip(base.getdata(), mask) if k}, key=lum)
    lo, hi = lum(cols[0]), lum(cols[-1])
    m = {}
    for c in cols:
        t = (lum(c) - lo) / (hi - lo or 1)
        m[c] = RAMP[min(int(t * (len(RAMP) - 1) + 0.5), len(RAMP) - 1)]
    im = base.copy()
    im.putdata([m[p[:3]] + (p[3],) if k else p for p, k in zip(base.getdata(), mask)])
    return im


def palette_img():
    im = Image.new("RGB", (len(RAMP), 1)); im.putdata(RAMP); return im


def generate(base, item, strength, seed, w, h):
    base = prerecolor(base)
    flat = Image.new("RGB", base.size, (255, 255, 255))
    flat.paste(base, mask=base.split()[3])
    body = {"prompt": PROMPT.format(item=item), "prompt_style": STYLE, "width": w, "height": h,
            "num_images": 1, "seed": seed, "input_image": b64(flat), "strength": strength, "remove_bg": True, **({"input_palette": b64(palette_img())} if os.environ.get("RD_PAL") else {})}
    task = call("POST", "/inferences", body)["task_id"]
    while True:
        time.sleep(2)
        t = call("GET", f"/inferences/tasks/{task}")
        if t["status"] == "succeeded":
            res = t["result"]
            print(f"  cost {res.get('balance_cost')} remaining {res.get('remaining_balance')}", flush=True)
            return Image.open(io.BytesIO(base64.b64decode(res["base64_images"][0]))).convert("RGBA")
        if t["status"] == "failed":
            raise RuntimeError(json.dumps(t))


def main():
    out, strength, seed = sys.argv[1], float(sys.argv[2]), int(sys.argv[3])
    os.makedirs(out, exist_ok=True)
    for arg in sys.argv[4:]:
        name, path = arg.split("=", 1)
        path, _, template = path.partition("|")
        base = Image.open(path).convert("RGBA")
        global MASK
        MASK = material_mask(base, template)
        item = os.environ.get("RD_ITEM") or name.replace("_", " ")
        print(name, flush=True)
        raw = generate(base, item, strength, seed, *base.size)
        raw = raw.resize(base.size, Image.NEAREST)
        prerecolor(base).save(os.path.join(out, name + "_pre.png"))
        raw.save(os.path.join(out, name + "_raw.png"))
        # keep the exact netherite silhouette: alpha from base, colour from RD (fallback: base colour)
        px = []
        for b, r, k in zip(base.getdata(), raw.getdata(), MASK):
            if b[3] == 0:
                px.append((0, 0, 0, 0))
            elif not k:
                px.append(b)
            else:
                px.append((r[:3] if r[3] else b[:3]) + (b[3],))
        fin = Image.new("RGBA", base.size); fin.putdata(px)
        fin.save(os.path.join(out, name + ".png"))


if __name__ == "__main__":
    main()
