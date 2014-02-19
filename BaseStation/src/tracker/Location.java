package tracker;

/**
 * Represents a location in 2 dimensional space with floors being a sort of 3rd dimension.
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
	
	/**
	 * Floor
	 */
	public int floor;
	
	public Location()
	{
	}
	
	public Location(float x, float y, int floor)
	{
		this.x = x;
		this.y = y;
		this.floor = floor;
	}
}
