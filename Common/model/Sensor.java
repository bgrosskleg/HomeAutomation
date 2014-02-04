package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Sensor extends CanvasObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	
	//Size must be even
	private final int size = 16;
	
	//Member variables will be unique for each object
	private Point2D.Double location;
	private String identification;
	private int RSSI;

	
	/**
	 * creates a new sensor at point p
	 * @param p the new point
	 */
	public Sensor(Point2D.Double p, String ID)
	{
		//Super(unselectedColor, selectedColor)
		super(new Color(200,0,235), Color.RED);	
		setLocation(p);
		identification = ID;
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
		return "Sensor ID: " + identification;
	}

	public Point2D.Double getLocation() {
		return location;
	}

	public void setLocation(Point2D.Double location) {
		this.location = location;
	}

	@Override
	public boolean equals(CanvasObject object) 
	{
		if(location.equals(((Sensor) object).location) && identification.equals(((Sensor) object).identification))
		{
			return true;
		}
		return false;
	}
}
