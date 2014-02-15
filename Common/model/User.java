package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class User extends ModelObject
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
	public User(String name, String ID, int preferredLightingValue, Color color)
	{
		super(color, color);
		this.name = name;
		this.MACAddress = ID;
		this.preferredLightingValue = preferredLightingValue;
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
	
	public String getName()
	{
		return name;
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
	public String toString()
	{
		return ("Type: User Name: " + name + " MAC Address: " + MACAddress + " Preferred Lighting Value: " + preferredLightingValue + " Location: " + location + " Color: " + unselectedColor);
	}
	
	@Override
	public String[] getParameters() 
	{
		return new String [] {"location", "name", "MACAddress", "preferredLightingValue", "color"};
	}

	@Override
	public Object[] getValues() 
	{
		return new Object [] {location, name, MACAddress, preferredLightingValue, unselectedColor};
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
					if(!location.equals((Point2D.Double)values[iter]))
					{
						location = (Point2D.Double)values[iter];
						objectEditted = true;
					}
				}
				else
				{
					throw new Exception("Object " + iter + "is not of type Point2D.Double!");
				}
			}
			else if(parameters[iter].equals("name"))
			{
				if(values[iter] instanceof String)
				{
					if(!name.equals((String)values[iter]))
					{
						name = (String)values[iter];
						objectEditted = true;
					}
				}
				else
				{
					throw new Exception("Object " + iter + "is not of type String!");
				}
			}
			else if(parameters[iter].equals("MACAddress"))
			{
				if(values[iter] instanceof String)
				{
					if(!MACAddress.equals((String)values[iter]))
					{
						MACAddress = (String)values[iter];
						objectEditted = true;
					}
				}
				else
				{
					throw new Exception("Object " + iter + "is not of type String!");
				}
			}
			else if(parameters[iter].equals("preferredLightingValue"))
			{
				if(values[iter] instanceof Integer)
				{
					if(preferredLightingValue != ((Integer)values[iter]))
					{
						preferredLightingValue = (Integer)values[iter];
						objectEditted = true;
					}
				}
				else
				{
					throw new Exception("Object " + iter + "is not of type String!");
				}
			}
			else if(parameters[iter].equals("color"))
			{
				if(values[iter] instanceof Color)
				{
					if(!unselectedColor.equals((Color)values[iter]))
					{
						unselectedColor = (Color)values[iter];
						selectedColor = (Color)values[iter];
						objectEditted = true;
					}
				}
				else
				{
					throw new Exception("Object " + iter + "is not of type Color!");
				}
			}
		}
		
		return objectEditted;
	}
	
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getUnselectedColor());
        Ellipse2D.Double user = new Ellipse2D.Double((location.x-size/2)-1, (location.y-size/2)-1, size , size);
        g2.fill(user);
	}


	@Override
	public boolean equals(Object other) 
	{
		if (other == null) 
		{return false;}
		
	    if (other == this) 
	    {return true;}
	    
	    if (!(other instanceof User))
	    {return false;}
	    
	    //Class specific comparison
	    User user = (User) other;
	    if(this.MACAddress.equals(user.MACAddress))
	    {return true;}
	    else
	    {return false;}    
	}
}
