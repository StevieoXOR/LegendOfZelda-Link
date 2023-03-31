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


public class Pot extends Sprite
{
	//Already contains xPos, yPos, currImgIndex, width, height, movSpeed, movDirX, movDirY from Sprite class

	public static final int INTACT_POT_IMG_INDEX = 0;
	public static final int BROKEN_POT_IMG_INDEX = 1;

	public static final int INITIAL_POT_SPEED = 1;	//Should be 15 if frictionIsEnabled, should be 1 if frictionIsDisabled.
	

	Pot()
	{
		if(Game.DEBUG){System.out.println("Pot default constructor called");}
		width = 30;
		height = 30;
		//Making the direction 0 with a nonzero movement speed allows the kickSprite() method in model to only update the direction without updating the speed,
		//  making kicking heavy and light objects require the same amount of code
		movDirX = 0;
		movDirY = 0;
		movSpeed = INITIAL_POT_SPEED;
		isBreakable = true;				//Clay pots are fragile
		breakageToDeletionTimer = 30;	//Broken pot will exist for 30 screen update cycles
		currImgIndex = 0;

		imgs = new BufferedImage[2];
		imgs[INTACT_POT_IMG_INDEX] = View.loadImage("images/pot.png", null);		//Putting imgs[0] instead of null doesn't change anything since it should already be null
		imgs[BROKEN_POT_IMG_INDEX] = View.loadImage("images/pot_broken.png", null);	//Putting imgs[1] instead of null doesn't change anything since it should already be null
	}
	Pot(int unsnappedX, int unsnappedY)
	{
		this();	//Calls Pot's default constructor (so I don't have to rewrite everything here too)
		if(Game.DEBUG){System.out.println("Pot NON-default constructor called");}
		posnX = unsnappedX;
		posnY = unsnappedY;
	}
	/*Pot(Sprite existingPot)	//Why a Sprite instead of a Pot? For compatibility with Model's spriteList
	{
		this();	//Calls Pot's default constructor (so I don't have to rewrite everything here too)
		this.movDirX = existingPot.movDirX;
		this.movDirY = existingPot.movDirY;
		this.posnX = existingPot.posnX;
		this.posnY = existingPot.posnY;
	}*/


	
	@Override	//Overrides Sprite's toString() method. Useful for methodNameTypos creating something not actually FROM Sprite
	public String toString()
		{return "***Pot (leftX,rightX,topY,bottomY) = ("+posnX+","+(posnX+width)+", "+posnY+","+(posnY+height)+")"+ ", PotWidth="+width+", PotHeight="+height
		      /*+ "\tPrevPotLocation (leftX,rightX,topY,bottomY) = ("+PREV_posnX+","+(PREV_posnX+width)+", "+PREV_posnY+","+(PREV_posnY+height)+")"   I DON'T USE THIS*/
			  +"\n***, PotSlidingFrictionEnabled="+!noSlidingFriction+",  PotMovementSpeed="+movSpeed+",  PotMovDirX="+movDirX+", PotMovDirY="+movDirY
			  + ",  PotImgs: "+((imgs==null)?"NULL":"exists");}

	@Override
	public boolean isPot()
		{return true;}

	@Override
	public void update()
	{
		//boolean continueCallingUpdate_becauseCollisionHasNotHappened = true;
		
		/*	OVERALL IDEA (this happening over the lifecycle of the pot)
		for(int speed=maxAndInitialSpeed; speed>=0; speed--)
		{
			x += speed*xDir;
			y += speed*yDir;

			//To go in Model.java
				//for(Sprite s : spriteList)
				//if(s.thisSpriteIsCollidingWith_NON_LINK_PassedInSprite())
				//	{shouldStillExist = false;}
		}*/

		posnX += movSpeed*movDirX;
		posnY += movSpeed*movDirY;
		if(Game.DEBUG){System.out.println("\tPot.update():  posnX,Y += movSpeed*movDirX,Y");}

		if(isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed)	//if(PotIsNowBroken){DecrementPotLifespan}
		{
			breakageToDeletionTimer--;
			if(Game.DEBUG){System.out.println("\tPot.update():  breakageToDeletionTimer--: "+breakageToDeletionTimer);}
		}

		if(!noSlidingFriction)	//if(sliding friction exists)
		{
			//Loses momentum (speed) as the object travels
			if(movSpeed>0 && (movDirX!=0 || movDirY!=0))	//if(thereIsSpeedToLose && PotIsActuallyMovingInAtLeastOneDirection){loseSpeed}
			{
				movSpeed--;
				if(Game.DEBUG){System.out.println("\tPot:  movSpeed--");}
			}

			//Reset movementSpeed and movementDirection so Pot can be kicked again
			//ILLEGAL FOR THIS ASSIGNMENT
			/*if(movSpeed==0)
			{
				movDirX = 0;
				movDirY = 0;
				movSpeed = INITIAL_POT_SPEED;
				if(Game.DEBUG){System.out.println("\tPot:  Reset movSpeed,movDirX,movDirY");}
			}*/
		}
		//else	//if(sliding friction does not exist)
		//{}	//Object has no friction and continues traveling at constant speed, so speed isn't updated



		//Object stops when hitting something, taken care of in Controller and Model via model.fixAllExistingCollisions().

		//return continueCallingUpdate_becauseCollisionHasNotHappened;
	}




	@Override
	public void draw(Graphics g, int xPosToDrawAt, int yPosToDrawAt)
	{
		//if(Game.DEBUG){System.out.println("Drawing Link (Actual: "+viewerPOV_LeftSideOfBodyX+","+headHeightY+") - (adjustedToFitOnScreen: "+xPosToDrawAt+","+yPosToDrawAt+")");}
		if(Game.DEBUG){System.out.println("Drawing Pot (width,height: "+width+","+height+") (ActualXY: "+posnX+","+posnY+") - (adjustedXYtoFitOnScreen: "+xPosToDrawAt+","+yPosToDrawAt+")");}
		if(Game.DEBUG){System.out.println("\t,  PotMovementSpeed="+movSpeed+",  PotImgIndexInArray (movDirX, movDirY): "+currImgIndex+" ("+movDirX +", "+ movDirY+")");}
				// Broken/Intact_PotImg, xPosToDrawAt, yPosToDrawAt, PotImgWidth, PotImgHeight, null );
		g.drawImage( imgs[currImgIndex], xPosToDrawAt, yPosToDrawAt,  this.width, this.height,  null );
	}
	public void updateImageNumUponCollision()
		{currImgIndex = BROKEN_POT_IMG_INDEX;}




	@Override
	//Turn info about a specific Pot into Json format, then returns that Json object
	public Json marshal()
	{
		Json jsonObj = Json.newObject();
		//Json tmpList = Json.newList();
		//jsonObj.add(tmpList);	//Binds tmpList to its new parent jsonObj
		//tmpList.add("potX",this.x);	//THESE ADD TO THE LIST INSTEAD OF THE JSONOBJ, WHICH IS WRONG
		//tmpList.add("potY",this.y);
		jsonObj.add("potX",this.posnX);
		jsonObj.add("potY",this.posnY);
		return jsonObj;
	}

	//Turn Json object into a Pot object via Constructor
	//This could be a   Pot unmarshal(){stuff;}   method, but the constructor is far easier here.
	public Pot(Json jsonObj)
	{
		this();	//Calls default Pot constructor. Must be first line in this constructor

		//Json tmpList = jsonObj.get("Pot Positions").get(0);	//Gets first set of {potX,potY} coords
		//System.out.println( jsonObj.get("potX").get(0).toString() );

		//There is an implicit "this." before all the below recipients of each assignment, meaning "thisLink.attributeThatEveryLinkHas"
		posnY = (int)( jsonObj.getLong("potY") );
		posnX = (int)( jsonObj.getLong("potX") );
	}
}