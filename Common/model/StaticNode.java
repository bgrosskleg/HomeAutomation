package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class StaticNode extends ModelObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	
	//Size must be even
	private final int size = 16;
	
	//Member variables will be unique for each object
	private Point2D.Double location;
	private String MACAddress;
	private int lightingValue;
	private int radius;
	private Region pairedRegion;

	
	//CONSTRUCTOR*****************************************************************
	/**
	 * creates a new sensor at point p
	 * @param p the new point
	 */
	public StaticNode(String ID, Region region, Point2D.Double p)
	{
		//Super(unselectedColor, selectedColor)
		super(new Color(200,0,235), Color.RED);	
		location = p;
		MACAddress = ID;
		radius = 75;
		setPairedRegion(region);
	}

	//MUTATORS AND ACCESSORS*******************************************************
	
	public String toString()
	{
		return "MAC Address: " + MACAddress;
	}
	
	public String getMACAddress()
	{
		return MACAddress;
	}
	
	public void setMACAddress(String address)
	{
		MACAddress = address;
	}

	public Point2D.Double getLocation() 
	{
		return location;
	}

	public void setLocation(Point2D.Double location) 
	{
		this.location = location;
	}
	
	public int getLightingValue() 
	{
		return lightingValue;
	}

	public void setLightingValue(int lightingValue) 
	{
		this.lightingValue = lightingValue;
	}
	
	public Region getPairedRegion() 
	{
		return pairedRegion;
	}

	public void setPairedRegion(Region pairedRegion) 
	{
		this.pairedRegion = pairedRegion;
	}
	
	
	//INTERFACE METHODS************************************************************
	
	
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
	    
	    if (!(other instanceof StaticNode))
	    {return false;}
	    
	    //Class specific comparison
	    StaticNode sensor = (StaticNode) other;
	    if(this.location.equals(sensor.location))
	    {return true;}
	    else
	    {return false;}    
	}
	
	@Override
	public String[] getParameters() 
	{
		return new String [] {"location", "MACAddress", "lightingValue", "radius", "pairedRegion"};
	}

	@Override
	public Object[] getValues() 
	{
		return new Object [] {location, MACAddress, lightingValue, radius, pairedRegion};
	}
	
	@Override
	public boolean edit(String [] parameters, Object [] values) throws Exception 
	{
		boolean objectEditted = false;
		
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
					if(!location.equals((Point2D.Double) values[iter]))
					{
						location = (Point2D.Double)values[iter];
						objectEditted = true;
					}
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type Point2D.Double!");
				}
			}
			else if(parameters[iter].equals("MACAddress"))
			{
				if(values[iter] instanceof String)
				{
					if(!MACAddress.equals((String) values[iter]))
					{
						MACAddress = (String) values[iter];
						objectEditted =  true;
					}
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type String!");
				}
			}
			else if(parameters[iter].equals("lightingValue"))
			{
				if(values[iter] instanceof Integer)
				{
					if(lightingValue != (Integer) values[iter])
					{
						lightingValue = (Integer) values[iter];
						objectEditted =  true;
					}
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type Integer!");
				}
			}
			else if(parameters[iter].equals("pairedRegion"))
			{
				if(values[iter] instanceof Region || values[iter] == null)
				{
					if(pairedRegion == null || !pairedRegion.equals((Region) values[iter]))
					{
						pairedRegion = (Region) values[iter];
						objectEditted = true;
					}
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type Region!");
				}
			}
			
		}
		return objectEditted;
	}
	
	/**
	 * Paints staticNode as dot and ring
	 */
	@Override
	public void paintComponent(Graphics g) 
	{
		//Paint circle at location
		Graphics2D g2 = (Graphics2D) g;
        
        //Draw sensor
        Ellipse2D.Double sensor = new Ellipse2D.Double(getLocation().x-size/2, getLocation().y-size/2, size , size);
        g2.fill(sensor);
        
        //Draw RSSI radius
        Ellipse2D.Double RSSICircle = new Ellipse2D.Double((getLocation().x-radius), (getLocation().y-radius), radius*2 , radius*2);
        g2.setStroke(new BasicStroke(4));
        g2.draw(RSSICircle);
	}
}
