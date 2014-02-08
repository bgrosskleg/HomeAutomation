package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class User extends HouseObject
{
	private static final long serialVersionUID = 1L;
	
	
	//Size must be even
	private static int size = 16;
		
	//Member variables will be unique for each object
	private Point2D.Double location;	
	private String name;
	private String MACAddress;
	private int preferredLightingValue = 100;
	
	
	//CONSTRUCTOR*********************************************************
	public User(String name, String ID, Color color)
	{
		super(color, color);
		this.name = name;
		this.MACAddress = ID;
		this.location = new Point2D.Double(50,50);
	}
	
	
	//MUTATORS AND ACCESSORS***********************************************
	public void setLocation(Point2D.Double p)
	{
		location = p;
	}
	
	public Point2D.Double getLocation()
	{
		return location;
	}
	
	@Override
	public String toString()
	{
		return "User: " +  name;
	}

	public String getMACAddress() 
	{
		return MACAddress;
	}

	public void setMacAddress(String MAC) 
	{
		MACAddress = MAC;
	}
	
	public int getPreferredLightingValue() 
	{
		return preferredLightingValue;
	}

	public void setPreferredLightingValue(int preferredLightingValue) 
	{
		this.preferredLightingValue = preferredLightingValue;
	}
	
	
	
	//INTERFACE METHODS***********************************************************
	
	@Override
	public HouseObject clone() 
	{	
		return new User(name, MACAddress, unselectedColor);
	}
	
	@Override
	public boolean equals(HouseObject object)
	{
		if(object instanceof User)
		{
			if(location.equals(((User) object).location) && MACAddress.equals(((User) object).MACAddress)
				&& name.equals(((User) object).name))
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
					throw new Exception("Object " + iter + "is not of type Point2D.Double!");
				}
			}
		}
		throw new Exception("No parameters editted!");
	}
	
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getUnselectedColor());
        Ellipse2D.Double user = new Ellipse2D.Double((location.x-size/2)-1, (location.y-size/2)-1, size , size);
        g2.fill(user);
	}
}
