package controller;

import interfaces.ModelSubscriber;

import java.util.ArrayList;

import model.ModelObject;
import model.StaticNode;
import model.SystemModel;
import model.User;

public abstract class GenericController implements ModelSubscriber
{			
	public static final boolean VERBOSE = false;
	
	//List of subscribers
	protected ArrayList<ModelSubscriber> modelSubscriberList;
	
	//Controller objects
	protected SystemModel systemModel;
	protected GenericCommunicationThread comThread;
		
	//CONSTRUCTOR************************************************************
	public GenericController()
	{
		modelSubscriberList = new ArrayList<ModelSubscriber>();
		systemModel = new SystemModel();							
	}
	
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
	
	public abstract void modifyObject(ModelObject object, String [] parameters, Object [] values);
	
	
	public ArrayList<ModelObject> getModelObjects()
	{
		return systemModel.getModelObjectList();
	}
	
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
	
	public StaticNode getSensor(String MACAddress)
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

	//houseModel subscribers
	public void addModelSubscriber(ModelSubscriber subscriber) 
	{
		modelSubscriberList.add(subscriber);		
	}

	public void removeModelSubscriber(ModelSubscriber subscriber) 
	{
		modelSubscriberList.remove(subscriber);
	}

	public void notifyModelSubscribers() 
	{
		for(ModelSubscriber subscriber : modelSubscriberList)
		{
			subscriber.modelChanged();
		}
	}
}
