package controller;

import interfaces.HouseModelSubscriber;
import interfaces.UserModelSubscriber;

import java.util.ArrayList;

import model.HouseObject;
import model.SystemModel;
import model.User;

public abstract class GenericController implements HouseModelSubscriber, UserModelSubscriber
{		
	//List of subscribers
	protected ArrayList<HouseModelSubscriber> houseModelSubscriberList;
	protected ArrayList<UserModelSubscriber> usersModelSubscriberList;
	
	//Controller objects
	protected SystemModel systemModel;
	protected GenericCommunicationThread comThread;
	
	//CONSTRUCTOR************************************************************
	public GenericController()
	{
		houseModelSubscriberList = new ArrayList<HouseModelSubscriber>();
		usersModelSubscriberList = new ArrayList<UserModelSubscriber>();
		systemModel = new SystemModel();	
	}
		
	
	//MODIFY MODEL - HOUSE OBJECTS****************************************************
	public void addHouseObject(HouseObject object)
	{
		//Add this controller as subscriber to the object
		
		
		systemModel.getHouseObjectList().add(object);
		notifyHouseModelSubscribers();
	}

	public void removeHouseObject(HouseObject object)
	{
		systemModel.getHouseObjectList().remove(object);
		notifyHouseModelSubscribers();
	}
	
	public boolean modifyObject(HouseObject object)
	{
		return false;
		
	}

	public ArrayList<HouseObject>  getHouseObjectList()
	{
		return systemModel.getHouseObjectList();
	}

	public void setHouseObjectList(ArrayList<HouseObject> newObjects)
	{
		systemModel.setHouseObjectList(newObjects);
		notifyHouseModelSubscribers();
	}


	//MODIFY MODEL -  USERS*************************************************************
	public void addUser(User user)
	{
		systemModel.getUserList().add(user);
		notifyUserModelSubscribers();
	}
	public void removeUser(User user)
	{
		systemModel.getUserList().remove(user);
		notifyUserModelSubscribers();
	}

	public ArrayList<User> getUserList()
	{
		return systemModel.getUserList();
	}

	public void setUserList(ArrayList<User> newUsers)
	{
		systemModel.setUserList(newUsers);
		notifyUserModelSubscribers();
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
	public void addHouseModelSubscriber(HouseModelSubscriber subscriber) 
	{
		houseModelSubscriberList.add(subscriber);		
	}

	public void removeHouseModelSubscriber(HouseModelSubscriber subscriber) 
	{
		houseModelSubscriberList.remove(subscriber);
	}

	public void notifyHouseModelSubscribers() 
	{
		for(HouseModelSubscriber subscriber : houseModelSubscriberList)
		{
			subscriber.houseModelChanged();
		}
	}


	//usersModel subscribers
	public void addUserModelSubscriber(UserModelSubscriber subscriber) 
	{
		usersModelSubscriberList.add(subscriber);		
	}

	public void removeUserModelSubscriber(UserModelSubscriber subscriber) 
	{
		usersModelSubscriberList.remove(subscriber);
	}

	public void notifyUserModelSubscribers() 
	{
		for(UserModelSubscriber subscriber : usersModelSubscriberList)
		{
			subscriber.userModelChanged();
		}
	}
}
