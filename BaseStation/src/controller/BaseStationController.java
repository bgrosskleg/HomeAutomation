package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.SystemModel;

public class BaseStationController extends GenericController
{		
	private static final long serialVersionUID = 1L;
	
	private String modelPath;
	
	public BaseStationController()
	{
		super();
		
		//Path to model
		//Computer: = 	modelPath = C:/Users/Brian Grosskleg/Desktop/model.ser
		//Pi: 			modelPath = /var/www/model.ser
		modelPath = "C:/Users/Brian Grosskleg/Desktop/model.ser";
		//modelPath = "/var/www/model.ser";
		
		//Load model from file, create new one if failed
		SystemModel temp = readModelFromFile();
		if(temp == null)
		{
			System.err.println("Creating new model.");
			temp = new SystemModel();
		}
		systemModel = temp;
		
		
		//Add this controller as subscriber
		systemModel.addHouseModelSubscriber(this);
		systemModel.addUsersModelSubscriber(this);
		
		
		//Create communication thread
		comThread = new BaseStationCommunicationThread(this);
		comThread.start();
	}
		
	//MODEL FILE HANDLING*********************************************	
	
  	//Save model
  	public void saveModelToFile()
    {
  		System.out.println("Saving model to file...");
  		FileOutputStream fos = null;
  		ObjectOutputStream oos = null;
  		
      	try 
      	{
      		fos = new FileOutputStream(modelPath);
      		oos = new ObjectOutputStream(fos);
      		oos.writeObject(getSystemModel());
      		oos.close();
      		fos.close();
      		System.out.println("Model saved to file!");
      	} 
      	catch(FileNotFoundException e)
        {
        	System.err.println("File not found: " + modelPath);
        }
      	catch (Exception e) 
      	{
      		//Close any open streams
      		try
      		{
  	    		if(fos != null)
  	    		{
  	    			fos.close();
  	    		}
  	    		
  	    		if(oos != null)
  	    		{
  	    			oos.close();
  	    		}
  			} 
      		catch (Exception e1) 
      		{
  				e1.printStackTrace();
      		}
          	finally
          	{
          		System.err.println("Saving model to file failed, model not saved.");
          	}
      	}	
    }
  	
  	//Load model
    public SystemModel readModelFromFile() 
    {   
    	System.out.println("Loading model from file...");
    	    	
		//Load model from model path
      	FileInputStream fis = null;
  		ObjectInputStream ois = null;
  		
        try 
        {
          	fis = new FileInputStream(modelPath);
            ois = new ObjectInputStream (fis);
            SystemModel read = (SystemModel) ois.readObject();
  			ois.close();
  			fis.close();
  			System.out.println("Model loaded from file!");
  			return read;
  		} 
        catch(FileNotFoundException e)
        {
        	System.err.println("File not found: " + modelPath);
          	return null;
        }
        catch (Exception e) 
        {
          	//Close any open streams
          	try
          	{
          		if(ois != null)
          		{
          			ois.close();
          		}
      		
          		if(fis != null)
          		{
          			fis.close();
          		}
  			} 
      		catch (Exception e1) 
      		{
  				e1.printStackTrace();
      		}

          	System.err.println("Loading from file failed.");
          	e.printStackTrace();
          	return null;
  		}
  	}

	@Override
	public void houseModelChanged() 
	{
		System.out.println("houseModelChanged()");
		
		//Save model to file
		saveModelToFile();
	}

	@Override
	public void usersModelChanged() 
	{
		System.out.println("userModelChanged()");
		
		//Save model to file
		saveModelToFile();
		
		//Send update to applet
		if(comThread.isConnected())
		{comThread.sendUserList();}
	}
}
