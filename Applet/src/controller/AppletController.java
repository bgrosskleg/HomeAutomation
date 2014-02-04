package controller;

import view.Canvas;
import model.CurrentModel;

public class AppletController 
{	
	private static CurrentModel CM;
	private static Canvas canvas;
	private static String currentTool;
	

	//REFERENCE TO CANVAS****************************************
	public static void setCanvas(Canvas canvas) 
	{
		AppletController.canvas = canvas;
	}
	
	public static Canvas getCanvas() 
	{
		return canvas;
	}
	
	//CURRENT MODEL**********************************************
	public static CurrentModel getCM() 
	{
		return CM;
	}

	public static void setCM(CurrentModel cM) 
	{
		CM = cM;
		canvas.repaint();
	}	
		
	
	//CURRENT TOOL***********************************************
	public static String getCurrentTool() 
	{
		return currentTool;
	}

	public static void setCurrentTool(String currentTool) 
	{
		AppletController.currentTool = currentTool;
	}

}
