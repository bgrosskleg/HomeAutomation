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

public class Region extends ModelObject
{
	//Static variables will be same for all wall objects
	private static final long serialVersionUID = 1;
	private static int strokeWidth = 4;
		
	private Point2D.Double startPoint;
	private Point2D.Double nextPoint;
	
	private GeneralPath path;
	private boolean finalized;
	
	//Region name
	private String name;
	
	//List of all sensors tied to the region
	private ArrayList<StaticNode> staticNodes;
	
	//List of all users occupying the region
	private ArrayList<User> users;
	
	//Lighting value 0-100%
	private int lightingValue;
	
	
	//CONSTRUCTOR*************************************************************************
	/**
	 * Creates a region with no objects
	 */
	public Region(Point2D.Double start, Point2D.Double next, Color color)
	{		
		super(color, color.darker().darker());	
				
		startPoint = start;
		nextPoint = next;
						
		path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 1);
		path.moveTo(getStartPoint().x, getStartPoint().y);
		
		finalized = false;
		
		//Region features
		staticNodes = new ArrayList<StaticNode>();
		users = new ArrayList<User>();
		lightingValue = 0;
	}
	
	//CONSTRUCTING METHODS*****************************************************************
	
	public void addPointToRegion()
	{
		Point2D.Double temp = (Point2D.Double)nextPoint.clone();
		path.lineTo(temp.x, temp.y);
	}
	
	public void finalize()
	{
		path.closePath();		
		finalized = true;
	}
	
	public void addStaticNode(StaticNode s)
	{
		staticNodes.add(s);
	}
	
	public void removeStaticNode(StaticNode s)
	{
		staticNodes.remove(s);
	}
	
	//XBEE COMMUNICATION*******************************************************************
	
	public void notifyStaticNodes()
	{
		for(StaticNode staticNode : staticNodes)
		{
			//Send lighting command
			//TO BE DONE
			System.out.println("SEND XBEE COMMAND TO:");
			System.out.println("STATIC NODE: " + staticNode.getMACAddress());
			System.out.println("LIGHTING VALUE: " + lightingValue);
		}
	}
	
	//MUTATORS AND ACCESSORS***************************************************************
	
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

	public Point2D.Double getStartPoint() 
	{
		return startPoint;
	}

	public ArrayList<StaticNode> getStaticNodes()
	{
		return staticNodes;
	}
	
	public ArrayList<User> getUsers()
	{
		return users;
	}

	public GeneralPath getPath() 
	{
		return path;
	}
	
	public int getLightingValue() 
	{
		return lightingValue;
	}

	public void setLightingValue(int lightingValue) 
	{
		this.lightingValue = lightingValue;		
	}
	
	
	//INTERFACE METHODS********************************************************************
	
	@SuppressWarnings("unchecked")
	@Override
	public Region clone() 
	{
		Region result = new Region((Point2D.Double)startPoint.clone(), (Point2D.Double)nextPoint.clone(), unselectedColor);
		
		//Copy ints and strings
		result.finalized = this.finalized;
		result.name = this.name;
		result.lightingValue = this.lightingValue;
		
		//Clone objects that are references
		result.path = (GeneralPath) this.path.clone();
		result.staticNodes = (ArrayList<StaticNode>) this.staticNodes.clone();
		
		return result;
	}
	
	@Override
	public String[] getParameters() 
	{
		return new String [] {"startPoint", "nextPoint", "path", "finalized", "name", "staticNodes", "users", "lightingValue"};
	}

	@Override
	public Object[] getValues() 
	{
		return new Object [] {startPoint, nextPoint, path, finalized, name, staticNodes, users, lightingValue};
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean edit(String [] parameters, Object [] values) throws Exception 
	{
		if(parameters.length != values.length)
		{
			throw new Exception("Parameter list not the same length as value list!");
		}
		
		for(int iter = 0; iter < parameters.length; iter++)
		{
			if(parameters[iter].equals("name"))
			{
				if(values[iter] instanceof String)
				{
					name = (String)values[iter];
					return true;
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type String!");
				}
			}
			else if(parameters[iter].equals("staticNodes"))
			{
				if(values[iter] instanceof ArrayList)
				{
					staticNodes = (ArrayList<StaticNode>) values[iter];
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type ArrayList!");
				}
			}
			else if(parameters[iter].equals("users"))
			{
				if(values[iter] instanceof ArrayList)
				{
					users = (ArrayList<User>) values[iter];
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type ArrayList!");
				}
			}
			else if(parameters[iter].equals("lightingValue"))
			{
				if(values[iter] instanceof Integer)
				{
					lightingValue = (Integer) values[iter];
				}
				else
				{
					throw new Exception("Value " + iter + " is not of type integer!");
				}
			}
		}
		return false;
	}	
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
             
        if(!finalized)
        {
        	//Draw region boundary
        	g2.fill(new Ellipse2D.Double((getStartPoint().x-5)-1, (getStartPoint().y-5)-1, 10, 10));
        	g2.draw(path);
        	
        	g2.draw(new Line2D.Double(path.getCurrentPoint().getX(), path.getCurrentPoint().getY(), nextPoint.getX(), nextPoint.getY()));
        }
        else
        {
        	//Set region fill color and transparency
        	g2.fill(path);       		
        }
	}

	@Override
	public boolean equals(Object other) 
	{
		if (other == null) 
		{return false;}
		
	    if (other == this) 
	    {return true;}
	    
	    if (!(other instanceof Region))
	    {return false;}
	    
	    //Class specific comparison
	    Region region = (Region) other;
	    if(this.path.equals(region.path))
	    {return true;}
	    else
	    {return false;}  
	}
}
