//Steven Lynch
//Mar 30, 2023
//Link LegendOfZelda Project: Character named Link can traverse graphical map via arrow keys or A,W,D,X.
//Map can switch between jumping between rooms and scrolling between rooms by pressing key J.
//Can save and load Link, Tile, Clay Pot, Boomerang locations via ArrayList and Json file by pressing 's'(save) or 'l'(load).
//To be able to add/remove Tiles (boundaries that Link cannot cross) or add a Clay Pot,
//  1) Enter edit mode by pressing key E
//  2) Switch to AddPot mode (exit TileAddition/Removal mode) by pressing keyP.
//Press key CTRL to throw a boomerang.

import javax.swing.JFrame;
import java.awt.Toolkit;

public class Game extends JFrame
{
	public static final boolean DEBUG = true;	//Every class (in the same folder) should be able to access this variable.


	Model model = new Model();
	Controller controller = new Controller(model);
	View view = new View(controller, model);

	final int BORDER_WIDTH  = View.BORDER_WIDTH;
	final int BORDER_HEIGHT = View.BORDER_HEIGHT;
	
	public Game()
	{
		//Model model = new Model();
		//Controller controller = new Controller(model);
		//View view = new View(controller, model);
		view.addMouseListener(controller); //Controller is in charge of handling mouse clicks
		this.addKeyListener(controller);   //Controller is in charge of handling key events
		
		this.setTitle("A5 - Link + Map Editor + Boomerang+Pot");
		//this.setSize(Tile.width*10, Tile.height*10+25);
		this.setSize((new Tile()).width*10+BORDER_WIDTH, (new Tile()).height*10+BORDER_HEIGHT);
			//getHeight() is this.getHeight(), which is JFrame.getHeight(), which is JFrame.#pixelsTall
			//SizeOfOneRoom = SizeOfWindow
			//Sets the View class's heightPerRoom and widthPerRoom class variables to the JFrame's height and width
			View.heightPerRoom = getHeight();
			View.widthPerRoom  = getWidth();
		this.setFocusable(true);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
	}
	

	public void run()
	{
		while(true)
		{
			controller.update();
			model.update();
			view.repaint();						//This will indirectly call View.paintComponent
			Toolkit.getDefaultToolkit().sync(); //Updates screen

			// Go to sleep for 50 milliseconds. [1000ms/sec]/[50ms/frameUpdate]=20frames/sec
			try
			{
				//Thread.sleep(50);
				Thread.sleep(40); //[1000ms/sec]/[x ms/frameUpdate]=25frames/sec. 1000/25=40ms
			} catch(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			//System.out.println("Game is running");
		}
	}

}
