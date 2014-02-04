package controller;

import java.util.Observer;

import model.CurrentModel;

public abstract class GenericController implements Observer
{	
	protected CurrentModel CM;
	
	public GenericController()
	{
		CM = new CurrentModel();
	}
	
	public CurrentModel getCM()
	{
		return CM;
	}

	public abstract void setCM(CurrentModel cM);
}
