package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Light extends ModelObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	//Size must be even
	private static int size = 16;
	
	//Member variables will be unique for each object
	/**
	 * Location of the light in x and y pixel coordinates
	 */
	private Point2D.Double location;	
	
	//CONSTRUCTOR**********************************************************************
	
	/**
	 * Creates new light at position p
	 * @param p position of new light
	 */
	public Light(Point2D.Double p)
	{
		super(Color.YELLOW, Color.RED);	
		location = p;
	}


	//INTERFACE METHODS****************************************************************	
	/**
	 * Equals is used in the ArrayList contains function
	 * By implementing equals for each type of object it is possible to call
	 * ArrayList<ModelObject>.containts(User, Region etc) and it will work
	 */
	@Override
	public boolean equals(Object other) 
	{
		if (other == null) 
		{return false;}
		
	    if (other == this) 
	    {return true;}
	    
	    if (!(other instanceof Light))
	    {return false;}
	    
	    //Class specific comparison
	    Light light = (Light) other;
	    if(this.location.equals(light.location))
	    {return true;}
	    else
	    {return false;}    
	}


	@Override
	public String toString() 
	{
		return ("Type: Light Location: " + location);
	}
	
	@Override
	public String[] getParameters() 
	{
		return new String [] {"location"};
	}


	@Override
	public Object[] getValues() 
	{
		return new Object [] {location};
	}
	
	@Override
	public boolean edit(String [] parameters, Object [] values) throws Exception 
	{
		//Lights are unedittable
		return false;
	}
	
	@Override
	public void paintComponent(Graphics g) 
	{
		//Paint circle at location
		Graphics2D g2 = (Graphics2D) g;
        Ellipse2D.Double light = new Ellipse2D.Double((location.x-size/2)-1, (location.y-size/2)-1, size , size);
        g2.fill(light);
	}
	
	//MUTATORS AND ACCESSORS***********************************************************
	
	public Point2D.Double getLocation()
	{
		return location;
	}


	

}
