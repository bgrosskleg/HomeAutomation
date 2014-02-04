package controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import view.Canvas;
import model.CanvasObject;
import model.Light;
import model.Region;
import model.Sensor;
import model.Wall;

public class CanvasMouseAdapter extends MouseInputAdapter
{	
	private Canvas canvas;

	public CanvasMouseAdapter(Canvas canvas)
	{
		super();
		this.canvas = canvas;
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		canvas.snapMouseToGrid(e);
		
		//Check for regions under click, open region editor
		if(AppletController.getCM() != null && AppletController.getCM().getRegions() != null)
		{
			for(Region region : AppletController.getCM().getRegions())
			{
				if(region.getRegion().contains(canvas.getCursorPoint()))
				{
					
				}
			}	
		}
		
		
		//Add new objects
		switch (AppletController.getCurrentTool())
		{
		case "Walls":			if(!canvas.isCurrentlyBuildingWall())
								{
									canvas.setTempWall(new Wall((Point2D.Double)canvas.getCursorPoint().clone(), canvas.getCursorPoint()));
									canvas.setCurrentlyBuildingWall(true);	
								}
								else
								{
									if(!canvas.getTempWall().startingPoint.equals(canvas.getTempWall().endingPoint))
									{AppletCommunicationThread.addObject(canvas.getTempWall());}
									
									canvas.setTempWall(null);
									canvas.setCurrentlyBuildingWall(false);
								}
								break;
								
		case "Regions":			if(!canvas.isCurrentlyBuildingRegion())
								{
									//Create region
									canvas.setTempRegion(new Region((Point2D.Double)canvas.getCursorPoint().clone(),canvas.getCursorPoint()));
									canvas.setCurrentlyBuildingRegion(true);
								}
								else if(!canvas.getTempRegion().getStartPoint().equals(canvas.getCursorPoint()))
								{
									//Append point to region
									canvas.getTempRegion().addPointToRegion();
								}
								else
								{
									//Complete region
									canvas.getTempRegion().finalize();
					
									String name = JOptionPane.showInputDialog(null, "Please enter the region name:", "Add Region", JOptionPane.INFORMATION_MESSAGE);
									if(name != null && name.length() > 0)
									{				
										canvas.getTempRegion().setName(name);				
							
										AppletCommunicationThread.addObject(canvas.getTempRegion());
									}
					
									canvas.setTempRegion(null);
									canvas.setCurrentlyBuildingRegion(false);
								}
					
								break;

		case "Lights":		//add light
							AppletCommunicationThread.addObject(new Light(canvas.getCursorPoint()));	
							break;

		case "Sensors":		//add sensor
							//Specify which region this sensor controls
							ArrayList<String> possibilities = new ArrayList<String>();
							for(Region region : AppletController.getCM().getRegions())
							{
								possibilities.add(region.getName());
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
									for(Region temp : AppletController.getCM().getRegions())
									{
										if(temp.getName().equals(selection))
										{
											region = temp;
				
										}
									}
									
									if(region != null)
									{
										Sensor newSensor = new Sensor(canvas.getCursorPoint(), ID);
										AppletCommunicationThread.addObject(newSensor);
										region.addSensor(newSensor);
									}
								}				
							}
							break;
			
		case "Erase":		for(CanvasObject object : canvas.getSelected())
							{
								System.out.println("Removing object!");
								object.unSelect();
								AppletCommunicationThread.removeObject(object);
							}
							canvas.getSelected().clear();
							break;
			
		default:
							break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		canvas.snapMouseToGrid(e);
	}


	@Override
	public void mouseMoved(MouseEvent e)
	{
		canvas.snapMouseToGrid(e);
		
		//Check if objects are hovered over
		switch(AppletController.getCurrentTool())
		{
		case "Erase":		selectObjects();
							break;
		}			
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		canvas.EraseTempObjects();
	}
	
	private void selectObjects()
	{
		canvas.getSelected().clear();
		
		//Order of selecting items is important to only select "top" most item
	
		if(AppletController.getCM() != null && AppletController.getCM().getSensors() != null)
		{
			for(Sensor sensor : AppletController.getCM().getSensors())
			{
				sensor.unSelect();
				if(sensor.location.equals(canvas.getCursorPoint()))
				{	
					canvas.getSelected().add(sensor);
				}						
			}
		}
		
		if(AppletController.getCM() != null && AppletController.getCM().getLights() != null)
		{
			for(Light light : AppletController.getCM().getLights())
			{
				light.unSelect();
				if(light.location.equals(canvas.getCursorPoint()))
				{
					canvas.getSelected().clear();
					canvas.getSelected().add(light);
				}
			}
		}

		if(AppletController.getCM() != null && AppletController.getCM().getWalls() != null)
		{
			for(Wall wall : AppletController.getCM().getWalls())
			{
				wall.unSelect();
				if(wall.line.intersects(new Rectangle2D.Double(canvas.getCursorPoint().getX()- canvas.gridSize/2, canvas.getCursorPoint().getY()- canvas.gridSize/2, canvas.gridSize, canvas.gridSize)))
				{
					if(canvas.getSelected().isEmpty())
					{
						canvas.getSelected().add(wall);
					}
				}
			}
		}

		if(AppletController.getCM() != null && AppletController.getCM().getRegions() != null)
		{
			for(Region region : AppletController.getCM().getRegions())
			{
				region.unSelect();
				if(region.getRegion().contains(canvas.getCursorPoint()))
				{
					if(canvas.getSelected().isEmpty())
					{
						canvas.getSelected().add(region);
					}
				}
			}	
		}
		
		for(CanvasObject object : canvas.getSelected())
		{
			object.Select();
		}
		
		canvas.repaint();
	}
}
