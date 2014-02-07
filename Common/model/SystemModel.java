package model;

import java.io.Serializable;
import java.util.ArrayList;

public class SystemModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<HouseObject> houseObjectList;
	private ArrayList<User>	userList;
	
	//CONSTRUCTOR*************************************************************
	public SystemModel()
	{		
		setHouseObjectList(new ArrayList<HouseObject>());
		setUserList(new ArrayList<User>());
	}

	
	//MUTATORS AND ACCESSORS**************************************************
	public ArrayList<HouseObject> getHouseObjectList() {
		return houseObjectList;
	}

	public void setHouseObjectList(ArrayList<HouseObject> houseObjectList) {
		this.houseObjectList = houseObjectList;
	}

	public ArrayList<User> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}
}
