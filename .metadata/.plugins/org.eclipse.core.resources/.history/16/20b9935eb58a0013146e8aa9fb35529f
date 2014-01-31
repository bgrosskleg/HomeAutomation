package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Region extends CanvasObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	private static int strokeWidth = 4;
		
	public GeneralPath region;
	public String name;
	private ArrayList<Point2D.Double> points;
	public Point2D.Double startPoint;
	private boolean finalized;
	
	public ArrayList<Sensor> sensors;
	
	
	/**
	 * Creates a region with no objects
	 */
	public Region(Point2D.Double startPoint)
	{		
		super(null, null);	
		
		Color color = randomColor();
		this.unselectedColor = color;
		this.selectedColor = color.darker().darker();
		this.currentColor = unselectedColor;
		
		sensors = new ArrayList<Sensor>();
		this.startPoint = startPoint;
		points = new ArrayList<Point2D.Double>();
		name = null;
		finalized = false;
		
		//Add last point, so points is not null, that is updated on mouse move
		points.add(startPoint);
		
		region = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
		region.moveTo(startPoint.x, startPoint.y);
		
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
	
	public void addPointToRegion(Point2D.Double point)
	{
		points.add(point);
		region.lineTo(point.x, point.y);
	}
	
	public void setLastPoint(Point2D.Double point)
	{
		//If new point is not the current last point
		if(!region.getCurrentPoint().equals(point))
		{			
			points.set(points.size()-1, point);
			//Remake region with new last point
			region.reset();
			region.moveTo(startPoint.x, startPoint.y);
			for (int index = 1; index < points.size(); index++) 
			{
				region.lineTo(points.get(index).x, points.get(index).y);
			}
			//Append new line
			region.lineTo(point.x, point.y);
		}
	}
	
	public void finalize()
	{
		region.closePath();		
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
        //Create start point indicator
        if(!points.isEmpty())
        {
        	if(!finalized)
        	{
        		g2.setColor(currentColor);
        		g2.fill(new Ellipse2D.Double((startPoint.x-5)-1, (startPoint.y-5)-1, 10, 10));
        		g2.draw(region);
        	}
        	else
        	{
        		//Order is important for layering
        		//Set region fill color and transparency
        		g2.setColor(currentColor);
        		g2.fill(region);       		
        	}
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
	
}
