# LegendOfZelda-Link
Character named Link can traverse graphical map via arrow keys or the keys A, W, D, X.

Map can toggle between jumping between rooms (when Link reaches the edge of the room) and scrolling between rooms (A,W,D,X) by pressing 'J' (jumpRooms).

Can save and load Link, Tile, Clay Pot, Boomerang locations via ArrayList and Json file by pressing key 'S'(save) or 'L'(load).

To be able to add/remove Tiles (boundaries that Link cannot cross) or add a Clay Pot, 1) Enter edit mode by pressing key 'E', 2) Switch to AddPot mode (exit TileAddition/Removal mode) by pressing key 'P'.

Press key CTRL to throw a boomerang.

ALL IMAGE FILES MUST BE CUT AND PASTED INTO A FOLDER NAMED "images" FOR THE PROGRAM TO WORK CORRECTLY.

I'll post earlier versions of this project sometime later (I have them saved off Github).


Unexpected Behavior:
* Jumping rooms when Link reaches a room border AFTER scrolling across rooms sometimes creates a scrollOffset that would go off screen, making the potential room jump illegal to display via validator methods: hence, the room cannot be jumped to
