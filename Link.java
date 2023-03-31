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


//Only one class can be declared public per file, so don't name this public
enum Direction
{
	//LEFT, RIGHT, UP, DOWN	//Use only this line if I never need to access the digits that they actually represent. Notice how there is NO semicolon.
	LEFT(1), RIGHT(2), UP(3), DOWN(0);	//I'm assigning #s based on the order they will be read into imagesOfLink[]. The #s COULD be the same if I needed them to be.

	public int direction;	//All enum constants are public final
	private Direction(int d)	//The constructor is implicitly private (writing "private" is redundant). Source: Professor Lora Streeter
	{
		this.direction = d;
	}
}
//Every class can access this enum class




class Link extends Sprite
{
	public static final int startPos = (new Tile().width) + 5;

	//Already contains posnX and posnY from Sprite class
		//int headHeightY;
		//int toesHeightY;
		//int viewerPOV_LeftSideOfBodyX;
		//int viewerPOV_RightSideOfBodyX;

	//Already contains PREV_posnX and PREV_posnY from Sprite class
		//Link's previous (legal) position
		//int PREV_headHeightY;
		//int PREV_toesHeightY;
		//int PREV_viewerPOV_LeftSideOfBodyX;
		//int PREV_viewerPOV_RightSideOfBodyX;

	//Link sprites
	//"imgs" really means imagesOfLink
	//Down(13 imgs),Left (13 imgs),Right (13 imgs),Up (11 imgs). I will have the same # of sprites (only 8) for each direction.
		//static BufferedImage[] imagesOfLink = null;	//Now unnecessary because Sprite class has the "imgs" Array
	static final int numImagesTotal = 32;
	static final int numImagesPerDirection = 8;
		//int currCycle_imageIndex;						//Used only for the animation	REPLACED BY currImgIndex FROM Sprite
	Direction heading;	//Based on where images are in the array

	//Already contains width and height from Sprite class
		//Size of Link's DISPLAYED image
		//static final int LinkImgWidth  = 30;
		//static final int LinkImgHeight = 40;

	//double movementSpeed = 5.0;	//This was always unnecessarily a double and I hated every second of having to use it. Now I have movSpeed that Sprite declares.

	Link()
	{
		posnY  = startPos;
		posnX  = startPos;
		PREV_posnY  = startPos;
		PREV_posnX  = startPos;

		width  = 30;
		height = 40;

		heading = Direction.DOWN;						//Initial direction Link is facing
		//System.out.println(heading);
		//System.out.println(heading.direction);
		//for(Direction d  :  Direction.values()){System.out.println(d);}	//Prints the variableName/*not integer? I think?*/ representation of every enum variable

		movSpeed = 7;
		
		if(imgs==null)
		{
			//Allocates numImagesTotal*sizeof(BufferedImage) bytes of memory that the Array named "imgs" can now use
			imgs = new BufferedImage[numImagesTotal];

			//Load all images of Link into imgs Array
			for(int i=0; i<numImagesTotal; i++)
				{imgs[i] = View.loadImage("images/link"+(i+1)+".png", imgs[i]);}
				//From (inside) the (same) folder that the View class is in, enter the folder named "images", retrieve file "Link#.png", store resultant Image into imagesOfLink[i]
				//Example Image Location: "images/Link1.png"
		}
		currImgIndex = 0;
	}

	@Override
	public String toString()
		{return "***Link (leftX,rightX,topY,bottomY) = ("+posnX+","+(posnX+width)+", "+posnY+","+(posnY+height)+")"
		      + "\tPrevLinkLocation (leftX,rightX,topY,bottomY) = ("+PREV_posnX+","+(PREV_posnX+width)+", "+PREV_posnY+","+(PREV_posnY+height)+")";}

	@Override
	public boolean isLink()
		{return true;}

	//Doesn't do anything since Link doesn't move on his own (he only moves when the user tells him to move OR when he is getting his collision fixed)
	@Override
	public void update(){}

	

	public boolean linkIsCollidingWith_PassedInTile(Sprite specificTile)
		{return thisSpriteIsCollidingWith_PassedInSprite(specificTile);}



	//FIX COLLISION
	//Uses prevPositionOfLink
	//It's possible to interact with multiple Tiles simultaneously
	//*****ASSUMES THAT PREVIOUS POSITION WAS LEGAL
	public void pushLinkOutOfTile(Sprite currTile)
	{
		//Sprite Collision Fixing method
		this.pushThisSprite_OutOf_PassedInSprite(currTile);
	}


	//heading.direction == numerical value of the enum label (e.g., LEFT == 3)
	//Should probably NOT work as expected if (roomScrollEnabled && roomJumpDisabled)
	@Override
	public void draw(Graphics g, int xPosToDrawAt, int yPosToDrawAt)
	{
		final int imgIndexInEntireArray = currImgIndex + (heading.direction)*numImagesPerDirection;
		//if(Game.DEBUG){System.out.println("Drawing Link (Actual: "+viewerPOV_LeftSideOfBodyX+","+headHeightY+") - (adjustedToFitOnScreen: "+xPosToDrawAt+","+yPosToDrawAt+")");}
		if(Game.DEBUG){System.out.println("Drawing Link (width,height: "+width+","+height+") (ActualXY: "+posnX+","+posnY+") - (adjustedXYtoFitOnScreen: "+xPosToDrawAt+","+yPosToDrawAt+")");}
		if(Game.DEBUG){System.out.println("LinkImgIndexInArray (subArrayForSpecificDirection_currCycle_imageIndex + (heading.direction)*numImagesPerDirection): "
											+imgIndexInEntireArray+"=("+currImgIndex +"+"+ ((heading.direction)*numImagesPerDirection)+")");}
		//By subArray, I mean part of the array of images (i.e. indices 0-7 are for DownFacingLink, indices 8-15 are for LeftFacingLink,
		//   indices 16-23 are for RightFacingLink, indices 24-31 are for UpFacingLink). Indices 16-23 would be one subarray.
		// currLinkImgInCurrLinkDirection'sCycle, xPosToDrawAt, yPosToDrawAt, LinkImgWidth, LinkImgHeight, null );
		g.drawImage( imgs[imgIndexInEntireArray], xPosToDrawAt, yPosToDrawAt,   this.width, this.height,   null );
	}

	//This method can't go in Sprite because not every Sprite's imgs Array is set up this way.
	public void updateImageNumUponMoving(Direction dir)
	{
		heading = dir;
		currImgIndex++;		//This one value can be associated with 4 different images since there are 4 different directions.
		if(currImgIndex >= numImagesPerDirection)
			{currImgIndex = 0;}
	}


	//Turn info about a specific Link into Json format, then returns that Json object
	public Json marshal()
	{
		Json jsonObj = Json.newObject();
		//Json tmpList = Json.newList();
		//jsonObj.add(tmpList);	//Binds tmpList to its new parent jsonObj
		//tmpList.add("tileX",this.x);	//THESE ADD TO THE LIST INSTEAD OF THE JSONOBJ, WHICH IS WRONG
		//tmpList.add("tileY",this.y);
		jsonObj.add("linkX",this.posnX);
		jsonObj.add("linkY",this.posnY);

		//this.heading.direction == thisLinkCharacter.DirectionEnum.convertHeadingToInteger
		jsonObj.add("dirFacing",this.heading.direction);
		
		return jsonObj;
	}

	//Turn Json object into a Link object via Constructor
	//This could be a   Link unmarshal(){stuff;}   method, but the constructor is far easier here.
	public Link(Json jsonObj)
	{
		this();	//Calls default Link constructor. Must be first line in this constructor

		//Json tmpList = jsonObj.get("Tile Positions").get(0);	//Gets first set of {tileX,tileY} coords
		//System.out.println( jsonObj.get("tileX").get(0).toString() );

		//There is an implicit "this." before all the below recipients of each assignment, meaning "thisLink.attributeThatEveryLinkHas"
		posnY = (int)( jsonObj.getLong("linkY") );
		posnX = (int)( jsonObj.getLong("linkX") );
		heading.direction = (int)( jsonObj.getLong("dirFacing") );
	}
}


