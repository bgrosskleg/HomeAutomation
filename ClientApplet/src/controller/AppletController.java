package controller;

import javax.swing.ImageIcon;

import model.ModelObject;
import model.Region;
import model.StaticNode;
import view.Canvas;
import view.ClientApplet;

public class AppletController extends GenericController
{		
	/**
	 * reference to the applet, required to get codebase to load images from server
	 */
	private ClientApplet applet;
	
	private String currentTool;
	
	//CONSTRUCTOR***********************************************
	/**
	 * Creates the applet controller, the applet controller adds additional functions to the generic controller
	 * @param applet	the applet that created this controller
	 * @param canvas	the canvas that is in the applet, used for repainting
	 */
	public AppletController(ClientApplet applet, Canvas canvas)
	{
		super();
		
		this.applet = applet;
		
		//Add canvas as subscriber to model
		addModelSubscriber(canvas);

		//Create communication thread
		comThread = new AppletCommunicationThread(this);
		comThread.start();
	}
	
	//MUTATORS AND ACCESSORS*************************************
	public ClientApplet getClientApplet()
	{
		return applet;
	}
	
	public String getCurrentTool() 
	{
		return currentTool;
	}

	public void setCurrentTool(String currentTool) 
	{
		this.currentTool = currentTool;
	}
	
	//MODIFY MODEL ***********************************************************
	/**
	 * Add's object to systemModel, notifies local subscribers and sends the model over the communication channel to BaseStation
	 * @param object	ModelObject to be added
	 */
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

	/**
	 * Remove an object from systemModel, removes any staticNode-region pairing references
	 * Notifies local subscribers and sends model over communication channel to BaseStation
	 * @param object
	 */
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
	
	/**
	 * If the object exists, edit the object with the parameters and values passed to the function
	 * @param object 	the modelObject to be editted
	 * @param parameters the String list of parameters to be editted
	 * @param values the object List of the values to be used
	 */
	@Override
	public void modifyObject(ModelObject object, String[] parameters, Object[] values) 
	{
		if(systemModel.getModelObjectList().contains(object))
		{
			try 
			{
				if(object.edit(parameters, values))
				{									
					//Notify local subscribers
					notifyModelSubscribers();
					
					//Send model to other end
					comThread.sendModel();
				}
			} 
			catch (Exception e) 
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("Object not part of systemModel list!");
		}	
	}
		
	
	//LOAD IMAGE FROM HOST************************************************************
	
	/**
	 * Handles loading an image from the server the applet came from
	 * @param filepath the filepath of the requested image
	 * @return the imageIcon requested
	 */
	public ImageIcon loadImageIconFromHost(String filepath)
	{
		return new ImageIcon(applet.getImage(((AppletCommunicationThread)comThread).getCodebase(), filepath));
	}
}
