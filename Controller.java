//Steven Lynch
//Mar 29, 2023
//Project: Character named Link can traverse graphical map via keys A,W,D,X. Map can switch between jumping between rooms
//  and scrolling between rooms. Can save and load Tile and Clay Pot locations (part of the map) via ArrayList and JSON file.

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
	boolean keyLeft;
	boolean keyRight;
	boolean keyUp;
	boolean keyDown;
	boolean keyQ;	//Q,Escape are quit keys
	boolean keyEsc;
	boolean keyA;	//A,D,W,X are roomScroll keys
	boolean keyD;
	boolean keyW;
	boolean keyX;
	boolean jumpRooms_doNotScroll;	//true==jumpRoomsByKeypress, false=scrollAcrossRoomsByKeypress
									//Arbitrarily chosen key for switching between jumping from room to room
									//  and scrolling between rooms
	boolean keyE;				//Used for enabling Edit mode
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
		jumpRooms_doNotScroll = true;
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
		int mouseX=e.getX(), mouseY=e.getY();
		int adjustedPosX = mouseX + view.roomScrollPosX;
		int adjustedPosY = mouseY + view.roomScrollPosY;
		//int tilePosX = model.snapToGrid(mouseX);	//'Snap' the tile coordinate to a grid block
		//int tilePosY = model.snapToGrid(mouseY);
		if(view.inEditMode)
		{
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
			//case KeyEvent.VK_Q:		keyQ     = true; break;
			//case KeyEvent.VK_ESCAPE:keyEsc   = true; break;
			case KeyEvent.VK_A:		keyA	 = true; break;
			case KeyEvent.VK_D:		keyD	 = true; break;
			case KeyEvent.VK_W:		keyW	 = true; break;
			case KeyEvent.VK_X:		keyX	 = true; break;
			//case KeyEvent.VK_P:		keyP	 = true; break;
		}

		/*if(!jumpRooms_doNotScroll)	//if(scrollAcrossRooms)
		{
			if(keyD && view.willNotScrollRoomOutOfBounds(5, 0))
				{view.roomScrollPosX+=5;}
			if(keyA && view.willNotScrollRoomOutOfBounds(-5, 0))
				{view.roomScrollPosX-=5;}
			if(keyX && view.willNotScrollRoomOutOfBounds(0, 5))
				{view.roomScrollPosY+=5;}
			if(keyW && view.willNotScrollRoomOutOfBounds(0, -5))
				{view.roomScrollPosY-=5;}
		}*/



		
		
		
	}

	//Can only happen once, so this is where save, load, and exit should go
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT: keyRight = false; break;
			case KeyEvent.VK_LEFT:   keyLeft = false; break;
			case KeyEvent.VK_UP:       keyUp = false; break;
			case KeyEvent.VK_DOWN:   keyDown = false; break;
			//case KeyEvent.VK_Q:		    keyQ = false; break;
			//case KeyEvent.VK_ESCAPE:  keyEsc = false; break;
			case KeyEvent.VK_A:		keyA	 = false; break;
			case KeyEvent.VK_D:		keyD	 = false; break;
			case KeyEvent.VK_W:		keyW	 = false; break;
			case KeyEvent.VK_X:		keyX	 = false; break;
			case KeyEvent.VK_J:	jumpRooms_doNotScroll = !jumpRooms_doNotScroll; break;
			//^^^Flip the truth value of jumpRooms_doNotScroll every time key 'J' is pressed and then released
			case KeyEvent.VK_P:	  view.editPotsAndNotTiles = !view.editPotsAndNotTiles;   break;
			//^^^Flip the truth value of editPotsAndNotTiles every time key 'P' is pressed and then released
			case KeyEvent.VK_E:		keyE	 = false; break;
		}

		char c = Character.toLowerCase(e.getKeyChar());
		if(c=='q' || c=='Q' || c==27/*ESC*/)	//if(q||Q||Esc isPressedAndReleased)
			{System.exit(0);}//{Exit program;}. The uppercase Q case never happens btw
		if(c=='s'){model.saveSpriteList();}//Save Tile locations to file
		if(c=='l'){model.loadSpriteList();}//Load Tile locations from file
		if(c=='e'){view.inEditMode = !view.inEditMode;}	//Toggles printing Editmode to screen.

		//if(c=='b'/*Character.CONTROL*/){model.addBoomerang();}	//Make Link throw Boomerang when keyB is pressed *and* released
		if(e.getKeyCode()==KeyEvent.VK_CONTROL){model.addBoomerang_usingLinkPosAndLinkDir();}	//Make Link throw Boomerang when ControlKey is pressed *and* released

		//Debug
		//if(jumpRooms_doNotScroll){System.out.println("jumpRooms");}
		//else{System.out.println("scrollRooms");}
		//System.out.printf("roomScrollPosX: %d, roomScrollPosY: %d\n", view.roomScrollPosX, view.roomScrollPosY);

		if(jumpRooms_doNotScroll)
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

			//CANNOT USE if(keyD) OR if(keyEsc) DUE TO UPDATING PROCESS ABOVE. MUST USE WHAT IS BELOW.
			//Modify view's visible-stuff-area
			/*if(c=='d' && view.willNotJumpRoomOutOfBounds(1,0))
				{view.roomScrollPosX+=View.widthPerRoom;}
			if(c=='a' && view.willNotJumpRoomOutOfBounds(-1,0))
				{view.roomScrollPosX-=View.widthPerRoom;}
			if(c=='x' && view.willNotJumpRoomOutOfBounds(0,1))
				{view.roomScrollPosY+=View.heightPerRoom;}
			if(c=='w' && view.willNotJumpRoomOutOfBounds(0,-1))
				{view.roomScrollPosY-=View.heightPerRoom;}*/
		}
	}

	public void keyTyped(KeyEvent e) {}

	//Update on keyboard presses. Does nothing when mouse is used.
	//update() == update_asTimeProgressesAtAConstantRate
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
		//int tileHeight   = view.tileImg.getHeight();//Height of turtle picture (#pixels)
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
		//final boolean noCollisionsBetweenTilesAndLink = model.linkIsNotCollidingWithAnyTiles();
		//final boolean noCollisionsBetweenPotsAndLink  = model.linkIsNotCollidingWithAnyPots();
		final boolean noCollisionsBetweenLinkAndAnyOtherSprite = model.linkIsNotCollidingWithAnyOtherSprites();



	
		model.updateAllPreviousLocations_whenLegal();

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

		
		//(LinkToesAreBelowBottomOfCurrRoom && roomBelowToJumpToIsLegal)
		//if(aboutToEnterRoomBelowCurrRoom){TriggerRoomSwitchToAdjacentRoomBelow}
		//Notice how I'm not changing Link's actual position when I change the room
		if((linkToesHeightY > bottomOfCurrRoomY)  &&  view.willNotJumpRoomOutOfBounds(0,1))
			{view.roomScrollPosY += View.heightPerRoom;}
		
		//(LinkToesAreAboveTopOfCurrRoom && roomAboveToJumpToIsLegal)
		//if(aboutToEnterRoomAboveCurrRoom){TriggerRoomSwitchToAdjacentRoomAbove}
		//Notice how I'm not changing Link's actual position when I change the room
		if((linkToesHeightY < topOfCurrRoomY)  &&  view.willNotJumpRoomOutOfBounds(0,-1))
			{view.roomScrollPosY -= View.heightPerRoom;}
		
		//(LinkRightSideIs_toLeftOf_LeftSideOfCurrRoom && roomToLeft_ToJumpTo_IsLegal)
		//if(aboutToEnterRoomToLeftOfCurrRoom){TriggerRoomSwitchToAdjacentRoomToLeft}
		//Notice how I'm not changing Link's actual position when I change the room
		if((linkRightSideX < leftOfCurrRoomX)  &&  view.willNotJumpRoomOutOfBounds(-1,0))
			{view.roomScrollPosX -= View.widthPerRoom;}
		
		//(LinkLeftSideIs_toRightOf_RightSideOfCurrRoom && roomToRight_ToJumpTo_IsLegal)
		//if(aboutToEnterRoomToRightOfCurrRoom){TriggerRoomSwitchToAdjacentRoomToRight}
		//Notice how I'm not changing Link's actual position when I change the room
		if((model.link.posnX > rightOfCurrRoomX)  &&  view.willNotJumpRoomOutOfBounds(1,0))
			{view.roomScrollPosX += View.widthPerRoom;}


		//I don't actually use these yet because I can't get the LinkShouldMove**** to work correctly
		if(keyDown)
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
		}


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