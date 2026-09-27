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
without editing the mod itself. You can replace a machine's model and textures,
retune its recipes, and change which blocks it is built from, with an ordinary
resource pack and data pack. Nothing is decompiled and no mod file is edited,
so an override survives updating the mod as long as the file it replaces still
exists.

Two terms are used throughout. The **mod id** is the short name a mod goes by,
`immersivetechnology` for Immersive Technology, and it is the namespace your
files go under. The **machine id** is the short name of one machine, such as
`alternator` or `boiler_tank`, and it is written as `<id>` below.

## Where overrides go
There is no special folder. A pack overrides a file by carrying its own copy at
the same path the mod uses, under the mod's own namespace, and the pack's copy
is read instead of the mod's. Get the path right, and the file, and it works;
nothing else is needed. Models, blockstates and textures are client files and
go in a resource pack under `assets`; the structure a machine is built from and
its recipes are server data and go in a data pack under `data`. `<material>`
below is `metal` or `stone`, the folder the mod keeps the machine under.

| What you are changing | The mod's file | Your copy |
| --- | --- | --- |
| Which model a machine uses | `assets/<modid>/blockstates/<id>.json` | Resource pack, same path |
| The model a blockstate points at | `assets/<modid>/models/multiblock/<material>/<id>.json` | Resource pack, same path |
| Geometry | `assets/<modid>/models/multiblock/<material>/<id>/<id>.obj` and its `.mtl` | Resource pack, same path |
| Textures | `assets/<modid>/textures/multiblock/<material>/<id>.png` | Resource pack, same path |
| Machine recipes | `data/<modid>/recipes/<machine>/` | Data pack, same path |
| Blocks it is built from | `data/<modid>/structures/multiblocks/<id>.nbt` | Data pack, same path |
| Ports and collision shapes | `data/<modid>/multiblocks/<id>.json` | Data pack, same path |

A resource pack has to be enabled in the resource pack screen and takes effect
on the next reload, F3+T included. A data pack has to be in the world's
`datapacks` folder and enabled there, and takes effect on the next start or
`/reload`; on a dedicated server that is the server world's folder. A pack that
sits above another wins where both carry the same file.

To get the original file to edit, open the mod's `.jar` with any zip program and
copy the file out of it. An override replaces the whole file rather than merging
with it, so always start from the original instead of writing a short file with
only the parts you want changed.

You are not limited to replacing files at the mod's own paths. A pack may add
new files under the mod's namespace and point an overridden blockstate at them,
so your model does not have to carry the mod's name or sit where the mod's model
sat.

Where a machine's pipes, wires and hoppers connect, and the shape you walk into,
come from `data/<modid>/multiblocks/<id>.json`. It is data, so a data pack copy
at the same path replaces it on the next start or `/reload`, and the server
sends it to every player who joins, so a client's collision matches the
server's. Immersive Convergence ships the same kind of file for Immersive
Engineering's and Immersive Petroleum's machines, at
`data/immersiveengineering/multiblocks/<id>.json` and
`data/immersivepetroleum/multiblocks/<id>.json`, and applies the shapes and
ports it finds there over the machine's own; a data pack overrides those the
same way. The cell order of `shapeAABB` follows the structure file's size, so a
pack that changes a machine's structure has to change this file to match.

## Immersive Engineering and Immersive Petroleum
Immersive Engineering's own machines, and Immersive Petroleum's, are not
Immersive Convergence's files, but they are overridden the same way: put your
copy at the path their own jar uses, under their own namespace, in a pack.
The originals come out of their jars.

| What you are changing | Immersive Engineering | Immersive Petroleum |
| --- | --- | --- |
| Which model a machine uses | `assets/immersiveengineering/blockstates/<id>.json` | `assets/immersivepetroleum/blockstates/<id>.json` |
| Models | `assets/immersiveengineering/models/block/metal_multiblock/` and `models/block/stone_multiblocks/` | `assets/immersivepetroleum/models/multiblock/` |
| Textures | `assets/immersiveengineering/textures/block/multiblocks/` | `assets/immersivepetroleum/textures/multiblock/` |
| Machine recipes | `data/immersiveengineering/recipes/<machine>/` | `data/immersivepetroleum/recipes/<machine>/` |
| Blocks it is built from | `data/immersiveengineering/structures/multiblocks/<id>.nbt` | `data/immersivepetroleum/structures/multiblocks/<id>.nbt` |

Their models are drawn by Immersive Engineering's own model code rather than
sliced by Immersive Convergence, so the notes further down about building one
model of the whole machine do not apply to them. A blockstate there points at a
`<id>_split.json` wrapper around the `.obj`, with a `<id>_mirrored_split.json`
beside it, and those wrappers are what to copy and edit.

## Machine recipes
Machine recipes are ordinary data pack files, one per recipe, sorted into a
folder per machine at `data/<modid>/recipes/<machine>/`. They carry the mod's
own recipe type rather than a vanilla one, but the usual data pack rules apply:
a file at the same path replaces that recipe, and a file under any other name is
added alongside the existing ones. This is the distiller turning water into
distilled water, with salt as a chance byproduct:

```json
{
  "type": "immersivetechnology:distiller",
  "chance": 0.5,
  "energy": 10000,
  "inputAmount": 1000,
  "inputTag": "minecraft:water",
  "itemOutput": { "count": 1, "id": "immersivetechnology:salt" },
  "result": { "amount": 500, "id": "immersivetechnology:distilled_water" },
  "time": 20
}
```

To take a recipe out rather than change it, override it with a copy carrying a
condition that never passes. That is the loader's own way of switching a recipe
off, and it leaves nothing behind for another pack to trip over:

```json
{
  "forge:conditions": [ { "type": "forge:false" } ],
  "type": "immersivetechnology:distiller",
  "energy": 10000,
  "inputAmount": 1000,
  "inputTag": "minecraft:water",
  "result": { "amount": 500, "id": "immersivetechnology:distilled_water" },
  "time": 20
}
```

## Ports and collision
`data/<modid>/multiblocks/<id>.json` holds everything about a machine that is
not its look: which block is the master, which block the hammer forms it from,
where its pipes, wires, redstone and comparators connect, the shape you walk
into and click on, and how large the engineer's manual draws it. It is data, so
a data pack copy at the same path replaces it on the next start or `/reload`,
and every port in the file, including the fluid cell the sneak hammer empties,
follows the pack from that point on: a formed machine picks the change up
without being rebuilt, a pipe or cable already attached reconnects at the new
cell through an ordinary neighbor update instead of staying on the old one,
and the server sends the change to every connected player, not only one who
joins after it, so both sides keep agreeing.

This is an abridged `data/immersivetechnology/multiblocks/alternator.json`:

```json
{
  "manualScale": 16,
  "pointsOfInterest": [
    { "name": "master", "pos": [0, 0, 0], "facing": null },
    { "name": "trigger", "pos": [1, 1, 3], "facing": null },
    { "name": "mechanical_input0", "pos": [1, 1, 0], "facing": "front" },
    { "name": "energy_right0", "pos": [2, 0, 3], "facing": "left" },
    { "name": "energy_right0", "pos": [2, 1, 3], "facing": "left" },
    { "name": "comparator0", "pos": [0, 0, 0], "facing": null }
  ],
  "shapeAABB": [
    [[0.0, 0.0, 0.5, 1.0, 1.0, 1.0], [0.375, 0.0, 0.0, 1.0, 1.0, 0.5]],
    [],
    null
  ]
}
```

| Field | Meaning |
| --- | --- |
| `manualScale` | How far the engineer's manual zooms out to draw the machine; larger draws it smaller |
| `pointsOfInterest` | The blocks of the machine that do something, one entry per block and job |
| `name` | The job, looked up by the mod by exact name |
| `pos` | The block, as `[x, y, z]` within the structure, counted from `0` |
| `facing` | The side of that block the connection is on, one value or a list of them; `null` for none, `"any"` for every side |
| `shapeAABB` | The collision and selection boxes, one entry per block of the structure |

### Points of interest
`master` is the block that runs the machine and holds its tanks, inventory and
progress, and everything else, the model included, is placed relative to it.
It is the one entry a pack cannot move: it decides which block holds the
machine's data, so it is read once while the game starts. `trigger` is the block
the engineer's hammer forms the machine from, and `symmetric_trigger` marks a
second block that forms it too.

The other names belong to the mod, which finds its ports by them. Keep every
name that is in the file and change only its `pos` and `facing`: a name the mod
does not know does nothing, and a missing name the machine needs is an error.
Names with a different number on the end are separate ports, so `fluid_input0`
and `fluid_input1` are two inputs. The same name listed on several blocks is one
port reachable from each of them, which is how the alternator's `energy_right0`
covers a whole side.

Immersive Technology uses these names:

| Name | What it marks |
| --- | --- |
| `fluid_input`, `fluid_output`, `fluid_io` | Pipe connections |
| `item_input`, `item_output` | Item connections |
| `energy_input`, `energy_input_hv`, `energy_input_mv`, `energy_left`, `energy_right` | Wire or cable connections |
| `mechanical_input`, `mechanical_output` | Where a turbine and an alternator meet |
| `heat_input`, `heat_output` | The heat link between a burner boiler and the boiler tank |
| `baseheater` | Where the advanced coke oven's base heaters attach |
| `redstone` | The redstone control block |
| `comparator`, `comparator_layer`, `comparator_base` | Blocks that give a comparator reading |
| `ignition` | The block a burner boiler is lit from |
| `link`, `sun`, `reflector`, `beam` | How the solar tower, solar melter and reflectors find and aim at each other |
| `sound`, `sound_running`, `sound_starter`, `sound_ignite`, `sound_spark`, `sound_arc`, `smoke`, `particle`, `exhaust` | Where sounds and effects come from; these need no free face |

The files for Immersive Engineering's and Immersive Petroleum's machines use
only `master`, `trigger` and ports whose names start with `fluid_`, `item_` or
`energy_`; each such entry moves or adds that kind of port at its block.

`facing` names a side relative to the machine, so it holds in every rotation:
`front`, `back`, `left`, `right`, `up` and `down`. Most shipped files put their
`front` ports in the `z = 0` row and their `back` ports in the highest `z` row,
which is the quickest way to get your bearings. A port needs its facing side to
be open to the world.

### Collision shape
`shapeAABB` has one entry per block of the structure, in order along `x` first,
then `z`, then `y`: entry number `x + z * width + y * width * length`, where the
width, height and length are the size of the machine's structure file. The
points of interest and the shape count their blocks the same way. Each entry is
one of:

| Entry | Result |
| --- | --- |
| A list of boxes | Each box is `[minX, minY, minZ, maxX, maxY, maxZ]`, measured within that one block from `0` to `1` |
| `[]` | A full block |
| `null` | No collision at all for that block |

The list must have exactly `width * height * length` entries; any other count
is logged as an error and the whole machine falls back to full blocks. A
`shapeAABB` that is itself an empty list, `[]`, makes every block full. The
order follows the structure file, so a pack that changes a machine's structure
has to change this file to match.

Writing the boxes by hand is slow for anything curved. The
[`bb_shape.py`](https://github.com/tgstyle/MCT-Immersive-Technology/blob/1.21.1-3.0-Dev/mb_shapes_v2/bb_shape.py)
script, in the
[`mb_shapes_v2`](https://github.com/tgstyle/MCT-Immersive-Technology/tree/1.21.1-3.0-Dev/mb_shapes_v2)
folder of Immersive Technology's 1.21.1 branch, turns a Blockbench `.bbmodel`
of the machine into a finished `shapeAABB` list, and its `readme.txt` explains
the setup and the options. An OBJ goes through `obj_to_bbmodel.py` and
`bb_sterilize.py` first. Check the result in game with F3+B before shipping it.

## Blocks a machine is built from
The layout of a machine is a vanilla structure file, the same format a structure
block saves. Replacing it in a data pack changes what the player has to build
and what the hammer forms.

Two things follow from that file rather than from any model. The machine is only
formed if every block matches, so a structure listing blocks a player cannot get
makes the machine unbuildable. And the model is cut up along the same cells, so
adding or removing blocks changes how the model is sliced without you touching
the model at all.

One thing about the structure cannot change: its overall width, height and
length. The game checks a loaded structure's size against what the machine
registered for it at the start, and a `.nbt` with a different bounding box
fails that check. The blocks inside the box are free to change; only the
footprint itself is fixed.

## Models and textures
A machine is drawn as one model of the whole thing, and the game slices it
across the machine's blocks while it loads. There is no per block model and no
file describing the slicing; whatever model the blockstate ends up pointing at
is what gets cut. A model that does not match the machine's shape exactly still
draws in full, because anything sticking out past the machine is drawn by the
closest block rather than being cut off.

A blockstate file maps each state of a block to a model. These use the vanilla
`variants` format, one entry per combination, and this is an abridged example
with the four `facing` values on one combination:

```json
{
  "variants": {
    "facing=north,mirrored=false,multiblockslave=false": { "model": "immersivetechnology:multiblock/metal/distiller", "uvlock": true },
    "facing=east,mirrored=false,multiblockslave=false": { "model": "immersivetechnology:multiblock/metal/distiller", "y": 90, "uvlock": true },
    "facing=south,mirrored=false,multiblockslave=false": { "model": "immersivetechnology:multiblock/metal/distiller", "y": 180, "uvlock": true },
    "facing=west,mirrored=false,multiblockslave=false": { "model": "immersivetechnology:multiblock/metal/distiller", "y": 270, "uvlock": true }
  }
}
```

The list of properties a block has comes from the mod's code, not from your
file, so your blockstate has to account for every combination the machine can
produce. Leaving one out leaves that state with no model rather than falling
back to something sensible. Copy the original file and change what you need
instead of writing one from scratch, and this stays out of your way.

| Property | What it means |
| --- | --- |
| `facing` | Which way the machine was built: `north`, `south`, `east` or `west` |
| `multiblockslave` | `false` on the master block, `true` on all the others |
| `mirrored` | Whether the machine was built mirrored, on machines that allow it |
| `active` | An on and off state, used by machines that change appearance when running, such as the solid fuel boiler |

The master and the slave variants point at the same model. That is deliberate:
every block of the machine is handed the same whole machine model and draws only
its own slice of it.

The model a blockstate names is an ordinary block model file, so
`immersivetechnology:multiblock/metal/distiller` is
`assets/immersivetechnology/models/multiblock/metal/distiller.json`. For an OBJ
machine that file is a thin wrapper naming the geometry:

```json
{
  "parent": "minecraft:block/block",
  "loader": "forge:obj",
  "model": "immersivetechnology:models/multiblock/metal/distiller/distiller.obj",
  "ambientocclusion": false,
  "automatic_culling": false,
  "shade_quads": true,
  "flip_v": true,
  "emissive_ambient": true,
  "textures": { "particle": "immersivetechnology:multiblock/metal/distiller" }
}
```

Note that the `model` key here is a full path including `models/` and the file
extension, unlike the blockstate's reference above. A plain vanilla JSON model
works in this slot too; it does not have to be an OBJ.

## Making your own machine model
You build the whole machine as a single object in something like Blockbench,
export it, and the game cuts it into per block pieces while it loads. You never
have to think about where the seams fall.

Getting the position right is the one fiddly part. One unit in the model is one
block in the world, and the point `0, 0, 0` is the corner of the **master
block**, the block the machine forms around. Everything else is placed relative
to that. Two of the shipped machines show the range this covers:

- The alternator is 3 wide, 3 high and 4 long with its master at the corner of
  the structure, so its model runs from `0` to `3` across, `0` to `3` up and `0`
  to `4` deep.
- The distiller is 3 by 3 by 3 with its master in the middle, so its model runs
  from `-1` to `2` in every direction.

Build the machine in the orientation the blockstate leaves unrotated, which is
the `facing=north` entry with no `y` on it; the game rotates it for the other
three.

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
| Direction | Build it in the orientation of the unrotated `facing` entry; the game rotates it for the other three |
| Extent | The model runs from minus the master's position to the size of the structure minus it |
| Textures | The model sets `flip_v`, so textures are flipped vertically |

Textures are named by a companion `.mtl` file, listed at the top of the OBJ with
`mtllib` as a bare file name and kept beside it. In it, `map_Kd` gives a texture
by mod id and path, with no `textures/` in front and no `.png` at the end, so
`immersivetechnology:multiblock/metal/distiller` is the image saved at
`assets/immersivetechnology/textures/multiblock/metal/distiller.png`:

```
mtllib distiller.mtl

newmtl m_distiller
map_Kd immersivetechnology:multiblock/metal/distiller
```

Machines that can be built either way round do not need a second model. The
`mirrored=true` variants point at a `<id>_mirrored.json` that wraps the base
model in Immersive Convergence's mirror loader, registered under the mod's own
namespace (`immersivetechnology:mirror` for Immersive Technology), which reflects
the model left to right about the master block while it loads, so one OBJ
serves both. The wrapper carries a full copy of the base model definition
rather than a reference to it:

```json
{
  "parent": "minecraft:block/block",
  "loader": "immersivetechnology:mirror",
  "ambientocclusion": false,
  "inner_model": {
    "parent": "minecraft:block/block",
    "loader": "forge:obj",
    "model": "immersivetechnology:models/multiblock/metal/distiller/distiller.obj",
    "flip_v": true,
    "textures": { "particle": "immersivetechnology:multiblock/metal/distiller" }
  },
  "textures": { "particle": "immersivetechnology:multiblock/metal/distiller" }
}
```

Replacing the OBJ at its own path is therefore mirrored along with it and needs
nothing else. Pointing the base model at a different OBJ means changing the
`model` inside `inner_model` too, or the machine keeps mirroring the old
geometry.

## A replacement model keeps the original's groups
An OBJ file splits its geometry into named groups (the `o` and `g` lines) and
names its materials (the `usemtl` lines), and the mod's model files show, hide or
retexture parts of a model by those names. A replacement model has to carry the
same groups as the one it replaces: a part in a renamed group is never switched,
and a missing group leaves that state with nothing to show. Open the original,
note every `o`, `g` and `usemtl` name in it, and give your model the same ones,
with the same parts in each.

These are Immersive Technology's models whose groups do something:

| Model | Group or material | What uses it |
| --- | --- | --- |
| `models/block/metal/valve_fluid/valve_fluid.obj` | `Pipe`, `Handle_Open`, `Handle_Closed` | `valve_fluid_open.json` and `valve_fluid_closed.json` each hide the other handle through `visibility` |
| `models/block/metal/valve_load/valve_load.obj` | `Base`, `Handle_Open`, `Handle_Closed` | Same as the fluid valve, in `valve_load_open.json` and `valve_load_closed.json` |
| `models/multiblock/metal/boiler_solid/boiler_solid.obj` | material `cube_front` | `boiler_solid_active.json` points the `cube_front` texture at the lit front while the boiler runs |
| `models/block/metal/advanced_coke_oven_baseheater/advanced_coke_oven_baseheater.obj` | `Fan`, `Rotor` | Hidden by the base heater's model files; the spinning fan is its own model, `advanced_coke_oven_baseheater_fan.obj`, drawn by the block |

Every other machine model is a single group, and its name is free. The turbine
rotors are separate models under `models/multiblock/metal/rotor/`, drawn and
spun by the machine, so replacing a turbine's body leaves its rotor as it was.

# Reporting issues
When you are reporting bugs, please attach the crash report, mod and forge version.<br/>

# Help translate the mod
Feel free to translate the mod and put it in a pull request.<br/>

# About Modpack and License
Immersive Convergence is licensed under the GNU GENERAL PUBLIC LICENSE Version 3. You may use it in modpacks, reviews or any other form as long as you abide by the terms. Assets are protected under the terms in the LICENSE_ASSETS.txt<br/>
