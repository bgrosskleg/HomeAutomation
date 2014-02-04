package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Region extends CanvasObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	private static int strokeWidth = 4;
		
	private Point2D.Double startPoint;
	private Point2D.Double nextPoint;
	
	private GeneralPath region;
	private boolean finalized = false;
	
	//Region name
	private String name;
	
	//List of all sensors tied to the region
	public ArrayList<Sensor> sensors;
	
	//Lighting value 0-255
	public int lightingValue;
	
	
	/**
	 * Creates a region with no objects
	 */
	public Region(Point2D.Double start, Point2D.Double next)
	{		
		super(null, null);	
		
		Color color = randomColor();
		this.unselectedColor = color;
		this.selectedColor = color.darker().darker();
		this.currentColor = unselectedColor;
		
		startPoint = start;
		nextPoint = next;
		
		sensors = new ArrayList<Sensor>();
		lightingValue = 0;
		
		//Add last point, so points is not null, that is updated on mouse move
		//points.add(getStartPoint());
		
		region = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 1);
		region.moveTo(getStartPoint().x, getStartPoint().y);
	}
	
	private static Color randomColor()
	{
		Random rand = new Random();
		
		/*
		//Make a random pastel color
		final float hue = rand.nextFloat();
		// Saturation between 0.1 and 0.3
		final float saturation = (rand.nextInt(2000) + 1000) / 10000f;
		final float luminance = 0.9f;
		final Color pastelColor = Color.getHSBColor(hue, saturation, luminance);
		
		//Create transparent color, red, green, blue, alpha, 0-255
		int alpha = 100;
		Color color = new Color(pastelColor.getRed(), pastelColor.getGreen(), pastelColor.getBlue(), alpha);
		*/
		
		//Makes lighter colors (125 + random number 0-125)
		final int r = rand.nextInt(126)+125;
		final int g = rand.nextInt(126)+125;
		final int b = rand.nextInt(126)+125;
		
		//Alpha 0-255 (transparent -> solid)
		final int alpha = 200;
		
		final Color color = new Color(r,g,b,alpha);
		
		return color;
	}
	
	public void addPointToRegion()
	{
		Point2D.Double temp = (Point2D.Double)nextPoint.clone();
		getRegion().lineTo(temp.x, temp.y);
	}
	
	public void finalize()
	{
		getRegion().closePath();		
		finalized = true;
	}
	
	public void addSensor(Sensor s)
	{
		sensors.add(s);
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
             
        if(!finalized)
        {
        	//Draw region boundary
        	g2.setColor(currentColor);
        	g2.fill(new Ellipse2D.Double((getStartPoint().x-5)-1, (getStartPoint().y-5)-1, 10, 10));
        	g2.draw(getRegion());
        	
        	g2.draw(new Line2D.Double(region.getCurrentPoint().getX(), region.getCurrentPoint().getY(), nextPoint.getX(), nextPoint.getY()));
        }
        else
        {
        	//Set region fill color and transparency
        	g2.setColor(currentColor);
        	g2.fill(region);       		
        }
        
	}
	
	public boolean isFinalized()
	{
		return finalized;
	}
	
	@Override
	public String toString()
	{
		return "Region: " + name;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public Point2D.Double getStartPoint() {
		return startPoint;
	}


	public GeneralPath getRegion() {
		return region;
	}	
}
