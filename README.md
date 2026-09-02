# Links
- [Official Discord for Immersive Technology](https://discord.gg/ujY2mV9)<br/>

- [Official Discord for Immersive Geology](https://discord.gg/team-immersive-geologys-eco-friendly-tm-server-610912351142674434)<br/>

- [Immersive Technology on CurseForge](https://www.curseforge.com/minecraft/mc-mods/immersive-technology)
- [Immersive Technology on Modrinth](https://modrinth.com/mod/mct-immersive-technology)

- [Immersive Geology on CurseForge](https://www.curseforge.com/minecraft/mc-mods/immersive-geology)
- [Immersive Geology on Modrinth](https://modrinth.com/mod/immersive-geology)

# Immersive Convergence
Common API for IE Addons.<br/>

# Overriding content
Any mod built on Immersive Convergence lets you change its multiblock machines
without editing the mod itself. You can retune a machine's recipes, move where
its pipes and wires connect, change which blocks it is built from, and replace
its model and textures.

You do this by placing your own copy of a file where the game looks for it
first. Nothing is decompiled and no mod file is edited, so an override survives
updating the mod as long as the file it replaces still exists.

Two terms are used throughout. The **mod id** is the short name a mod goes by,
`immersivetech` for Immersive Technology, and it is the folder name your
overrides go under. The **machine id** is the short name of one machine, such as
`alternator` or `boiler_tank`, and it is written as `<id>` below.

Immersive Engineering's own machines and Immersive Petroleum's distillation
tower and pumpjack are covered the same way. Their files ship inside Immersive
Convergence's jar rather than their own, under the mod id of the mod that owns
the machine, so overrides for them go under `overrides/immersiveengineering/`
and `overrides/immersivepetroleum/`, and the original to copy comes out of
Immersive Convergence's jar.

## Where overrides go
Overrides live in a folder called `overrides`, sitting in your instance
alongside `config` and `mods`. Inside, the layout copies the mod's own files:
take the path a file has under `assets/<modid>/` and put your copy at the same
path under `overrides/<modid>/`.

| What you are changing | The mod's file | Your copy |
| --- | --- | --- |
| Machine recipes | `assets/<modid>/recipes_multiblocks/` | `overrides/<modid>/recipes_multiblocks/` |
| Ports and collision | `assets/<modid>/multiblocks/<id>.json` | `overrides/<modid>/multiblocks/<id>.json` |
| Blocks it is built from | `assets/<modid>/structures/multiblocks/<id>.nbt` | `overrides/<modid>/structures/multiblocks/<id>.nbt` |

To get the original file to edit, open the mod's `.jar` with any zip program and
copy the file out of it. An override replaces the whole file rather than merging
with it, so always start from the original instead of writing a short file with
only the parts you want changed.

These files are read once while the game starts, so a change needs a restart to
take effect. The `overrides/<modid>/recipes_multiblocks` folder is created for
you the first time the game runs; the other two you make yourself.

Models and textures work differently. They are ordinary client files and go in a
resource pack, which is covered further down.

## Machine recipes
Recipes are one JSON file each, sorted into a folder per machine. Put your file
at the same folder and file name the mod uses and it replaces that recipe; give
it any other name and it is added as a new recipe alongside the existing ones.
To remove a recipe outright, override it with a file containing nothing but
`{}`, which replaces the original with a recipe that registers nothing.

Anything whose name starts with `_` is skipped, so you can keep a `_notes.json`
beside your files, or park unused recipes in a `_disabled` folder, without the
game reading them.

```json
{
  "type": "immersivetech:boiler_tank",
  "input": {
    "amount": 250,
    "fluid": "water"
  },
  "result": {
    "amount": 450,
    "fluid": "steam"
  },
  "time": 10,
  "requiredHeat": 600.0
}
```

Every recipe needs a `type`, which says which machine it belongs to and is
always written as `<modid>:<machine>`. The type also decides what else the
recipe needs, so the remaining fields differ from machine to machine. The mod's
own files under `assets/<modid>/recipes_multiblocks/` are the best reference for
what a given machine expects, which is another reason to start from a copy of
one.

A recipe can also carry `conditions`, the standard Forge mechanism for switching
a recipe off when something is missing. Immersive Convergence adds one condition
of its own, `<modid>:fluid_exists`, which is true only when a named fluid is
registered by some mod. It is how a mod ships recipes for fuels that may not
exist in a given pack: the recipe below is used when another mod adds diesel,
and quietly ignored when nothing does.

```json
{
  "conditions": [
    {
      "type": "immersivetech:fluid_exists",
      "fluid": "diesel"
    }
  ],
  "type": "immersivetech:boiler_liquid",
  "input": {
    "amount": 14,
    "fluid": "diesel"
  },
  "time": 10,
  "heatPerTick": 0.1,
  "targetHeat": 600.0
}
```

There is also a way to offer several versions of one recipe and take whichever
fits the pack. Give the file the type `<modid>:conditional` and a `recipes`
list, where each entry has its own `conditions` and a `recipe`. The first entry
whose conditions pass is the one used, and an entry with no conditions always
passes, so putting it last makes it the fallback.

## Ports and collision
`overrides/<modid>/multiblocks/<id>.json` describes the parts of a machine that
are not its shape: where its inputs, outputs and control blocks sit, the shape
you bump into and click on, and how large it is drawn in the engineer's manual.

This file and the structure template are deliberately not resource pack
material. They decide how a machine forms, where it can be connected to and what
you collide with, and a server and its players have to agree on all three. A
resource pack only exists on the client, and a dedicated server has no resource
pack support at all in this version, so putting these in one would let a player's
pack disagree with the server about where a machine's pipes are.

```json
{
  "manualScale": 11,
  "pointsOfInterest": [
    { "name": "master", "pos": [1, 1, 3], "facing": null },
    { "name": "trigger", "pos": [1, 1, 3], "facing": null },
    { "name": "energy_output0", "pos": [0, 0, 3], "facing": "left" },
    { "name": "mechanical_input0", "pos": [1, 1, 0], "facing": "front" }
  ],
  "shapeAABB": [[[0.0, 0.0, 0.0, 1.0, 0.5, 1.0]], []]
}
```

| Field | Meaning |
| --- | --- |
| `manualScale` | How far the manual zooms out when drawing the machine. Larger means smaller on the page |
| `pointsOfInterest` | The list of blocks in the machine that do something special |
| `name` | What the spot is for. `master` and `trigger` are explained below; the rest are named by the mod itself |
| `pos` | Which block in the machine, as `[x, y, z]` counted from one corner |
| `facing` | Which side of that block the connection is on, or `null` if it has none |
| `shapeAABB` | The boxes you collide with, one entry per block, each box `[minX, minY, minZ, maxX, maxY, maxZ]` measured within that block |

Two names are special. `master` is the block that actually runs the machine and
holds its inventory, tanks and progress. It is not a block you need to find
while playing, since right clicking any block of a formed machine opens the same
interface, but everything else in the machine is positioned relative to it,
including the model. `trigger` is the block you hit with the engineer's hammer
to form the machine.

Every other name belongs to the mod, which looks them up to find its own fluid
ports, energy connections and redstone inputs, so keep the names that are
already in the file and only move them.

`facing` takes a name relative to the machine rather than a compass direction, so
the machine keeps working in every rotation:

| Value | Face |
| --- | --- |
| `front` | The side the machine faces |
| `back` | Opposite the front |
| `left`, `right` | The two sides, as seen facing the machine's front |
| `up`, `down` | Top and bottom |

Two counting quirks will trip you up if you move something, and they disagree
with each other, so it is worth reading twice.

A point of interest counts `z` from the **back** of the machine, so a port on
the front face has the largest `z`, not zero.

The `shapeAABB` list counts `z` from the **front** instead. Its entries are not
labeled: they are simply read in order, running along X first, then Z, then Y
(entry number `x + z * width + y * width * length`). An empty entry does not mean
an empty block: wherever the blueprint has a block, an empty entry falls back to
a full cube. That is why most machines list `[]` for their solid blocks and only
spell out boxes for the shaped ones.

## Blocks a machine is built from
`overrides/<modid>/structures/multiblocks/<id>.nbt` is the machine's blueprint,
saying which block has to sit in each position before the machine will form. It is
an ordinary vanilla structure file, the kind a structure block saves, so the
easiest way to make one is to build the machine you want in a creative world,
save it with a structure block, and copy the resulting file in. Remember that
the machine's own ports have to stay where the layout file says they are, so
rearranging blocks and moving points of interest usually go together.

Air and structure void count as empty. Nothing is required in those positions
and they are left alone when the machine forms, which is how machines with
hollow interiors or overhangs are described.

A position is satisfied by the same block with the same variant. Mods can also
mark certain materials as interchangeable, which is what lets a machine accept
steel from any mod that provides it rather than one particular steel block, so
substituting an equivalent material often works even when the file names a
specific one.

## Models and textures
Machine models are client files, so a normal resource pack replaces them, with
no `overrides` folder involved. You supply a blockstate file pointing at your own
model and textures under the mod's namespace, and the game slices whatever you
give it across the machine's blocks. A model that does not match the machine's
shape exactly still draws in full, because anything sticking out past the
machine is drawn by the closest block rather than being cut off.

A blockstate file is what maps a block to the model used for it. These use the
Forge format, where each property gets its own section listing what to do for
each value, and the game combines the sections:

```json
{
  "forge_marker": 1,
  "defaults": {
    "transform": "forge:default-block",
    "custom": { "flip-v": true }
  },
  "variants": {
    "inventory,type=alternator": [
      { "model": "immersivetech:multiblock/metal/alternator/alternator.obj" }
    ],
    "type": { "alternator": {} },
    "facing": {
      "north": { "transform": { "rotation": { "y": 0 } } },
      "south": { "transform": { "rotation": { "y": 180 } } },
      "west": { "transform": { "rotation": { "y": 90 } } },
      "east": { "transform": { "rotation": { "y": -90 } } }
    },
    "_0multiblockslave": {
      "false": {},
      "true": { "model": "immersiveengineering:ie_empty" }
    },
    "_1dynamicrender": { "false": {}, "true": {} },
    "boolean0": { "false": {}, "true": {} }
  }
}
```

One rule matters more than the rest, and it is the usual cause of a crash on
startup after editing one of these. The list of properties a block has comes
from the mod's code, not from your file, so your blockstate has to account for
**every** combination the machine can produce. Leave one out and the game stops
with a `MissingVariantException` naming the variant it could not find, rather
than simply drawing nothing. Copy the original file and change what you need
instead of writing one from scratch, and this stays out of your way.

| Property | What it means |
| --- | --- |
| `type` | Which machine, and whether this is its main block or one of the `_slave` blocks around it |
| `facing` | Which way the machine was built: `north`, `south`, `east` or `west` |
| `_0multiblockslave` | `false` on the master block, `true` on all the others |
| `_1dynamicrender` | `false` for the fixed body, `true` for moving parts such as turbine rotors |
| `boolean0` | Whether the machine was built mirrored |
| `boolean1` | An on and off state, used by machines that change appearance when running, such as the solid fuel boiler |

Model paths in a blockstate are resolved under `models/block/`, so
`immersivetech:multiblock/metal/alternator/alternator.obj` is the file at
`assets/immersivetech/models/block/multiblock/metal/alternator/alternator.obj`.

Immersive Petroleum's pumpjack is drawn from files Immersive Convergence ships
under Petroleum's namespace, because Petroleum has no model for it that a pack
can touch. The body is `assets/immersivepetroleum/blockstates/metal_multiblock_pumpjackparent.json`
pointing at `models/block/multiblock/pumpjack.obj` and its `_mirrored` twin,
textured from Petroleum's `textures/models/pumpjack.png`. The moving parts are
separate files beside it, each modeled around the point it pivots on:
`pumpjack_arm.obj` (the walking beam, pivot at the top of the samson post),
`pumpjack_swing.obj` (the crank and counterweights), `pumpjack_connector.obj`
(the pitman rod between them) and `pumpjack_well_long.obj` /
`pumpjack_well_short.obj` (the rod into the well, whichever length reaches).
Replace any of them with a model of the same name and the animation carries
on using it.

## Making your own machine model
A machine is one model of the entire thing, not a separate model per block. You
build the whole machine as a single object in something like Blockbench, export
it as an OBJ, and the game cuts it into per block pieces while it loads. You
never have to think about where the seams fall.

Getting the position right is the one fiddly part. One unit in the model is one
block in the world, and the point `0, 0, 0` is the corner of the **master
block**, the one the layout file marks as `master`. Everything else is placed
relative to that, so the block one to the left and two behind the master fills
the cube from `-1, 0, -2` to `0, 1, -1`. Build the machine facing north, and
since a machine runs away from you toward the north, it extends into negative Z.
As a worked example, the alternator is 3 wide, 3 high and 4 long with its master
in the middle of the front face, and its model runs from `-1` to `2` across,
`-1` to `2` up, and `-3` to `1` deep.

Each block draws whatever sits inside its own cube. Anything crossing from one
block into the next is cut at the join and shared between them, so you do not
need to line parts up to the grid or split the model yourself. Parts that hang
off the machine entirely are not lost either, they get drawn by the nearest
block that is part of the machine, which is how chimneys and vents can stick
out.

One consequence is worth planning for: since each block draws only its own
slice, a machine is lit block by block rather than as one object, and a slice is
only ever seen from outside its own cube. Machines that are solid all the way
through look right; a model with large hollow interiors can show its inside
faces where the cuts fall.

| Convention | Detail |
| --- | --- |
| Scale | One unit is one block |
| Center point | The corner of the master block |
| Direction | Build it facing north; the game rotates it for the other three |
| Depth | The machine extends into negative Z |
| Textures | The blockstate sets `flip-v`, so textures are flipped vertically |

Textures are named by a companion `.mtl` file, listed at the top of the OBJ with
`mtllib` and kept beside it. In it, `map_Kd` gives a texture by mod id and path,
with no `textures/` in front and no `.png` at the end, so
`immersivetech:multiblock/metal/alternator` is the image saved at
`assets/immersivetech/textures/multiblock/metal/alternator.png`:

```
mtllib alternator.mtl

newmtl m_alternator
map_Kd immersivetech:multiblock/metal/alternator
```

Most machines can be built facing either way round, so they need a second,
mirrored model, which is the `_mirrored` file the blockstate uses for
`boolean0=true`. It is the first model flipped across the middle of the master
block: a point at `x` moves to `1 - x`, so a model running from `-2` to `2`
across becomes one running from `-1` to `3`. If your modeling program flips the
faces inside out when mirroring, turn them back the right way round, or the
machine will look hollow from outside. A machine that is the same on both sides
does not need two files and can point at the same one twice, which is what the
alternator does.

# Reporting issues
When you are reporting bugs, please attach the crash report, mod and forge version.<br/>

# Help translate the mod
Feel free to translate the mod and put it in a pull request.<br/>

# About Modpack and License
Immersive Convergence is licensed under the GNU GENERAL PUBLIC LICENSE Version 3. You may use it in modpacks, reviews or any other form as long as you abide by the terms. Assets are protected under the terms in the LICENSE_ASSETS.txt<br/>
