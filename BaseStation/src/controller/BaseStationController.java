package controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.CurrentModel;

public class BaseStationController 
{		
	private static CurrentModel CM;
	private static String modelPath;


	//CURRENT MODEL**********************************************
	public static CurrentModel getCM()
	{
		return CM;
	}

	public static void setCM(CurrentModel cM) 
	{
		CM = cM;
	}
	
//MODEL FILE HANDLING*********************************************
  	
  	//Model path
  	public static String getModelPath() {
  		return modelPath;
  	}
  	
  	public static void setModelPath(String path) {
  		modelPath = path;
  	}
  	
  	//Save model
  	public static void saveModeltoFile()
    {
  		System.out.println("Saving model to file...");
  		FileOutputStream fos = null;
  		ObjectOutputStream oos = null;
  		
      	try 
      	{
      		fos = new FileOutputStream(modelPath);
      		oos = new ObjectOutputStream(fos);
      		oos.writeObject(BaseStationController.getCM());
      		oos.close();
      		fos.close();
      		System.out.println("Model saved to file!");
      	} 
      	catch (Exception e1) 
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
  	    		
  	    		System.out.println("Existing file stream closed.");
  			} 
      		catch (Exception e) 
      		{
  				System.err.println("Failure closeing file stream");
  				e.printStackTrace();
      		}
      		
      		System.err.println("Model not saved to file!");
      		e1.printStackTrace();
      	}	
    }
  	
  	//Load model
    public static CurrentModel loadModelfromFile() 
    {   
    	//Determine model path
    	System.out.println("Loading model from file...");
    	
    	//If being run locally by eclipse, load the model from local path
		//If being run on webserver on Pi, load model from the /var/www path
		if(CommunicationThread.getObjectSocket().getLocalAddress().toString().equals("/127.0.0.1"))
		{
			System.out.println("modelPath = C:/Users/Brian Grosskleg/Desktop/model.ser");
			BaseStationController.setModelPath("C:/Users/Brian Grosskleg/Desktop/model.ser");
		}
		else if(CommunicationThread.getObjectSocket().getLocalAddress().toString().equals("/172.16.1.85"))
		{
			System.out.println("modelPath = var/www/model.ser");
			BaseStationController.setModelPath("/var/www/model.ser");
		}
		else
		{
			System.err.println("Could not set model path, creating new model.");
			return new CurrentModel();	
		}
    	
    	
		//Load model from model path
		
      	FileInputStream fis = null;
  		ObjectInputStream ois = null;
  		
        try 
        {
          	fis = new FileInputStream(modelPath);
            ois = new ObjectInputStream (fis);
  			CurrentModel result = (CurrentModel)ois.readObject();
  			ois.close();
  			fis.close();
  			System.out.println("Model loaded from file!");
  			return result;
  		} 
        catch (Exception e1) 
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
          		System.out.println("Existing file stream closed.");
  			} 
      		catch (Exception e) 
      		{
  				System.err.println("Failure closeing file stream");
  				e.printStackTrace();
      		}
      		
          	System.err.println("Model not loaded from file, creating new model.");
  			return new CurrentModel();
  		}
  	}
}
