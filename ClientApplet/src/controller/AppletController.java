package controller;

import javax.swing.ImageIcon;
import view.Canvas;
import view.ClientApplet;

public class AppletController extends GenericController
{		
	private ClientApplet application;
	
	private Canvas canvas;
	
	private String currentTool;
	
	private boolean isFirstLoad;
	
	//CONSTRUCTOR***********************************************
	public AppletController(ClientApplet applet, Canvas canvas)
	{
		super();
		
		this.application = applet;
		
		this.canvas = canvas;
		
		isFirstLoad = true;
		
		//Add this controller as subscriber
		addHouseObjectsModelSubscriber(this);
		addUsersModelSubscriber(this);

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
	public void houseModelChanged() 
	{		
		canvas.repaint();
		
		//Send model to Pi, if not initial loading
		if(!isFirstLoad && comThread.isConnected())
		{
			comThread.sendModel(GenericCommunicationThread.HOUSEOBJECTS);
		}
		
		isFirstLoad = false;
	}

	@Override
	public void userModelChanged() 
	{
		canvas.repaint();
	}
	
	
	public ImageIcon loadImageIconFromHost(String filepath)
	{
		return new ImageIcon(application.getImage(((AppletCommunicationThread)comThread).getCodebase(), filepath));
	}
}
