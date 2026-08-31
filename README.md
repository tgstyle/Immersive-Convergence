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
and change which blocks it is built from, using nothing but a resource pack and
a data pack. Nothing is decompiled and no mod file is edited, so an override
survives updating the mod as long as the file it replaces still exists.

Two terms are used throughout. The **mod id** is the short name a mod goes by,
`immersivetechnology` for Immersive Technology, and it is the namespace your
files go under. The **machine id** is the short name of one machine, such as
`alternator` or `boiler_tank`, and it is written as `<id>` below.

## Where overrides go
There is no special folder. Models, blockstates and textures are client files
and belong in a resource pack; the structure a machine is built from is server
data and belongs in a data pack. Either way you put your file at the same path
the mod uses, under the mod's own namespace, and yours is read instead of the
mod's.

| What you are changing | The mod's file | Goes in |
| --- | --- | --- |
| Which model a machine uses | `assets/<modid>/blockstates/<id>.json` | Resource pack |
| The model a blockstate points at | `assets/<modid>/models/multiblock/<material>/<id>.json` | Resource pack |
| Geometry | `assets/<modid>/models/multiblock/<material>/<id>/<id>.obj` and its `.mtl` | Resource pack |
| Textures | `assets/<modid>/textures/multiblock/<material>/<id>.png` | Resource pack |
| Machine recipes | `data/<modid>/recipe/<machine>/` | Data pack |
| Blocks it is built from | `data/<modid>/structure/multiblocks/<id>.nbt` | Data pack |

To get the original file to edit, open the mod's `.jar` with any zip program and
copy the file out of it. An override replaces the whole file rather than merging
with it, so always start from the original instead of writing a short file with
only the parts you want changed.

You are not limited to replacing files at the mod's own paths. A pack may add
new files under the mod's namespace and point an overridden blockstate at them,
so your model does not have to carry the mod's name or sit where the mod's model
sat.

One thing cannot be overridden. Where a machine's pipes, wires and hoppers
connect, and the shape you walk into, come from
`assets/<modid>/multiblocks/<id>.json`, which the mod reads straight out of its
own jar rather than through the resource system. A pack copy of that file is
ignored.

## Machine recipes
Machine recipes are ordinary data pack files, one per recipe, sorted into a
folder per machine at `data/<modid>/recipe/<machine>/`. They carry the mod's
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
  "neoforge:conditions": [ { "type": "neoforge:false" } ],
  "type": "immersivetechnology:distiller",
  "energy": 10000,
  "inputAmount": 1000,
  "inputTag": "minecraft:water",
  "result": { "amount": 500, "id": "immersivetechnology:distilled_water" },
  "time": 20
}
```

## Blocks a machine is built from
The layout of a machine is a vanilla structure file, the same format a structure
block saves. Replacing it in a data pack changes what the player has to build
and what the hammer forms.

Two things follow from that file rather than from any model. The machine is only
formed if every block matches, so a structure listing blocks a player cannot get
makes the machine unbuildable. And the model is cut up along the same cells, so
adding or removing blocks changes how the model is sliced without you touching
the model at all.

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
  "loader": "neoforge:obj",
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

Machines that can be built either way round need a second, mirrored model, which
is the `_mirrored` file the blockstate uses for `mirrored=true`. It is the first
model reflected left to right about the vertical plane through the middle of the
machine's width, so a machine 3 wide with its master in the middle sends a point
at `x` to `1 - x`, while one 3 wide with its master at the corner sends it to
`3 - x`. If your modeling program flips the faces inside out when mirroring, turn
them back the right way round, or the machine will look hollow from outside. A
machine that is the same on both sides does not need two files and can point at
the same one twice.

The mirrored model keeps the original's materials by naming them explicitly, so
if you export it as its own file give it an `mtl_override` in the model JSON
pointing at the base machine's `.mtl` rather than duplicating the material file.

# Reporting issues
When you are reporting bugs, please attach the crash report, mod and forge version.<br/>

# Help translate the mod
Feel free to translate the mod and put it in a pull request.<br/>

# About Modpack and License
Immersive Convergence is licensed under the GNU GENERAL PUBLIC LICENSE Version 3. You may use it in modpacks, reviews or any other form as long as you abide by the terms.<br/>
