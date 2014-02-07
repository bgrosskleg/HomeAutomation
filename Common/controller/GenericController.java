package controller;

import interfaces.HouseObjectsModelSubscriber;
import interfaces.UsersModelSubscriber;

import java.util.ArrayList;

import model.HouseObject;
import model.SystemModel;
import model.User;

public abstract class GenericController implements HouseObjectsModelSubscriber, UsersModelSubscriber
{		
	//List of subscribers
	protected ArrayList<HouseObjectsModelSubscriber> houseModelSubscriberList;
	protected ArrayList<UsersModelSubscriber> usersModelSubscriberList;
	
	//Controller objects
	protected SystemModel systemModel;
	protected GenericCommunicationThread comThread;
	
	//CONSTRUCTOR************************************************************
	public GenericController()
	{
		houseModelSubscriberList = new ArrayList<HouseObjectsModelSubscriber>();
		usersModelSubscriberList = new ArrayList<UsersModelSubscriber>();
		systemModel = new SystemModel();	
	}
		
	
	//MODIFY MODEL - HOUSE OBJECTS****************************************************
	public void addHouseObject(HouseObject object)
	{
		systemModel.getHouseObjectList().add(object);
		notifyHouseObjectsModelSubscribers();
	}

	public void removeHouseObject(HouseObject object)
	{
		systemModel.getHouseObjectList().remove(object);
		notifyHouseObjectsModelSubscribers();
	}
	
	public boolean modifyObject(HouseObject object, String [] parameters, Object [] values)
	{
		if(systemModel.getHouseObjectList().contains(object))
		{
			try 
			{
				object.edit(parameters, values);
				notifyHouseObjectsModelSubscribers();
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

	public ArrayList<HouseObject>  getHouseObjectList()
	{
		return systemModel.getHouseObjectList();
	}

	public void setHouseObjectList(ArrayList<HouseObject> newObjects)
	{
		systemModel.setHouseObjectList(newObjects);
		notifyHouseObjectsModelSubscribers();
	}


	//MODIFY MODEL -  USERS*************************************************************
	public void addUser(User user)
	{
		systemModel.getUserList().add(user);
		notifyUsersModelSubscribers();
	}
	public void removeUser(User user)
	{
		systemModel.getUserList().remove(user);
		notifyUsersModelSubscribers();
	}

	public ArrayList<User> getUserList()
	{
		return systemModel.getUserList();
	}

	public void setUserList(ArrayList<User> newUsers)
	{
		systemModel.setUserList(newUsers);
		notifyUsersModelSubscribers();
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
	public void addHouseObjectsModelSubscriber(HouseObjectsModelSubscriber subscriber) 
	{
		houseModelSubscriberList.add(subscriber);		
	}

	public void removeHouseObjectsModelSubscriber(HouseObjectsModelSubscriber subscriber) 
	{
		houseModelSubscriberList.remove(subscriber);
	}

	public void notifyHouseObjectsModelSubscribers() 
	{
		for(HouseObjectsModelSubscriber subscriber : houseModelSubscriberList)
		{
			subscriber.houseModelChanged();
		}
	}


	//usersModel subscribers
	public void addUsersModelSubscriber(UsersModelSubscriber subscriber) 
	{
		usersModelSubscriberList.add(subscriber);		
	}

	public void removeUsersModelSubscriber(UsersModelSubscriber subscriber) 
	{
		usersModelSubscriberList.remove(subscriber);
	}

	public void notifyUsersModelSubscribers() 
	{
		for(UsersModelSubscriber subscriber : usersModelSubscriberList)
		{
			subscriber.userModelChanged();
		}
	}
}
