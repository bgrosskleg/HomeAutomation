package model;

import java.io.Serializable;
import java.util.ArrayList;

import subscribers.HouseModelSubscriber;
import subscribers.UserModelSubscriber;

public class SystemModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	//List of subscribers
	protected ArrayList<HouseModelSubscriber> houseModelSubscriberList;
	protected ArrayList<UserModelSubscriber> usersModelSubscriberList;
	
	private ArrayList<HouseObject> houseModelObjects;
	private ArrayList<User>	users;
	
	//CONSTRUCTOR*************************************************************
	public SystemModel()
	{
		houseModelSubscriberList = new ArrayList<HouseModelSubscriber>();
		usersModelSubscriberList = new ArrayList<UserModelSubscriber>(); 
		
		houseModelObjects = new ArrayList<HouseObject>();
		users = new ArrayList<User>();
	}
	
	
	//MODIFY HOUSE OBJECTS****************************************************
	public void addHouseObject(HouseObject object)
	{
		houseModelObjects.add(object);
		notifyHouseModelSubscribers();
	}
	
	public void removeHouseObject(HouseObject object)
	{
		houseModelObjects.remove(object);
		notifyHouseModelSubscribers();
	}
	
	public ArrayList<HouseObject>  getHouseObjectList()
	{
		return houseModelObjects;
	}
	
	public void setHouseObjectList(ArrayList<HouseObject> newObjects)
	{
		houseModelObjects = newObjects;
		notifyHouseModelSubscribers();
	}
	
	
	//MODIFY USERS*************************************************************
	public void addUser(User user)
	{
		users.add(user);
		notifyUserModelSubscribers();
	}
	public void removeUser(User user)
	{
		users.remove(user);
		notifyUserModelSubscribers();
	}
	
	public ArrayList<User> getUserList()
	{
		return users;
	}
	
	public void setUserList(ArrayList<User> newUsers)
	{
		users = newUsers;
		notifyUserModelSubscribers();
	}
	
	public User getUser(String MACAddress)
	{
		for(User user : users)
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
	
	//Necessary for storing model without reference to subscribers
	public void removeAllSubscribers()
	{
		houseModelSubscriberList.clear();
		usersModelSubscriberList.clear();
	}

}
