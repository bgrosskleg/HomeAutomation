package controller;

import java.util.Observable;

import javax.swing.JApplet;

import view.Canvas;

public class AppletController extends GenericController
{	
	private JApplet application;
	private Canvas canvas;
	private String currentTool;
	

	public AppletController(JApplet app, Canvas canvas)
	{
		super();
		
		this.application = app;
		this.canvas = canvas;
	}
	
	//CURRENT MODEL*********************************************
	
	@Override
	public void update(Observable o, Object arg) 
	{
		//Repaint canvas
		if(canvas != null)
		{canvas.repaint();}
	}
	

	//CURRENT TOOL***********************************************
	public String getCurrentTool() 
	{
		return currentTool;
	}

	public void setCurrentTool(String currentTool) 
	{
		this.currentTool = currentTool;
	}

	
	//REFERENCE TO CANVAS****************************************	
	public Canvas getCanvas() 
	{
		return canvas;
	}
	
	
	//REFERENCE TO APPLICATION***********************************
	public JApplet getApplication() {
		return application;
	}
}
