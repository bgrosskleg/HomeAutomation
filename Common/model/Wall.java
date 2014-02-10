package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Wall extends ModelObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	private static int strokeWidth = 4;
	//private static Image texture = MainController.getApplication().getImage(MainController.getCodebase(), "resources/chalkTexture.png");
	
	//Member variables will be unique for each object
	private Point2D.Double startPoint;
	private Point2D.Double endPoint;
	private Line2D.Double line;
		
	
	//CONSTRUCTOR***********************************************************************
	/**
	 * Creates a wall with a start and an end point
	 * @param start
	 * @param end
	 */
	public Wall(Point2D.Double start, Point2D.Double tempEnd)
	{
		super(Color.BLACK, Color.RED);	
		startPoint = start;
		endPoint = tempEnd;
		line = new Line2D.Double(start, tempEnd);
	}
	
	
	//CONSTRUCTING METHODS**************************************************************
	
	public void finalize()
	{
		endPoint = (Point2D.Double) endPoint.clone();
		setLine(new Line2D.Double(startPoint, endPoint));
	}
	
	
	//MUTATORS AND ACCESSORS*************************************************************
	public Point2D.Double getStartPoint() 
	{
		return startPoint;
	}
	
	public Point2D.Double getEndPoint() 
	{
		return endPoint;
	}

	public Line2D.Double getLine() 
	{
		return line;
	}

	private void setLine(Line2D.Double line) 
	{
		this.line = line;
	}

	
	//INTERFACE METHODS******************************************************************
	
	@Override
	public Wall clone()
	{
		return new Wall((Point2D.Double) startPoint.clone(), (Point2D.Double) endPoint.clone());
	}

	@Override
	public String[] getParameters() 
	{
		return new String [] {"startPoint", "endPoint", "line"};
	}

	@Override
	public Object[] getValues() 
	{
		return new Object [] {startPoint, endPoint, line};
	}
	
	@Override
	public boolean edit(String [] parameters, Object [] values) 
	{
		return false;
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.draw(new Line2D.Double(startPoint, endPoint));
	}
	
	@Override
	public String toString()
	{
		return ("Type: Wall StartPoint: " + startPoint + " EndPoint " + endPoint);
	}


	@Override
	public boolean equals(Object other) 
	{
		if (other == null) 
		{return false;}
		
	    if (other == this) 
	    {return true;}
	    
	    if (!(other instanceof Wall))
	    {return false;}
	    
	    //Class specific comparison
	    Wall wall = (Wall) other;
	    if(this.startPoint.equals(wall.startPoint) && this.endPoint.equals(wall.endPoint))
	    {return true;}
	    else
	    {return false;}    
	}


	
}
