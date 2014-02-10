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
	private Point2D.Double location;	
	
	//CONSTRUCTOR**********************************************************************
	
	public Light(Point2D.Double p)
	{
		super(Color.YELLOW, Color.RED);	
		location = p;
	}


	//INTERFACE METHODS****************************************************************	
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
		if(parameters.length != values.length)
		{
			throw new Exception("Parameter list not the same length as value list!");
		}
		
		for(int iter = 0; iter < parameters.length; iter++)
		{
			if(parameters[iter].equals("location"))
			{
				if(values[iter] instanceof Point2D.Double)
				{
					location = (Point2D.Double)values[iter];
					return true;
				}
				else
				{
					throw new Exception("Object " + iter + "is not of type Point2D.Double!");
				}
			}
		}
		throw new Exception("No parameters editted!");
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
