package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;

import model.CurrentModel;

public class BaseStationController extends GenericController
{		
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
		loadModelfromFile();
	}
	
	//CURRENT MODEL*************************************************
	
	@Override
	public void setCM(CurrentModel cM) 
	{
		//Replace entire model (if necessary)
		CM = cM;

		//Add this BaseStationController to model's observer list
		CM.addObserver(this);	
		
		//Notify observers model has changed
		CM.currentModelChanged();
	}
	
	@Override
	public void update(Observable o, Object arg) 
	{
		//Save model state to file
		saveModeltoFile();
				
		//Send entire model to applet if connected		
		if(Firmware.getComThread() != null && Firmware.getComThread().isConnected())
		{Firmware.getComThread().sendModel();}		
	}
		
	
	//MODEL FILE HANDLING*********************************************	
	
  	//Save model
  	public void saveModeltoFile()
    {
  		System.out.println("Saving model to file...");
  		FileOutputStream fos = null;
  		ObjectOutputStream oos = null;
  		
      	try 
      	{
      		fos = new FileOutputStream(modelPath);
      		oos = new ObjectOutputStream(fos);
      		oos.writeObject(getCM());
      		oos.close();
      		fos.close();
      		System.out.println("Model saved to file!");
      	} 
      	catch(FileNotFoundException e)
        {
        	System.err.println("File not found, model not saved.");
          	setCM(new CurrentModel());
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
              	setCM(new CurrentModel());
          	}
      	}	
    }
  	
  	//Load model
    public void loadModelfromFile() 
    {   
    	System.out.println("Loading model from file...");
    	    	
		//Load model from model path
      	FileInputStream fis = null;
  		ObjectInputStream ois = null;
  		
        try 
        {
          	fis = new FileInputStream(modelPath);
            ois = new ObjectInputStream (fis);
            setCM((CurrentModel) ois.readObject());
  			ois.close();
  			fis.close();
  			System.out.println("Model loaded from file!");
  		} 
        catch(FileNotFoundException e)
        {
        	System.err.println("File not found, creating new model.");
          	setCM(new CurrentModel());
          	saveModeltoFile();
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
          	finally
          	{
          		System.err.println("Loading from file failed, creating new model.");
              	setCM(new CurrentModel());
              	saveModeltoFile();
          	}	
  		}
  	}
}
