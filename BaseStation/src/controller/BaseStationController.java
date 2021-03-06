package controller;

import interfaces.ModelSubscriber;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import tracker.LocationThread;
import model.ModelObject;
import model.Region;
import model.StaticNode;
import model.SystemModel;
import model.User;

/**
 * Controller the runs on RaspberryPi basestation
 * @author Brian Grosskleg
 *
 */
public class BaseStationController extends GenericController implements ModelSubscriber
{		
	
	private String modelPath;
	
	//Serial serial;
	
	LocationThread locThread;
	
	/**
	 * Create baseStation Controller
	 */
	public BaseStationController()
	{
		super();
		
		//Path to model
		//Computer: = 	modelPath = C:/Users/Brian Grosskleg/Desktop/model.ser
		//Pi: 			modelPath = /var/www/model.ser
		//modelPath = "C:/Users/Brian Grosskleg/Desktop/model.ser";
		modelPath = "/var/www/model.ser";
		
		//Load model from file, create new one if failed
		SystemModel temp = readModelFromFile();
		if(temp == null)
		{
			System.err.println("Creating new model.");
			temp = new SystemModel();
		}
		updateSystemModel(temp);
		
		
		//Add this controller as subscriber
		addModelSubscriber(this);
		
		
		//Create communication thread
		comThread = new BaseStationCommunicationThread(this);
		comThread.start();
		
		// Create the location controlling thread
		locThread = new LocationThread(this);
		locThread.start();
		
		
		//Create Xbee serial communication
		/*try 
        {
			serial = SerialFactory.createInstance();
			
			serial.addListener(new SerialDataListener() 
			{
				String temp = "";
				@Override
				public void dataReceived(SerialDataEvent event) 
				{
					temp += event.getData();
					if(temp.length() > 100)
						System.out.println(temp);
					//System.out.println(event.getData());
				}
			});
			
		    // wait 1 second before opening
			Thread.sleep(1000);
			
	
	        // open the default serial port provided on the GPIO header
	        serial.open(Serial.DEFAULT_COM_PORT, 9600);
	        
	        // wait 1 second before continuing
			Thread.sleep(1000);        
        } 
        catch (Exception e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	/**
	 * Called everytime the model is changed
	 * in this case saves the model to disk
	 */
	@Override
	public void modelChanged() 
	{		
		//Save model to file
		saveModelToFile();
	}
	
		
	//MODEL FILE HANDLING*********************************************	
	
  	//Save model
	/**
	 * Saves the model as a serialized binary file at the filepath location set in constructor
	 */
  	public void saveModelToFile()
    {
  		if(GenericController.VERBOSE)
  		{System.out.println("Saving model to file...");}
  		
  		FileOutputStream fos = null;
  		ObjectOutputStream oos = null;
  		
      	try 
      	{
      		fos = new FileOutputStream(modelPath);
      		oos = new ObjectOutputStream(fos);
      		
      		oos.writeObject(systemModel);
      		
      		oos.close();
      		oos = null;
      		fos.close();
      		fos = null;
      		
      		if(GenericController.VERBOSE)
      		{System.out.println("Model saved to file!");}
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
  	    			fos = null;
  	    		}
  	    		
  	    		if(oos != null)
  	    		{
  	    			oos.close();
  	    			oos = null;
  	    		}
  			} 
      		catch (Exception e1) 
      		{
  				e1.printStackTrace();
      		}
          	finally
          	{
          		e.printStackTrace();
          		System.err.println("Saving model to file failed, model not saved.");
          	}
      	}	
    }
  	
  	//Load model
  	/**
  	 * Loads model from filepath set in constructor, returns null if not found
  	 * @return the SystemModel that is stored at the filepath, null if not found
  	 */
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
  			ois = null;
  			fis.close();
  			fis = null;
  			
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
          			ois = null;
          		}
      		
          		if(fis != null)
          		{
          			fis.close();
          			fis = null;
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


	//MODIFY OBJECT*************************************************************************************

    /**
     * Modify the object with the parameters and values
     * The basestation version is almost identical to the applet version but adds the updateOccupancy function
     */
	@Override
	public synchronized void modifyObject(ModelObject object, String[] parameters, Object[] values)
	{
		if(systemModel.getModelObjectList().contains(object))
		{
			try 
			{
				if(object.edit(parameters, values))
				{					
					//Re-determine occupied regions
					if(object instanceof User)
					{updateOccupancy();}
					
					//Notify local subscribers
					notifyModelSubscribers();
					
					//Send model to other end
					comThread.sendModel();
				}
			} 
			catch (Exception e) 
			{
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("Object not part of systemModel list!");
		}
	}
	
	/**
	 * This function is called if a user is editted (likely editted a location)
	 */
	private synchronized void updateOccupancy()
	{
		//O(n^2)
		
		for(ModelObject object1 : systemModel.getModelObjectList())
		{
			if(object1 instanceof Region)
			{
				Region region = (Region) object1;
				
				for(ModelObject object2 : systemModel.getModelObjectList())
				{
					if(object2 instanceof User)
					{
						User user = (User) object2;
						
						if(region.getPath().contains(user.getLocation()))
						{
							//If the user is within the region
							
							//Add user to region
							if(!region.getUsers().contains(user))
							{
								region.getUsers().add(user);
								
								//Set region lighting value to highest user
								if(region.getLightingValue() < user.getPreferredLightingValue())
								{
									region.setLightingValue(user.getPreferredLightingValue());
								}
								
								//Notify all regions static nodes
								for(StaticNode staticNode : region.getStaticNodes())
								{
									updateStaticNode(staticNode, region.getLightingValue());
								}
							}
						}
						else
						{
							//If the user is not within the region
							
							//Remove user from region
							if(region.getUsers().contains(user))
							{
								region.getUsers().remove(user);
								
								//Set region lighting to next highest value, off if empty
								if(region.getUsers().isEmpty())
								{
									region.setLightingValue(0);
								}
								else
								{
									for(User user2 : region.getUsers())
									{
										if(region.getLightingValue() < user2.getPreferredLightingValue())
										{
											region.setLightingValue(user2.getPreferredLightingValue());
										}
									}
								}
								
								//Notify all regions static nodes
								for(StaticNode staticNode : region.getStaticNodes())
								{
									updateStaticNode(staticNode, region.getLightingValue());
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Responsible for transmitting serial out
	 * @param staticNode	the staticNode to send the command to	
	 * @param lightingValue	the lightingValue to send
	 */
	private void updateStaticNode(StaticNode staticNode, int lightingValue)
	{
		staticNode.setLightingValue(lightingValue);
		
		//Send lighting command
		/*System.out.println("SEND XBEE COMMAND TO:");
		System.out.println("STATIC NODE: " + staticNode.getMACAddress());
		System.out.println("LIGHTING VALUE: " + lightingValue);*/
		
		//This timing seems to work for configuring Xbee address
		//10ms guard times
		//10*100 = 1000ms command mode timeout
		//75ms delay seems stable

		//no '\r' required for "+++"
		//'\r' required to end all other commands

		try
		{		
			String message;
			
			if(lightingValue == 100)
			{
				message = ("!" + String.valueOf(lightingValue));
			}
			else if(lightingValue < 100 && lightingValue > 9)
			{
				message = ("!0" + String.valueOf(lightingValue));
			}
			else if(lightingValue < 10)
			{
				message = ("!00" + String.valueOf(lightingValue));
			}
			else
			{
				throw new Exception("Impossible case");
			}
			
			locThread.xbee.SendMessage(staticNode.getMACAddress(), message);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
