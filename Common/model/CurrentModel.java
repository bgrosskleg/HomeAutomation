package model;

import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

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
	public ArrayList<Point> points;
	public ArrayList<Wall> walls;
	public ArrayList<Region> regions;
	public ArrayList<Light> lights;
	public ArrayList<Sensor> sensors;
	public ArrayList<User> users;
	
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
	
	
	/*BE SURE TO CALL THE MODEL CHANGED FUNCTION WHEN MODIFYING THE MODEL ie
	 * public void setTestColor(Color color)
		{
			testColor = color;
			currentModelChanged();
		}
	 */
	
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

		//Notify local subscribers
		Object[] subscribers = subscriberList.getListenerList();
	    for (int i = 0; i < subscribers.length; i = i+2) {
	      if (subscribers[i] == CurrentModelSubscriber.class) {
	        ((CurrentModelSubscriber) subscribers[i+1]).currentModelChanged();
	      }
	    }
	}
	
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
		
	
	public byte[] serialize()
	  {		 		  
		  byte[] data = null;
		  
	      try {	
	    	  //http://www.easywayserver.com/blog/save-serializable-object-in-java/
	    	  ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	  ObjectOutputStream oos;

	    	  oos = new ObjectOutputStream(bos);

	    	  oos.writeObject(this);
	    	  oos.flush();
	    	  oos.close();
	    	  bos.close();

	    	 data = bos.toByteArray();
	        
	      } catch (IOException e) {
	    	  	JOptionPane.showMessageDialog(null,"Failure generating house model!");
				System.out.println("Failure generating house model!");
				e.printStackTrace();
	      }
	      
	      return data;
	  }
}
