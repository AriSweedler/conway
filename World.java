// World - the world class, runs the majority of the game

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

// World implements Runnable -- similar to extending thread but more flexible (ie World can extend JPanel now instead)
// this adds the functionality of running the classes's methods on new threads -- see comments on this classes's begin and run methods
public class World extends JPanel implements Runnable
{
	private Thread gameThread;						// the thread that this runs on
	public static BufferedImage BACK;				// background image
	public static Image GROUND, finishPole;			// ground and finish pole	(finish pole is not a FixedImage because it must appear in front of the player)
	private int backPos, groundDisp;				// displacements for background and ground images
	// backPos = displacement of background,
	// groundDisp = displacement of GROUND blocks
	private int distanceFromLastEnemy;	
	private boolean firstCrowd = true;
	private boolean gameRunning;
	private static final int FPS = 60;				// used only to calculate NS_PER_FRAME
	private static final double NS_PER_FRAME = 1000000000.0 / FPS;	// in nanoseconds
	private int groundSpeed = 0;					// rate of change of this.groundDisp (later changed by the player's movement)
	public Dude player;
	private ArrayList<CubeEnemy> enemies;
	private static final int ENEMY_SPAWN_DISTANCE = 1000;
	private static final int WINNING_KILLS = 3;		// # of kills to win
	private ArrayList<Coin> coins;
	private int enemiesTowardsShop;					// counter until next shop
	private int enemiesKilled;
	private FixedImage shop, crowd, finish;
	/* keys: */
	private boolean leftDown = false;
	private boolean rightDown = false;
	private boolean spacebarDown = false;
	private boolean cDown = false;
	private boolean oneDown = false;
	private boolean twoDown = false;
	// both min and max are x-coords of beginning of player image:
	public static int MIN_X = 450;
	public static int MAX_X = WorldRunner.WIDTH - MIN_X;

	// pre - none
	// post - constructs a new World (a new game)
	public World()
	{
		gameThread = new Thread(this);
		setVisible(true);
		setFocusable(true);
		setBackground(Color.BLUE);
		
		enemiesTowardsShop = 7;
		enemiesKilled = 0;
		groundDisp = 0;
		backPos = 300;

		// using ImageIO (which needs a try and catch) here instead of Casey's way
		//   because this is the best way to load a BufferedImage instead of just an Image
		//	 a BufferedImage is easier to get subimages from
		try
		{
			GROUND = ImageIO.read(new File("Images/grass3.jpg"));
			BACK = ImageIO.read(new File("Images/background.jpg"));
		}
		catch (IOException e)
		{System.out.println(e);}
		
		addAllKeyStrokes();
		player = new Dude(this);
		enemies = new ArrayList<CubeEnemy>();
		coins = new ArrayList<Coin>();
	}
	
	   /****************************/
	  /*     SETUP METHODS,       */
	 /*    SETTERS AND GETTERS   */
	/****************************/
	
	// pre - none
	// post - calls other methods to key bind each control
	private void addAllKeyStrokes()
	{
		addKeyStroke("LEFT", 			"leftPress");
		addKeyStroke("released LEFT", 	"leftReleased");
		addKeyStroke("RIGHT", 			"rightPress");
		addKeyStroke("released RIGHT", 	"rightReleased");
		addKeyStroke("SPACE",			"spacebarPress");
		addKeyStroke("released SPACE",	"spacebarReleased");
		addKeyStroke("C",				"cPress");
		addKeyStroke("released C",		"cReleased");
		addKeyStroke("ESCAPE", 			"escPressed");
		addKeyStroke("1",				"onePressed");
		addKeyStroke("2",				"twoPressed");
		addKeyStroke("3",				"threePressed");
		addKeyStroke("released 1",		"oneReleased");
		addKeyStroke("released 2",		"twoReleased");
		addKeyStroke("released 3",		"threeReleased");
	}
	
	// inputmap takes a keystroke, goes to the actionmap with the same tag, calls that action
	// all JComponents have built in inputmaps and actionmaps
	// pre - the string representation for the keyboard key (from KeyStroke docs:
	/* 	
	 	<modifiers>* (<typedID> | <pressedReleasedID>)


    	modifiers := shift | control | ctrl | meta | alt | altGraph
    	typedID := typed <typedKey>
    	typedKey := string of length 1 giving Unicode character.
    	pressedReleasedID := (pressed | released) key
    	key := KeyEvent key code name, i.e. the name following "VK_".)
    	*/
	// pre - a tag that represents the action (defined in this method's body)
	// post - adds the input keys to the inputmap and actionmap of this
	private void addKeyStroke(String key, String tag2)
	{
		final String tag = tag2;		// must be final in order to be referenced in the anonymous class below (i dont know why)
		InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = this.getActionMap();
		
		inputMap.put(KeyStroke.getKeyStroke(key), tag);		// inputmap takes a key representation, and a user given tag
		actionMap.put(tag, new AbstractAction()				// action map takes the user given tag of the input he wants to map the action to, and an Action()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (tag == "leftPress")
					leftDown = true;
				else if (tag == "leftReleased")
					leftDown = false;
				else if (tag == "rightPress")
					rightDown = true;
				else if (tag == "rightReleased")
					rightDown = false;
				else if (tag == "spacebarPress")
					spacebarDown = true;
				else if (tag == "spacebarReleased")
					spacebarDown = false;
				else if (tag == "cPress")
					cDown = true;
				else if (tag == "cReleased")
					cDown = false;
				else if (tag == "escPressed")
				{
					leftDown = false;
					rightDown = false;
					spacebarDown = false;
					cDown = false;
					WorldRunner.endGame();
				}
				else if (tag == "onePressed")
					oneDown = true;
				else if (tag == "twoPressed")
					twoDown = true;
				else if (tag == "oneReleased")
					oneDown = false;
				else if (tag == "twoReleased")
					twoDown = false;
			}
		});
	}
	
	// pre - none
	// post - returns the height of the ground
	public int getGroundHeight()
	{return GROUND.getHeight(null);}
	
	// pre - none
	// post - returns the width of the ground (in most cases the ground is made of square tiles, so width == height
	public int getGroundWidth()
	{return GROUND.getWidth(null);}
	
	// pre - none
	// post - returns true if the player has health 0 or below, else false 
	public boolean isGameOver()
	{return (player.getHealth() <= 0);}
	
	
	
	// makes a new thread to run these methods on so that repaint() doesnt get inhibited by methods or loops here
	// pre - none
	// post - this both starts the gameThread, and calls the thread's run() method. since the gameThread was initiated 
	// 			 with this class as its subject, the run() call just calls this class's overriding run() method
	//	threads:  https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
	public void begin()
	{
		gameRunning = true;
		if (!gameThread.isAlive())	// if not already started (ie if it's a new game)
			gameThread.start();
	}
	
	// ends game by making gameRunning false;
	// pre - none
	// post - changes gameRunning to false
	public void end()
	{
		gameRunning = false;
	}
	
	// called when the thread containing this object calls its start() method
	// pre - none
	// post - runs a loop that constantly updates and repaints the game
	public void run()
	{
		int frames = 0;
		long firstTime = System.nanoTime();	// int too short
		long lastTime = firstTime;
		
		/* this game loop regulates the number of times the game updates and repaints per second.
		 * this prevents the game from running faster in different sized windows or faster computers
	  	 * the game should perform at a significantly different speed only on terrible pieces of trash computers
		 * also, once the gameThread is started, this loop runs indefinitely; the only thing that changes when a game is paused is
		 * 		whether or not the program enters the if statement's body. The gameThread only ends when a new World is constructed in 
		 *		place of this in WorldRunner
		 */
		while (true)
		{
			if (gameRunning)
			{
				long currTime = System.nanoTime();
				if (currTime - lastTime >= NS_PER_FRAME)	// if NS_PER_FRAME has passed
				{
					lastTime = currTime;
					frames++;
					update();		// change the actual game stuff (calculations, etc)
					repaint();		// display
				}
				// just for displaying frames per second:
				if (currTime - firstTime >= 1000000000)	// 1000000000 nanoseconds in 1 second
				{
					firstTime = currTime;
					System.out.println("FPS: " + frames);
					frames = 0;
				}
			}
		}
	}
	
	  /****************************/
	 /*     UPDATE METHODS       */
	/****************************/
	
	// pre - none
	// post - processes all of the game calculations each cycle (ie moves, collisions, etc)
	private void update()
	{
		moveBackground();
		moveEnemies();
		moveCoins();
		movePlayer();
		moveFixedImages();
		checkUpgrades();		
		if (enemies.size() >= 2)		// this is the limiter to how many enemies can spawn at once
			distanceFromLastEnemy = 0;
		checkCollisions();
		
		// make shop
		if (enemiesTowardsShop >= 7)	// a shop appears every 7 enemies killed
		{
			shop = new FixedImage(player.getX() + 1000, this, "Images/shop-icon2.png");
			enemiesTowardsShop = 0; 
		}
		
		// make end (dont repeat if crowd is already made)
		if (crowd == null && enemiesKilled >= WINNING_KILLS)
		{
			crowd = new FixedImage(player.getX() + 1400, this, "Images/finish line/cheeringCrowd.png");
			finishPole = new ImageIcon("Images/finish line/pole.png").getImage();
			finish = new FixedImage(player.getX() + 2000, this, "Images/finish line/finish.png");
			distanceFromLastEnemy = -2000;
			enemies = new ArrayList<CubeEnemy>();	// get rid of all enemies
		}		
	}
	
	// pre - none
	// post - changes the ground based on the dude's speed, and changes the background position based on the dude's speed and the ground's speed
	private void moveBackground()
	{
		if (rightDown && player.getX() >= MAX_X  - player.getWidth() && backPos != BACK.getWidth() - this.getWidth())
			groundSpeed = -1 * Dude.SPEED;
		else if (leftDown && player.getX() <= MIN_X && backPos != 0)
			groundSpeed = Dude.SPEED;
		else
			groundSpeed = 0;
		
		// groundSpeed is either Dude.SPEED, -1 * Dude.SPEED, or 0
		// this scrolls the background in the opposite direction of groundSpeed
		backPos += -1 * groundSpeed/Dude.SPEED;
		distanceFromLastEnemy += Math.abs(groundSpeed);
		if (distanceFromLastEnemy >= ENEMY_SPAWN_DISTANCE && crowd == null)		// if crowd != null, you already won!
			spawnEnemy();
		
		groundDisp += groundSpeed;		// move
		
		// if ground moves too much, start over the displacement; getGroundWidth() to load one image off panel
		// used to make the ground continuous
		if (groundDisp >= getGroundWidth())
			groundDisp = 0;
		if (groundDisp <= -getGroundWidth())
			groundDisp = 0;
	}
	
	// pre - none
	// post - processes each enemy's move() in enemies arraylist
	private void moveEnemies()
	{
		for (int i = 0; i < enemies.size(); i++)
		{
			CubeEnemy enemy = enemies.get(i);
			if (!enemy.isAlive())		// if enemy is dead, then remove it
			{
				enemies.remove(i);
				enemiesTowardsShop++;
				enemiesKilled++;
				i--;
			}
			else 						// otherwise, move it!
				enemy.move(groundSpeed);
		}
	}
	
	// pre - none
	// post - moves all of the coins in the coins arraylist
	private void moveCoins()
	{
		for(Coin coin: coins)
		{
			coin.move(groundSpeed);
		}
	}
	
	// pre - none
	// post - moves the player based on what keys are pressed
	private void movePlayer()
	{
		if (rightDown)
			player.moveRight();
		else if (leftDown)
			player.moveLeft();
		else
			player.still();
			
		if (spacebarDown && player.isOnGround())
			player.initiateJump(Dude.JUMP_STRENGTH);
		
		if (cDown)
			player.trySwing();
			
		player.move();	
	}
	
	// pre - none
	// post - moves FixedImages to go along with the ground
	private void moveFixedImages()
	{
		if (shop != null)
			shop.move(groundSpeed);
		if (crowd != null)
			crowd.move(groundSpeed);
		if (finish != null)
			finish.move(groundSpeed);
	}
	
	// pre - none
	// post - upgrades player's stats if the player is in front of a shop and the correct keys are pressed
	private void checkUpgrades()
	{
		if (shop != null && player.getBounds().intersects(shop.getBounds()))
		{
			if (oneDown)
				player.buyHealth();
			if (twoDown)
				player.buyDamage();
		}
	}
	
	// pre - none
	// post - checks if the player collides with any enemies (bad), or if his sword does (good)
	private void checkCollisions()
	{
		Rectangle playerBounds = player.getBounds();
		Rectangle swordBounds = player.getSwordBounds();
		Rectangle enemyBounds;
		Rectangle coinBounds;
		Rectangle shopBounds = null;
		if (shop != null)
			shopBounds = shop.getBounds();
		
		for (CubeEnemy enemy: enemies)
		{
			enemyBounds = enemy.getBounds();
			if (enemyBounds.intersects(playerBounds))
			{
				player.hitByCube(enemy);
				enemy.hitByPlayer();
			}
			else if (player.isSwinging() && enemyBounds.intersects(swordBounds))
				enemy.hitBySword(player);
			
			if (shopBounds != null && shopBounds.intersects(enemyBounds))
				enemy.setX((int)shopBounds.getMaxX());
		}
		
		int i = 0;
		while (i < coins.size())
		{
			Coin coin = coins.get(i);
			coinBounds = coin.getBounds();
			if (playerBounds.intersects(coinBounds))
			{
				player.pickUpCoin(coin);
				coins.remove(i);
				i--;
			}
			i++;
		}
	}
	
	// pre - a coin
	// post - adds the parameter coin to the arraylist of coins
	public void addCoin(Coin coin)
	{
		coins.add(coin);
	}
	
	// pre - none
	// post - creates a new enemy in front of the player
	private void spawnEnemy()
	{
		distanceFromLastEnemy = 0;
		int spawnHeight = 250;
		int spawnX = player.getX() + 600;
		int size = 150;
		//enemies get stronger the farther you travel
		int level = enemiesKilled;
		int strength = (int)(Math.random()*20) + 5*level;
		int health = (int)(Math.random()*100) + 50*level;
		
		if ((health * strength / 15) < 100)
			size = 75;
		else if ((health * strength / 20) < 250)
			size = 100;
		else if ((health * strength / 20) < 500)
			size = 150;
		else if ((health * strength / 20) < 750)
			size = 200;
		else if ((health * strength / 20) < 1000)
			size = 250;
		
		if (!player.lastMovedRight())
			spawnHeight = 0;
		
		String color;
		int rand = (int)(Math.random()*7);
		if (rand == 0)
			color = "orange";
		else if (rand == 1)
			color = "yellow";
		else if (rand == 2)
			color = "green";
		else if (rand == 3)
			color = "blue";
		else if (rand == 4)
			color = "black";
		else if (rand == 5)
			color = "white";
		else
			color = "grey";
		enemies.add(new CubeEnemy(spawnX, spawnHeight, size, strength, health, this, color));
	}
	
	  /****************************/
	 /*     PAINT METHODS        */
	/****************************/
	
	// pre - a graphics object (automatically passed by the JComponent methods that call this method)
	// post - uses the Graphics object to draw everything on the screen
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		drawBackground(g);
		drawHUD(g); 			// draw health bar and money
		drawFixedImages(g);
		drawPlayer(g);
		if(player.isSwinging())	// only draw sword if the player is swinging
			drawSword(g);
		drawEnemies(g);
		drawCoins(g);
		drawGround(g);
		
		// check if game over
		if (player.getHealth() <= 0)
			drawEndGame(g);
		else if (finish != null && player.getX() >= finish.getX() + finish.getWidth())
			drawWinScreen(g);
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the stats for the player (HUD = heads-up display)
	private void drawHUD(Graphics g)
	{
		
		int hpBarSize = 150;
		int currHPPercent = (int)(((double)player.getHealth() * hpBarSize)/(Dude.MAX_HEALTH));
		int fontSize = 20;
		int fontHeightInPixels = 14;
		int dispFromEdge = 10;
		int healthBarHeight = 30;
		int upperLeftOfHPBar = getWidth() - dispFromEdge - hpBarSize;
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
		
		// health bar
		g.drawRect(		upperLeftOfHPBar,		//puts the health bar dispFromEdge pixels away from the edge
						dispFromEdge,			//dispFromEdge many pixels down from the top
						hpBarSize,				//hpBarSize = width
						healthBarHeight);		//healthBarHeight = height
		// health bar			
		g.fillRect(		upperLeftOfHPBar,
						dispFromEdge,
						currHPPercent,
						healthBarHeight);
		// health bar			
		g.drawString(	"Health: " + player.getHealth() + "/" + Dude.MAX_HEALTH,
						upperLeftOfHPBar - 140,
						dispFromEdge + healthBarHeight/2 + fontHeightInPixels/2);
		// money
		g.drawString(	"Money: " + player.getMoney(),
						dispFromEdge,
						dispFromEdge + healthBarHeight/2 + fontHeightInPixels/2);
						
		// strength / damage
		g.drawString(	"Strength: " + player.getStrengthStat(),
						this.getWidth() / 4 - 10,
						dispFromEdge + healthBarHeight/2 + fontHeightInPixels/2);
						
		g.drawString(	"Enemies Killed: " + (enemiesKilled),
						this.getWidth() * 2 / 4,
						dispFromEdge + healthBarHeight/2 + fontHeightInPixels/2);
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the player
	private void drawPlayer(Graphics g)
	{
		int playerY = this.getHeight() - (getGroundHeight() + player.getHeight() + player.getY());
		g.drawImage(player.getImage(), player.getX(), playerY, null);	// draws player
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the player's sword
	private void drawSword(Graphics g)
	{
		int playerY = this.getHeight() - (getGroundHeight() + player.getHeight() + player.getY());
		g.drawImage(player.getSwordImage(), player.getSwordX(), playerY + 34 * player.swordNumber(), null);
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the cube enemies
	private void drawEnemies(Graphics g)
	{
		for(CubeEnemy enemy: enemies)
		{
			int enemyX = (enemy.getX());
			int enemyY = this.getHeight() - (getGroundHeight() + enemy.getHeight() + enemy.getY());
			g.drawImage(enemy.getImage(), enemyX, enemyY, null);
			drawEnemyStats(enemy, enemyX, enemyY, g);
		}
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the coins in the coins arraylist
	private void drawCoins(Graphics g)
	{
		for(int i = 0; i < coins.size(); i++)
		{
			Coin coin = coins.get(i);
			int coinX = (coin.getX());
			int coinY = this.getHeight() - (getGroundHeight() + coin.getHeight() + coin.getY());
			g.drawImage(coin.getImage(), coinX, coinY, null);
		}
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// pre - should be called before all other draw methods
	// post - draws the background
	private void drawBackground(Graphics g)
	{
		int backWidth = BACK.getWidth(null);
		Image backImg1;
		Image backImg2;
		
		if (backPos >= backWidth)
			backPos = 0;

		if(backPos + this.getWidth() > backWidth)
		{
			backImg1 = BACK.getSubimage(backPos,						// starting X
										900,  							// starting Y
										backWidth - backPos,			// width of the image
										this.getHeight());				//height of the image
										
			backImg2 = BACK.getSubimage(0,								// starting X
										900,  							// starting Y
										this.getWidth() - (backWidth - backPos),	// width of the image
										this.getHeight());				//height of the image
										
			g.drawImage(backImg1, 0, 0, null);
			g.drawImage(backImg2, backWidth - backPos, 0, null);
		}
		else
		{
			backImg1 = BACK.getSubimage(backPos, 900, this.getWidth(), this.getHeight());	// take only a part of BACK that fits on jframe
			g.drawImage(backImg1, 0, 0, null);
		}
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the ground
	private void drawGround(Graphics g)
	{
		int groundTilesNeeded = getWidth()/getGroundWidth() + 3;
		// One extra tile is to round up. (So if we need 11.2 tiles we display 12 instead of 11)
		// two extra tiles are to cover the places that only drawing a tile that goes off the screen can cover
		for (int groundNum = 1; groundNum <= groundTilesNeeded; groundNum++)
		{
			// draw a tile every "groundNum" pixels starting at -groundNum and ending at getWidth()
			int groundX = (groundNum - 2)*getGroundWidth() + groundDisp;
			//the bottom of the ground tile will match the bottom of the screen
			int groundY = this.getHeight() - getGroundHeight();
			g.drawImage(GROUND, groundX, groundY, null);
		}
		if (finishPole != null)
			g.drawImage(finishPole, finish.getX() + finish.getWidth(), this.getHeight() - getGroundHeight() - finishPole.getHeight(null), null);
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws stats (attack and health) for each cube enemy
	private void drawEnemyStats(CubeEnemy enemy, int enemyX, int enemyY, Graphics g)
	{
		int dispFromEnemy = 5;
		int hpBarSize = enemy.getWidth() - 2 * dispFromEnemy;
		int currHPPercent = (int)(((double)enemy.getHealth() * hpBarSize)/(enemy.getMaxHealth()));
		int healthBarHeight = 15;
		int outputX = enemyX + dispFromEnemy;
		int outputY = enemyY - (healthBarHeight + dispFromEnemy);
			
		g.setFont(new Font("TimesRoman", Font.PLAIN, 14));
		g.drawRect(outputX, outputY, hpBarSize, 	healthBarHeight);
		g.fillRect(outputX, outputY, currHPPercent, healthBarHeight);
		g.drawString("Attack: " + enemy.getStrength(), outputX + dispFromEnemy, outputY - dispFromEnemy);
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// pre - should be called before everything except background
	// post - draws the fixedImages (ie shop and crowd) onto the World
	private void drawFixedImages(Graphics g)
	{
		if (crowd != null)
			g.drawImage(crowd.getImage(), crowd.getX(), this.getHeight() - GROUND.getHeight(null) - crowd.getImage().getHeight(null), null);
		if (shop != null)
			g.drawImage(shop.getImage(), shop.getX(), this.getHeight() - GROUND.getHeight(null) - shop.getImage().getHeight(null), null);
		if (finish != null)
			g.drawImage(finish.getImage(), finish.getX(), this.getHeight() - GROUND.getHeight(null) - finish.getImage().getHeight(null), null);
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the end game screen
	private void drawEndGame(Graphics g)
	{
		int xPos = this.getWidth() / 3;
		int yPos = this.getHeight() / 2 - 40;
		g.setFont(new Font("TimesRoman", Font.PLAIN, 120));
		g.drawString("GG WP.", xPos, yPos);
		g.setFont(new Font("TimesRoman", Font.ITALIC, 32));
		g.drawString("press escape to return to menu", xPos, yPos + 40);
		gameRunning = false;
		System.out.println("gg get good nerd");
	}
	
	// pre - a the graphics object associated with this jpanel (to be passed from paintComponent)
	// post - draws the win screen
	private void drawWinScreen(Graphics g)
	{
		int xPos = this.getWidth() / 3 - 30;
		int yPos = this.getHeight() / 5 + 15;
		g.setFont(new Font("TimesRoman", Font.PLAIN, 120));
		g.drawString("YOU WIN!", xPos, yPos);
		g.setFont(new Font("TimesRoman", Font.ITALIC, 32));
		g.drawString("press escape to return to menu", xPos, yPos + 40);
		gameRunning = false;
		System.out.println("gg guess you're good nerd");
	}
}