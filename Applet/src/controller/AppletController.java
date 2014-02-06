package controller;

import javax.swing.JApplet;

import view.Canvas;

public class AppletController extends GenericController
{	
	private static final long serialVersionUID = 1L;
	
	private JApplet application;
	private Canvas canvas;
	private String currentTool;
	
	private boolean isFirstLoad;
	
	//CONSTRUCTOR***********************************************
	public AppletController(JApplet app, Canvas canvas)
	{
		super();
		
		this.application = app;
		this.canvas = canvas;
		
		isFirstLoad = true;
		
		//Add this controller as subscriber
		systemModel.addHouseModelSubscriber(this);
		systemModel.addUsersModelSubscriber(this);

		//Create communication thread
		comThread = new AppletCommunicationThread(this);
		comThread.start();
	}
	
	//MUTATORS AND ACCESSORS*************************************
	
	public JApplet getApplication() 
	{
		return application;
	}	
	
	public Canvas getCanvas() 
	{
		return canvas;
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
		System.out.println("houseModelChanged()");
		
		canvas.repaint();
		
		//Send model to Pi, if not initial loading
		if(!isFirstLoad && comThread.isConnected())
		{
			getComThread().sendHouseObjectList();
		}
		
		isFirstLoad = false;
	}

	@Override
	public void usersModelChanged() 
	{
		System.out.println("usersModelChanged()");
		canvas.repaint();
	}
}
