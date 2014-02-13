package controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import view.ClientApplet;
import view.Canvas;
import view.RegionEditor;
import view.StaticNodeEditor;
import model.ModelObject;
import model.Light;
import model.Region;
import model.StaticNode;
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
			switch (ClientApplet.getController().getCurrentTool())
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
											ClientApplet.getController().addModelObject(canvas.getTempWall().clone());
										}
										
										canvas.setTempWall(null);
										canvas.setCurrentlyBuildingWall(false);
									}
									break;
									
			case "Regions":			if(!canvas.isCurrentlyBuildingRegion())
									{
										//Create region
										canvas.setTempRegion(new Region((Point2D.Double)canvas.getCursorPoint().clone(),canvas.getCursorPoint(), ModelObject.randomColor()));
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
								
											ClientApplet.getController().addModelObject(canvas.getTempRegion().clone());
										}
						
										canvas.setTempRegion(null);
										canvas.setCurrentlyBuildingRegion(false);
									}
						
									break;
	
			case "Lights":		//add light
								ClientApplet.getController().addModelObject(new Light((Point2D.Double)canvas.getCursorPoint().clone()));
								break;
	
			case "Static Node":	//add static node
								String MACAddress = JOptionPane.showInputDialog(null, "Please enter the sensor's MAC address:", "Add Sensor", JOptionPane.INFORMATION_MESSAGE);
								if(MACAddress != null && MACAddress.length() > 0)
								{									
									//Specify which region this sensor controls
									ArrayList<String> possibilities = new ArrayList<String>();
									String defaultOption = "None - Must be paired to a region later";
									possibilities.add(defaultOption);
									for(ModelObject object : ClientApplet.getController().getModelObjects())
									{
										if(object instanceof Region)
										{
											possibilities.add(((Region)object).getName());
										}
									}
									
									String selection = (String) JOptionPane.showInputDialog(
											null, 
											"Please select which region this sensor is controlling\n",
											"Add Sensor",
											JOptionPane.PLAIN_MESSAGE,
											null, 
											possibilities.toArray(),
											possibilities.get(0));


									//Add sensor to the region
									if(selection == defaultOption)
									{
										ClientApplet.getController().addModelObject(new StaticNode(MACAddress, null, (Point2D.Double)canvas.getCursorPoint().clone()));
									}
									else
									{
										for(ModelObject object : ClientApplet.getController().getModelObjects())
										{
											if(object instanceof Region && ((Region) object).getName().equals(selection))
											{
												StaticNode newStaticNode = new StaticNode(MACAddress, (Region)object, (Point2D.Double)canvas.getCursorPoint().clone());
												((Region)object).addStaticNode(newStaticNode);
												ClientApplet.getController().addModelObject(newStaticNode);
												break;
											}
										}
									}
								}
								break;
				
			case "Erase":		for(ModelObject object : canvas.getSelected())
								{
									ClientApplet.getController().removeModelObject(object);
								}
								break;
				
			default:
								break;
			}
		}
		else if(SwingUtilities.isRightMouseButton(e))
		{
			//Check for edittable objects under right click, open object editor
			boolean editSensor = false;
			
			//Edit sensor
			for(ModelObject object : canvas.getSelected())
			{
				if(object instanceof StaticNode)
				{
					new StaticNodeEditor((StaticNode)object);
					editSensor = true;
				}
				break;
			}
			
			//if no sensor at location, Edit region
			if(!editSensor)
			{
				for(ModelObject object : canvas.getSelected())
				{
					//Get the first selected region or sensor
					if(object instanceof Region)
					{new RegionEditor((Region)object);}
					break;
				}
			}
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
