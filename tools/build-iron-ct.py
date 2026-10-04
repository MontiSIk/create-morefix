from pathlib import Path
from io import BytesIO
from zipfile import ZipFile
from PIL import Image

jar = Path(r'C:/Users/sstei/AppData/Roaming/ModrinthApp/profiles/Kriate/mods/create-1.21.1-6.0.10.jar')
out = Path(__file__).resolve().parent.parent / 'src/main/resources/assets/catwalk_orientation/textures/block'
out.mkdir(parents=True, exist_ok=True)
with ZipFile(jar) as archive:
    for name in ('industrial_iron_block', 'industrial_iron_block_top', 'weathered_iron_block', 'weathered_iron_block_top', 'andesite_block', 'zinc_block', 'brass_block'):
        source = Image.open(BytesIO(archive.read(f'assets/create/textures/block/{name}.png'))).convert('RGBA')
        w, h = source.size
        sheet = Image.new('RGBA', (w * 4, h * 4))
        border = max(1, w // 8)
        # The side texture's lower frame starts one pixel before the other
        # edges. Clamping to that row stretches its dark rail across a join.
        left, right = border, w - border
        top, bottom = border, h - border - (1 if name in ('industrial_iron_block', 'weathered_iron_block') else 0)
        for index in range(16):
            tile = source.copy()
            for y in range(h):
                for x in range(w):
                    sx, sy = x, y
                    if (index & 1 and y < top) or (index & 2 and y >= bottom):
                        sy = top + (y - top) % (bottom - top)
                    if (index & 4 and x < left) or (index & 8 and x >= right):
                        sx = left + (x - left) % (right - left)
                    tile.putpixel((x, y), source.getpixel((sx, sy)))
            sheet.paste(tile, (index % 4 * w, index // 4 * h))
        sheet.save(out / f'{name}_connected.png')
        print(name, source.size, sheet.size)
