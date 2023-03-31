//Steven Lynch
//Mar 30, 2023
//Link LegendOfZelda Project: Character named Link can traverse graphical map via arrow keys or A,W,D,X.
//Map can switch between jumping between rooms and scrolling between rooms by pressing key J.
//Can save and load Link, Tile, Clay Pot, Boomerang locations via ArrayList and Json file by pressing 's'(save) or 'l'(load).
//To be able to add/remove Tiles (boundaries that Link cannot cross) or add a Clay Pot,
//  1) Enter edit mode by pressing key E
//  2) Switch to AddPot mode (exit TileAddition/Removal mode) by pressing keyP.
//Press key CTRL to throw a boomerang.

import java.awt.image.BufferedImage;
import java.awt.Graphics;



public abstract class Sprite
{
	//Set to 0 by default (since they're class (primitive) variables)
	//Not every Sprite will use movSpeed, movDirX, movDirY, and noSlidingFriction, but it makes it easier to include it here in case I ever
	//  change my mind and want to make a sprite movable.
	//currImgIndex is only used for animation
	//movDirX and movDirY should only ever be -1,0,1. Other values would improperly speed up/slow down the Sprite.
	int posnX, posnY, PREV_posnX, PREV_posnY, currImgIndex, width, height, movSpeed, movDirX, movDirY, breakageToDeletionTimer;
	public boolean isBreakable = false;		//Default value MUST be false since Link is not considered breakable. Breakable Sprites must override this line
	public boolean isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed = false;	//Default value MUST be false since all Sprites should exist on screen for at least a little while
	public static final boolean noSlidingFriction = true;	//false==frictionExists, true==frictionDNE

	//Set to null by default (since it's a class (reference) variable)
	//Only the 1st element in the image ArrayList is needed for Tile, but the Link and Pot classes need more than 1 image sprite.
	//Each specific Sprite class will need to allocate memory for the Array
	BufferedImage[] imgs;


	//This isn't abstract because I make a default toString here, but every Sprite really should Override this toString because
	//  not every Sprite uses ALL the info Sprite provides
	@Override
	public String toString()
	{return "!!!!!Sprite:  posnX="+posnX+", posnY="+posnY+", PREV_posnX="+PREV_posnX+", PREV_posnY="+PREV_posnY
		+",  width="+width+", height="+height+",  movSpeed="+movSpeed+",  movDirX="+movDirX+", movDirY="+movDirY+", noSlidingFriction: "+noSlidingFriction
		+",  imgs: "+((imgs==null)?"NULL":"exists");}

	//These functions will be overridden in the subclasses by the subclasses that extend Sprite
	//I forgot to implement isTile() in the Tile class, leading to inaccurate falsehoods, and it took forever to find this error since it defaulted to false
	//  , so I was going to make it abstract to force it to be implemented by every Sprite, but that violates inheritance principles and could be dangerous.
	//  Also, it would mean that there would be no default value (because there will be no implementation) in this class (both a good and bad thing).
	public boolean isTile(){		return false;}
	public boolean isLink(){		return false;}
	public boolean isPot(){			return false;}
	public boolean isBoomerang(){	return false;}

	//These methods must be implemented in some class that extends this Sprite class
	public abstract void update();
	public abstract void draw(Graphics g, int adjusted_xPosToDrawAt, int adjusted_yPosToDrawAt);
	public abstract Json marshal();
	//public abstract void unmarshal();	//Simplified name of this line is just a constructor, which can't be labeled polymorphically without a wrapper
	//  like savedDataConstructor();, where each class would then have to implement something like savedDataConstructor(){Tile(savedDataFrom_SaveFile);}





	//Sets previousSpriteLocation to currentSpriteLocation
	//This method is only called to determine traveling direction, not travel amount
	public void setPreviousLocation()
	{
		PREV_posnY = posnY;
		PREV_posnX = posnX;
		if(Game.DEBUG) System.out.println("Sprite.setPreviousLocation():  Set Sprite's Previous Location");
	}

	public void move_walk(int distanceX, int distanceY)
	{
		posnY += distanceY;
		posnX += distanceX;

			 if(distanceX>0){movDirX= 1;}
		else if(distanceX<0){movDirX=-1;}
		else{				 movDirX= 0;}
			 if(distanceY>0){movDirY= 1;}
		else if(distanceY<0){movDirY=-1;}
		else{				 movDirY= 0;}
		if(Game.DEBUG) System.out.println("Sprite.move_walk():  Sprite moved/walked and updated its movement directions");
	}



	//Collision Detection
	public boolean thisSpriteIsCollidingWith_PassedInSprite(Sprite otherSprite)
	{
		//this.Left == turtle_LeftSideOfBody		     In this method's example, this==specificCharacterNamedLink
		//otherSprite.x == someTile_LeftSideXcoordinate        otherSprite.x + Tile.width == someTile_RightSideXcoordinate
		//VERY IMPORTANT THESE ARE NOT else if()S BECAUSE THEN THEY WOULDN'T <bold>ALL</bold> BE CHECKED.
		if(this.posnX+this.width <= otherSprite.posnX){				return false;}	//if(Link_rightSide toLeftOf  Tile_leftSide)
		if(this.posnX  >= otherSprite.posnX + otherSprite.width){	return false;}	//if(Link_leftSide  toRightOf Tile_rightSide)

		//Remember that +y is in the usuallyNegativeY direction, meaning that above and below have swapped signs. I'm still using the standard definition of above and below
		if(this.posnY+this.height <= otherSprite.posnY){			return false;}	//if(Link_feet ABOVE Tile_top)
		if(this.posnY >= otherSprite.posnY + otherSprite.height){	return false;}	//if(Link_head BELOW Tile_bottom)

		//Implicitly calls toString() for this Sprite (ex: Link) and otherSprite (ex: Tile)
		if(Game.DEBUG) System.out.println(">>Sprite.thisSpriteIsCollidingWith_PassedInSprite():  These Two Colliding Sprites:\n"+this+"\n>>,\n"+otherSprite);
		return true;
	}


	//Collision Detection and Fixing
	//Only modifies the Sprite calling this method. DOES NOT MODIFY THE SPRITE PASSED INTO THE METHOD (Sprite s2) due to Java's read-only pass-by-reference.
	public void pushThisSprite_OutOf_PassedInSprite(Sprite s2)
	{
		if(Game.DEBUG){System.out.println("\n>>Sprite.pushThisSprite_OutOf_PassedInSprite():  Attempted to get Sprite (willGetFixed)\n"
											+this.toString()+"\n>>out of Sprite (willStay)\n"+s2.toString());}


		//In the examples, Link is Sprite1 (i.e., "this") and Tile is Sprite2
		int spr2_top    = s2.posnY;				//Top edge of Tile
		int spr2_bottom = spr2_top + s2.height;	//Bottom edge of Tile
		int spr2_left  = s2.posnX;				//Left edge of Tile
		int spr2_right = spr2_left + s2.width;	//Right edge of Tile

		//if(LinkRightSideOfBody isProbablyInsideTile  &&  PREV_LinkRightSideBody isOutsideTile)
		//if(viewerPOV_RightSideOfBodyX >= spr2_left  &&  PREV_viewerPOV_RightSideOfBodyX <= spr2_left  &&  (viewerPOV_LeftSideOfBodyX - PREV_viewerPOV_LeftSideOfBodyX)>0)
		if(this.posnX+this.width >= spr2_left  &&  this.PREV_posnX+this.width <= spr2_left  &&  (this.posnX - this.PREV_posnX)>0)
		{
			//Notice that this is the 1st if() block in a series of if()s, so if stationary objects are colliding, this will be the 1st correction to be executed
			//viewerPOV_RightSideOfBodyX = spr2_left;
			this.posnX = spr2_left - this.width;
			if(Game.DEBUG)System.out.println("Collision from Left");
		}
		//if(LinkLeftSideOfBody isProbablyInsideTile  &&  PREV_LinkLeftSideBody isOutsideTile)
		//if(viewerPOV_LeftSideOfBodyX <= spr2_right  &&  PREV_viewerPOV_LeftSideOfBodyX >= spr2_right  &&  (viewerPOV_LeftSideOfBodyX - PREV_viewerPOV_LeftSideOfBodyX)<0)
		if(this.posnX <= spr2_right  &&  this.PREV_posnX >= spr2_right  &&  (this.posnX - this.PREV_posnX)<0)
		{
			this.posnX = spr2_right;
			if(Game.DEBUG)System.out.println("Collision from Right");
		}
		//if(LinkHead isProbablyInsideTile  &&  PREV_LinkHead isOutsideTile)
		//if(LinkHeadIsAboveBottomOfTile && PREV_LinkHeadIsBelowTopOfTile && LinkTraveledUpward)
		//if(headHeightY <= spr2_bottom  &&  PREV_headHeightY >= spr2_bottom  &&  (headHeightY - PREV_headHeightY) < 0)
		if(this.posnY <= spr2_bottom  &&  this.PREV_posnY >= spr2_bottom  &&  (this.posnY - this.PREV_posnY) < 0)
		{
			this.posnY = spr2_bottom;
			if(Game.DEBUG)System.out.println("Collision from Below");
		}
		//if(LinkToes areAboveBottomOfTile && PREV_LinkToesAreAboveTopOfTile && LinkTraveledDownward)
		//if(toesHeightY <= spr2_bottom  &&  PREV_toesHeightY <= spr2_top  &&  (headHeightY - PREV_headHeightY) > 0)
		if(this.posnY+this.height <= spr2_bottom  &&  this.PREV_posnY+this.height <= spr2_top  &&  (this.posnY - this.PREV_posnY) > 0)
		{
			this.posnY = spr2_top - this.height;
			if(Game.DEBUG)System.out.println("Collision from Above");
		}

		/*//if(currY-prevY isPositive){currYisBigger,meaningLinkMovedDOWN,meaningCorrectionShouldBeToMoveUP,meaningMoveInNegativeYDir}
		if(headHeightY<tileBottom)	//if(LinkHeadIsAboveBottomOfTile)
		{
			if(headHeightY - PREV_headHeightY  >  0)
			{
				toesHeightY = tileTop;					//LinkToesHeight = TopOfTile
				headHeightY = toesHeightY-LinkImgHeight;	//LinkHeadHeight = TopOfTile-LinkHeight
				if(Game.DEBUG)System.out.println("FixCollision: LinkToesY = "+currTile.y+" (goUp)");
			}
			else	//if(LinkHitBottomOfTile fromBelow)
			{
				headHeightY = tileBottom;		//LinkHeadHeight = BottomOfTile
				toesHeightY = headHeightY+LinkImgHeight;	//LinkToesHeight = BottomOfTile+LinkHeight
				if(Game.DEBUG)System.out.println("FixCollision: LinkHeadHeightY = "+headHeightY+" (goDown)");
			}
		}


		//Only one of the values should be updated at a time, preventing weird side scrolling upon a collision
		//if(currX-prevX isPositive){currXisBigger,meaningLinkMovedRIGHT,meaningCorrectionShouldBeToMoveLEFT,meaningMoveNegativeXDir}
		if(viewerPOV_LeftSideOfBodyX - PREV_viewerPOV_LeftSideOfBodyX  >  0)	//if(moveRightIntoTile)
		{
			viewerPOV_RightSideOfBodyX = tileLeft;								//LinkRight = TileLeft
			viewerPOV_LeftSideOfBodyX  = viewerPOV_RightSideOfBodyX - LinkImgWidth;	//LinkLeft = TileLeft-LinkHorizontalChubbiness
			if(Game.DEBUG)System.out.println("FixCollision: LinkLeftSideOfBodyX = "+viewerPOV_LeftSideOfBodyX+" (goLeft)");
		}
		else																	//if(moveLeftIntoTile)
		{
			viewerPOV_LeftSideOfBodyX  = tileRight;				//LinkLeft = TileRight
			viewerPOV_RightSideOfBodyX = viewerPOV_LeftSideOfBodyX + LinkImgWidth;//LinkRight = TileRight+LinkHorizontalChubbiness
			if(Game.DEBUG)System.out.println("FixCollision: LinkLeftSideOfBodyX = "+viewerPOV_LeftSideOfBodyX+" (goRight)");
		}*/
	}


	//Collision Detection and Fixing
	//Only modifies the Sprite calling this method. DOES NOT MODIFY THE SPRITE PASSED INTO THE METHOD (Sprite s2) due to Java's read-only pass-by-reference.
	public void kickThisSprite_AwayFrom_PassedInSprite(Sprite kicker)
	{
		if(Game.DEBUG){System.out.println("\n>>Sprite.kickThisSprite_AwayFrom_PassedInSprite():  Attempted to kick Sprite\n"
											+this.toString()+"\n>>away from Sprite\n"+kicker.toString());}


		//In an example, Pot is Sprite1 (i.e., "this") and Link is kicker
		int kicker_top   = kicker.posnY;				//Top edge of Link
		int kicker_left  = kicker.posnX;				//Left edge of Link
		int kicker_PREVleft = kicker.PREV_posnX;			//Previous left edge of Link
		int kicker_PREVtop  = kicker.PREV_posnY;			//Previous top edge of Link

		//if(kicker_bottom <= this.posnY + this.height)	UNUSED BECAUSE THIS IS NOT COLLISION DETECTION

		//if(kickerMovedRight){recipient_xDir = right;}	+1 == +xDirection == rightward
		if(kicker_PREVleft < kicker_left){movDirX =  1;  if(Game.DEBUG){System.out.println("kickSprite: movDirX =  rightward");}}

		//if(kickerMovedLeft){recipient_xDir = left;}	-1 == -xDirection == leftward
		if(kicker_PREVleft > kicker_left){movDirX = -1;  if(Game.DEBUG){System.out.println("kickSprite: movDirX =  leftward");}}

		//if(kickerMovedDown){recipient_yDir = down;}	+1 == +yDirection == downward
		if(kicker_PREVtop < kicker_top){movDirY =  1;  if(Game.DEBUG){System.out.println("kickSprite: movDirY =  downward");}}

		//if(kickerMovedUp){recipient_yDir = up;}		-1 == -yDirection == upward
		if(kicker_PREVtop > kicker_top){movDirY = -1;  if(Game.DEBUG){System.out.println("kickSprite: movDirY =  upward");}}
		
		movSpeed++;	//The longer the time period the Sprite is kicked, the more times this method is called, acting like a foot carrying-through and applying more
		//force to the kicked Sprite
	}
}