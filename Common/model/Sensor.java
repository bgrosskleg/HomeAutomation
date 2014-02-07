package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Sensor extends HouseObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	
	//Size must be even
	private final int size = 16;
	
	//Member variables will be unique for each object
	private Point2D.Double location;
	private String MACAddress;
	private int lightingValue;
	private int RSSI;

	
	//CONSTRUCTOR*****************************************************************
	/**
	 * creates a new sensor at point p
	 * @param p the new point
	 */
	public Sensor(String ID, Point2D.Double p)
	{
		//Super(unselectedColor, selectedColor)
		super(new Color(200,0,235), Color.RED);	
		location = p;
		MACAddress = ID;
		RSSI = 75;
	}

	//MUTATORS AND ACCESSORS*******************************************************
	
	public String toString()
	{
		return "Sensor ID: " + MACAddress;
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
	
	
	//INTERFACE METHODS************************************************************
	
	@Override
	public HouseObject clone() 
	{
		Sensor result = new Sensor(MACAddress, (Point2D.Double)location.clone());
		result.RSSI = this.RSSI;
		return result;
	}
	
	@Override
	public boolean equals(HouseObject object) 
	{
		if(object instanceof Sensor)
		{
			Sensor temp = (Sensor) object;
			if(this.location.equals(temp.location) && this.MACAddress.equals(temp.MACAddress))
			{
				return true;
			}
			return false;
		}
		return false;
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
					throw new Exception("Value " + iter + " is not of type Point2D.Double!");
				}
			}
			else if(parameters[iter].equals("MACAddress"))
			{
				if(values[iter] instanceof String)
				{
					MACAddress = (String) values[iter];
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type String!");
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
        
        //Draw sensor
        Ellipse2D.Double sensor = new Ellipse2D.Double(getLocation().x-size/2, getLocation().y-size/2, size , size);
        g2.fill(sensor);
        
        //Draw RSSI radius
        Ellipse2D.Double RSSICircle = new Ellipse2D.Double((getLocation().x-RSSI), (getLocation().y-RSSI), RSSI*2 , RSSI*2);
        g2.draw(RSSICircle);
	}	
}
