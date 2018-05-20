// Sanjay Mohan - 6, Ari Sweedler - 5, Aidan Adams-Campeau - 1
// Period 3, June 2, 2015

// WorldRunner - driver for the game, including top frame manipulation

import javax.swing.*;

public class WorldRunner
{
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 700;
	private static JFrame frame;		// the top frame
	private static World w;
	
	/* curPage always holds the current page in order to remove it later (need to keep reference to use frame.remove(component))
	 * whenever curPage is removed from this.frame, it should be reassigned to whatever is added to this.frame
	 */
	private static JPanel curPage;
	
	public static void main(String[] args)
	{
		/* makes new thread for this, runs it later ("thread-safe")
		 * this allows main() to finish constructing the frame before 
		 * displaying it or reaching the end of main()
		 * (the internet told me to do this)
		 */
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createGUI();
			}
		});
	}
	
	// pre - none
	// post - constructs the top frame for the game
	public static void createGUI()
	{		
		// Set up top container window to hold everything
		frame = new JFrame("Conway");
		frame.setBounds(60, 200, WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		curPage = new Menu();
		frame.add(curPage);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	// pre - newGame indicates whether a new game is to be started (true) or an old one to be resumed (false)
	// post - adds the World JPanel to the frame and starts a game
	public static void startGame(boolean newGame)
	{
		frame.remove(curPage);
		
		if (newGame)				// else, it's resuming an old game
			w = new World();
		
		frame.remove(curPage);
		curPage = w;
		frame.add(w);
		frame.setVisible(true);		// easy/quick way to show changed display in a JFrame
		
		w.begin();					// start the game
	}
	
	// pre - none
	// post - ends the current game (to be resumed or replaced later) and goes back to menu
	public static void endGame()
	{
		w.end();
		backToMenu();
	}
	
	// pre - menuPage - the page to be navigated to
	// post - changes frame to show another page (eg settings or credits)
	public static void goToPage(JPanel menuPage)
	{
		frame.remove(curPage);
		curPage = menuPage;
		frame.add(menuPage);
		frame.setVisible(true);
	}
	
	// pre - none
	// post - changes frame to show main menu
	public static void backToMenu()
	{
		frame.remove(curPage);
		curPage = new Menu();
		frame.add(curPage);
		frame.setVisible(true);
	}
	
	// pre - none
	// post - returns true if a game is currently active (playing or paused), else false
	// post - a game is "active" if it has been started and the player has not died (regardless of what page is being viewed)
	public static boolean gameActive()
	{
		return (w != null && !w.isGameOver());
	}
}