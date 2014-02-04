package model;

import java.io.Serializable;
import java.util.ArrayList;

public class CurrentModel implements Serializable
{
	private static final long serialVersionUID = 1;
	
	

	
	//Lists of model objects
	
	private ArrayList<Wall> walls;
	private ArrayList<Region> regions;
	private ArrayList<Light> lights;
	private ArrayList<Sensor> sensors;
	private ArrayList<User> users;
	
		
	
	//ACCESSORS***************************************************************
	
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