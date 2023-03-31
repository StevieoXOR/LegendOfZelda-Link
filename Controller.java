//Steven Lynch
//Mar 30, 2023
//Link LegendOfZelda Project: Character named Link can traverse graphical map via arrow keys or A,W,D,X.
//Map can switch between jumping between rooms and scrolling between rooms by pressing key J.
//Can save and load Link, Tile, Clay Pot, Boomerang locations via ArrayList and Json file by pressing 's'(save) or 'l'(load).
//To be able to add/remove Tiles (boundaries that Link cannot cross) or add a Clay Pot,
//  1) Enter edit mode by pressing key E
//  2) Switch to AddPot mode (exit TileAddition/Removal mode) by pressing keyP.
//Press key CTRL to throw a boomerang.

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


class Controller implements ActionListener, MouseListener, KeyListener
{
	View view;
	Model model;

	//Necessary for preventing jerkiness caused by randomly-and-constantly updating.
	boolean keyLeft;	//Arrow keys
	boolean keyRight;
	boolean keyUp;
	boolean keyDown;
	boolean keyA;	//A,D,W,X are roomScroll (independent of Link) keys
	boolean keyD;
	boolean keyW;
	boolean keyX;
	boolean LinkShouldMoveRight;
	boolean LinkShouldMoveLeft;
	boolean LinkShouldMoveUp;
	boolean LinkShouldMoveDown;
	boolean JFrameBorderWidthsAreAddedToView;


	
	Controller(){}		//Default constructor
	Controller(Model m)	//Constructor
	{
		model = m;

		//Default values
		View.numRoomsWide = View.numRoomsTall = 3;
		model.loadSpriteList();	//Load the sprite list from "map.json" immediately upon starting the game
	}

	void setView(View v)
		{view = v;}
	public void actionPerformed(ActionEvent e)
	{
		//System.out.println("Hey! I said not to push that button!");
		//view.removeButton();	//View method
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e)
	{
		if(view.inEditMode)	//if(allowModificationsToMap)
		{
			int mouseX=e.getX(), mouseY=e.getY();
		
			//Without view.roomScrollPosXY, all Tiles added would be mapped to the leftmostAndUppermost room, regardless of what the screen shows.
			int adjustedPosX = mouseX + view.roomScrollPosX;
			int adjustedPosY = mouseY + view.roomScrollPosY;

			//I don't need to do this here because model.addTile() takes care of it
			//int tilePosX = model.snapToGrid(mouseX);	//'Snap' the tile coordinate to a grid block.
			//int tilePosY = model.snapToGrid(mouseY);

			if(view.editPotsAndNotTiles)
				{model.addPot(adjustedPosX, adjustedPosY);}					//if(inPotEditMode){addPotToListOfSprites}
			else
			{
				if( model.existingTileWasClickedOn(adjustedPosX, adjustedPosY))
					{model.removeTile(adjustedPosX, adjustedPosY);}					//if(areaClickedOnAlreadyHasTile){removeTileFromListOfTiles}
				else{model.addTile(adjustedPosX, adjustedPosY);}					//if(areaClickedOnDoesNotHaveTile){addTileToListOfTiles}
			}
		}
	}
	public void mouseEntered(MouseEvent e)  {}
	public void mouseExited(MouseEvent e)   {}
	public void mouseClicked(MouseEvent e)  {}
	
	
	//Can be pressed for a period of time, causing many (possibly unnecessary) updates
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT: keyRight = true; break;
			case KeyEvent.VK_LEFT:	keyLeft  = true; break;
			case KeyEvent.VK_UP:	keyUp    = true; break;
			case KeyEvent.VK_DOWN:	keyDown  = true; break;
			case KeyEvent.VK_A:		keyA	 = true; break;
			case KeyEvent.VK_D:		keyD	 = true; break;
			case KeyEvent.VK_W:		keyW	 = true; break;
			case KeyEvent.VK_X:		keyX	 = true; break;

			//These keys trigger one-at-a-time events (quitGame, switchEditMode), so they should be in keyReleased.
			//Also, they should not be changing a keyState variable, they should be modifying a different type of variable DIRECTLY.
				//case KeyEvent.VK_Q:		keyQ     = true; break;
				//case KeyEvent.VK_ESCAPE:  keyEsc   = true; break;
				//case KeyEvent.VK_P:		keyP	 = true; break;
		}
	}

	//Can only happen once, so this is where save, load, and exit should go
	public void keyReleased(KeyEvent e)
	{
		int  keyLifted_ID   = e.getKeyCode();
		char keyLifted_Char = e.getKeyChar();
		char c = Character.toLowerCase(keyLifted_Char);
		//All of the above variables could have been used in the switch() statement below, but keyLifted_ID is more useful because it defines the ESCAPE key.
		//For VK_someKey and keyCode, capitalization doesn't matter because it's the KEY that's focused on, not the capitalization.
		//For keyChar, capitalization DOES matter.
		//KeyEvent.VK_CONTROL (isIntValue17)  !=  Character.CONTROL (isCharValue15)
		//  ^Java Specification						^Unicode Specification
		//Character.CONTROL exists, but things like Character.KEY_A (and even Character.ESCAPE) don't exist.
		//  ^This means you'll have to use KeyEvent.VK_{KEYNAME} for almost every keyRelease (Character.CONTROL being the sole exception)
		switch(keyLifted_ID)
		{
			case KeyEvent.VK_RIGHT: keyRight = false; break;
			case KeyEvent.VK_LEFT:   keyLeft = false; break;
			case KeyEvent.VK_UP:       keyUp = false; break;
			case KeyEvent.VK_DOWN:   keyDown = false; break;
			
			case KeyEvent.VK_A:		keyA	 = false; break;
			case KeyEvent.VK_D:		keyD	 = false; break;
			case KeyEvent.VK_W:		keyW	 = false; break;
			case KeyEvent.VK_X:		keyX	 = false; break;


			//There's no break for the first statement because I want it to fall through to the second so it can exit the game
			case KeyEvent.VK_Q:
			case KeyEvent.VK_ESCAPE:  System.exit(0); break;

			//Save or Load  Link, Tile, Pot, and Boomerang locations to Json file
			case KeyEvent.VK_S: model.saveSpriteList(); break;
			case KeyEvent.VK_L: model.loadSpriteList(); break;

			//There's no break for the first statement because I want it to fall through to the second so it can throw the Boomerang
			//Make Link throw Boomerang when ControlKey OR keyB is pressed *and* released
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_B:	model.addBoomerang_usingLinkPosAndLinkDir(); break;


			//Flip the truth value of jumpRooms_doNotScroll every time key 'J' is pressed and then released
			case KeyEvent.VK_J:	 view.jumpRooms_doNotScroll = !view.jumpRooms_doNotScroll;	break;

			//Flip the truth value of inEditMode every time key 'E' is pressed and then released
			case KeyEvent.VK_E:				view.inEditMode = !view.inEditMode;				break;

			//Flip the truth value of editPotsAndNotTiles every time key 'P' is pressed and then released
			case KeyEvent.VK_P:	   view.editPotsAndNotTiles = !view.editPotsAndNotTiles;	break;
		}

		
		

		//The switch block already takes care of these: The commented-out lines below just show another way to do it.		
			/*if(c=='q' || c=='Q' || c==27)	//ESCAPE_KEYCODE==27==KeyEvent.VK_ESCAPE. if(q||Q||Esc isPressedAndReleased)
			{System.exit(0);}//{Exit program;}. Btw, the uppercase Q case never happens because keyReleased is converted to lowercase.

			if(c=='s'){model.saveSpriteList();}//Save Link, Tile, Pot, and Boomerang locations to file
			if(c=='l'){model.loadSpriteList();}//Load Link, Tile, Pot, and Boomerang locations to file

			if(c=='e'){view.inEditMode = !view.inEditMode;}	//Toggles printing Editmode to screen.

			//Make Link throw Boomerang when ControlKey or keyB is pressed *and* released
			if(c==Character.CONTROL || c=='b'){model.addBoomerang_usingLinkPosAndLinkDir();}
			if(keyLifted_ID==KeyEvent.VK_CONTROL || c=='b'){model.addBoomerang_usingLinkPosAndLinkDir();}*/


		//Debug
		//if(view.jumpRooms_doNotScroll){System.out.println("jumpRooms");}
		//else{System.out.println("scrollRooms");}
		//System.out.printf("roomScrollPosX: %d, roomScrollPosY: %d\n", view.roomScrollPosX, view.roomScrollPosY);

		if(view.jumpRooms_doNotScroll)
		{
			//DEBUG     System.out.println("roomJumpRight: "+view.willNotJumpRoomOutOfBounds(1,0)+'\n');

			//I didn't get the fix (for the weird halftiles) working yet :/
			//if(showingExactlyOneRoom)
			/*if((view.roomScrollPosX % View.widthPerRoom == 0)  && (view.roomScrollPosY % View.heightPerRoom == 0))
			{
				if( (view.roomScrollPosX!=0) && (view.roomScrollPosY!=0) )	//if(theOnlyShownRoomIsNotUpperLeftRoom)
				{
					view.roomScrollPosX += View.BORDER_WIDTH;
					view.roomScrollPosY += View.BORDER_HEIGHT;
					JFrameBorderWidthsAreAddedToView = true;
				}
				else
				{
					view.roomScrollPosX -= View.BORDER_WIDTH;
					view.roomScrollPosY -= View.BORDER_HEIGHT;
					JFrameBorderWidthsAreAddedToView = false;
				}
			}*/

			//CANNOT USE if(keyD){...} OR if(keyEsc){...} ETC DUE TO booleanVarUpdatedByPressAndReleaseKeyFunctions UPDATING PROCESS ABOVE.
			//  MUST USE WHAT IS BELOW (getKeyChar or getKeyCode INSTEAD OF booleanVarUpdatedByPressAndReleaseKeyFunctions).
			//Modify view's visible-stuff-area
			if(c=='d' && view.willNotJumpRoomOutOfBounds(1,0))
				{view.roomScrollPosX += View.widthPerRoom;}
			if(c=='a' && view.willNotJumpRoomOutOfBounds(-1,0))
				{view.roomScrollPosX -= View.widthPerRoom;}
			if(c=='x' && view.willNotJumpRoomOutOfBounds(0,1))
				{view.roomScrollPosY += View.heightPerRoom;}
			if(c=='w' && view.willNotJumpRoomOutOfBounds(0,-1))
				{view.roomScrollPosY -= View.heightPerRoom;}
		}
	}

	public void keyTyped(KeyEvent e) {}

	//update()'s input:  takes variables that were modified by keyboard presses and mouse input (position, click, release)
	//update() == update_asTimeProgressesAtAConstantRate == updateWhenFrameIsRedrawn_forThisGameAtLeast
	void update()
	{
		//			TURTLE-RELATED STUFF
		/*int turtDestX = model.dest_x;	//Current model's(turtle's) X-coordinate destination
		int turtDestY = model.dest_y;
		int turtPosnX = model.turtle_x;	//Current model's(turtle's) X-coordinate position
		int turtPosnY = model.turtle_y;
		//System.out.printf("Dest: %d, %d\n",turtDestX, turtDestY);
		//System.out.printf("Posn: %d, %d\n\n",turtPosnX, turtPosnY);*/

		//int windowWidth  = view.getWidth();
		//int windowHeight = view.getHeight();
		//int tileWidth    = view.tileImg.getWidth();	//Width  of turtle picture (#pixels)
		//int tileHeight   = view.tileImg.getHeight();	//Height of turtle picture (#pixels)
		//int movDist = Model.movSpeed_pixls;	//Gets turtle's movement speed from the Model
											//  CLASS (not object, hence the capital 'M')
		
		//Images are drawn from the upper left
		//It's very important that I modify model.dest_x and model.dest_y, NOT turtDestX nor turtDestY
		//  due to local variables versus class instance variables





		


		//Ease-of-use Readonly variables
		final int topOfCurrRoomY    = view.roomScrollPosY;						//No matter which room you're in, the bottom is always off by View.heightPerRoom
		final int bottomOfCurrRoomY = (view.roomScrollPosY + View.heightPerRoom);	//No matter which room you're in, the bottom is always off by View.heightPerRoom
		final int leftOfCurrRoomX  = view.roomScrollPosX;							//No matter which room you're in, the right is always off by View.widthPerRoom
		final int rightOfCurrRoomX = (view.roomScrollPosX + View.widthPerRoom);	//No matter which room you're in, the right is always off by View.widthPerRoom
		final int linkToesHeightY = model.link.posnY + model.link.height;
		final int linkRightSideX  = model.link.posnX + model.link.width;
		
		//Unused
		//final boolean noCollisionsBetweenTilesAndLink = model.linkIsNotCollidingWithAnyTiles();
		//final boolean noCollisionsBetweenPotsAndLink  = model.linkIsNotCollidingWithAnyPots();
		//final boolean noCollisionsBetweenLinkAndAnyOtherSprite = model.linkIsNotCollidingWithAnyOtherSprites();



	
		model.updateAllPreviousLocations_whenAllAreLegal();

		//I don't currently use this for anything since model.fixAllExistingCollisions() takes care of it, but it exists if I want to use it in the future
		//Must be that (Link_PREVx - Link_currX  !=  0)
		//final Sprite kickedPot = model.detectLinkCollidingWithPot_kickThatPot();
		//if(kickedPot != null){}


		//Detect collision, then fix collision if detected
		//int a = model.tileList.size();
	//	for(int i=0; i</*a*/model.tileList.size(); i++)
	//	{
	//		Tile currTile = model.tileList.get(i);
	//		//if(linkIsCollidingWithCURRENTtileIn_listOfTiles)
	//		if( model.link.linkIsCollidingWith_PassedInTile(currTile) )
	//		{
	//			model.link.pushLinkOutOfTile(currTile);
	//
	//			//a = model.tileList.size();	//Since the for() loop checks the size() every iteration and it's not compiled as a constant,
	//				//tileListSize is updated every iteration, making this line unnecessary
	//			//break;	//Exits the for() loop once the first collision is detected to prevent invalid ArrayList size (because it is removed once a collision is detected)
	//				//Note that the comment ^ is for general collision detection when the object will be deleted from a list upon which the for loop depends on the list's size
	//				//I'm not removing Tiles when Link collides with them, so these comments are irrelevant for this specific assignment
	//		}
	//	}
		model.fixAllExistingCollisions();

		
		//Change rooms based on Link moving into edge of room
		//Notice how I'm not changing Link's actual position when I change the room, and how it applies to EVERY room change.
		if(view.jumpRooms_doNotScroll)	//if(jumpEntireRoomsAtATime)
		{
			//(LinkToesAreBelowBottomOfCurrRoom && roomBelowToJumpToIsLegal)
			//if(aboutToEnterRoomBelowCurrRoom){TriggerRoomSwitchToAdjacentRoomBelow}
			if((linkToesHeightY > bottomOfCurrRoomY)  &&  view.willNotJumpRoomOutOfBounds(0,1))
				{view.roomScrollPosY += View.heightPerRoom;		if(Game.DEBUG) System.out.println("roomScrollPosY += roomHeight");}
			
			//(LinkToesAreAboveTopOfCurrRoom && roomAboveToJumpToIsLegal)
			//if(aboutToEnterRoomAboveCurrRoom){TriggerRoomSwitchToAdjacentRoomAbove}
			if((linkToesHeightY < topOfCurrRoomY)  &&  view.willNotJumpRoomOutOfBounds(0,-1))
				{view.roomScrollPosY -= View.heightPerRoom;		if(Game.DEBUG) System.out.println("roomScrollPosY -= roomHeight");}
			
			//(LinkRightSideIs_toLeftOf_LeftSideOfCurrRoom && roomToLeft_ToJumpTo_IsLegal)
			//if(aboutToEnterRoomToLeftOfCurrRoom){TriggerRoomSwitchToAdjacentRoomToLeft}
			if((linkRightSideX < leftOfCurrRoomX)  &&  view.willNotJumpRoomOutOfBounds(-1,0))
				{view.roomScrollPosX -= View.widthPerRoom;		if(Game.DEBUG) System.out.println("roomScrollPosX -= roomWidth");}
			
			//(LinkLeftSideIs_toRightOf_RightSideOfCurrRoom && roomToRight_ToJumpTo_IsLegal)
			//if(aboutToEnterRoomToRightOfCurrRoom){TriggerRoomSwitchToAdjacentRoomToRight}
			if((model.link.posnX > rightOfCurrRoomX)  &&  view.willNotJumpRoomOutOfBounds(1,0))
				{view.roomScrollPosX += View.widthPerRoom;		if(Game.DEBUG) System.out.println("roomScrollPosX += roomWidth");}
		}
		//Should go in Controller.update() instead of Controller.keyPressed() to avoid jerkiness
		if(!view.jumpRooms_doNotScroll)	//if(scrollAcrossRooms)
		{
			final int scrollSpeed = 15;
			if(keyD && view.willNotScrollRoomOutOfBounds(scrollSpeed, 0))
				{view.roomScrollPosX += scrollSpeed;}
			if(keyA && view.willNotScrollRoomOutOfBounds(-scrollSpeed, 0))
				{view.roomScrollPosX -= scrollSpeed;}
			if(keyX && view.willNotScrollRoomOutOfBounds(0, scrollSpeed))
				{view.roomScrollPosY += scrollSpeed;}
			if(keyW && view.willNotScrollRoomOutOfBounds(0, -scrollSpeed))
				{view.roomScrollPosY -= scrollSpeed;}
		}

		//I don't actually use these yet because I can't get the LinkShouldMove**** to work correctly
		/*if(keyDown)
		{
			//possibleLinkY += (int)model.link.movementSpeed;
			if(noCollisionsBetweenLinkAndAnyOtherSprite)
				{LinkShouldMoveDown = true;}
			else{LinkShouldMoveDown = false;}
		}
		if(keyUp)
		{
			//possibleLinkY -= (int)model.link.movementSpeed;
			if(noCollisionsBetweenLinkAndAnyOtherSprite)
				{LinkShouldMoveUp = true;}
			else{LinkShouldMoveUp = false;}
		}
		if(keyLeft)
		{
			//possibleLinkX += (int)model.link.movementSpeed;
			if(noCollisionsBetweenLinkAndAnyOtherSprite)
				{LinkShouldMoveLeft = true;}
			else{LinkShouldMoveLeft = false;}
		}
		if(keyRight)
		{
			//possibleLinkX -= (int)model.link.movementSpeed;
			if(noCollisionsBetweenLinkAndAnyOtherSprite)
				{LinkShouldMoveRight = true;}
			else{LinkShouldMoveRight = false;}
		}*/


		//			FORMERLY TURTLE-RELATED STUFF
		int numPixelsWalked = (int)model.link.movSpeed;
		if(keyRight)
		{	//Direction.RIGHT.direction  ==  enumClass.enumElementName.getNumericalValue
			model.link.updateImageNumUponMoving(Direction.RIGHT);	//Iterate through Link_spriteImageList to make Link appear like he's moving
			model.link.move_walk(numPixelsWalked,0);			//Make Link walk numPixelsWalked pixels in the +x direction
		}
		if(keyLeft)
		{
			model.link.updateImageNumUponMoving(Direction.LEFT);
			model.link.move_walk(-numPixelsWalked,0);				//Make Link walk numPixelsWalked pixels in the -x direction
		}
		if(keyDown)
		{
			model.link.updateImageNumUponMoving(Direction.DOWN);
			model.link.move_walk(0,numPixelsWalked);					//Make Link walk numPixelsWalked pixels in the +y direction
		}
		if(keyUp)
		{
			model.link.updateImageNumUponMoving(Direction.UP);
			model.link.move_walk(0,-numPixelsWalked);					//Make Link walk numPixelsWalked pixels in the -y direction
		}




		if(model.spriteList != null)	//if(spriteList isNotEmpty){set1stElementToEasilyAccessedLink}
		{
			model.spriteList.set(0, model.link);	//Update ArrayList of Sprites (only index 0) with model's copy of link
			if(Game.DEBUG) System.out.println("Controller.update(): Non-empty spriteList, so Updated spriteList[0] with model's copy of link\n\n");
		}
		else		//if(spriteList isEmpty){set1stElementToEasilyAccessedLink}
		{
			model.spriteList.add(model.link);
			if(Game.DEBUG) System.out.println("Controller.update(): Empty spriteList, so Added model's copy of link to spriteList\n\n");
		}
	}
}