// Dude - the playable character in the game

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.net.URL;
import javax.swing.ImageIcon;


public class Dude
{
	private int x, velocityX, bounceVelocity;
	private int y, velocityY, accelerationY;
	private int swingCounter, strideCounter, lastHitCounter;
	private int health, strength, money;
	private boolean onGround;
	private boolean isSwinging;
	private boolean lastMovedRight;
	
	private Image facingRight, facingRight2, facingRight3, facingLeft, facingLeft2, facingLeft3;
	private Image jumpingRight, jumpingLeft;
	private Image swordRight, swordRight2, swordRight3, swordLeft, swordLeft2, swordLeft3;
	public Image display;
	
	
	public static final int HIT_COOLDOWN = 15;
	public static final int SWING_LENGTH = 15;
	public static final int SWING_COOLDOWN = 35;	// how long you must wait to swing again
	public static final int STRIDE_LENGTH = 18;
	public static final int SPEED = 10;
	public static final int JUMP_STRENGTH = 20;		// for standard spacebar jumps
	public static final int MAX_HEALTH = 500;
	private World w;
	
	// pre - the world that contains this dude
	// post - constructs a dude
	public Dude(World world)
	{
		this.w = world;
		this.strength = 40;
		loadImages();
		x = 0;
		y = 0;
		health = MAX_HEALTH;
		onGround = true;
		isSwinging = false;
		lastMovedRight = true;
	}
	
	// pre - none
	// post - loads all images for the Dude's various poses
	private void loadImages()
	{
		facingRight 	= new ImageIcon("Images/player/playerRight.png").getImage();	// insert the directories here
		facingRight2 	= new ImageIcon("Images/player/playerRight2.png").getImage();
		facingRight3 	= new ImageIcon("Images/player/playerRight3.png").getImage();
		facingLeft 		= new ImageIcon("Images/player/playerLeft.png").getImage();
		facingLeft2 	= new ImageIcon("Images/player/playerLeft2.png").getImage();
		facingLeft3 	= new ImageIcon("Images/player/playerLeft3.png").getImage();
		
		jumpingRight	= new ImageIcon("Images/player/playerJumpRight.png").getImage();
		jumpingLeft		= new ImageIcon("Images/player/playerJumpLeft.png").getImage();
		
		swordRight		= new ImageIcon("Images/sword/swordRight.png").getImage();
		swordRight2		= new ImageIcon("Images/sword/swordRight2.png").getImage();
		swordRight3		= new ImageIcon("Images/sword/swordRight3.png").getImage();
		swordLeft		= new ImageIcon("Images/sword/swordLeft.png").getImage();
		swordLeft2		= new ImageIcon("Images/sword/swordLeft2.png").getImage();
		swordLeft3		= new ImageIcon("Images/sword/swordLeft3.png").getImage();
		
		display = facingRight;
	}
	
	// pre - none
	// post - changes the Dude's position based on velocities, shows proper image
	public void move()
	{
		x += velocityX + bounceVelocity;
		bounceVelocity *= 0.7;
		strideCounter = (strideCounter%STRIDE_LENGTH) + 1;
		
		y += velocityY;
		velocityY += accelerationY;
		
		checkWindowBounds();	// prevents the Dude from moving out of the boundaries
		if (swingCounter <= SWING_COOLDOWN)
			swingCounter++;
		if (lastHitCounter <= HIT_COOLDOWN)
			lastHitCounter++;
		groundCheck();
		getImage();
	}
	
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
	// post - returns strength (calculated with strength and falling velocity)
	// to be used for calculations (not displaying in HUD in World)
	public int getStrength()
	{return strength - velocityY;}
	
	// pre - none
	// post - returns strength
	// to be used for displaying HUD (not for calculations)
	public int getStrengthStat()
	{return strength;}
	
	// pre - none
	// post - returns height
	public int getHeight()
	{return display.getHeight(null);}
	
	// pre - none
	// post - returns width
	public int getWidth()
	{return display.getWidth(null);}
	
	// pre - none
	// post - returns amount of $$$
	public int getMoney()
	{return money;}
	
	// pre - none
	// post - returns the number of the sword image to be used for painting
	public int swordNumber()
	{return ((3*swingCounter)/SWING_LENGTH) + 1;}
	
	// pre - none
	// post - returns true if the Dude is currently in the process of swinging his sword, else false
	public boolean isSwinging()
	{return (swingCounter < SWING_LENGTH);}
	
	// pre - none
	// post - returns true if the Dude is NOT swinging AND if the cool-down he must wait between swings is over, else false
	public boolean swingReady()
	{return (!isSwinging && swingCounter > SWING_COOLDOWN);}
	
	// pre - none
	// post - returns true if the Dude is on the ground of his world, else false
	public boolean isOnGround()
	{return onGround;}
	
	// pre - none
	// post - returns true if the Dude moved right most recently (ie he is facing right currently)
	public boolean lastMovedRight()
	{return lastMovedRight;}
	
	// pre - none
	// post - returns the Dude's image depending on what direction he is facing and whether he is jumping
	public Image getImage()
	{
		if (lastMovedRight)
		{
			if (onGround)
				display = getWalkingRightImage();
			else
				display = getJumpingRightImage();
		}
		else
		{
			if (onGround)
				display = getWalkingLeftImage();
			else
				display = getJumpingLeftImage();
		}
		return display;
	}
	
	// pre - none
	// post - returns the Dude's walking right image based on what stage of walking he is in
	private Image getWalkingRightImage()
	{
		if (velocityX == 0)
		{
			strideCounter = 0;
			return facingRight;
		}
		
		int totalImages = 3;
		// looks at the number of total images and the stride counter and returns which image we should use
		int imageNumber = (strideCounter * totalImages)/(STRIDE_LENGTH);		
		if (imageNumber == 1)
			return facingRight;
		else if (imageNumber == 2)
			return facingRight2;
		else
			return facingRight3;
	}
	
	// pre - none
	// post - returns the Dude's walking left image based on what stage of walking he is in
	private Image getWalkingLeftImage()
	{
		if (velocityX == 0)
		{
			strideCounter = 0;
			return facingLeft;
		}
		
		int totalImages = 3;
		// looks at the number of total images and the stride counter and returns which image we should use
		int imageNumber = (strideCounter * totalImages)/(STRIDE_LENGTH);		
		if (imageNumber == 1)
			return facingLeft;
		else if (imageNumber == 2)
			return facingLeft2;
		else// if (imageNumber == 3)
			return facingLeft3;
		
	}
	
	// pre - none
	// post - returns the Dude's jumping left image
	private Image getJumpingLeftImage()
	{
		return jumpingLeft;
	}
	
	// pre - none
	// post - returns the Dude's jumping right image
	private Image getJumpingRightImage()
	{
		return jumpingRight;
	}
	
	// pre - none
	// post - returns the Dude's sword image
	public Image getSwordImage()
	{
		Image display;
		int swordNumber = swordNumber();
		if(lastMovedRight)	
		{
			if (swordNumber == 1)
				display = swordRight;
			else if (swordNumber == 2)
				display = swordRight2;
			else
				display = swordRight3;
		}
		else
		{
			if (swordNumber == 1)
				display = swordLeft;
			else if (swordNumber == 2)
				display = swordLeft2;
			else /*if (swordNumber == 3)*/
				display = swordLeft3;
		}
		return display;
	}
	
	// pre - none
	// post - sets the x coord to one of the bounds if the Dude exceeds one of these bounds
	public void checkWindowBounds()
	{
		if (x <= World.MIN_X)
			x = World.MIN_X;
		if (x + getWidth() >= World.MAX_X)
			x = World.MAX_X - getWidth();	
	}	
	
	// pre - none
	// post - sets the Dude to move to the right (positive)
	public void moveRight()
	{
		lastMovedRight = true;
		velocityX = SPEED;
	}
	
	// pre - none
	// post - sets the Dude to move to the left (negative)
	public void moveLeft()
	{	
		lastMovedRight = false;
		velocityX = -SPEED;
	}
	
	// pre - none
	// post - makes Dude stand still
	public void still()
	{
		velocityX = 0;
	}
	
	// pre - jumpStrength: how hard the Dude is to jump
	// post - starts the Dude in a jumping motion by setting y velocity
	public void initiateJump(int jumpStrength)
	{
		velocityY = jumpStrength;
		accelerationY = -1;
		onGround = false;
	}
	
	// pre - none
	// post - makes the Dude fall if he's above the ground, or stand on the ground if he's on or below the ground of the world that contains him
	public void groundCheck()
	{
		if (y != 0)
			accelerationY = -1;
		
		if (y <= 0)
		{
			y = 0;
			velocityY = 0;
			accelerationY = 0;
			onGround = true;
		}
	}
	
	// to be called if the Dude is hit by a cube
	// pre - an enemy cube
	// post - processes the proper movement for the Dude depending on how he hits the cube
	public void hitByCube(CubeEnemy enemy)
	{
		Rectangle playerBounds = this.getBounds();
		Rectangle enemyBounds = enemy.getBounds();
		
		int playerMidX = (int)(playerBounds.getCenterX());			//the x value of the middle of the player
		int playerBottomY = (int)(playerBounds.getMaxY());			//the y value of the bottom of the player
		int enemyMidX = (int)(enemyBounds.getCenterX());			//the x value of the middle of the enemy
		int enemyTopY = (int)(enemyBounds.getMinY());				//the y value of the top of the enemy
		
		boolean onTop = Math.abs(playerMidX - enemyMidX) <= (int)((enemyBounds.getWidth()) / 2 + (playerBounds.getWidth()))
						&& playerBottomY <= enemyTopY + 10;		// <= because screen coords start in upper left corner
		if (onTop)
		{
			y = (int)enemyBounds.getHeight();			//displaces the player the enemy's height up off the ground
			initiateJump(enemy.getStrength());
		}
		else if (playerMidX - enemyMidX > 0)			//or pushes the player away from the enemy
			bounceVelocity = (int)(enemy.getStrength() * 3);
		else
			bounceVelocity = (int)(enemy.getStrength() * -3);
		
		if(lastHitCounter >= HIT_COOLDOWN)
		{
			changeHealth(-enemy.getStrength());
			lastHitCounter = 0;
		}
	}
	
	// pre - none
	// post - returns the rectangle representing the coords for collision for this big Dude
	public Rectangle getBounds()
	{
		Rectangle bounds = new Rectangle(this.x, 
										w.getHeight() - (World.GROUND.getHeight(null) + getHeight() + this.y),
										display.getWidth(null),
										display.getHeight(null));
		return bounds;
	}
	
	// pre - none
	// post - returns the rectangle representing the coords for collision for this big Dude's sword
	public Rectangle getSwordBounds()
	{
		Rectangle bounds = new Rectangle(this.getSwordX(), 
				w.getHeight() - (World.GROUND.getHeight(null) + getHeight() + this.y) + 80,
				getSwordImage().getWidth(null) + 5,
				getSwordImage().getHeight(null));
		return bounds;
	}
	
	// pre - the value to add to health
	// post - adds val to health, sets health to 0 if health is below 0
	public void changeHealth(int val)
	{
		health += val;
		if (health < 0)
			health = 0;
	}
	
	// pre - the value to add to strength
	// post - adds val to strength
	public void changeStrength(int val)
	{
		strength += val;
	}
	
	// pre - none
	// post - returns the x coord of the Dude's sword
	public int getSwordX()
	{
		int swordX = this.getX() - getSwordImage().getWidth(null) + 5;
		if(lastMovedRight)
			swordX += this.getWidth() + getSwordImage().getWidth(null) - 10;
		return swordX;
	}
	
	// pre - none
	// post - swings the sword if the Dude is ready to swing (and plays sound?)
	public void trySwing()
	{
		if(swingReady())
		{
			swingCounter = 0;
			// sound effects?
			try
			{
				System.out.println("sound!");
				AudioClip clip = Applet.newAudioClip(new URL("file:/sword.wav"));
				clip.play();
			}
			catch (Exception e) {System.out.println("no sound\n" + e);}
		}
	}
	
	// pre - the coin that the Dude picks up
	// post - adds the value of the coin to the Dude's pocket $$
	public void pickUpCoin(Coin coin)
	{
		money += coin.getValue();
	}
	
	// pre - none
	// post - removes money, if the player has enough, to receive more health, up to MAX_HEALTH
	// $10 --> 1 health
	public void buyHealth()
	{
		if (money >= 10 && health < MAX_HEALTH)
		{
			health += 1;
			money -= 10;
		}
	}
	
	// pre - none
	// post - adds damage by removing money, if the player has enough
	// $70 --> 1 damage
	public void buyDamage()
	{
		if (money >= 75)
		{
			strength += 1;
			money -= 75;
		}
	}
}
