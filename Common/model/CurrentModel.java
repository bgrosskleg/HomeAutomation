package model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

public class CurrentModel extends Observable implements Serializable
{
	private static final long serialVersionUID = 1;
				
	//Lists of model objects
	private ArrayList<Wall> walls;
	private ArrayList<Region> regions;
	private ArrayList<Light> lights;
	private ArrayList<Sensor> sensors;
	private ArrayList<User> users;
	
	public CurrentModel()
	{							
		walls = new ArrayList<Wall>();
		regions = new ArrayList<Region>();
		lights = new ArrayList<Light>();
		sensors = new ArrayList<Sensor>();
		users = new ArrayList<User>();	
		
		users.add(0, new User("Brian","ABCD1234ABCD1234", new Point2D.Double(25/2, 25/2)));
	}	
	
	public void addCanvasObject(CanvasObject object) 
	{
		//Adding objects, doesn't matter that references are broken on transmission
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
		
		currentModelChanged();
	}

	public void removeCanvasObject(CanvasObject object) 
	{
		//References are broken when transmitted in stream, must compare important features using equals() rather than referring to object	directly	
		if(object instanceof Wall)
		{
			for(Wall wall : walls)
			{
				if(wall.equals((Wall)object))
				{
					walls.remove(wall);
					currentModelChanged();
					return;
				}
			}
		}
		
		if(object instanceof Region)
		{
			for(Region region : regions)
			{
				if(region.equals((Region)object))
				{
					regions.remove(region);
					currentModelChanged();
					return;
				}
			}
		}
		
		if(object instanceof Light)
		{
			for(Light light : lights)
			{
				if(light.equals((Light)object))
				{
					lights.remove(light);
					currentModelChanged();
					return;
				}
			}
		}
		
		if(object instanceof Sensor)
		{
			for(Sensor sensor : sensors)
			{
				if(sensor.equals((Sensor)object))
				{
					sensors.remove(sensor);
					currentModelChanged();
					return;
				}
			}
		}
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
	
	public void setUsers(ArrayList<User> users) 
	{
		this.users = users;
		currentUsersChanged();
	}
}
