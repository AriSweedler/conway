// CubeEnemy - the cube enemies in the game

import java.awt.*;
import javax.swing.ImageIcon;

public class CubeEnemy
{
	private int x, velocityX, accelerationX;
	private int y, velocityY, accelerationY;
	private int hitPlayerTurnRedCD = 10;	// how many frames the cube is red when it gets hit
	private int playerHitCounter = hitPlayerTurnRedCD + 1;
	private int maxHealth, health, strength, width;
	private String color;
	public Image redImage, colorImage, display;
	private boolean isAlive = true;
	private boolean isHit = false;
	private boolean onGround = true;
	//private boolean readyToJump = true;
	private static final int JUMP_STRENGTH = 12;
	private World w;
	
	// pre - x and y coords, width, strength, health, world containing this enemy, colorType
	// post - constructs a new CubeEnemy
	public CubeEnemy(int x, int y, int width, int strength, int health, World world, String colorType)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.strength = strength;
		this.w = world;
		this.maxHealth = health;
		this.health = health;
		this.color = colorType;
		loadImages(colorType);
	}
	
	// pre - colorType
	// post - loads an image with size based on width and color based on colorType
	private void loadImages(String colorType)
	{
		String directory = new String ("Images/cube/size" + width + "/");
		String directoryRed = 	new String (directory + "red" 		+ "Cube.png");
		String directoryColor = new String (directory + colorType 	+ "Cube.png");
		colorImage	= new ImageIcon(directoryColor).getImage();
		redImage 	= new ImageIcon(directoryRed).getImage();
		display = colorImage;
	}
	
	// pre - int xPos
	// post - sets x coord to xPos
	public void setX(int xPos)
	{x = xPos;}
	
	// pre - none
	// post - returns x coord
	public int getX()
	{return x;}
	
	// pre - none
	// post - returns y coord
	public int getY()
	{return y;}
	
	// pre - none
	// post - returns health
	public int getHealth()
	{return health;}
	
	// pre - none
	// post - returns health
	public int getMaxHealth()
	{return maxHealth;}
	
	// pre - none
	// post - returns width
	public int getWidth()
	{return display.getWidth(null);}
	
	// pre - none
	// post - returns height = width
	public int getHeight()
	{return getWidth();} // lol
	
	// pre - none
	// post - returns rectangle representing collision coords of the cube
	public Rectangle getBounds()
	{
		if(!isAlive || isHit)
			return new Rectangle(0,0,0,0);
		return new Rectangle(x,
							w.getHeight() - (w.getGroundHeight() + getHeight() + y),
							display.getWidth(null),
							display.getHeight(null));
	}
	
	// pre - none
	// post - returns the normal colorImage unless it has recently been hit, in which case returns red image
	public Image getImage()
	{
		if (isHit || playerHitCounter < hitPlayerTurnRedCD)
			display = redImage;
		else
			display = colorImage;
				
		return display;
	}
	
	// pre - the speed of the ground of the world containing the cube
	// post - changes the cube position based on groundSpeed, velocities, and a special move based on color
	public void move(int groundSpeed)
	{
		cubePersonalityMove();
		
		x += velocityX + groundSpeed;
		velocityX += accelerationX;
		
		y += velocityY;
		velocityY += accelerationY;
		
		hitCheck();
		checkHealth();
		groundCheck();
		getImage();
	}
	
	// pre - none
	// post - reduces velocity towards 0
	private void slowDown()
	{
		if(velocityX >= 2)
			velocityX-=2;
		else if(velocityX <= -2)
			velocityX+=2;
	}
	
	// pre - boolean playerIsToTheLeft
	// post - speeds cube up away from player
	private void speedUp(boolean playerIsToTheLeft)
	{
		if (playerIsToTheLeft)
			velocityX--;
		else
			velocityX++;
	}
	
	// pre - none
	// post - if health is below 0, sets cube to not alive and releases $$$ for the player
	public void checkHealth()
	{
		if (health <= 0 && isAlive)
		{
			releaseCoins();
			isAlive = false;
		}
	}
	
	// pre - none
	// post - reduces the cool down of getting hit by one if the cool down is currently active
	private void hitCheck()
	{
		if (playerHitCounter <= hitPlayerTurnRedCD)
			playerHitCounter++;
	}
	
	// pre - none
	// post - returns true if alive, else false
	public boolean isAlive()
	{return isAlive;}
	
	// pre - none
	// post - returhs strength of cube
	public int getStrength()
	{return strength;}
	
	// pre - none
	// post - executes a special move based on the color of the cube
	private void cubePersonalityMove()
	{
		boolean playerIsToTheLeft = w.player.getBounds().getX() < getBounds().getX();
		
		if (color.equals("orange"))
			orangeMove(playerIsToTheLeft);
		else if (color.equals("yellow"))
			yellowMove(playerIsToTheLeft);
		else if (color.equals("green"))
			greenMove(playerIsToTheLeft);
		else if (color.equals("blue"))
			blueMove(playerIsToTheLeft);
		else if (color.equals("pink"))
			pinkMove(playerIsToTheLeft);
		else if (color.equals("black"))
			blackMove(playerIsToTheLeft);
		else if (color.equals("white"))
			whiteMove(playerIsToTheLeft);
		else if (color.equals("grey"))
			greyMove(playerIsToTheLeft);
	}
	
	// pre - boolean playerIsToTheLeft
	// post - random chance to make quick leap towards player
	private void orangeMove(boolean playerIsToTheLeft)
	{
		if (onGround && Math.random() > 0.95)
		{
			initiateJump(8);
			if (playerIsToTheLeft)
				velocityX = -15;
			else
				velocityX = 15;
		}
		else
		{
			 slowDown();
		}
		
	}
	
	// pre - boolean playerIsToTheLeft
	// post - cube moves towards player semi-quickly
	private void yellowMove(boolean playerIsToTheLeft)
	{
		if (playerIsToTheLeft)
			velocityX = -4;
		else
			velocityX = 4;
	}
	
	// pre - boolean playerIsToTheLeft
	// post - small chance to run really quickly away from player
	private void greenMove(boolean playerIsToTheLeft)
	{
		if (onGround && Math.random() > 0.97)
		{
			initiateJump(12);
			if (playerIsToTheLeft)
				velocityX = 15;
			else
				velocityX = -15;
		}
		else if(onGround && velocityX <= 4)
		{
			yellowMove(playerIsToTheLeft);
		}
		else
			slowDown();
	}
	
	// pre - boolean playerIsToTheLeft
	// post - moves slowly towards player, if hit, runs quickly towards player
	private void blueMove(boolean playerIsToTheLeft)
	{
		if (playerIsToTheLeft)
			velocityX = -1;
		else
			velocityX = 1;
		
		if(isHit)
		{
			y = 0;
			if (playerIsToTheLeft)
				velocityX = -7;
			else
				velocityX = 7;
		}
	}
	
	/* currently unavailable */
	// pre - boolean playerIsToTheLeft
	// post - random chance to make quick leap at player
	private void pinkMove(boolean playerIsToTheLeft)
	{
		//randomly jump up and dash to the player while in the air.
		//If it is on the ground, then creep slowly to the player.
		//do not zoom to the player while hit
		if (Math.random() > 0.95 && onGround && !isHit)
		{
			initiateJump(15);
		}
		else if (!onGround && !isHit && Math.random() > 0.7)
			speedUp(playerIsToTheLeft);
		else if (onGround && !isHit)
			greyMove(playerIsToTheLeft);
		else if (onGround)
			slowDown();
	}
	
	// pre - boolean playerIsToTheLeft
	// post - moves slowly towards player normally
	private void greyMove(boolean playerIsToTheLeft)
	{
		if (playerIsToTheLeft)
			velocityX = -1;
		else
			velocityX = 1;
	}
	
	// pre - boolean playerIsToTheLeft
	// post - stays still unless hit, in which case moves away
	private void whiteMove(Boolean playerIsToTheLeft)
	{
		velocityX = 0;
		if (isHit && playerIsToTheLeft)
			velocityX = 10;
		else if (isHit && !playerIsToTheLeft)
			velocityX = -10;
	}
	
	// pre - boolean playerIsToTheLeft
	// post - moves erratically
	private void blackMove(Boolean playerIsToTheLeft)
	{
		if (isHit || Math.random() > 0.95)
			velocityX = 0;
		else if (Math.random() > 0.3 && Math.abs(velocityX) <= 10)
			speedUp(playerIsToTheLeft);
		else
			slowDown();
	}
	
	// pre - none
	// post - begins a jump by making a nonzero y-velocity and a non zero y-acceleration
	public void initiateJump()
	{
		velocityY = JUMP_STRENGTH;
		accelerationY = -1;
		onGround = false;
	}
	
	// pre - int jumpStrength
	// post - begins a jump with jumpStrength power 
	public void initiateJump(int jumpStrength)
	{
		velocityY = jumpStrength;
		accelerationY = -1;
		onGround = false;
	}
	
	// pre - none
	// post - sets cube on ground if it is on or below ground; else sets onGround to false
	public void groundCheck()
	{
		if (y <= 0)
		{
			if (isAlive)
			{
				y = 0;
				velocityY = 0;
				accelerationY = 0;
				onGround = true;
			}
			isHit = false;
		}
		else
		{
			onGround = false;
			accelerationY = -1;
		}
	}
	
	// pre - a Dude
	// post - processes momvement and damage taken by the Dude's sword
	public void hitBySword(Dude p)
	{
		Rectangle enemyBounds = this.getBounds();
		Rectangle swordBounds = p.getSwordBounds();
		int fleeSpeed = 5;
		
		int swordLeftX = (int)(swordBounds.getMinX());	//the leftmost x-value of the sword
		int swordRightX = (int)(swordBounds.getMaxX());	//the rightmost x-value of the sword
		int enemyLeftX = (int)(enemyBounds.getMinX());	//the leftmost x-value of the enemy
		int enemyRightX = (int)(enemyBounds.getMaxX());	//the rightmost x-value of the enemy
		
		if (swordLeftX < enemyLeftX)		//if cube was hit from the left
			this.flee(fleeSpeed);			//then it runs to the right
		else if (swordRightX > enemyRightX)	//else if the cube was hit from the right
			this.flee(-fleeSpeed);			//then it runs to the left
		
		if (onGround)	
			initiateJump();
			
		this.health -= p.getStrength();
		isHit = true;
	}
	
	// pre - int fleeSpeed
	// post - runs with fleeSpeed velocity and decreasing speed
	public void flee(int fleeSpeed)
	{
		velocityX = fleeSpeed;
		accelerationX = -(Math.abs(fleeSpeed)/fleeSpeed);
	}
	
	// pre - none
	// post - resets hit cooldown
	public void hitByPlayer()
	{
		playerHitCounter = 0;
	}
	
	// pre - none
	// post - called when killed by Dude, spawns coins proportional to strength with random velocity
	public void releaseCoins()
	{
		int moneyToGive = (int)(maxHealth * strength / 30) + 10;
		
		while (moneyToGive >= Coin.GOLD_VALUE)
		{
			w.addCoin(new Coin(x, y, w, "gold"));
			moneyToGive -= Coin.GOLD_VALUE;
		}
		while (moneyToGive >= Coin.SILVER_VALUE)
		{
			w.addCoin(new Coin(x, y, w, "silver"));
			moneyToGive -= Coin.SILVER_VALUE;
		}
		while (moneyToGive >= Coin.BRONZE_VALUE)
		{
			w.addCoin(new Coin(x, y, w, "bronze"));
			moneyToGive -= Coin.BRONZE_VALUE;
		}
	}
}