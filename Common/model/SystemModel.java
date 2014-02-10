package model;

import java.io.Serializable;
import java.util.ArrayList;

public class SystemModel implements Serializable
{
	private static final long serialVersionUID = 1L;
			
	private ArrayList<ModelObject> modelObjectList;
	
	//CONSTRUCTOR*************************************************************
	public SystemModel()
	{		
		modelObjectList = new ArrayList<ModelObject>();
	}

	//MUTATORS AND ACCESSORS**************************************************	
	public ArrayList<ModelObject> getModelObjectList()
	{
		return modelObjectList;
	}	
	
	public String toString()
	{
		String result = "System Model\n";
		
		for(ModelObject object : modelObjectList)
		{
			result += ("HouseObject: " + object.toString() + "\n");
		}
				
		return result;
	}
}
