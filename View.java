//Steven Lynch
//Mar 29, 2023
//Project: Character named Link can traverse graphical map via keys A,W,D,X. Map can switch between jumping between rooms
//  and scrolling between rooms. Can save and load Tile and Clay Pot locations (part of the map) via ArrayList and JSON file.

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
//java.awt == namespace

class View extends JPanel
{
	public static final int BORDER_WIDTH = 12;
	public static final int BORDER_HEIGHT = 37;

	//JButton b1;		TURTLE-RELATED
	Model model;
	int roomScrollPosX;
	int roomScrollPosY;
	static int numRoomsWide;
	static int numRoomsTall;
	static int widthPerRoom;	//Even though this class extends JPanel, THE Game CLASS must SET the room size (these 2 vars) IF the room size is
	static int heightPerRoom;	//  based off the window's (frame's) width because Game extends JFrame while View doesn't extend JFrame

	boolean inEditMode;
	boolean editPotsAndNotTiles;	//Used for switching between (adding/removing Tiles) and (adding Pots)


	View(Controller c, Model m)
	{
		c.setView(this);
		model = m;
		inEditMode = false;
		
		//		TURTLE-RELATED STUFF
		/*b1 = new JButton("Don't push me");
		b1.addActionListener(c);
		this.add(b1);*/
	}

	//This is where I figured out that you can read an object by passing it in (functions are pass-by-value in Java) but
	//  you CAN'T WRITE TO THE ORIGINAL VARIABLE (you can write to the copy though), hence the return type not being void
	public static BufferedImage loadImage(String filenameAsString, BufferedImage destinationBuffImg)
	{
		try
		{
			BufferedImage buffImgToReturn = null;
			if(destinationBuffImg == null)
			{
				buffImgToReturn = ImageIO.read( new File(filenameAsString));
				if(Game.DEBUG){System.out.println("View.loadImage( "+filenameAsString+"): Successfully loaded");}
			}
			else{if(Game.DEBUG)System.out.println("Image "+filenameAsString+" already loaded.");}
			return buffImgToReturn;
		}
		catch(Exception e)
		{
			System.out.println("Couldn't find image "+filenameAsString);
			e.printStackTrace(System.err);
			System.exit(1);		//Exit the entire game
		}
		System.out.println("!!!!!View.loadImage( "+filenameAsString+"): This shouldn't have been reached");
		return null;
	}

	@Override	//Not necessary, but this shows that this
	//method overrides the one found in the extended class JPanel
	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(128,255,255));
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		//g.drawImage(this.turtImg,0,0,null);	//Debug picture draw location

		Iterator<Sprite> spriteIterator = model.spriteList.iterator();
		//for(int i=0; i<model.tileList.size(); i++)	//For every Tile object in tileList
		while( spriteIterator.hasNext() )	//For every Sprite (e.g. Tile and Pot objects) in spriteList
		{
			Sprite currSprite = spriteIterator.next();	//Get the next Tile (or Link or Pot) from the list of tiles (or links or pots)	//iterator.next()
			int xPosToDrawAt  =  currSprite.posnX - roomScrollPosX;// - BORDER_WIDTH;
			int yPosToDrawAt  =  currSprite.posnY - roomScrollPosY;// - BORDER_HEIGHT;

			currSprite.draw(g, xPosToDrawAt, yPosToDrawAt);
		}

		g.setColor(new Color(0,200,50));
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawString("Edit mode (e): "+inEditMode, 60, 60);
		g.drawString("Edit Pots(andNotTiles) (p): "+editPotsAndNotTiles, 60, 75);
		//drawString: X and Y don't use scrollPos because drawnStringLocation is independent of calculating where things in a room go

		//int xPosToDrawAt  =  model.link.posnX - roomScrollPosX;
		//int yPosToDrawAt  =  model.link.posnY - roomScrollPosY;
		//model.link.draw(g,xPosToDrawAt,yPosToDrawAt);
	}


	private boolean posnIsWithinBounds(int positionX, int positionY)
	{
		//return true if in bounds of the room (i.e. if legal)
		/*System.out.printf("boundsCheck(): posnXIsBelowMaxX: %b, posnYIsBelowMaxY: %b\n",
						(positionX<=(View.widthPerRoom *View.numRoomsWide)),
						(positionY<=(View.heightPerRoom*View.numRoomsTall)));	//Debug
		System.out.printf("boundsCheck(): positionX: %d, maxPixelsWide: %d\n",positionX,(View.widthPerRoom *View.numRoomsWide));
		System.out.printf("boundsCheck(): positionY: %d, maxPixelsTall: %d\n",positionY,(View.heightPerRoom *View.numRoomsTall));*/
		return   positionX<=(View.widthPerRoom *(View.numRoomsWide-1))	//-1 because roomScrollPos is in terms of the LEFT and UPPER side
			  && positionY<=(View.heightPerRoom*(View.numRoomsTall-1))	// of the View, not the right or lower side
			  && positionX>=0
			  && positionY>=0;
	}

	//Allows for numRoomsTall rooms tall and numRoomsWide rooms wide
	boolean willNotScrollRoomOutOfBounds(int posnXModifier, int posnYModifier)
	{
		int possibleFutureX = (this.roomScrollPosX + posnXModifier);
		int possibleFutureY = (this.roomScrollPosY + posnYModifier);
		//System.out.printf("willNotScrollRoomOutOfBounds(): possibleFutureX: %d, possibleFutureY: %d\n", possibleFutureX, possibleFutureY);	//Debug

		return posnIsWithinBounds(possibleFutureX,possibleFutureY);
	}
	boolean willNotJumpRoomOutOfBounds(int numRoomsJumped_xDir, int numRoomsJumped_yDir)
	{
		int possibleFutureX = (this.roomScrollPosX + (View.widthPerRoom * numRoomsJumped_xDir));
		int possibleFutureY = (this.roomScrollPosY + (View.heightPerRoom * numRoomsJumped_yDir));
		//System.out.printf("willNotJumpRoomOutOfBounds(): possibleFutureX: %d, possibleFutureY: %d\n", possibleFutureX, possibleFutureY);	//Debug

		return posnIsWithinBounds(possibleFutureX,possibleFutureY);
	}

	//TURTLE-RELATED FUNCTION
	/*void removeButton()
	{
		this.remove(b1);
		this.repaint();
	}*/
}
