//Steven Lynch
//Mar 29, 2023
//Project: Character named Link can traverse graphical map via keys A,W,D,X. Map can switch between jumping between rooms
//  and scrolling between rooms. Can save and load Tile and Clay Pot locations (part of the map) via ArrayList and JSON file.
import java.util.ArrayList;


//THIS CLASS should do collision detection since it has access to both tileList AND Link
class Model
{
	View view;
	/*int turtle_x;		TURTLE-RELATED STUFF
	int turtle_y;*/
	ArrayList<Sprite> spriteList;
	//ArrayList has .size(), Array has .length
	Link link;
	boolean useJsonLink_NOTcurrGameLink_WhenLoadingSaveData;
	
	/*		TURTLE-RELATED STUFF
	//# of pixels the turtle will move per window update
	protected static final int movSpeed_pixls = 4;
	*/


	Model()
	{
		spriteList = new ArrayList<Sprite>();

		/*Tile t0 = new Tile(20, 20);		DEBUG
		spriteList.add(t0);*/

		link = new Link();		//Model's more-easily-accessible-and-modifiable Link shorthand
		//  (just "model.link" instead of "model.spriteList.get(0)" and "model.spriteList.set(0,new Link(posnX,posnY,dir))")

		spriteList.add(link);	//Makes sure that Link is always 1st in the list of Sprites.

		//The assignment likely expects this to be "false", not "true".
		//WARNING: SETTING THIS TO TRUE WILL RESULT IN UNTESTED AND BUGGY BEHAVIOR UPON LOADING
		useJsonLink_NOTcurrGameLink_WhenLoadingSaveData = false;
	}
	/*Model(View v)
	{view = v;}*/

	public void update()
	{
		//0,0 is topLeft corner
		//Math.min(4,-137) == -137

		/*		TURTLE-RELATED STUFF
		//turtle_x == this.turtle_x   (only for this specific class)
		
		// Move the turtle's position (as long as coordinates are + and don't cause flipped movement)
		//(Doesn't restrict bounds down and right, just up and left)
		if(this.turtle_x < this.dest_x)
		//if(turtleIsToLeftOfDest){setDestToFartherRight}
			//this.turtle_x += 1;
			turtle_x += Math.min(movSpeed_pixls, dest_x-turtle_x); //turt<dest, so - to achieve +distance
		else if(this.turtle_x > this.dest_x)
		//else if(turtleIsToRightOfDest){setDestToFartherLeft}
			//this.turtle_x -= 1;
			turtle_x -= Math.min(movSpeed_pixls, dest_x+turtle_x); //turt>dest, so + to achieve +distance
		if(this.turtle_y < this.dest_y)
		//if(turtleIsAboveDest){setDestToFartherDown}
			//this.turtle_y += 1;
			turtle_y += Math.min(movSpeed_pixls, dest_y-turtle_y); //turt<dest, so - to achieve +distance
		else if(this.turtle_y > this.dest_y)
		//else if(turtleIsBelowDest){setDestToFartherUp}
			//this.turtle_y -= 1;
			turtle_y -= Math.min(movSpeed_pixls, dest_y+turtle_y); //turt>dest, so + to achieve +distance
		*/


		//Call update method for all Sprites (including Link, Tiles, Pots)
		Sprite s;
		for(int i=0; i<spriteList.size(); i++)
		{
			s = spriteList.get(i);

			s.update();

			//if(endOfLifeForCurrSprite){deleteCurrSpritefromEverything;}
			//if(sHasHitAnotherSprite && timeElapsedSinceDisplaying_BrokenSprite_Img>1second){delete_s_fromEverything;}
			//if(ThisSpriteIsPotOrBmrg AND thisSpriteShouldNOTstillExistOnScreen AND brokenPotOrBmrgImgHasBeenDisplayedLongEnough){deleteThisSpritefromEverything;}
			if(s.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed && s.breakageToDeletionTimer<=0)
			{
				//Display brokenSprite img for some amount of time before deleting the Sprite
				//while(s.breakageToDeletionTimer-- > 0){System.out.println("Model.update(): breakageToDeletionTimer: "+s.breakageToDeletionTimer);}
				//^^^ This doesn't work because it stops the ENTIRE rest of the program while executing the loop AND it's too variably-timed

				spriteList.remove(s);
				if(Game.DEBUG){System.out.println("Model.update():  Removed Pot or Boomerang from spriteList that has both broken and finished being displayed");}
			}
		}
	}

	//Turtle-related function
	//Used by Controller for mouseInput=>TurtleDest
	/*public void setDestination(int x, int y)
	{
		//if( dest_x > (view.getWidth()-view.turtImg.getWidth()) )		//if(xIsPastWindowToRight)
		//	{this.dest_x = (view.getWidth()-view.turtImg.getWidth());}	//{dest_x = rightSideOfWindow;}
		//else if(dest_x < 0)			//if(xIsPastWindowToLeft)
		//	{this.dest_x = 0;}		//{dest_x = leftSideOfWindow;}
		//else{this.dest_x = x;}	//xIsInWindow, so assign directly.
		//
		//if( dest_y > (view.getHeight()-view.turtImg.getHeight()) )		//if(yIsPastWindowToBottom)
		//	{this.dest_y = (view.getHeight()-view.turtImg.getHeight());}	//{dest_y = bottomSideOfWindow;}
		//else if(dest_y < 0)			//if(yIsPastWindowToTop)
		//	{this.dest_y = 0;}		//{dest_y = topSideOfWindow;}
		//else{this.dest_y = y;}	//yIsInWindow, so assign directly.
		dest_x = x;
		dest_y = y;
	}*/





	//BOOMERANG STUFF
	public void addBoomerang_usingLinkPosAndLinkDir()
	{
		//uses Link's position
		int bmrgSpawnPosX = 25;
			 if(this.link.movDirX ==  1){bmrgSpawnPosX = this.link.posnX + this.link.width+1;}	//if(LinkIsFacingRight){throwBmrgFromLink'sRightEdge, to avoid collision with Link}
		else if(this.link.movDirX == -1){bmrgSpawnPosX = this.link.posnX;}						//if(LinkIsFacingLeft){throwBmrgFromLink'sLeftEdge, to avoid collision with Link}
		else if(this.link.movDirX ==  0){bmrgSpawnPosX = this.link.posnX + this.link.width/2;}	//if(LinkIsFacingUpOrDown){throwBmrgAwayFromLink'sHorizontalCenterlineAxis, for aesthetics}
		int bmrgSpawnPosY = 25;
			 if(this.link.movDirY ==  1){bmrgSpawnPosY = this.link.posnY + this.link.height+1;}	//if(LinkIsFacingDown){throwBmrgFromLink'sFeet, to avoid collision with Link}
		else if(this.link.movDirY == -1){bmrgSpawnPosY = this.link.posnY;}						//if(LinkIsFacingUp){throwBmrgFromLink'sHead, to avoid collision with Link}
		else if(this.link.movDirY ==  0){bmrgSpawnPosY = this.link.posnY + this.link.height/2;}	//if(LinkIsFacingLeftOrRight){throwBmrgAwayFromLink'sVerticalCenterlineAxis, for aesthetics}
		
		if(this.link.movDirX ==  0  && this.link.movDirY ==  0)
			{System.out.println("How is Link not facing any direction? Boomerangs will collide with Link as he throws them.");}

		int bmrgMovDirX = this.link.movDirX;	//uses Link's direction
		int bmrgMovDirY = this.link.movDirY;
		spriteList.add( new Boomerang(bmrgSpawnPosX, bmrgSpawnPosY, bmrgMovDirX, bmrgMovDirY) );
	}

	//This isn't necessary except mayyyyyyyybeeee for a more-specific debug
	public void removeBoomerang(int posX, int posY)
	{
		//i=0 is Link's sprite
		for(int i=1; i<spriteList.size(); i++)	//Deletes ALL coordinate matches of the Pot in the ArrayList
		{
			Sprite currSpriteInList = spriteList.get(i);
			//System.out.printf("bmrgPosX: %d, bmrgPosY: %d, currListBmrgX: %d, currListBmrgY: %d\n", bmrgPosX,bmrgPosY, currSpriteInList.x, currSpriteInList.y);
			//if(unsnappedFunctionInputX == currBoomerangInArrayList.x  &&  ...){removeCurrentBoomerangFromArrayList();}
			if( (posX == currSpriteInList.posnX) && (posY == currSpriteInList.posnY) )
			{spriteList.remove(i);}
		}
		
		//Debug
		//System.out.printf("#Sprites: %d\n",spriteList.size());
	}




	//POT STUFF
	public void addPot(int posX, int posY)
		{spriteList.add( new Pot(posX, posY) );}
	public void removePot(int posX, int posY)
	{
		//i=0 is Link's sprite
		for(int i=1; i<spriteList.size(); i++)	//Deletes ALL coordinate matches of the Pot in the ArrayList
		{
			Sprite currSpriteInList = spriteList.get(i);
			//System.out.printf("potPosX: %d, potPosY: %d, currListPotX: %d, currListPotY: %d\n", potPosX,potPosY, currSpriteInList.x, currSpriteInList.y);
			//if(unsnappedFunctionInputX == currPotInArrayList.x  &&  ...){removeCurrentPotFromArrayList();}
			if( (posX == currSpriteInList.posnX) && (posY == currSpriteInList.posnY) )
			{spriteList.remove(i);}
		}
		
		//Debug
		//System.out.printf("#Sprites: %d\n",spriteList.size());
	}


	//TILE STUFF
	public void addTile(int posX, int posY)
	{
		int tilePosX = snapToGrid(posX);
		int tilePosY = snapToGrid(posY);
		spriteList.add( new Tile(tilePosX, tilePosY) );
		
		//Debug
		//System.out.printf("Tile x: %d, Tile y %d\n", tilePosX, tilePosY);
		//System.out.printf("MouseClicked x: %d, MouseClicked y %d\n", posX, posY);
		//System.out.printf("#Tiles: %d\n",tileList.size());
	}
	public void removeTile(int posX, int posY)
	{
		int tilePosX = snapToGrid(posX);
		int tilePosY = snapToGrid(posY);
		for(int i=0; i<spriteList.size(); i++)	//Deletes ALL coordinate matches of the Tile in the ArrayList
		{
			//System.out.printf("tilePosX: %d, tilePosY: %d, currListTileX: %d, currListTileY: %d\n",tilePosX, tilePosY, tileList.get(i).x, tileList.get(i).y);
			//if(snappedFunctionInputX == currTileInArrayList.x  &&  ...){removeCurrentTileFromArrayList();}
			if( (tilePosX == spriteList.get(i).posnX) && (tilePosY == spriteList.get(i).posnY) )
			{spriteList.remove(i);}
		}
		
		//Debug
		//System.out.printf("#Tiles: %d\n",tileList.size());
	}


	

	//Assumes Tile width == Tile height
	public int snapToGrid(int unsnappedCoord)
	{return unsnappedCoord - (unsnappedCoord % (new Tile()).width);}	//Snap "unsnappedCoord" to a grid coordinate
	//If Java graphics drew from the lower right corner instead of the upper left corner, it would be '+' instead of '-'
	//If Java graphics drew from the middle of the right side instead of the upper left corner,
	//  this one method would need to be split into x and y variants, with their correct sign (+-) usage.

	//Can't go in the Tile class because it doesn't have access to the ArrayList.
	//This class (Model) does have access to the ArrayList of Tiles.
	boolean existingTileWasClickedOn(int posX, int posY)
	{
		//I did it in Model, looped through the Tile arraylist, compared
		//snapToGrid(clickX) == currTileInListX && snapToGrid(clickY) == currTileInListY,
		//returning true IMMEDIATELY upon a match, but returning false after entire arraylist is looped through once.
		//Keep in mind that everything in the ArrayList is ALREADY 'snapped to a grid'
		int tilePosX = snapToGrid(posX);
		int tilePosY = snapToGrid(posY);
		for(int i=0; i<spriteList.size(); i++)
		{
			if( (tilePosX == spriteList.get(i).posnX) && (tilePosY == spriteList.get(i).posnY) )
			{return true;}
		}
		return false;
	}




	//Updates either 1)All Sprites, 2)Only model's version of Link, 3)No Sprites.
	//This is meant to be faster rather than ultra-thorough, since this happens EVERY single displayed game frame.
	public void updateAllPreviousLocations_whenLegal()
	{
		final boolean noCollisions = (detectCollisionsInSpriteList() == null);

		if(noCollisions)
		{
			for(int i=0; i<spriteList.size(); i++)
			{
				Sprite spr = spriteList.get(i);
				//Only set previous location if current location is valid (no collisions at all among all Sprites)
				//  AND updating the previous location could be useful.
				if(spr.PREV_posnX != spr.posnX   &&   spr.PREV_posnY != spr.posnY)
				{
					spr.setPreviousLocation();	//Update the COPY of spriteList's currSprite
					spriteList.set(i, spr);		//Update(replace) spriteList's index i with the (updated) currSprite
					if(Game.DEBUG) System.out.println("Controller.update(): noCollisionsWithAnyOtherSprites AND prevPosn!=currPosn, so Set currSprite's Previous Location");
				}
			}
		}

		//Updates model's copy of Link, which the above block doesn't do
		//Only set previous location if current Link location is valid (no collisions at all between Link and other Sprites)
		//  AND updating the previous location could be useful.
		if(linkIsNotCollidingWithAnyOtherSprites()   &&   this.link.PREV_posnX != this.link.posnX  &&  this.link.PREV_posnY != this.link.posnY)
		{
			this.link.setPreviousLocation();
			if(Game.DEBUG) System.out.println("Controller.update(): linkIsNotCollidingWithAnyOtherSprites AND prevPosn!=currPosn, so Set Link's Previous Location");
		}
	}




	//Returns two colliding Sprites (which could be null)
	//  and
	//the spriteList indices of the two colliding Sprites in the 3rd Sprite (formatted as a Tile's x and y positions)
	public Sprite[] detectCollisionsInSpriteList()
	{
		//if(Game.DEBUG) System.out.println("Entered Model.detectCollisionsInSpriteList()");
		//int spriteListLength = model.spriteList.size();	//This line doesn't update as often as needed, causing indexOutOfBounds errors inside the for() loops
		//The for() loop below needs to make sure the object isn't checking if it's colliding with itself. if(i!=j){checkForCollision} satisfies that.
		for(int i=1; i<this.spriteList.size(); i++)
		{
			Sprite currSprite = spriteList.get(i);

			//if(LinkIsCollidingWithAnyOtherObjectIn_listOfSprites){returnCollidingSprites;}
			//This block is necessary because I don't always make sure that the Link in spriteList has the same data as model.link (which is easier to work with)
			if(this.link.thisSpriteIsCollidingWith_PassedInSprite(currSprite))
			{
				//Yes, creating Tile(indxOfSpr1,indxOfSpr2) is a hacky way to pass in a simple pair of integers (along with the two other meaningful Sprites)
				Sprite indicesOfCollidingSprites_asPosnXandY_Carrier = new Tile(0,i);	//link'sIndexInSpriteList==0, otherCollidingSprite'sIndex_in_spriteList==i
				Sprite[] collidingSprites = {this.link, currSprite,  indicesOfCollidingSprites_asPosnXandY_Carrier};
				return collidingSprites;
			}

			//j=1 instead of j=0 because the Link in spriteList may not have accurate/up-to-date data, so it should be ignored entirely
			for(int j=1; j<this.spriteList.size(); j++)
			{
				Sprite otherSprite = spriteList.get(j);
				
				//Tiles don't move, so don't check for Tile-on-Tile collisions. Necessary because Tiles share boundaries. MAY improve performance.
				//Probably makes performance worse tbh since there is one more thing to check, UNLESS you have tons of tiles.
				//	//boolean bothSpritesAreNotTileSprites  =  currSprite.isTile()==false && otherSprite.isTile()==false;
				//    ^^^FAULTY LOGIC WHEN USED IN THE if() BELOW BECAUSE COMPARING EVEN ONE TILE MAKES IT NOT GET CHECKED FOR COLLISIONS
				boolean notBothSpritesAreTileSprites  =  currSprite.isTile()==false || otherSprite.isTile()==false;

				//Ensure the object isn't checking if it's colliding with itself.
				//  object123 will definitely collide with object123 since they're guaranteed to have the same coordinates and all other object-specific info
				boolean checkingForCollisionWithSelf = i==j;


				//if(Sprite_j_IsCollidingWithAnyOtherObjectIn_listOfSprites){returnCollidingSprites;}
				if( notBothSpritesAreTileSprites && !checkingForCollisionWithSelf  &&  currSprite.thisSpriteIsCollidingWith_PassedInSprite(otherSprite))
				{
					//Yes, creating Tile(indxOfSpr1,indxOfSpr2) is a hacky way to pass in a simple pair of integers (along with the two other meaningful Sprites)
					Sprite indicesOfCollidingSprites_asPosnXandY_Carrier = new Tile(i,j);
					Sprite[] collidingSprites = {currSprite,otherSprite,indicesOfCollidingSprites_asPosnXandY_Carrier};
					return collidingSprites;
				}
			}
		}
		return null;	//There are no colliding Sprites
	}
	public void fixCollision(Sprite[] collidingSprites)
	{
		//.length is fine (in terms of NullPointerExceptions) because collidingSprites is guaranteed to not be null due to early termination from the prior (Array!=null) check in the if()
		if(collidingSprites != null  && collidingSprites.length == 3)
		{
			Sprite spr1 = collidingSprites[0];
			Sprite spr2 = collidingSprites[1];

			//Yes, using Tile(indxOfSpr1,indxOfSpr2) is a hacky way to pass in a simple pair of integers (along with the two other meaningful Sprites)
			//In this method, I (may) modify both colliding Sprites, but I definitely write both Sprites back to spriteList
			int spriteListIndexOfSprite1ToFix = collidingSprites[2].posnX;
			int spriteListIndexOfSprite2ToFix = collidingSprites[2].posnY;


			//if(colliding Sprites are Pot and Link){kickPot}
			if((spr1.isPot() && spr2.isLink())  ||  (spr1.isLink() && spr2.isPot()))
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  Collision between Link and Pot Sprite");
				this.detectLinkCollidingWithPot_kickThatPot();
			}

			//if(colliding Sprites are Pot and Pot){breakPots,InitiatePotDeletionCountdownTimers}
			else if(spr1.isPot() && spr2.isPot())
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  Collision between Pot and Pot Sprites");

				//Pots are breakable/shatterable, Tiles and Links are not breakable/shatterable.
				//Since both spr1 and spr2 are Pots, they're both breakable, making   if(spr1.isBreakable)  unnecessary

				//breakTheSprite by showing the brokenSprite image
				spr1.currImgIndex = 1;	//Assuming index0==pictureOfIntactSprite and index1==pictureOfBrokenSprite
				spr2.currImgIndex = 1;	//Assuming index0==pictureOfIntactSprite and index1==pictureOfBrokenSprite

				//Mark these Sprites for deletion
				spr1.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;
				spr2.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;
				if(Game.DEBUG){System.out.println("Model.fixCollision():  PotPotCollision: forBOTHpots: Pot.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true");}

				//Wait x# of seconds then delete the Sprite entirely (done in Model.update(), which deletes a Sprite if its (isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed == false && breakageToDeletionTimer<=0))
				//while(breakageToDeletionTimer-- > 0){System.out.println("Sprite.kickThisSprite_AwayFrom_PassedInSprite(): breakageToDeletionTimer: "+breakageToDeletionTimer);}
				//^^^ This doesn't work because it stops the ENTIRE rest of the program while executing the loop AND it's too variably-timed

				//Next place:  Model.fixCollision(): Model.detectLinkCollidingWithPot_kickThatPot(): specificPot.kickThisSpriteAwayFromPassedInSprite: spriteList.remove();
			}
			//if(colliding Sprites are Pot and Boomerang){breakPotAndBoomerang,InitiatePotDeletionCountdownTimers}
			else if((spr1.isPot() && spr2.isBoomerang()) || (spr1.isBoomerang() && spr2.isPot()))
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  Collision between Pot and Boomerang Sprites");

				//Pots are breakable/shatterable, Tiles and Links are not breakable/shatterable. 
				//Since both spr1 and spr2 are a Pot and Boomerang (though not necessarily in that order), they're both breakable,
				//making   if(spr#.isBreakable)  unnecessary

				//breakTheSprite by showing the brokenSprite image
				//Assuming index0==pictureOfIntactSprite and index1==pictureOfBrokenSprite
					 if(spr1.isPot()){spr1.currImgIndex = 1;}
				else if(spr2.isPot()){spr2.currImgIndex = 1;}

				//Mark these Sprites for deletion
				spr1.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;
				spr2.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;
				if(Game.DEBUG){System.out.println("Model.fixCollision():  PotBoomerangCollision: forBOTHpotANDboomerang: PotAndBoomerang.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true");}

				//Wait x# of seconds then delete the Sprite entirely (done in Model.update(), which deletes a Sprite if its (isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed == false && breakageToDeletionTimer<=0))
				//while(breakageToDeletionTimer-- > 0){System.out.println("Sprite.kickThisSprite_AwayFrom_PassedInSprite(): breakageToDeletionTimer: "+breakageToDeletionTimer);}
				//^^^ This doesn't work because it stops the ENTIRE rest of the program while executing the loop AND it's too variably-timed

				//Next place:  Model.fixCollision(): Model.detectLinkCollidingWithPot_kickThatPot(): specificPot.kickThisSpriteAwayFromPassedInSprite: spriteList.remove();
			}
			//if(colliding Sprites are Pot and Pot){breakPots,InitiatePotDeletionCountdownTimers}
			else if((spr1.isTile() && spr2.isBoomerang()) || (spr1.isBoomerang() && spr2.isTile()))
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  Collision between Pot and Boomerang Sprites");

				//Pots are breakable/shatterable, Tiles and Links are not breakable/shatterable. 
				//Since both spr1 and spr2 are a Pot and Boomerang (though not necessarily in that order), they're both breakable,
				//making   if(spr#.isBreakable)  unnecessary

				//breakTheSprite by showing the brokenSprite image
				//Neither Tile nor Boomerang have brokenSprite images, so don't alter currImgIndex or stuff

				//Mark these Sprites for deletion
				if(!spr1.isTile()){spr1.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;}
				if(!spr2.isTile()){spr2.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;}
				if(Game.DEBUG){System.out.println("Model.fixCollision():  TileBoomerangCollision: forBoomerang: Boomerang.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true");}

				//Wait x# of seconds then delete the Sprite entirely (done in Model.update(), which deletes a Sprite if its (isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed == false && breakageToDeletionTimer<=0))
				//while(breakageToDeletionTimer-- > 0){System.out.println("Sprite.kickThisSprite_AwayFrom_PassedInSprite(): breakageToDeletionTimer: "+breakageToDeletionTimer);}
				//^^^ This doesn't work because it stops the ENTIRE rest of the program while executing the loop AND it's too variably-timed

				//Next place:  Model.fixCollision(): Model.detectLinkCollidingWithPot_kickThatPot(): specificPot.kickThisSpriteAwayFromPassedInSprite: spriteList.remove();
			}

			//if(colliding Sprites are Pot and Tile){breakPot,InitiatePotDeletionCountdownTimer}
			else if((spr1.isPot() && spr2.isTile())  ||  (spr1.isTile() && spr2.isPot()))
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  Collision between Pot and Tile Sprites");

				//Pots are breakable/shatterable, Tiles and Links are not breakable/shatterable.
	//POSSIBLE ERROR AREA BECAUSE I'M *SOMETIMES* TREATING spr1 and spr2 AS INTERCHANGEABLE, BUT NOT ALWAYS
				if(spr1.isBreakable)
				{
					//breakTheSprite by showing the brokenSprite image
					spr1.currImgIndex = 1;	//Assuming index0==pictureOfIntactSprite and index1==pictureOfBrokenSprite

					//Mark this Sprite for deletion
					spr1.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;
					if(Game.DEBUG){System.out.println("Model.fixCollision():  PotTileCollision: Pot.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true");}

					//Wait x# of seconds then delete the Sprite entirely (done in Model.update(), which deletes a Sprite if its (isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed == false && breakageToDeletionTimer<=0))
					//while(breakageToDeletionTimer-- > 0){System.out.println("Sprite.kickThisSprite_AwayFrom_PassedInSprite(): breakageToDeletionTimer: "+breakageToDeletionTimer);}
					//^^^ This doesn't work because it stops the ENTIRE rest of the program while executing the loop AND it's too variably-timed

					//Next place:  Model.fixCollision(): Model.detectLinkCollidingWithPot_kickThatPot(): specificPot.kickThisSpriteAwayFromPassedInSprite: spriteList.remove();
				}
				if(spr2.isBreakable)
				{
					//breakTheSprite by showing the brokenSprite image
					spr2.currImgIndex = 1;	//Assuming index0==pictureOfIntactSprite and index1==pictureOfBrokenSprite

					//Mark this Sprite for deletion
					spr2.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true;
					if(Game.DEBUG){System.out.println("Model.fixCollision():  TilePotCollision: Pot.isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = true");}
				}
			}
			else
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  Collision, but not between nor (Pot and Link), nor (Pot and Pot), nor (Pot and Tile)."+
													",  nor (Pot and Boomerang)");
			}

			if(Game.DEBUG) System.out.println("~~~~~spr1.isPot():  "+spr1.isPot() +", spr2.isPot():  "+spr2.isPot()+
											"\n~~~~~spr1.isLink(): "+spr1.isLink()+", spr2.isLink(): "+spr2.isLink()+
											"\n~~~~~spr1.isTile(): "+spr1.isTile()+", spr2.isTile(): "+spr2.isTile());

			//Only fix remaining collisions after taking care of special cases like Pot collisions
			//if(not_SpriteThatIsSupposedToBeImmovable){fixTheSprite'sCollision}
			if(!spr1.isTile())	//DON'T MOVE THE TILES
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  spr1_notTile.pushThisSprite_OutOf_PassedInSprite()");
				spr1.pushThisSprite_OutOf_PassedInSprite(spr2);
				if(Game.DEBUG) System.out.println("\n\n");	//Clarity in the console
			}
			else if(!spr2.isTile())	//DON'T MOVE THE TILES
			{
				if(Game.DEBUG) System.out.println("Model.fixCollision():  spr2_notTile.pushThisSprite_OutOf_PassedInSprite()");
				spr2.pushThisSprite_OutOf_PassedInSprite(spr1);
				if(Game.DEBUG) System.out.println("\n\n");	//Clarity in the console
			}

			//Update spriteList (at index spriteListIndexOfSprite_#_ToFix) with now-not-colliding Sprite spr1 and spr2
			spriteList.set(spriteListIndexOfSprite1ToFix, spr1);
			spriteList.set(spriteListIndexOfSprite2ToFix, spr2);
		}
		else if(collidingSprites != null)
			if(Game.DEBUG) System.out.println("Model.fixCollision():  A strange (and illegal) # of Sprite Collisions???. This method shouldn't have been called");
		else
			if(Game.DEBUG) System.out.println("Model.fixCollision():  No Sprite Collisions. This method shouldn't have been called");

	}

	public void fixAllExistingCollisions()
	{
		Sprite[] collidingSprites = detectCollisionsInSpriteList();	//Function returns the two 1st colliding Sprites in the list
		int numCollisionFixAttempts = 0;	//Prevents invalid PrevPosn from causing an infinite loop of trying to fix a Sprite's position
		//^^^This is a bandaid on a bigger issue - improperly fixing collisions (yes that's vague, but it's accurate)

		while(collidingSprites != null  &&  numCollisionFixAttempts++ < 10)	//while(thereAreAtLeastTwoCollidingSprites && reasonableMax#ofCollisionsExist)
		{
			if(Game.DEBUG) System.out.println("\n\n\n\nModel.fixAllExistingCollisions():  Collision between at least two Sprites");
			if(Game.DEBUG) System.out.println("Model.fixAllExistingCollisions():  numCollisionFixAttempts="+numCollisionFixAttempts);
			
			fixCollision(collidingSprites);

			collidingSprites = detectCollisionsInSpriteList();	//Update condition for while() loop
		}
	}



	//Check if Link is colliding with ANY Tile
	boolean linkIsNotCollidingWithAnyTiles()
	{
		//int possibleLinkX =  model.snapToGrid(model.link.viewerPOV_LeftSideOfBodyX);
		//int possibleLinkY =  model.snapToGrid(model.link.headHeightY);
		boolean atLeastOneCollision = false;
		for(int i=1; i<spriteList.size(); i++)
		{
			Sprite currSprite = spriteList.get(i);
			//Sprite listLink = spriteList.get(0);		//Link is always the first item (index 0) in the ArrayList
			
			//if( currSprite.isTile() && listLink.linkIsCollidingWith_PassedInTileOrOtherSprite(currSprite)_thisIsASpriteMethod )
			if( currSprite.isTile() && this.link.thisSpriteIsCollidingWith_PassedInSprite(currSprite) )
			{
				atLeastOneCollision = true;
				if(Game.DEBUG) System.out.println("Model.linkIsNotCollidingWithAnyTiles():  Collision between Link and Tile (Sprite #"+(i+1)+")\n\n");
			}
			//if( currTile.coordsMatchThisExistingTile(possibleLinkX, possibleLinkY) )
			//{atLeastOneCollision = true;}
		}
		return !atLeastOneCollision;
	}
	//Check if Link is colliding with ANY Tile
	boolean linkIsNotCollidingWithAnyPots()
	{
		boolean atLeastOneCollision = false;
		for(int i=1; i<spriteList.size(); i++)
		{
			Sprite currSprite = spriteList.get(i);
			//Sprite listLink = spriteList.get(0);		//Link is always the first item (index 0) in the ArrayList
			
			//if( currSprite.isPot() && model'sLinkSprite.linkIsCollidingWith_PassedInTileOrOtherSprite(currSprite)_thisIsASpriteMethod )
			if( currSprite.isPot() && this.link.thisSpriteIsCollidingWith_PassedInSprite(currSprite) )
			{
				atLeastOneCollision = true;
				if(Game.DEBUG) System.out.println("Model.linkIsNotCollidingWithAnyPots():  Collision between Link and Pot (Sprite #"+(i+1)+")\n\n");
			}
		}
		return !atLeastOneCollision;
	}
	//Check if Link is colliding with ANY other Sprite
	boolean linkIsNotCollidingWithAnyOtherSprites()
	{
		//int possibleLinkX =  model.snapToGrid(model.link.viewerPOV_LeftSideOfBodyX);
		//int possibleLinkY =  model.snapToGrid(model.link.headHeightY);
		boolean atLeastOneCollision = false;
		for(int i=1; i<spriteList.size(); i++)
		{
			Sprite currSprite = spriteList.get(i);
			//Sprite listLink = spriteList.get(0);		//Link is always the first item (index 0) in the ArrayList
			
			//if( model'sLinkSprite.linkIsCollidingWith_PassedInTileOrOtherSprite(currSpriteInListOfAllSprites)_thisIsASpriteMethod )
			if( this.link.thisSpriteIsCollidingWith_PassedInSprite(currSprite) )
			{
				atLeastOneCollision = true;
				if(Game.DEBUG) System.out.println("Model.linkIsNotCollidingWithAnyOtherSprites():  Collision between Link and Sprite #"+(i+1)+"\n\n");
			}
		}
		return !atLeastOneCollision;
	}

	//Checks if Link is colliding with ANY Pot
	//Returns the Sprite (Pot) that Link is colliding with (or returns null for no Link-Pot collisions)
	Sprite detectLinkCollidingWithPot_kickThatPot()
	{
		//int possibleLinkX =  model.snapToGrid(model.link.viewerPOV_LeftSideOfBodyX);
		//int possibleLinkY =  model.snapToGrid(model.link.headHeightY);
		//^^^ Pots aren't snapped to grid, meaning this wouldn't work

		for(int i=1; i<spriteList.size(); i++)
		{
			Sprite currSprite = spriteList.get(i);
			//Sprite listLink = spriteList.get(0);		//Link is always the first item (index 0) in the ArrayList
			
			//if( currSprite.isPot() && model.link.linkIsCollidingWith_PassedInTileOrOtherSprite(currSprite)_thisIsASpriteMethod )
			if( currSprite.isPot() && this.link.thisSpriteIsCollidingWith_PassedInSprite(currSprite) )
			{
				if(Game.DEBUG) System.out.println("Model.detectLinkCollidingWithAnyPot_kickThatPot():  Collision between Link and Pot (Sprite #"+(i+1)+")\t\tKicking Pot");
				
				//Kick the Pot away from Link. Updates the COPY of the Pot (named currSprite) from spriteList (not the actual Pot in the ArrayList named spriteList)
				currSprite.kickThisSprite_AwayFrom_PassedInSprite(this.link);

				//Update spriteList's Pot that was kicked
				spriteList.set(i, currSprite);
				if(Game.DEBUG) System.out.println("Model.detectLinkCollidingWithAnyPot_kickThatPot():  Update spriteList's Pot that was kicked\n");
				return currSprite;
			}
			//if( link.coordsMatchThisExistingPot'sHitbox(possiblePotX, possiblePotY) ){atLeastOneCollision = true;}
			//^^^ Pots aren't snapped to grid, meaning this wouldn't work
		}
		return null;
	}







	void saveSpriteList()
		{this.marshal();}
	//Turn tileList into JSON format, then save that format to file. Returns the Json object that was saved to file.
	Json marshal()
	{
		Json jsonObj = Json.newObject();
		Json tmpList = Json.newList();
		System.out.println("\n\nSaving map...\n\n");
		jsonObj.add("Sprite Positions",tmpList);
		for(int i=0; i<spriteList.size(); i++)
		{
			tmpList.add( spriteList.get(i).marshal() );
		}
		jsonObj.save("map.json");
		System.out.println("\n\nMap saved.\n\n");
		return jsonObj;
	}

	void loadSpriteList()
		{this.unmarshal();}
	void unmarshal()
	{
		System.out.println("\n\nLoading map...\n***Game's movement will be disabled during the loading process\n\n");
		spriteList.clear();	//Empty (and eventually overwrite) the existing ArrayList
		

		Json jsonObj = Json.load("map.json");			/*Loads Json list (of all Tile locations) into a Json object*/
		Json tmpList = jsonObj.get("Sprite Positions");	/*Gets (in the Json file) name of ENTIRE Json list*/

		//IF, UPON LOADING, YOU WANT TO USE LINK'S STORED/SAVED POSITION AND NOT WHAT YOU WERE PREVIOUSLY USING,
		//  useJsonLink_NOTcurrGameLink_WhenLoadingSaveData=false
		//  YOU MUST ALSO NOT USE "spriteList.add( this.link );" BECAUSE TWO DIFFERENT LinkS WITH POSSIBLY DIFFERENT DATA IS UNWANTED.
		if(useJsonLink_NOTcurrGameLink_WhenLoadingSaveData)
		{
			Json tmpLinkCharacter_data = tmpList.get(0);
			Link tmpLinkCharacter = new Link( tmpLinkCharacter_data );
			spriteList.add( tmpLinkCharacter );	//First item (0th index) in the Json list will now be Link's info (x,y,dir)
			link = tmpLinkCharacter;			//Update the Model's more-easily-accessible Link shorthand (just model.link instead of model.spriteList.get(0))
			if(Game.DEBUG)	System.out.println("useJsonLink_NOTcurrGameLink_WhenLoadingSaveData == true");
		}
		else
		{	//Add Model's link object (game character) to the empty list of game objects
			spriteList.add( this.link );
			if(Game.DEBUG)	System.out.println("useJsonLink_NOTcurrGameLink_WhenLoadingSaveData == false");
		}

		
		for(int i=1; i<tmpList.size(); i++)				//i=0 is Link's info
		{
			try
			{
				try{
					spriteList.add( new Tile( tmpList.get(i) ) );
					//ArrayList.add(new Tile( JsonObject )   )
				}
				catch(Exception e)
				{
					//e.printStackTrace(System.err);
					//System.out.println("Failed to add Tile to spriteList");
				}

				try{
					spriteList.add( new Pot(  tmpList.get(i) ) );
					//ArrayList.add(new Pot( JsonObject )   )
				}
				catch(Exception e)
				{
					//e.printStackTrace(System.err);
					//System.out.println("Failed to add Pot to spriteList");
				}

				try{
					spriteList.add( new Boomerang(  tmpList.get(i) ) );
					//ArrayList.add(new Pot( JsonObject )   )
				}
				catch(Exception e)
				{
					//e.printStackTrace(System.err);
					//System.out.println("Failed to add Boomerang to spriteList");
				}
				if(Game.DEBUG)	System.out.println("");
			}
			catch(Exception e)
			{
				e.printStackTrace(System.err);
				System.out.println("Failed to add Pot AND Failed to add Tile AND Failed to add Boomerang to spriteList");
				System.exit(1);		//Exit the entire game
			}
			//These complicated blocks could be eliminated by adding more Json lists to the map.json file
		}
		System.out.println("\n\nMap loaded.\n\n");
	}
}
