package controller;

import interfaces.ModelSubscriber;

import java.util.ArrayList;

import model.HouseObject;
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
	
	protected void setModel(SystemModel newModel)
	{
		systemModel = newModel;
		notifyModelSubscribers();
	}
		
	
	//MODIFY MODEL***********************************************************
	
	public boolean modifyObject(HouseObject object, String [] parameters, Object [] values)
	{
		if(systemModel.getHouseObjectList().contains(object) || systemModel.getUserList().contains(object))
		{
			try 
			{
				object.edit(parameters, values);
				notifyModelSubscribers();
				
				//Send model to other end
				comThread.sendModel();
				
				return true;
			} 
			catch (Exception e) 
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	//MODEL - HOUSE OBJECTS****************************************************
	
	public ArrayList<HouseObject>  getHouseObjectList()
	{
		return systemModel.getHouseObjectList();
	}

	//MODEL -  USERS*************************************************************

	public ArrayList<User> getUserList()
	{
		return systemModel.getUserList();
	}

	public User getUser(String MACAddress)
	{
		for(User user : systemModel.getUserList())
		{
			if(user.getMACAddress().equals(MACAddress))
			{
				return user;
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
