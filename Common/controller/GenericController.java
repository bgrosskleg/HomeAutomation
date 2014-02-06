package controller;

import java.io.Serializable;

import subscribers.HouseModelSubscriber;
import subscribers.UserModelSubscriber;
import model.SystemModel;

public abstract class GenericController implements Serializable, HouseModelSubscriber, UserModelSubscriber
{	
	private static final long serialVersionUID = 1L;
	
	protected SystemModel systemModel;
	protected GenericCommunicationThread comThread;
	
	
	public GenericController()
	{
		systemModel = new SystemModel();	
	}
	
	public SystemModel getSystemModel()
	{
		return systemModel;
	}
		
	public GenericCommunicationThread getComThread() 
	{
		return comThread;
	}
}
