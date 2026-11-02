Lucky Table

Build the Luck Table, unlock permanent Luck effects one stage at a time, and enjoy extra drops when mining and fighting.

A mod for Minecraft 1.21.1 using NeoForge.

What does the mod do?

Normally you only get the Luck effect for a short time from potions or commands. With Lucky Table you unlock it permanently:

Craft the Luck Table and place it.
Right-click it to pay for the next Luck stage from your inventory.
From then on you always carry the Luck effect, shown with the clover icon in your inventory screen.
Every item you mine or loot from a mob gives you extra drops.

Key points:

The default value is Luck 0. Nothing happens until you use the Luck Table.
Every player has their own progress. The effect only applies to the player who paid.
Stages must be unlocked in order. You cannot skip a stage.
Your progress is kept after death.
The effect never expires and shows no particles.
Stages

Right-click the Luck Table to pay for the next stage. The ingredients are taken from your inventory. If something is missing, the table shows what you still need above your hotbar.

Stage	Luck value	Price
1	Luck 3	10 Diamonds
2	Luck 6	1 Nether Star
3	Luck 9	1 Dragon Egg
Extra drops

When you break a block or kill a mob, every item type that would have dropped anyway gets as many bonus items as your Luck value. The bonus is fixed and has no randomness.

Example	Luck 0	Luck 3	Luck 6	Luck 9
1 Oak Log	1	4	7	10
Cow: 2 Leather and 2 Raw Beef	2 + 2	5 + 5	8 + 8	11 + 11

Important:

Only items that would have dropped anyway get a bonus. Blocks without a drop stay without a drop.
In Creative mode blocks drop no items when broken, so you will not see the bonus there.
Chest loot and fishing are unchanged. Luck works there just like in vanilla Minecraft.
The bonus is strong. At Luck 9 a diamond ore gives 10 diamonds. Keep this in mind when adding the mod to a modpack.
Luck Table recipe
Emerald   Diamond   Emerald
Obsidian  Book      Obsidian
Obsidian  Obsidian  Obsidian

You need 2 Emeralds, 1 Diamond, 1 Book and 5 Obsidian. The Luck Table can be broken without any special tool and placed again.

Requirements
Minecraft Java Edition 1.21.1
NeoForge 21.1.259 or newer (21.1.x)
Java 21
Installation
Install NeoForge for Minecraft 1.21.1.
Copy the file lucktable-VERSION.jar into the mods folder of your Minecraft directory.
Start the game.

On a server the mod must be installed on the server and on every player's client.

Trying it out (Survival mode with cheats)
/give @s lucktable:luck_table
/give @s minecraft:diamond 10
/give @s minecraft:nether_star
/give @s minecraft:dragon_egg

Then place the table, right-click it, and break a few blocks.

Notes
The mod manages the Luck effect itself. Luck effects from other sources (potions, commands) are removed within one second while you are at stage 0, and overwritten at higher stages.
The drop bonus also uses your Luck value if it comes from another source.
Customizing (for developers)
What	Where
Prices and stages	LuckTableBlock.java
Strength of the drop bonus	LuckBonusModifier.java, constant ITEMS_PER_LUCK
Crafting recipe	src/main/resources/data/lucktable/recipe/luck_table.json
Look and textures	src/main/resources/assets/lucktable/ (models, textures, languages)
Name, version, description	gradle.properties
Project structure
File	Purpose
ExampleMod.java	Main class: registers the block and item, applies the effect
LuckTableBlock.java	The block with the payment logic
ModAttachments.java	Stores the unlocked stage per player
LuckBonusModifier.java	Adds the extra drops
ModLootModifiers.java	Registers the drop modifier with the game
Building from source

You need an installed JDK 21. In the project folder:

.\gradlew build

The finished file is in build/libs. To test in game:

.\gradlew runClient
License

This project is licensed under the MIT License. See the `LICENSE` file for the full text.