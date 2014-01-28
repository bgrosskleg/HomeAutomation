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
	
	private static EventListenerList subsscriberList = new EventListenerList();
	
	//private boolean configured;
	public final int gridSize = 25;
	public final int width = 800;
	public final int height = 800;
	
	//Lists of model objects
	private ArrayList<Point> points;
	private ArrayList<CanvasObject> objects;
	private User user;
	
	public CurrentModel()
	{			
		objects = new ArrayList<CanvasObject>();
		points = new ArrayList<Point>();
		createGrid();		
		user = new User("Brian","ABCD1234ABCD1234", new Point2D.Double(50, 50));
	}
	
	public void createGrid()
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
		if( this.objects.equals(B.objects))
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
		subsscriberList.add(CurrentModelSubscriber.class, subscriber);
	}
	
	public void removeCurrentModelSubscriber(CurrentModelSubscriber subscriber)
	{
		subsscriberList.remove(CurrentModelSubscriber.class, subscriber);
	}
	
	public void currentModelChanged()
	{	

		//Notify local subscribers
		Object[] subscribers = subsscriberList.getListenerList();
	    for (int i = 0; i < subscribers.length; i = i+2) {
	      if (subscribers[i] == CurrentModelSubscriber.class) {
	        ((CurrentModelSubscriber) subscribers[i+1]).currentModelChanged();
	      }
	    }
	}
	
	public void addCanvasObject(CanvasObject object) {
		objects.add(object);
		currentModelChanged();
	}

	public void removeCanvasObject(CanvasObject object) {
		objects.remove(object);
		currentModelChanged();
	}
	
	public ArrayList<CanvasObject> getObjects()
	{
		return objects;
	}
	
	public ArrayList<Point> getPoints()
	{
		return points;
	}
	
	public User getUser()
	{
		return user;
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
