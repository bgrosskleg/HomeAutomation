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
	public double x;
	
	/**
	 * Y location
	 */
	public double y;

	
	public Location()
	{
	}
	
	public Location(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Location(Point2D.Double location)
	{
		x = (double) location.x;
		y = (double) location.y;
	}
}
