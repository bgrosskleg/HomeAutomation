package controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import view.Applet;
import view.Canvas;
import model.HouseObject;
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
		if(SwingUtilities.isLeftMouseButton(e))
		{
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
										if(!canvas.getTempWall().getStartPoint().equals(canvas.getTempWall().getEndPoint()))
										{
											canvas.getTempWall().finalize();
											Applet.getController().getSystemModel().addHouseObject(canvas.getTempWall().clone());
										}
										
										canvas.setTempWall(null);
										canvas.setCurrentlyBuildingWall(false);
									}
									break;
									
			case "Regions":			if(!canvas.isCurrentlyBuildingRegion())
									{
										//Create region
										canvas.setTempRegion(new Region((Point2D.Double)canvas.getCursorPoint().clone(),canvas.getCursorPoint(), canvas.randomColor()));
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
								
											Applet.getController().getSystemModel().addHouseObject(canvas.getTempRegion().clone());
										}
						
										canvas.setTempRegion(null);
										canvas.setCurrentlyBuildingRegion(false);
									}
						
									break;
	
			case "Lights":		//add light
								Applet.getController().getSystemModel().addHouseObject(new Light((Point2D.Double)canvas.getCursorPoint().clone()));
								break;
	
			case "Sensors":		//add sensor
								String MACAddress = JOptionPane.showInputDialog(null, "Please enter the sensor's MAC address:", "Add Sensor", JOptionPane.INFORMATION_MESSAGE);
								if(MACAddress != null && MACAddress.length() > 0)
								{									
									//Specify which region this sensor controls
									ArrayList<String> possibilities = new ArrayList<String>();
									for(HouseObject object : Applet.getController().getSystemModel().getHouseObjectList())
									{
										if(object instanceof Region)
										{
											possibilities.add(((Region)object).getName());
										}
									}
									
									if(possibilities.isEmpty())
									{
										JOptionPane.showMessageDialog(null, "Please create a region for this sensor to control first!", "Add Sensor", JOptionPane.PLAIN_MESSAGE);
										System.out.println("Please create a region for this sensor to control first!");
									}
									else
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
										for(HouseObject object : Applet.getController().getSystemModel().getHouseObjectList())
										{
											if(object instanceof Region && ((Region) object).getName().equals(selection))
											{
												Sensor newSensor = new Sensor(MACAddress, (Point2D.Double)canvas.getCursorPoint().clone());
												((Region)object).addSensor(newSensor);
												Applet.getController().getSystemModel().addHouseObject(newSensor);
											}
										}
									}
								}
								break;
				
			case "Erase":		for(HouseObject object : canvas.getSelected())
								{
									Applet.getController().getSystemModel().removeHouseObject(object);
								}
								break;
				
			default:
								break;
			}
		}
		else if(SwingUtilities.isRightMouseButton(e))
		{
			//Check for regions under right click, open object editor
			
		
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
