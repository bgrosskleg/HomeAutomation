package controller;

import interfaces.ModelSubscriber;

import java.util.ArrayList;

import model.ModelObject;
import model.Region;
import model.StaticNode;
import model.SystemModel;
import model.User;

public abstract class GenericController implements ModelSubscriber
{			
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
	
	protected void updateSystemModel(SystemModel newModel)
	{
		//Perform a diff function on the local model and new model and update local rather than overwrite to 
		//keep local model links etc intact
		//O(n^2)
		//Simply trying:
		//systemModel = newModel
		//overwrites all references to objects that might have been made elsewhere (ie lists, or location tracking modules
		//Therefore, adding or removing items is fine, but UPDATING objects should be done by exchanging paramters and values, not 
		//overwriting
		
		
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
		
		notifyModelSubscribers();
	}
		
	
	//MODIFY MODEL***********************************************************
	
	public void modifyObject(ModelObject object, String [] parameters, Object [] values)
	{
		if(systemModel.getModelObjectList().contains(object))
		{
			try 
			{
				if(object.edit(parameters, values))
				{					
					//Redetermine occupied regions
					if(object instanceof User)
					{updateStaticNodes();}
					
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
	}
	
	private void updateStaticNodes()
	{
		//O(n^2)
		
		for(ModelObject object1 : systemModel.getModelObjectList())
		{
			if(object1 instanceof Region)
			{
				Region region = (Region) object1;
				
				for(ModelObject object2 : systemModel.getModelObjectList())
				{
					if(object2 instanceof User)
					{
						User user = (User) object2;
						
						if(region.getPath().contains(user.getLocation()))
						{
							//Add user to region
							if(!region.getUsers().contains(user))
							{
								region.getUsers().add(user);
								
								//Set region lighting value to highest user
								if(region.getLightingValue() < user.getPreferredLightingValue())
								{
									region.setLightingValue(user.getPreferredLightingValue());
								}
								
								//Notify all regions static nodes
								region.notifyStaticNodes();
							}
						}
						else
						{
							//Remove user from region
							if(region.getUsers().contains(user))
							{
								region.getUsers().remove(user);
								
								//Set region lighting to next highest value, off if empty
								if(region.getUsers().isEmpty())
								{
									region.setLightingValue(0);
								}
								else
								{
									for(User user2 : region.getUsers())
									{
										if(region.getLightingValue() < user2.getPreferredLightingValue())
										{
											region.setLightingValue(user2.getPreferredLightingValue());
										}
									}
								}
								
								//Notify all regions static nodes
								region.notifyStaticNodes();
							}
						}
					}
				}
			}
		}
	}
	
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
