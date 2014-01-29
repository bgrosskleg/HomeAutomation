package controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import subscribers.TempObjectSubscriber;
import view.Canvas;
import model.CanvasObject;
import model.Light;
import model.Region;
import model.Sensor;
import model.Wall;

public class CanvasMouseAdapter extends MouseInputAdapter implements TempObjectSubscriber
{	
	private static EventListenerList subscriberList = new EventListenerList();

	private static Wall tempWall;
	private static Region tempRegion;
	//private static ArrayList<Point2D.Double> tempPoints;
	private static boolean currentlyBuildingRegion;
	private ArrayList<CanvasObject> selected;

	public CanvasMouseAdapter()
	{
		super();
		setCurrentlyBuildingRegion(false);
		selected = new ArrayList<CanvasObject>();
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		switch (ClientController.getCurrentTool())
		{
		case "Regions":		//draw region	
			if(getTempRegion() == null || !isCurrentlyBuildingRegion())
			{
				//Create region
				setTempRegion(new Region(Canvas.getCursorPoint()));
				setCurrentlyBuildingRegion(true);
			}
			else if(Canvas.getCursorPoint().equals(getTempRegion().startPoint))
			{
				//Complete region
				getTempRegion().addPointToRegion(Canvas.getCursorPoint());
				getTempRegion().finalize();

				String name = JOptionPane.showInputDialog(null, "Please enter the region name:", "Add Region", JOptionPane.INFORMATION_MESSAGE);
				if(name != null && name.length() > 0)
				{				
					getTempRegion().name = name;				
					
					ClientController.getCM().addCanvasObject(getTempRegion());
					ClientController.sendModel();
				}

				setTempRegion(null);
				setCurrentlyBuildingRegion(false);
			}
			else if(isCurrentlyBuildingRegion())
			{
				//Append point to region
				getTempRegion().addPointToRegion(Canvas.getCursorPoint());
			}
			else
			{
				System.out.println("Impossible case!");
			}

			break;

		case "Lights":		//add light
		
			ClientController.getCM().addCanvasObject(new Light(Canvas.getCursorPoint()));	
			ClientController.sendModel();
			break;

		case "Sensors":		//add sensor
			//Specify which region this sensor controls
			ArrayList<String> possibilities = new ArrayList<String>();
			for(Region region : ClientController.getCM().regions)
			{
				possibilities.add(region.name);
			}
			
			if(possibilities.isEmpty())
			{
				JOptionPane.showMessageDialog(null, "Please create a region for this sensor to control first!", "Add Sensor", JOptionPane.PLAIN_MESSAGE);
				System.out.println("Please create a region for this sensor to control first!");
			}
			else
			{
				String ID = JOptionPane.showInputDialog(null, "Please enter the sensor ID number:", "Add Sensor", JOptionPane.INFORMATION_MESSAGE);
				if(ID != null && ID.length() > 0)
				{
				
				
					String selection = (String) JOptionPane.showInputDialog(
							null, 
							"Please select which region this sensor is controlling\n",
							"Add Sensor",
							JOptionPane.PLAIN_MESSAGE,
							null, 
							possibilities.toArray(),
							possibilities.get(0));
				
					//Add sensor to the region
					Region region = null;
					for(Region temp : ClientController.getCM().regions)
					{
						if(temp.name.equals(selection))
						{
							region = temp;

						}
					}
					
					if(region != null)
					{
						Sensor newSensor = new Sensor(Canvas.getCursorPoint(), ID);
						ClientController.getCM().addCanvasObject(newSensor);
						ClientController.sendModel();
						region.addSensor(newSensor);
					}
				}				
			}
			break;
			
		case "Erase":		//check if something is selected
			for(CanvasObject object : selected)
			{
				ClientController.getCM().removeCanvasObject(object);
				ClientController.sendModel();
			}
			selected.clear();
			break;
			
		default:
			break;
		}

		tempObjectChanged();
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		switch (ClientController.getCurrentTool())
		{
		case "Walls":		//create temporary wall
			setTempWall(new Wall(Canvas.getCursorPoint(), Canvas.getCursorPoint()));	
			break;
			
		default:
			break;
		}

		tempObjectChanged();
	}


	@Override
	public void mouseDragged(MouseEvent e)
	{
		Canvas.setCursorPoint(snapPointToGrid(e));

		switch (ClientController.getCurrentTool())
		{
		case "Walls":		//update temp wall
			if(e.getX() < 0 || e.getY() < 0 || e.getX() > ClientController.getCM().width || e.getY() > ClientController.getCM().height)
			{
				//Somewhere mouse went off canvas, erase temp wall
				setTempWall(null);
			}
			else if(getTempWall() != null)
			{
				setTempWall(new Wall(getTempWall().startingPoint, Canvas.getCursorPoint()));							
			}
			break;
			
		default:
			break;
		}

		tempObjectChanged();
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		switch (ClientController.getCurrentTool())
		{
		case "Walls":		//draw wall
			if(getTempWall() != null)
			{
				setTempWall(new Wall(getTempWall().startingPoint, Canvas.getCursorPoint()));
				if(!getTempWall().startingPoint.equals(getTempWall().endingPoint))
				{	
					ClientController.getCM().addCanvasObject(getTempWall());
				}
				setTempWall(null);
			}
			ClientController.sendModel();
			break;
			
		default:
			break;
		}

		tempObjectChanged();
	}



	@Override
	public void mouseMoved(MouseEvent e)
	{
		Canvas.setCursorPoint(snapPointToGrid(e));

		//Check if hovered over
		switch(ClientController.getCurrentTool())
		{
		case "Regions":
							if(isCurrentlyBuildingRegion())
							{
								getTempRegion().setLastPoint(Canvas.getCursorPoint());
							}
							break;
			
		case "Erase":
						//Order of selecting items is important to only select "top" most item
						selected.clear();
			
						for(Sensor sensor : ClientController.getCM().sensors)
						{
							sensor.unSelect();
							if(sensor.location.equals(Canvas.getCursorPoint()))
							{	
								selected.add(sensor);
							}						
						}
						
						
						for(Light light : ClientController.getCM().lights)
						{
							light.unSelect();
							if(light.location.equals(Canvas.getCursorPoint()))
							{
								selected.clear();
								selected.add(light);
							}
						}
			
						
						for(Wall wall : ClientController.getCM().walls)
						{
							wall.unSelect();
							if(wall.line.intersects(new Rectangle2D.Double(Canvas.getCursorPoint().getX()- ClientController.getCM().gridSize/2, Canvas.getCursorPoint().getY()- ClientController.getCM().gridSize/2, ClientController.getCM().gridSize, ClientController.getCM().gridSize)))
							{
								if(selected.isEmpty())
								{
									selected.add(wall);
								}
							}
						}


						for(Region region : ClientController.getCM().regions)
						{
							region.unSelect();
							if(region.region.contains(Canvas.getCursorPoint()))
							{
								if(selected.isEmpty())
								{
									selected.add(region);
								}
							}
						}				
						
						for(CanvasObject object : selected)
						{
							object.Select();
						}
		}

		tempObjectChanged();
		ClientController.getCanvas().currentModelChanged();
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		EraseTempObjects();
	}


	private Point2D.Double snapPointToGrid(MouseEvent e)
	{
		//Quantify mouse position to be on grid
		//Equation split up to show where divide by zero, if any
		int tempx = ((Integer)(e.getX()/ClientController.getCM().gridSize));
		int tempy = ((Integer)(e.getY()/ClientController.getCM().gridSize));

		//Snap point to grid
		int finalx = tempx*ClientController.getCM().gridSize + ClientController.getCM().gridSize/2;
		int finaly = tempy*ClientController.getCM().gridSize + ClientController.getCM().gridSize/2;

		//Limit cursor to canvas size
		if(finalx >= ClientController.getCM().width)
		{
			finalx = ((Integer)(ClientController.getCM().width/ClientController.getCM().gridSize))*ClientController.getCM().gridSize - ClientController.getCM().gridSize/2;
		}
		else if(finalx <= 0)
		{
			finalx = ClientController.getCM().gridSize/2;
		}

		if(finaly >= ClientController.getCM().height)
		{
			finaly = ((Integer)(ClientController.getCM().height/ClientController.getCM().gridSize))*ClientController.getCM().gridSize - ClientController.getCM().gridSize/2;
		}
		else if(finaly <= 0)
		{
			finaly = ClientController.getCM().gridSize/2;;
		}

		return new Point2D.Double(finalx, finaly);
	}

	private void EraseTempObjects()
	{
		//Delete all temp objects
		//setTempWall(null); temp wall erase is done in mouseDragged function
		setTempRegion(null);
		setCurrentlyBuildingRegion(false);
		tempObjectChanged();
	}

	//Add subscribers
	public static void addTempObjectSubscriber(TempObjectSubscriber subscriber)
	{
		subscriberList.add(TempObjectSubscriber.class, subscriber);
	}

	//Remove subscriber
	public static void removeTempObjectSubscriber(TempObjectSubscriber subscriber)
	{
		subscriberList.remove(TempObjectSubscriber.class, subscriber);
	}

	public void tempObjectChanged() 
	{
		//Notify listeners
		Object[] subsscribers = subscriberList.getListenerList();
		for (int i = 0; i < subsscribers.length; i = i+2) {
			if (subsscribers[i] == TempObjectSubscriber.class) {
				((TempObjectSubscriber) subsscribers[i+1]).tempObjectChanged();
			}
		}
	}


	public Wall getTempWall() {
		return tempWall;
	}


	public void setTempWall(Wall tempWall) {
		CanvasMouseAdapter.tempWall = tempWall;
		tempObjectChanged();
	}

	public Region getTempRegion() {
		return tempRegion;
	}


	public void setTempRegion(Region tempRegion) {
		CanvasMouseAdapter.tempRegion = tempRegion;
		tempObjectChanged();
	}

	public static boolean isCurrentlyBuildingRegion() {
		return currentlyBuildingRegion;
	}


	public static void setCurrentlyBuildingRegion(boolean value) {
		CanvasMouseAdapter.currentlyBuildingRegion = value;
	}
}
