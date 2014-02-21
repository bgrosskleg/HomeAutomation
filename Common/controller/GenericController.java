package controller;

import interfaces.ModelSubscriber;

import java.util.ArrayList;

import model.ModelObject;
import model.StaticNode;
import model.SystemModel;
import model.User;

public abstract class GenericController
{			
	public static final boolean VERBOSE = false;
	
	/**
	 * List of model subscribers to be notified
	 */
	protected ArrayList<ModelSubscriber> modelSubscriberList;
	
	/**
	 * A barebones controller needs a comThread and systemModel
	 */
	protected SystemModel systemModel;
	protected GenericCommunicationThread comThread;
		
	//CONSTRUCTOR************************************************************
	/**
	 * Creates a simplistic controller with new model and subscriber list
	 */
	public GenericController()
	{
		modelSubscriberList = new ArrayList<ModelSubscriber>();
		systemModel = new SystemModel();							
	}
	
	/**
	 * Called when a new model is recieved in the comThread
	 * It does not overwrite the model, rather performs a diff function to add/remove or edit
	 * objects in order to maintain the references made to them
	 * @param newModel the new model recieved in the comThread
	 */
	protected synchronized void updateSystemModel(SystemModel newModel)
	{
		//Perform a diff function on the local model and new model and update local rather than overwrite to 
		//keep local model links etc intact
		//O(n^2)
		//Simply trying:
		//systemModel = newModel
		//overwrites all references to objects that might have been made elsewhere (ie lists, or location tracking modules
		//Therefore, adding or removing items is fine, but UPDATING objects should be done by exchanging paramters and values, not 
		//overwriting
		
		if(GenericController.VERBOSE)
		{System.out.println("OLD: " + systemModel.toString());}
		
		//Add missing objects
		for(ModelObject newObject : newModel.getModelObjectList())
		{
			if(!systemModel.getModelObjectList().contains(newObject))
			{
				systemModel.getModelObjectList().add(newObject);
			}
		}
		
		
		//Remove old objects
		//Create list of objects to remove first to avoid concurrent modification
		ArrayList<ModelObject> toBeRemoved = new ArrayList<ModelObject>();
		for(ModelObject oldObject : systemModel.getModelObjectList())
		{
			if(!newModel.getModelObjectList().contains(oldObject))
			{
				toBeRemoved.add(oldObject);
			}
		}
		for(ModelObject object : toBeRemoved)
		{
			systemModel.getModelObjectList().remove(object);
		}
		
		
		//Modify existing object with new details
			//HouseObjects
			for(ModelObject oldObject : systemModel.getModelObjectList())
			{
				for(ModelObject newObject : newModel.getModelObjectList())
				{
					if(oldObject.equals(newObject))
					{
						try 
						{
							oldObject.edit(newObject.getParameters(), newObject.getValues());
						}
						catch (Exception e) 
						{
							System.err.println("Problem editting existing object");
							e.printStackTrace();
						}
					}
				}
			}
		
		if(GenericController.VERBOSE)
		{System.out.println("NEW: " + systemModel.toString());}
		
		notifyModelSubscribers();
	}
		
	
	//MODIFY MODEL***********************************************************
	/**
	 * Modify object will be implemented differently in the BaseStation and Client Applet controllers
	 * @param object the object to be editted
	 * @param parameters the list of String parameters to edit
	 * @param values the list of Object values to use
	 */
	public abstract void modifyObject(ModelObject object, String [] parameters, Object [] values);
	
	
	public ArrayList<ModelObject> getModelObjects()
	{
		return systemModel.getModelObjectList();
	}
	
	/**
	 * Returns the user with the entered MAC address, null if not found
	 * @param MACAddress the MACAddress of user to get
	 * @return the User if found, null if not
	 */
	public User getUser(String MACAddress)
	{
		 for(ModelObject object : getModelObjects())
		 {
			 if(object instanceof User)
			 {
				 User user = (User) object;
				 if(user.getMACAddress().equals(MACAddress))
				 {
					 return user;
				 }
			 }
		 }
		 return null;
	}
	
	/**
	 * Returns the static Node with the entered MAC address, null if not found
	 * @param MACAddress of the staticNode to get
	 * @return the staticNode if found, null if not
	 */
	public StaticNode getStaticNode(String MACAddress)
	{
		 for(ModelObject object : getModelObjects())
		 {
			 if(object instanceof StaticNode)
			 {
				 StaticNode sensor = (StaticNode) object;
				 if(sensor.getMACAddress().equals(MACAddress))
				 {
					 return sensor;
				 }
			 }
		 }
		 return null;
	}
	

	//SUBSCRIBERS/OBSERVERS****************************************************************

	/**
	 * Add modelSubscriber
	 * @param subscriber the subscriber to add
	 */
	public void addModelSubscriber(ModelSubscriber subscriber) 
	{
		modelSubscriberList.add(subscriber);		
	}

	/**
	 * Remove modelSubscriber
	 * @param subscriber the subscriber to remove
	 */
	public void removeModelSubscriber(ModelSubscriber subscriber) 
	{
		modelSubscriberList.remove(subscriber);
	}

	/**
	 * Calls the modelChanged() function in all the modelSubscribers
	 */
	public void notifyModelSubscribers() 
	{
		for(ModelSubscriber subscriber : modelSubscriberList)
		{
			subscriber.modelChanged();
		}
	}
}
