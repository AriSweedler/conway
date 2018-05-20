// FixedImage - a class representing all fixed images (images that have fixed positions on the ground of the World
// each FixedImage has its own corresponding picture passed into the constructor
// current FixedImages in the World: shop, crowd

import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;


public class FixedImage
{
	private int x;
	private World w;
	private Image img;
	
	// pre - x position, world that contains this object, image path for the image for this object
	// post - constructs a FixedImage
	public FixedImage(int xPosition, World world, String imagePath)
	{
		w = world;
		x = xPosition;
		img = new ImageIcon(imagePath).getImage();
	}
	
	// pre - the speed of the ground of the world containing this object
	// post - moves this object to remain in the same position relative to the ground
	public void move(int groundSpeed)
	{
		x += groundSpeed;
	}
	
	// pre - none
	// post - returns the Rectangle representing the collision coordinates of this object
	public Rectangle getBounds()
	{
		Rectangle bounds = new Rectangle(this.x, 
				w.getHeight() - (World.GROUND.getHeight(null) + img.getHeight(null)),
				img.getWidth(null),
				img.getHeight(null));
		return bounds;
	}
	
	// pre - none
	// post - returns this objects image
	public Image getImage()
	{
		return img;
	}
	
	// pre - none
	// post - returns the x coord of this object
	public int getX()
	{
		return x;
	}
	
	// pre - none
	// post - returns width of image as int
	public int getWidth()
	{
		return img.getWidth(null);
	}
}
