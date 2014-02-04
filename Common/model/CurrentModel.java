package model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

public class CurrentModel extends Observable implements Serializable
{
	private static final long serialVersionUID = 1;
				
	//Lists of model objects
	private CanvasObjectList<Wall> walls;
	private CanvasObjectList<Region> regions;
	private CanvasObjectList<Light> lights;
	private CanvasObjectList<Sensor> sensors;
	private CanvasObjectList<User> users;
	
	public CurrentModel()
	{							
		walls = new CanvasObjectList<Wall>();
		regions = new CanvasObjectList<Region>();
		lights = new CanvasObjectList<Light>();
		sensors = new CanvasObjectList<Sensor>();
		users = new CanvasObjectList<User>();	
		
		users.add(0, new User("Brian","ABCD1234ABCD1234", new Point2D.Double(50, 50)));
	}	
	
	public void addCanvasObject(CanvasObject object) 
	{
		if(object instanceof Wall)
		{
			walls.customAdd((Wall)object);
		}
		
		if(object instanceof Region)
		{
			regions.customAdd((Region)object);
		}
		
		if(object instanceof Light)
		{
			lights.customAdd((Light)object);
		}
		
		if(object instanceof Sensor)
		{
			sensors.customAdd((Sensor)object);
		}
		
		currentModelChanged();
	}

	public void removeCanvasObject(CanvasObject object) 
	{
		if(object instanceof Wall)
		{
			System.out.println("Object is a wall");
			if(walls.customContains((Wall) object))
			{
				System.out.println("Wall is in list");
			}
			else
			{
				System.out.println("Wall is NOT in list");
			}
			
		}
		
		if(object instanceof Region)
		{
			System.out.println("Object is a region");
			regions.remove((Region)object);
		}
		
		if(object instanceof Light)
		{
			System.out.println("Object is a light");
			lights.remove((Light)object);
		}
		
		if(object instanceof Sensor)
		{
			System.out.println("Object is a sensor");
			sensors.remove((Sensor)object);
		}
		
		currentModelChanged();
	}	
	
	
	public void currentModelChanged()
	{
		setChanged();
		notifyObservers("model");
	}
	
	public void currentUsersChanged()
	{
		setChanged();
		notifyObservers("users");
	}
	
	//MUTATORS AND ACCESSORS***************************************************************
		
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
	
	public void setUsers(CanvasObjectList<User> users) 
	{
		this.users = users;
		currentUsersChanged();
	}
}
