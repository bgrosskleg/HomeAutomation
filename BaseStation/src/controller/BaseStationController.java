package controller;

import model.CurrentModel;

public class BaseStationController 
{		
	private static CurrentModel CM;


	//CURRENT MODEL**********************************************
	public static CurrentModel getCM()
	{
		return CM;
	}

	public static void setCM(CurrentModel cM) 
	{
		CM = cM;
	}
	
	
	
	
	

}
