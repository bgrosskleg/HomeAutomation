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
	private int RSSI;

	
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
	
	public String toString()
	{
		return "Sensor ID: " + MACAddress;
	}

	public Point2D.Double getLocation() {
		return location;
	}

	public void setLocation(Point2D.Double location) {
		this.location = location;
	}

	@Override
	public boolean equals(HouseObject object) 
	{
		if(object instanceof Sensor)
		{
			Sensor temp = (Sensor) object;
			if(this.location.equals(temp.location))
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
		Sensor result = new Sensor(MACAddress, (Point2D.Double)location.clone());
		result.RSSI = this.RSSI;
		return result;
	}
}
