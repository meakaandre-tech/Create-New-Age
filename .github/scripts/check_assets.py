#!/usr/bin/env python3
"""Fails when a Create: New Age block or item would render with a missing model or texture.

Checks every registered block and item, every blockstate and item definition in the
create_new_age namespace and each model they use (with its parents' texture variables resolved), the partial models and connected-texture sprites named in Java, and the
wire textures. References into the create namespace are checked against the Create Fly jar when one
is given; minecraft references are taken as present.

Usage: check_assets.py [--create-jar libs/create-fly-26.3.jar]
"""
import argparse
import json
import os
import re
import struct
import sys
import zipfile

NS = "create_new_age"
ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
ASSETS = os.path.join(ROOT, "src/main/resources/assets", NS)
JAVA = os.path.join(ROOT, "src/main/java/org/antarcticgardens/cna")

# Texture variables the common vanilla parents read; a child that leaves one unset renders it missing.
VANILLA_PARENT_VARS = {
    "minecraft:block/cube_all": {"all"},
    "minecraft:block/cube_column": {"end", "side"},
    "minecraft:block/cube_column_horizontal": {"end", "side"},
    "minecraft:block/cube_bottom_top": {"top", "bottom", "side"},
    "minecraft:block/orientable": {"top", "front", "side"},
    "minecraft:block/cube": {"down", "up", "north", "south", "east", "west"},
    "minecraft:block/cross": {"cross"},
    "minecraft:item/generated": {"layer0"},
    "minecraft:item/handheld": {"layer0"},
}

# Create Fly loads connected textures as single tiles <name>_connected/<i>.png, i from 1 to size - 1
# (tile 0 is the block's own texture), not as one sheet.
CT_TILE_COUNT = {"OMNIDIRECTIONAL": 47}

errors = []


def err(msg):
    if msg not in errors:
        errors.append(msg)


def rel(p):
    return os.path.relpath(p, ROOT)


def split_id(ref, default_ns="minecraft"):
    if ":" in ref:
        return tuple(ref.split(":", 1))
    return default_ns, ref


def png_size(path):
    with open(path, "rb") as f:
        head = f.read(24)
    if head[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    return struct.unpack(">II", head[16:24])


class CreateJar:
    def __init__(self, path):
        self.names = set(zipfile.ZipFile(path).namelist()) if path else None
        self.zip = zipfile.ZipFile(path) if path else None

    def has(self, path):
        return self.names is None or path in self.names

    def json(self, path):
        if self.zip is None or path not in self.names:
            return None
        return json.loads(self.zip.read(path))


def load_json(path):
    try:
        with open(path, encoding="utf-8") as f:
            return json.load(f)
    except (OSError, ValueError) as e:
        err(f"{rel(path)}: unreadable JSON ({e})")
        return None


def registered_ids():
    with open(os.path.join(JAVA, "CNABlocks.java"), encoding="utf-8") as f:
        blocks_src = f.read()
    with open(os.path.join(JAVA, "CNAItems.java"), encoding="utf-8") as f:
        items_src = f.read()
    blocks, items = [], []
    for m in re.finditer(r'=\s*block\(\s*"([a-z0-9_/]+)"', blocks_src):
        call_end = blocks_src.index(");", m.end())
        blocks.append(m.group(1))
        if not re.search(r",\s*null\s*$", blocks_src[m.end():call_end]):
            items.append(m.group(1))
    items += re.findall(r'=\s*(?:item|hidden)\(\s*"([a-z0-9_/]+)"', items_src)
    if not blocks or not items:
        err("could not read the block and item registrations from CNABlocks.java / CNAItems.java")
    return blocks, items


def collect_models(node, out):
    """Every model id in a blockstate or item definition, at any depth."""
    if isinstance(node, dict):
        for k, v in node.items():
            if k in ("model", "base") and isinstance(v, str):
                out.add(v)
            else:
                collect_models(v, out)
    elif isinstance(node, list):
        for v in node:
            collect_models(v, out)


class Models:
    def __init__(self, jar):
        self.jar = jar
        self.checked = {}

    def path(self, ref):
        ns, p = split_id(ref)
        if ns == NS:
            return os.path.join(ASSETS, "models", p + ".json")
        return f"assets/{ns}/models/{p}.json"

    def load(self, ref):
        ns, _ = split_id(ref)
        if ns == NS:
            p = self.path(ref)
            return load_json(p) if os.path.isfile(p) else None
        if ns == "create":
            return self.jar.json(self.path(ref))
        return None

    def exists(self, ref):
        ns, _ = split_id(ref)
        if ns == NS:
            return os.path.isfile(self.path(ref))
        if ns == "create":
            return self.jar.has(self.path(ref))
        return True

    def chain(self, ref, where):
        """The model and its parents, child first, until the chain leaves what can be read."""
        out, seen = [], set()
        while ref and ref not in seen:
            seen.add(ref)
            data = self.load(ref)
            if data is None:
                return out, ref
            out.append((ref, data))
            parent = data.get("parent")
            if parent is None:
                return out, None
            if split_id(parent) == ("minecraft", "builtin/generated") or parent.startswith("builtin/"):
                return out, None
            ref = parent if ":" in parent else "minecraft:" + parent
            if not self.exists(ref):
                err(f"{where}: parent model {parent} does not exist")
                return out, None
        return out, None

    def check(self, ref, where):
        ref = ref if ":" in ref else "minecraft:" + ref
        if ref in self.checked:
            return
        self.checked[ref] = True
        if not self.exists(ref):
            err(f"{where}: model {ref} does not exist")
            return
        ns, _ = split_id(ref)
        if ns != NS:
            return
        here = rel(self.path(ref))
        chain, external_parent = self.chain(ref, here)
        textures = {}
        faces = []
        for _, data in reversed(chain):
            textures.update(data.get("textures", {}))
        for _, data in chain:
            if "elements" in data:
                for el in data["elements"]:
                    for face in el.get("faces", {}).values():
                        if "texture" in face:
                            faces.append(face["texture"])
                break
        for name, value in textures.items():
            check_texture_value(value, textures, f"{here}: texture '{name}'", self.jar)
        for t in faces:
            check_texture_value(t, textures, f"{here}: element face", self.jar)
        if external_parent:
            required = VANILLA_PARENT_VARS.get(external_parent, set())
            for var in sorted(required - textures.keys()):
                err(f"{here}: parent {external_parent} needs texture '{var}', which is never set")


def check_texture_value(value, textures, where, jar, depth=0):
    if isinstance(value, dict):
        value = value.get("sprite", "")
    if value.startswith("#"):
        key = value[1:]
        if key not in textures or depth > 16:
            err(f"{where}: references #{key}, which is never set")
            return
        check_texture_value(textures[key], textures, where, jar, depth + 1)
        return
    check_texture(value, where, jar)


def check_texture(ref, where, jar):
    ns, p = split_id(ref)
    if ns == NS:
        f = os.path.join(ASSETS, "textures", p + ".png")
        if not os.path.isfile(f):
            err(f"{where}: texture {ref} does not exist ({rel(f)})")
        elif png_size(f) is None:
            err(f"{where}: texture {ref} is not a PNG")
    elif ns == "create" and not jar.has(f"assets/create/textures/{p}.png"):
        err(f"{where}: texture {ref} is not in Create Fly")


def check_ct_sprites(jar):
    src_path = os.path.join(JAVA, "client/CNASpriteShifts.java")
    with open(src_path, encoding="utf-8") as f:
        src = f.read()
    entries = [("OMNIDIRECTIONAL", n) for n in re.findall(r'\bomni\("([a-z0-9_]+)"\)', src)]
    entries += re.findall(r'getCT\(\s*AllCTTypes\.([A-Z_]+)\s*,\s*"([a-z0-9_]+)"\)', src)
    if jar.names is not None:
        # Confirm the tile layout against one of Create's own omnidirectional casings.
        tiles = [n for n in jar.names if re.fullmatch(r"assets/create/textures/block/andesite_casing_connected/\d+\.png", n)]
        if tiles and len(tiles) + 1 != CT_TILE_COUNT["OMNIDIRECTIONAL"]:
            err(f"Create Fly's omnidirectional casing has {len(tiles)} tiles; this check expects "
                f"{CT_TILE_COUNT['OMNIDIRECTIONAL'] - 1}. Update CT_TILE_COUNT.")
    for ct_type, name in entries:
        where = f"{rel(src_path)}: connected texture '{name}'"
        if ct_type not in CT_TILE_COUNT:
            err(f"{where}: type {ct_type} has no known tile count in check_assets.py")
            continue
        check_texture(f"{NS}:block/{name}", where, jar)
        folder = os.path.join(ASSETS, "textures/block", name + "_connected")
        for i in range(1, CT_TILE_COUNT[ct_type]):
            tile = os.path.join(folder, f"{i}.png")
            if not os.path.isfile(tile):
                err(f"{where}: tile {rel(tile)} does not exist")
            elif png_size(tile) != png_size(os.path.join(ASSETS, "textures/block", name + ".png")):
                err(f"{where}: tile {rel(tile)} is {png_size(tile)}, not the size of block/{name}")


def check_partials(models):
    for root, _, files in os.walk(JAVA):
        for fn in files:
            if not fn.endswith(".java"):
                continue
            p = os.path.join(root, fn)
            with open(p, encoding="utf-8") as f:
                src = f.read()
            for path in re.findall(r'PartialModel\.of\(\s*CreateNewAge\.asResource\("([^"]+)"\)', src):
                models.check(f"{NS}:{path}", f"{rel(p)}: partial model")
            for path in re.findall(r'asResource\("(textures/[^"]+\.png)"\)', src):
                if not os.path.isfile(os.path.join(ASSETS, path)):
                    err(f"{rel(p)}: texture {NS}:{path} does not exist")


def check_wires():
    p = os.path.join(JAVA, "content/electricity/wire/WireType.java")
    with open(p, encoding="utf-8") as f:
        src = f.read()
    body = src[src.index("{", src.index("enum WireType")) + 1:]
    body = body[:body.index(";")]
    for name in re.findall(r"^\s*([A-Z][A-Z0-9_]*)\s*\(", body, re.M):
        tex = os.path.join(ASSETS, "textures/wire", name.lower() + ".png")
        if not os.path.isfile(tex):
            err(f"{rel(p)}: wire texture {rel(tex)} does not exist")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--create-jar", help="Create Fly jar, to check references into the create namespace")
    args = ap.parse_args()
    jar = CreateJar(args.create_jar)
    models = Models(jar)

    blocks, items = registered_ids()
    for b in blocks:
        p = os.path.join(ASSETS, "blockstates", b + ".json")
        if not os.path.isfile(p):
            err(f"block {NS}:{b} has no blockstate ({rel(p)})")
    for i in items:
        p = os.path.join(ASSETS, "items", i + ".json")
        if not os.path.isfile(p):
            err(f"item {NS}:{i} has no item definition ({rel(p)})")

    for kind in ("blockstates", "items"):
        d = os.path.join(ASSETS, kind)
        for fn in sorted(os.listdir(d)):
            if not fn.endswith(".json"):
                continue
            data = load_json(os.path.join(d, fn))
            refs = set()
            collect_models(data, refs)
            if not refs:
                err(f"{rel(os.path.join(d, fn))}: names no model")
            for ref in sorted(refs):
                models.check(ref, rel(os.path.join(d, fn)))

    check_ct_sprites(jar)
    check_partials(models)
    check_wires()

    if errors:
        print(f"{len(errors)} missing model or texture problem(s):")
        for e in errors:
            print("  " + e)
        return 1
    print(f"assets OK: {len(blocks)} blocks, {len(items)} items, {len(models.checked)} models")
    return 0


if __name__ == "__main__":
    sys.exit(main())
