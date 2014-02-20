package tracker;

import java.awt.geom.Point2D;

/**
 * Represents a location in 2 dimensional space.
 */
public class Location 
{
	/**
	 * X location
	 */
	public float x;
	
	/**
	 * Y location
	 */
	public float y;

	
	public Location()
	{
	}
	
	public Location(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Location(Point2D.Double location)
	{
		x = (float) location.x;
		y = (float) location.y;
	}
}
