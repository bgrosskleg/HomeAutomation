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
	
	
	public User(String name, String ID, Point2D.Double p)
	{
		super(Color.BLUE, Color.BLUE);
		this.name = name;
		this.location = p;
		this.MACAddress = ID;
	}
	
	public void setLocation(Point2D.Double p)
	{
		location = p;
	}
	
	public Point2D.Double getLocation()
	{
		return location;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        Ellipse2D.Double light = new Ellipse2D.Double((location.x-size/2)-1, (location.y-size/2)-1, size , size);
        g2.fill(light);
	}
	
	@Override
	public String toString()
	{
		return "User: " +  name;
	}

	public String getMACAddress() {
		return MACAddress;
	}

	public void setMacAddress(String MAC) {
		MACAddress = MAC;
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
	public HouseObject clone() 
	{	
		return new User(name, MACAddress, (Point2D.Double)location.clone());
	}

}
