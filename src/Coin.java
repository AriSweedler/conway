// Coin - the coins that are dropped by enemies, can be picked up and spent on upgrades

import java.awt.*;
import javax.swing.ImageIcon;

public class Coin
{
	private Image img;
	private int x, y, vy, vx;
	private int value;
	private int bounceVY;
	private World w;
	public static final int GOLD_VALUE = 100;
	public static final int SILVER_VALUE = 10;
	public static final int BRONZE_VALUE = 1;
	private int coinLifeCounter;	// a cooldown before they can be picked up
	
	// pre - x and y position, the world in which the coin is to exist, the type of coin (ie value)
	// post - constructs a Coin object with near-random velocities and image based on given type
	public Coin(int x, int y, World world, String type)
	{
		coinLifeCounter = 0;
		w = world;
		loadImageAndValue(type);		
		this.x = x;
		this.y = y;
		this.bounceVY = (int)(Math.random()*20) + 10;
		this.vy = bounceVY;
		this.vx = (int)(Math.random()*10) - 3;
	}
	
	// pre - the type (value) of the coin
	// post - determines value and image for this object, assigns instance vars accordingly
	private void loadImageAndValue(String type)
	{
		if (type == "gold")
		{
			img = new ImageIcon("Images/coin/goldCoin.png").getImage();
			this.value = GOLD_VALUE;
		}
		else if (type == "silver")
		{
			img = new ImageIcon("Images/coin/silverCoin.png").getImage();
			this.value = SILVER_VALUE;
		}
		else if (type == "bronze")
		{
			img = new ImageIcon("Images/coin/bronzeCoin.png").getImage();
			this.value = BRONZE_VALUE;
		}
		else
		{
			img = new ImageIcon("Images/cube/baseCube.png").getImage();
			this.value = 0;
		}
	}
	
	// pre - none
	// post - returns x coord of coin
	public int getX()
	{return x;}
	
	// pre - none
	// post - returns y coord of coin
	public int getY()
	{return y;}
	
	// pre - none
	// post - returns height of coin
	public int getHeight()	
	{return img.getHeight(null);}
	
	// pre - none
	// post - returns $$ value of coin
	public int getValue()
	{return value;}
	
	// pre - none
	// post - returns image for coin
	public Image getImage()
	{return img;}
	
	// pre - none
	// post - returns the rectangle representing the collision bounds of the coin
	public Rectangle getBounds()
	{
		if (coinLifeCounter < 2)
			return new Rectangle(0,0,0,0);
		return new Rectangle(x,
							w.getHeight() - (w.getGroundHeight() + getHeight() + y),
							img.getWidth(null),
							img.getHeight(null));
	}
		
	// pre - the speed of the ground movement of the world containing this coin
	// post - changes coin position based on velocities and world groundSpeed
	public void move(int groundSpeed)
	{
		coinLifeCounter++;
		y += vy;
		x += vx + groundSpeed;
		
		vy--;
		
		if (y <= 0)
		{
			y = 0;
			bounceVY = (int)(bounceVY / 1.5);	// makes it bounce! the coin goes up slower each bounce
			vy = bounceVY;
			
			if (vx > 0)						// slows the coin down
				vx--;
			else if (vx < 0)
				vx++;
		}
		
		if (bounceVY == 0)
			vx = 0;
	}
}


