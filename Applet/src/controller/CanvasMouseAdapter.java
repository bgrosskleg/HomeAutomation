package controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import view.Applet;
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
		//Check for regions under click, open region editor
		if(Applet.getController().getCM() != null && Applet.getController().getCM().getRegions() != null)
		{
			for(Region region : Applet.getController().getCM().getRegions())
			{
				if(region.getRegion().contains(canvas.getCursorPoint()))
				{
					
				}
			}	
		}
		
		
		//Add new objects
		switch (Applet.getController().getCurrentTool())
		{
		case "Walls":			if(!canvas.isCurrentlyBuildingWall())
								{
									canvas.setTempWall(new Wall((Point2D.Double)canvas.getCursorPoint().clone(), canvas.getCursorPoint()));
									canvas.setCurrentlyBuildingWall(true);	
								}
								else
								{
									if(!canvas.getTempWall().getStartingPoint().equals(canvas.getTempWall().getEndingPoint()))
									{
										canvas.getTempWall().finalize();
										Applet.getComThread().addObject(canvas.getTempWall());
									}
									
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
							
										Applet.getComThread().addObject(canvas.getTempRegion());
									}
					
									canvas.setTempRegion(null);
									canvas.setCurrentlyBuildingRegion(false);
								}
					
								break;

		case "Lights":		//add light
							Applet.getComThread().addObject(new Light(canvas.getCursorPoint()));	
							break;

		case "Sensors":		//add sensor
							//Specify which region this sensor controls
							ArrayList<String> possibilities = new ArrayList<String>();
							for(Region region : Applet.getController().getCM().getRegions())
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
									for(Region temp : Applet.getController().getCM().getRegions())
									{
										if(temp.getName().equals(selection))
										{
											region = temp;
				
										}
									}
									
									if(region != null)
									{
										Sensor newSensor = new Sensor(canvas.getCursorPoint(), ID);
										Applet.getComThread().addObject(newSensor);
										region.addSensor(newSensor);
									}
								}				
							}
							break;
			
		case "Erase":		for(CanvasObject object : canvas.getSelected())
							{
								Applet.getComThread().removeObject(object);
							}
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
	}

	
	@Override
	public void mouseExited(MouseEvent e)
	{
		canvas.EraseTempObjects();
	}
	
}
