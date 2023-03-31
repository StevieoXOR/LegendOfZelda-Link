//Steven Lynch
//Mar 30, 2023
//Link LegendOfZelda Project: Character named Link can traverse graphical map via arrow keys or A,W,D,X.
//Map can switch between jumping between rooms and scrolling between rooms by pressing key J.
//Can save and load Link, Tile, Clay Pot, Boomerang locations via ArrayList and Json file by pressing 's'(save) or 'l'(load).
//To be able to add/remove Tiles (boundaries that Link cannot cross) or add a Clay Pot,
//  1) Enter edit mode by pressing key E
//  2) Switch to AddPot mode (exit TileAddition/Removal mode) by pressing keyP.
//Press key CTRL to throw a boomerang.

import java.awt.Graphics;
import java.awt.image.BufferedImage;


public class Boomerang extends Sprite
{
	final static int numImagesTotal = 4;
	final static int startPos = 40;

	Boomerang()
	{
		posnY  = startPos;
		posnX  = startPos;
		PREV_posnY  = startPos;
		PREV_posnX  = startPos;

		width  = 10;
		height = 10;
		movSpeed = 10;
		movDirX = 0;
		movDirY = 0;

		isBreakable = true;				//Boomerangs are fragile
		breakageToDeletionTimer = 0;	//Broken Boomerang will exist for 0 screen update cycles (because Boomerangs break immediately after contacting another Sprite)
		currImgIndex = 0;
		
		if(imgs==null)
		{
			//Allocates numImagesTotal*sizeof(BufferedImage) bytes of memory that the Array named "imgs" can now use
			imgs = new BufferedImage[numImagesTotal];

			//Load all images of Boomerang into imgs Array
			for(int i=0; i<numImagesTotal; i++)
				{imgs[i] = View.loadImage("images/boomerang"+(i+1)+".png", imgs[i]);}
				//From (inside) the (same) folder that the View class is in, enter the folder named "images", retrieve file "Boomerang#.png", store resultant Image into imagesOfBoomerang[i]
				//Example Image Location: "images/Boomerang1.png"
		}
	}
	Boomerang(int posX, int posY, int movementDirX, int movementDirY)	//movementDirXY should come from Link for this assignment
	{
		this();	//Calls Boomerang's default constructor
		posnY  = posY;
		posnX  = posX;
		PREV_posnY  = posY;
		PREV_posnX  = posX;

		movDirX = movementDirX;
		movDirY = movementDirY;
	}

	@Override
	public boolean isBoomerang()
		{return true;}

	@Override	//Overrides Sprite's toString() method. Useful for methodNameTypos creating something not actually FROM Sprite
	public String toString()
		{return "***Boomerang (leftX,rightX,topY,bottomY) = ("+posnX+","+(posnX+width)+", "+posnY+","+(posnY+height)+")"+ ", BmrgWidth="+width+", BmrgHeight="+height
		      /*+ "\tPrevBmrgLocation (leftX,rightX,topY,bottomY) = ("+PREV_posnX+","+(PREV_posnX+width)+", "+PREV_posnY+","+(PREV_posnY+height)+")"   I DON'T USE THIS*/
			  +"\n***, BmrgMovementSpeed="+movSpeed+",  BmrgMovDirX="+movDirX+", BmrgMovDirY="+movDirY
			  + ",  BmrgImgs: "+((imgs==null)?"NULL":"exists");}

	@Override
	public void update()
	{
		if(currImgIndex+1 < numImagesTotal)
			{currImgIndex++;}
		else{currImgIndex = 0;}


		posnX += movSpeed*movDirX;
		posnY += movSpeed*movDirY;
		if(Game.DEBUG){System.out.println("\tBoomerang.update():  posnX,Y += movSpeed*movDirX,Y");}

		if(isBrokenANDshouldBeDeletedAfterBrokenImgDisplayed)	//if(PotIsNowBroken){DecrementPotLifespan}
		{
			//breakageToDeletionTimer--;		//No need since it will delete immediately upon contact with another Sprite
			//There is no broken boomerang image, so the Boomerang should immediately delete upon contact with another Sprite
			if(Game.DEBUG){System.out.println("\tBoomerang.update():  breakageToDeletionTimer--: "+breakageToDeletionTimer);}
		}
	}

	@Override
	public void draw(Graphics g, int xPosToDrawAt, int yPosToDrawAt)
	{
		if(Game.DEBUG){System.out.println("Drawing Boomerang (width,height: "+width+","+height+") (ActualXY: "+posnX+","+posnY+") - (adjustedXYtoFitOnScreen: "+xPosToDrawAt+","+yPosToDrawAt+")");}
		if(Game.DEBUG){System.out.println("\t,  BmrgMovementSpeed="+movSpeed+",  BmrgImgIndexInArray (movDirX, movDirY): "+currImgIndex+" ("+movDirX +", "+ movDirY+")");}
				// BoomerangImg, xPosToDrawAt, yPosToDrawAt, BmrgImgWidth, BmrgImgHeight, null );
		g.drawImage( imgs[currImgIndex], xPosToDrawAt, yPosToDrawAt,  this.width, this.height,  null );
	}




	@Override
	//Turn info about a specific Pot into Json format, then returns that Json object
	public Json marshal()
	{
		Json jsonObj = Json.newObject();
		//Json tmpList = Json.newList();
		//jsonObj.add(tmpList);	//Binds tmpList to its new parent jsonObj
		//tmpList.add("potX",this.x);	//THESE ADD TO THE LIST INSTEAD OF THE JSONOBJ, WHICH IS WRONG
		//tmpList.add("potY",this.y);
		jsonObj.add("boomerangX",this.posnX);
		jsonObj.add("boomerangY",this.posnY);
		jsonObj.add("boomerangMovDirX",this.movDirX);
		jsonObj.add("boomerangMovDirY",this.movDirY);
		return jsonObj;
	}

	//Turn Json object into a Pot object via Constructor
	//This could be a   Pot unmarshal(){stuff;}   method, but the constructor is far easier here.
	public Boomerang(Json jsonObj)
	{
		this();	//Calls default Boomerang constructor. Must be first line in this constructor
		this.posnY = (int)( jsonObj.getLong("boomerangY") );
		this.posnX = (int)( jsonObj.getLong("boomerangX") );
		this.movDirX = (int)jsonObj.getLong("boomerangMovDirX");
		this.movDirY = (int)jsonObj.getLong("boomerangMovDirY");
	}
}