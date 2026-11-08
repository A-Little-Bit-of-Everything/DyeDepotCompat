# Dye Depot Compat

A NeoForge compatibility mod for **Minecraft 1.21.1** that expands [Dye Depot](https://modrinth.com/mod/dye-depot) color support across popular mods:

- **[Arts and Crafts](https://modrinth.com/mod/arts-and-crafts)**: Adds full support for all 16 Dye Depot colors across Arts and Crafts blocks and items (Chalk sticks/blocks/dust, Plaster, Flower Pots, Decorated Pots, Paintbrushes, Mud Bricks sets, Soapstone sets, and Terracotta Shingles sets), including models, blockstates, textures, recipes, loot tables, and paintbrush palettes.
- **[Music Disc Maker](https://modrinth.com/mod/music-disc-maker)**: Lets the disc-dyeing table accept Dye Depot dyes and gives each Dye Depot color a distinct disc tint on music disc labels.

Both Arts & Crafts and Music Disc Maker are **optional** dependencies: if either (or both) are present alongside Dye Depot, their respective compatibilities are automatically enabled.

## Requirements

| Dependency | Version | Status |
|------------|---------|--------|
| Minecraft | 1.21.1 | Required |
| NeoForge | 21.1.262+ | Required |
| [Dye Depot](https://modrinth.com/mod/dye-depot) | 2.1.2+ | Required |
| [Arts and Crafts](https://modrinth.com/mod/arts-and-crafts) | 1.5.0+ | Optional |
| [Music Disc Maker](https://modrinth.com/mod/music-disc-maker) | 3.0.0+ | Optional |

## Features

### Arts and Crafts Compatibility
- **Chalk & Chalk Dust**: All 16 Dye Depot colors for Chalk blocks, Chalk Sticks, and Chalk Dust drawings.
- **Plaster**: All 16 Dye Depot colors for Plaster blocks.
- **Mud Bricks**: Full block, slab, stairs, and wall sets in all 16 colors.
- **Terracotta Shingles**: Full block, slab, stairs, and wall sets in all 16 colors.
- **Soapstone**: Soapstone, Polished Soapstone, and Soapstone Bricks (with slabs, stairs, and walls) in all 16 colors.
- **Pots**: Flower Pots (including potted plants) and Decorated Pots in all 16 colors with atlas palette permutations.
- **Paintbrushes**: Craft paintbrushes in all 16 Dye Depot colors to paint both Arts & Crafts blocks and Dye Depot blocks in-world.

### Music Disc Maker Compatibility
- Injects the 16 Dye Depot colors into `DiscDye.VALUES` with precise HSL definitions.
- Resizes `DiscDyeTint` color caches so the dyeing table renders unique tints without crashing.
- Server-side and client-side safe.

## Building From Source

```bash
./gradlew build
```

The built jar is located in `build/libs/`.

## Credits

- **TheAndromedaCat**
- A Little Bit of Everything Team
- Not a Glitch Studios
