package controller;

import java.util.Observer;

import model.CurrentModel;

public abstract class GenericController implements Observer
{	
	protected CurrentModel CM;
	
	public GenericController()
	{
		CM = new CurrentModel();
		CM.addObserver(this);
	}
	
	public CurrentModel getCM()
	{
		return CM;
	}

	public void setCM(CurrentModel cM)
	{
		//Replace entire model
		CM = cM;
	
		//Add this BaseStationController to model's observer list
		CM.addObserver(this);	
			
		//Notify observers model has changed
		CM.currentModelChanged();
	}
}
