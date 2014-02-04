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
		
		users.add(0, new User("Brian","ABCD1234ABCD1234", new Point2D.Double(50, 50)));
	}
	

	public boolean equals(CurrentModel B) 
	{
		if( this.walls.equals(B.walls) && 
				this.regions.equals(B.regions) &&
				this.lights.equals(B.lights) && 
				this.sensors.equals(B.sensors))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public void addCanvasObject(CanvasObject object) 
	{
		if(object instanceof Wall)
		{
			walls.add((Wall)object);
			System.out.println("Object is a wall.");
		}
		
		if(object instanceof Region)
		{
			regions.add((Region)object);
			System.out.println("Object is a region.");
		}
		
		if(object instanceof Light)
		{
			lights.add((Light)object);
			System.out.println("Object is a light.");
		}
		
		if(object instanceof Sensor)
		{
			sensors.add((Sensor)object);
			System.out.println("Object is a sensor.");
		}
		
		
		System.out.println("Gets into addCanvasObject");
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
		
		currentModelChanged();
	}	
	
	
	public void modifyCanvasObject(CanvasObject object) 
	{
		// TODO Auto-generated method stub
		currentModelChanged();
	}
	
	public void currentModelChanged()
	{
		setChanged();
		notifyObservers();
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
}
