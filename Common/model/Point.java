package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Point extends HouseObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	//Size must be even
	private static int size = 4;
		
	//Member variables will be unique for each object
	public Point2D.Double location;
	public int weight;
	
	
	//CONSTRUCTOR*********************************************************************
	/**
	 * Create a point on the Grid with an x and y value
	 * @param x the x axis value
	 * @param y the y axis value
	 */
	public Point(Point2D.Double p)
	{
		super(Color.white,Color.white);
		location = p;
		weight = 0;
	}
	
	
	//INTERFACE METHODS***************************************************************
	
	@Override
	public HouseObject clone() 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(HouseObject object) 
	{
		if(location.equals(((Point) object).location))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean edit(String [] parameters, Object [] values)
	{
		return false;
		// TODO Auto-generated method stub
		
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
        Ellipse2D.Double point = new Ellipse2D.Double((location.x-size/2)-1, (location.y-size/2)-1, size , size);
        g2.fill(point);
	}
}
