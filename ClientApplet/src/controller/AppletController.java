package controller;

import javax.swing.ImageIcon;

import model.ModelObject;
import model.Region;
import model.StaticNode;
import view.Canvas;
import view.ClientApplet;

public class AppletController extends GenericController
{		
	private ClientApplet application;
	
	private Canvas canvas;
	
	private String currentTool;
	
	//CONSTRUCTOR***********************************************
	public AppletController(ClientApplet applet, Canvas canvas)
	{
		super();
		
		this.application = applet;
		
		this.canvas = canvas;
		
		//Add this controller as subscriber to model
		addModelSubscriber(this);

		//Create communication thread
		comThread = new AppletCommunicationThread(this);
		comThread.start();
	}
	
	//MUTATORS AND ACCESSORS*************************************
	public ClientApplet getClientApplet()
	{
		return application;
	}
	
	public String getCurrentTool() 
	{
		return currentTool;
	}

	public void setCurrentTool(String currentTool) 
	{
		this.currentTool = currentTool;
	}

	@Override
	public void modelChanged() 
	{		
		canvas.repaint();
	}
	
	//MODIFY MODEL ***********************************************************
	
	public void addModelObject(ModelObject object)
	{
		if(systemModel.getModelObjectList().add(object));
		{
			//Notify local subscribers
			notifyModelSubscribers();
			
			//Send to baseStation
			comThread.sendModel();
		}
	}

	public void removeModelObject(ModelObject object)
	{
		if(systemModel.getModelObjectList().remove(object));
		{
			//Remove links from static nodes and regions
			if(object instanceof StaticNode)
			{
				StaticNode staticNode = (StaticNode) object;
				if(staticNode.getPairedRegion() != null)
				{staticNode.getPairedRegion().removeStaticNode(staticNode);}
			}
			
			if(object instanceof Region)
			{
				Region region = (Region) object;
				for(StaticNode staticNode : region.getStaticNodes())
				{
					staticNode.setPairedRegion(null);
				}
			}
			
			
			
			//Notify local subscribers
			notifyModelSubscribers();
		
			//Send to baseStation
			comThread.sendModel();
		}
	}
		
	
	//LOAD IMAGE FROM HOST************************************************************
	
	public ImageIcon loadImageIconFromHost(String filepath)
	{
		return new ImageIcon(application.getImage(((AppletCommunicationThread)comThread).getCodebase(), filepath));
	}
}
