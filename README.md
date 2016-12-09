# Fencing Magician
##Overview
Fencing Magician is a prototype game developed in LibGDX. It's a top down action game where the main character is a fencing magician, skilled in both sword attacks and magic. The idea was to interweave the two so that hitting an enemy with a sword attack would empower the magic attacks. When an enemy is hit by the sword they are "tagged", making them glow red. Using the magic projectile attack, for example, will automatically home in on nearby tagged enemies, making it beneficial to hit enemies with a sword attack before using magic. The player also has a dash move which immediately moves them forward. The dash is extremely useful for getting closer to or avoiding enemies. 

While this was the original premise for the game, I later began experimenting with other gameplay mechanics. For example, I added an orb the player can control with the mouse that can be used to hit enemies. Currently the project is on hold as I am attempting to expand the dialogue mechanic into a full project, as can be seen [here](https://github.com/wizered67/ProjectVisualNovel). 

The game uses LibGDX, Box2D for collision detection, Box2DLights for lighting effects, and Tiled for level editing.


##Demonstrations
Note: Gifs may take some time to load and appear slow while first loading. Graphics are not mine. They are placeholder and for testing purposes only.

![gif](fencingMagicianDashLegacy.gif)

![attack](fencingMagicianAttacks.gif)

##Programming
* Wrote custom extension of LibGDX map loader to load different shapes for collision detection.
* Uses shaders for enemy outlines when they're tagged.
* Uses Box2D raycasting for dash move.
* Wrote code for sliding along rectangular collision shapes.

The code for the project can be seen [here] (https://github.com/wizered67/fencing-magician/tree/master/core/src/com/wizered67/game).
