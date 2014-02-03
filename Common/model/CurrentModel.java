package model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import controller.CommunicationThread;
import subscribers.CurrentModelSubscriber;

public class CurrentModel implements Serializable
{
	private static final long serialVersionUID = 1;
	
	private static EventListenerList subscriberList = new EventListenerList();
	
	//private boolean configured;
	public final int gridSize = 25;
	public final int width = 800;
	public final int height = 800;
	
	//Lists of model objects
	private ArrayList<Point> points;
	private ArrayList<Wall> walls;
	private ArrayList<Region> regions;
	private ArrayList<Light> lights;
	private ArrayList<Sensor> sensors;
	private ArrayList<User> users;
	
	public CurrentModel()
	{		
		points = new ArrayList<Point>();
		createGrid();
		
		walls = new ArrayList<Wall>();
		regions = new ArrayList<Region>();
		lights = new ArrayList<Light>();
		sensors = new ArrayList<Sensor>();
		
		users = new ArrayList<User>();	
		users.add(0, new User("Brian","ABCD1234ABCD1234", new Point2D.Double(50, 50)));
	}
	
	private void createGrid()
	{
		for (int i = gridSize/2; i < width; i = i + gridSize) 
		{
			for (int j = gridSize/2; j < height; j = j + gridSize)
			{
				points.add(new Point(new Point2D.Double(i,j)));
			}
		}
	}
	

	public boolean equals(CurrentModel B) 
	{
		if( this.walls.equals(B.walls) && 
				this.regions.equals(B.regions) &&
				this.lights.equals(B.lights) && 
				this.sensors.equals(B.sensors) && 
				this.users.equals(B.users))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/*BE SURE TO CALL THE MODEL CHANGED FUNCTION WHEN MODIFYING THE MODEL ie
	 * public void setTestColor(Color color)
		{
			testColor = color;
			currentModelChanged();
		}
	 */
	public void addCanvasObject(CanvasObject object) 
	{
		if(object instanceof Wall)
		{
			walls.add((Wall)object);
		}
		
		if(object instanceof Region)
		{
			regions.add((Region)object);
		}
		
		if(object instanceof Light)
		{
			lights.add((Light)object);
		}
		
		if(object instanceof Sensor)
		{
			sensors.add((Sensor)object);
		}
		
		//Notify subscribers
		currentModelChanged();
	}

	public void removeCanvasObject(CanvasObject object) 
	{
		if(object instanceof Wall)
		{
			walls.remove((Wall)object);
		}
		
		if(object instanceof Region)
		{
			regions.remove((Region)object);
		}
		
		if(object instanceof Light)
		{
			lights.remove((Light)object);
		}
		
		if(object instanceof Sensor)
		{
			sensors.remove((Sensor)object);
		}
		
		//Notify subscribers
		currentModelChanged();
	}	
	
	
	
	//SUBSCRIBERS***************************************************************
	
	public void addCurrentModelSubscriber(CurrentModelSubscriber subscriber)
	{
		subscriberList.add(CurrentModelSubscriber.class, subscriber);
	}
	
	public void removeCurrentModelSubscriber(CurrentModelSubscriber subscriber)
	{
		subscriberList.remove(CurrentModelSubscriber.class, subscriber);
	}
	
	public void currentModelChanged()
	{	
		//Submit model to Pi, if streams are up and running
		if(CommunicationThread.getActiveStreamIn() != null && CommunicationThread.getActiveStreamOut() != null
				&& CommunicationThread.getObjectStreamIn() != null && CommunicationThread.getObjectStreamOut() != null
				&& CommunicationThread.isConnected())
		{CommunicationThread.sendModel();}
		
		//Notify local subscribers
		Object[] subscribers = subscriberList.getListenerList();
	    for (int i = 0; i < subscribers.length; i = i+2) 
	    {
	    	if (subscribers[i] == CurrentModelSubscriber.class) 
	    	{
	    		((CurrentModelSubscriber) subscribers[i+1]).currentModelChanged();
	    	}
	    }
	}
	
	
	//ACCESSORS***************************************************************
	
	public ArrayList<Point> getPoints()
	{
		return points;
	}
	
	public ArrayList<Wall> getWalls()
	{
		return walls;
	}
	
	public ArrayList<Region> getRegions()
	{
		return regions;
	}
	
	public ArrayList<Light> getLights()
	{
		return lights;
	}
	
	public ArrayList<Sensor> getSensors()
	{
		return sensors;
	}
	
	public ArrayList<User> getUsers()
	{
		return users;
	}
}
