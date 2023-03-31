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


//Doesn't need ArrayList<Tile> because Tiles don't need to know about other Tiles around them.
//Collision detection is taken care of by the Model class, NOT the objects themselves
public class Tile extends Sprite
{
	//Already contains posnX and posnY from Sprite class
	//int x, y;

	//Already contains img from Sprite class
	//static BufferedImage tileImg = null;

	//Already contains width and height from Sprite class, but they won't be used
		//static because every possible Tile object that could be created will have the same value for width (and height).
		//final because these values will never be changed once initialized.
		//static final int width  = 40;
		//static final int height = 40;

	//Class variables are initialized to 0 (for primitives) or null (objects) by default, so I don't need to assign everything (i.e., positions) in here.
	Tile()
	{
		if(Game.DEBUG){System.out.println("Tile default constructor called");}
		this.width  = 40;
		this.height = 40;


		//Already contains img from Sprite class
			//if(tileImg == null)
			//	{tileImg = View.loadImage("images/tile.jpg",tileImg);}

		//Only the 1st element in the image ArrayList is needed.
		this.imgs = new BufferedImage[1];				//Allocates 1*sizeof(BufferedImage) bytes of memory that the Array can now use
		this.imgs[0] = View.loadImage("images/tile.jpg",null);
		//From (inside) the (same) folder that the View class is in, enter the folder named "images", retrieve file "tile.jpg", store resultant Image into tileImg
	}

	Tile(int posX, int posY)
	{
		this();	//Calls Tile's default constructor first, then executes the next line below this line. This must be the 1st line in this constructor.
		if(Game.DEBUG){System.out.println("Tile NON-default constructor called");}
		this.posnX = posX;
		this.posnY = posY;

		this.width  = 40;
		this.height = 40;
	}
	
	@Override
	public boolean isTile(){	return true;}

	@Override
	public String toString()
		{return "***Tile (leftX,rightX, topY,bottomY) = ("+posnX+","+(posnX+width)+", "+posnY+","+(posnY+height)+")";}
	
	//Doesn't do anything since Tiles don't move on their own (regardless of situation)
	@Override
	public void update(){}
	
	@Override
	public void draw(Graphics g, int adjusted_xPosToDrawAt, int adjusted_yPosToDrawAt)
	{
		if(this.imgs == null)	//if(arrayListOfImagesThatBelongToOneTileObject isNull)
		{
			imgs = new BufferedImage[1];	//Allocates 1*sizeof(BufferedImage) bytes of memory that the Array can now use

			//From (inside) the (same) folder that the View class is in, enter the folder named "images", retrieve file "tile.jpg", store resultant Image into tileImgs[0]
			this.imgs[0] = View.loadImage("images/tile.jpg",null);

			if(Game.DEBUG){System.out.println("specificTile.draw() loaded Tile image");}
		}
		if(imgs[0]==null)
			{System.out.println("specificTile.draw():  TILE IMAGE IS NULL BUT IT SHOULDN'T BE");}
		
		/*boolean successFullyDrewImg =*/ g.drawImage(this.imgs[0], adjusted_xPosToDrawAt, adjusted_yPosToDrawAt, this.width, this.height, null);	//Draw that specific Tile
		/*if(Game.DEBUG){System.out.println("specificTile.drawYourself():  adjusted_xPosToDrawAt,adjusted_yPosToDrawAt, width,height = "
											+adjusted_xPosToDrawAt+","+adjusted_yPosToDrawAt+", "+width+","+height);}
		if(successFullyDrewImg)
			{System.out.println("successFullyDrewTileImg");}
		else{System.out.println("DID NOT successFullyDrewImg");}*/
	}


	//This method is not used
	boolean coordsMatchThisExistingTile(int xSnappedUserClick, int ySnappedUserClick)
	{
		boolean trueIffTheUserClickedOnTheTile = (posnX==xSnappedUserClick) && (posnY==ySnappedUserClick);
		return trueIffTheUserClickedOnTheTile;
		//Tile removal and addition are done elsewhere in the program, which is implicitly okay according to instruction 9
	}




	@Override
	//Turn info about a specific Tile into JSON format
	public Json marshal()
	{
		Json jsonObj = Json.newObject();
		//Json tmpList = Json.newList();
		//jsonObj.add(tmpList);	//Binds tmpList to its new parent jsonObj
		//tmpList.add("tileX",this.x);	//THESE ADD TO THE LIST INSTEAD OF THE JSONOBJ, WHICH IS WRONG
		//tmpList.add("tileY",this.y);
		jsonObj.add("tileX",this.posnX);
		jsonObj.add("tileY",this.posnY);
		
		return jsonObj;
	}

	//Turn JSON object into a Tile object via Constructor
	//This could be a   Tile unmarshal(){stuff;}   method, but the constructor is far easier here.
	public Tile(Json jsonObj)
	{
		this();	//Calls default Tile constructor. Must be first line in this constructor

		//Json tmpList = jsonObj.get("Tile Positions").get(0);	//Gets first set of {tileX,tileY} coords
		//System.out.println( jsonObj.get("tileX").get(0).toString() );
		posnY = (int)( jsonObj.getLong("tileY") );
		posnX = (int)( jsonObj.getLong("tileX") );
	}
}