# SimpleMaterialGenerators (SMG)

A Slimefun4 addon that provides simple material generators with **Networks** and **Cargo** support.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-green)
![Slimefun](https://img.shields.io/badge/Slimefun-RC--37+-blue)
![Java](https://img.shields.io/badge/Java-21-orange)

---

## ✨ Features

- 🔧 **Simple Generators** - Automatically produce materials over time
- 🌐 **Networks Support** - Works with Network Grabber to extract items
- 📦 **Cargo Support** - Compatible with Slimefun Cargo system
- 📝 **Config-based** - Add/modify generators via `generators.yml`
- 🗂️ **Organized Categories** - SMG Materials & SMG Generators

---

## 📦 Default Items

### SMG Materials (Crafting Components)

| Item | Material | Description |
|------|----------|-------------|
| Generator Core | Chorus Flower | Core component for all generators |
| Compressed Cobblestone | Cobblestone | 9x Cobblestone |
| Compressed Granite | Polished Granite | 9x Granite |
| Compressed Andesite | Polished Andesite | 9x Andesite |
| Compressed Diorite | Polished Diorite | 9x Diorite |
| Compressed Stone | Stone | 9x Stone |
| Compressed Iron | Raw Iron | 9x Raw Iron |
| Compressed Gold | Raw Gold | 9x Raw Gold |
| Compressed Copper | Raw Copper | 9x Raw Copper |
| Compressed Gravel | Gravel | 9x Gravel |
| Compressed Sand | Sand | 9x Sand |
| Compressed Prismarine Shard | Prismarine Shard | 9x Prismarine Shard |
| Compressed Prismarine Crystals | Prismarine Crystals | 9x Prismarine Crystals |
| Compressed Netherrack | Nether Bricks | 9x Netherrack |
| Compressed Soul Sand | Soul Soil | 9x Soul Sand |
| Compressed Quartz | Quartz | 9x Quartz |
| Compressed Redstone | Redstone | 9x Redstone |
| Compressed Lapis Lazuli | Lapis Lazuli | 9x Lapis Lazuli |
| Compressed Amethyst Shard | Amethyst Shard | 9x Amethyst Shard |
| Compressed Obsidian | Polished Blackstone Button | 9x Obsidian |

### SMG Generators

| Generator | Output | Rate (ticks) |
|-----------|--------|--------------|
| Cobblestone Generator | Cobblestone | 2 |
| Granite Generator | Granite | 4 |
| Andesite Generator | Andesite | 4 |
| Diorite Generator | Diorite | 4 |
| Stone Generator | Stone | 4 |
| Raw Iron Generator | Raw Iron | 8 |
| Raw Gold Generator | Raw Gold | 8 |
| Raw Copper Generator | Raw Copper | 8 |
| Gravel Generator | Gravel | 4 |
| Sand Generator | Sand | 4 |
| Prismarine Shard Generator | Prismarine Shard | 8 |
| Prismarine Crystals Generator | Prismarine Crystals | 8 |
| Netherrack Generator | Netherrack | 2 |
| Soul Sand Generator | Soul Sand | 8 |
| Quartz Generator | Quartz | 16 |
| Redstone Generator | Redstone | 16 |
| Lapis Lazuli Generator | Lapis Lazuli | 8 |
| Amethyst Shard Generator | Amethyst Shard | 8 |
| Obsidian Generator | Obsidian | 32 |

---

## 🎮 How to Use

### Method 1: With Chest (Classic)
```
     [CHEST]      ← Items are pushed here
   [GENERATOR]
```
Place a chest directly above the generator. Items will be automatically pushed into the chest.

### Method 2: With Networks Grabber
```
   [GRABBER]  ← Extracts items from generator's GUI
   [GENERATOR]
```
Place a Network Grabber adjacent to the generator. It will extract items from the generator's internal inventory.

### Method 3: With Slimefun Cargo
Connect Cargo nodes to the generator to extract items automatically.

> **Note:** Generators will produce up to 64 items (1 stack) in their internal inventory, then pause until items are removed.

---

## 🛠️ Crafting

### Step 1: Craft Generator Core
```
[Smooth Stone] [Smooth Stone] [Smooth Stone]
[Lava Bucket]  [Iron Pickaxe] [Water Bucket]
[Smooth Stone] [Smooth Stone] [Smooth Stone]
```

### Step 2: Craft Compressed Material
```
[Material] [Material] [Material]
[Material] [Material] [Material]
[Material] [Material] [Material]
```

### Step 3: Craft Generator
```
[Compressed] [Compressed] [Compressed]
[Compressed] [Gen Core]   [Compressed]
[Compressed] [Compressed] [Compressed]
```

---

## 📁 Configuration

### generators.yml

You can customize existing generators or add new ones in `plugins/SimpleMaterialGenerators/generators.yml`:

```yaml
generators:
  # Example: Custom Diamond Generator
  diamond:
    enabled: true
    name: "&bDiamond Generator"
    category: generators        # "generators" or "materials"
    material: DIAMOND_BLOCK     # Block appearance
    output: DIAMOND             # Item produced
    rate: 100                   # Ticks per item (higher = slower)
    lore:
      - "&6Rate: &e100 ticks"
      - "&5Super Rare!"
      - ""
      - "&9&oSimpleMaterialGenerators"
    recipe:
      type: ENHANCED_CRAFTING_TABLE
      shape:
        - "ABA"
        - "BCB"
        - "ABA"
      ingredients:
        A: DIAMOND
        B: "SF:REINFORCED_ALLOY_INGOT"  # Slimefun item
        C: "GEN:smg_core"                # SMG item
```

### Recipe Ingredient Prefixes

| Prefix | Description | Example |
|--------|-------------|---------|
| (none) | Vanilla Material | `DIAMOND`, `STONE` |
| `SF:` | Slimefun Item | `SF:SOLDER_INGOT` |
| `GEN:` | SMG Item | `GEN:smg_core`, `GEN:cobblestone` |

### Recipe Types

- `ENHANCED_CRAFTING_TABLE`
- `SMELTERY`
- `GRIND_STONE`
- `ORE_CRUSHER`
- `MAGIC_WORKBENCH`
- `ARMOR_FORGE`
- `COMPRESSOR`
- `PRESSURE_CHAMBER`

---

## 🗂️ Categories

Items are organized in the Slimefun Guide:

```
Slimefun Guide
└── SMG
    ├── SMG Materials    (Compressed items, Generator Core)
    └── SMG Generators   (Working generators)
```

Use `category: materials` or `category: generators` in config.

---

## 📥 Installation

1. Download the latest release
2. Place the `.jar` file in your `plugins/` folder
3. Ensure you have **Slimefun4** installed
4. Restart the server
5. (Optional) Edit `generators.yml` to customize

---

## 🔗 Dependencies

**Required:**
- [Slimefun4](https://github.com/Slimefun/Slimefun4) (RC-37 or newer)

**Optional (Soft Dependencies):**
- [Networks](https://github.com/Sefiraat/Networks) - For Network Grabber support

---

## 🛠️ Building from Source

```bash
git clone https://github.com/waleks647/SMG.git
cd SMG
mvn clean package
```

The compiled plugin will be in `target/SimpleMaterialGenerators v2.0.0.jar`

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🐛 Bug Reports

Found a bug? Please report it on the [Issues page](https://github.com/waleks647/SMG/issues).

---

## 🙏 Credits

- Original Author: [waleks647](https://github.com/waleks647)
- Slimefun Team for the amazing API
