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
	private static int size = 16;
	
	//Member variables will be unique for each object
	public Point2D.Double location;
	public String identification;
	public int RSSI;

	
	
	/**
	 * creates a new sensor at point p
	 * @param p the new point
	 */
	public Sensor(Point2D.Double p, String ID)
	{
		//Super(unselectedColor, selectedColor)
		super(new Color(200,0,235), Color.RED);	
		location = p;
		identification = ID;
		RSSI = 75;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setColor(currentColor);
        
        //Draw sensor
        Ellipse2D.Double sensor = new Ellipse2D.Double(location.x-size/2, location.y-size/2, size , size);
        g2.fill(sensor);
        
        //Draw RSSI radius
        Ellipse2D.Double RSSICircle = new Ellipse2D.Double((location.x-RSSI), (location.y-RSSI), RSSI*2 , RSSI*2);
        g2.draw(RSSICircle);
	}	
	
	public String toString()
	{
		return "Sensor ID: " + identification;
	}
}
