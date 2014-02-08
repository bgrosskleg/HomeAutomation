package controller;

import javax.swing.ImageIcon;

import model.HouseObject;
import model.User;
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
	
	//MODIFY MODEL - HOUSE OBJECTS****************************************************
	
	public void addHouseObject(HouseObject object)
	{
		if(systemModel.getHouseObjectList().add(object));
		{
			//TEST
			systemModel.getUserList().add(new User("Brian", "ABCDEDGH12345678", HouseObject.randomColor()));
			notifyModelSubscribers();
			
			//Send to baseStation
			comThread.sendModel();
		}
	}

	public void removeHouseObject(HouseObject object)
	{
		if(systemModel.getHouseObjectList().remove(object));
		{notifyModelSubscribers();}
		
		//Send to baseStation
		comThread.sendModel();
	}
	
	
	//MODIFY MODEL -  USERS*************************************************************
	/*public void addUser(User user)
	{
		systemModel.getUserList().add(user);
		notifyModelSubscribers();
	}
	public void removeUser(User user)
	{
		systemModel.getUserList().remove(user);
		notifyModelSubscribers();
	}*/
	
	
	//LOAD IMAGE FROM HOST************************************************************
	
	public ImageIcon loadImageIconFromHost(String filepath)
	{
		return new ImageIcon(application.getImage(((AppletCommunicationThread)comThread).getCodebase(), filepath));
	}
}
