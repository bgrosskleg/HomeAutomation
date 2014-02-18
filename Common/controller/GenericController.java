package controller;

import interfaces.ModelSubscriber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

import model.ModelObject;
import model.Region;
import model.StaticNode;
import model.SystemModel;
import model.User;

public abstract class GenericController implements ModelSubscriber
{			
	public static final boolean VERBOSE = false;
	
	//List of subscribers
	protected ArrayList<ModelSubscriber> modelSubscriberList;
	
	//Controller objects
	protected SystemModel systemModel;
	protected GenericCommunicationThread comThread;
	
	//TESTING USART HANDLER
	Serial serial;
	
	//CONSTRUCTOR************************************************************
	public GenericController()
	{
		modelSubscriberList = new ArrayList<ModelSubscriber>();
		systemModel = new SystemModel();	
		
						//TESTING CREATING USART HANDLER
						try 
				        {
							serial = SerialFactory.createInstance();
							
							serial.addListener(new SerialDataListener() 
							{
								@Override
								public void dataReceived(SerialDataEvent event) 
								{
									System.out.println(event.getData());
								}
							});
							
						    // wait 1 second before opening
							Thread.sleep(1000);
							
					
					        // open the default serial port provided on the GPIO header
					        serial.open(Serial.DEFAULT_COM_PORT, 9600);
					        
					        // wait 1 second before continuing
							Thread.sleep(1000);
							

								/*BufferedReader br = 
					                      new BufferedReader(new InputStreamReader(System.in));
					 				
								System.out.println("ATDH:");
								String input1 = br.readLine();
								
								System.out.println("ATDL:");
								String input2 = br.readLine();
																
								//10ms guard times
								//10ms command mode timeout, setting sleep to 10 seems to fast to recognize
								
								System.out.println("Sent: +++");
					            serial.write("+++");
					            
					            Thread.sleep(25);
					  
					            System.out.println("Sent: ATDH" + input1);
								serial.write("ATDH" + input1 + '\r');
								
								Thread.sleep(25);
								
					            System.out.println("Sent: ATDL" + input2);
								serial.write("ATDL" + input2 + '\r');
								
								Thread.sleep(25);
								

								for(;;)
								{
									System.out.println("MESSAGE:");
									input1 = br.readLine();
	
									if(input1.equals("exit"))
									{break;}
									
									//10ms guard times
									//10ms command mode timeout
									
									System.out.println("Sent: " + input1);
						            serial.write(input1);
						            
						            Thread.sleep(25);
									
									Thread.sleep(500);
									
								}
									*/	        
				        } 
				        catch (Exception e) 
				        {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
	}
	
	protected synchronized void updateSystemModel(SystemModel newModel)
	{
		//Perform a diff function on the local model and new model and update local rather than overwrite to 
		//keep local model links etc intact
		//O(n^2)
		//Simply trying:
		//systemModel = newModel
		//overwrites all references to objects that might have been made elsewhere (ie lists, or location tracking modules
		//Therefore, adding or removing items is fine, but UPDATING objects should be done by exchanging paramters and values, not 
		//overwriting
		
		if(GenericController.VERBOSE)
		{System.out.println("OLD: " + systemModel.toString());}
		
		//Add missing objects
		for(ModelObject newObject : newModel.getModelObjectList())
		{
			if(!systemModel.getModelObjectList().contains(newObject))
			{
				systemModel.getModelObjectList().add(newObject);
			}
		}
		
		
		//Remove old objects
		//Create list of objects to remove first to avoid concurrent modification
		ArrayList<ModelObject> toBeRemoved = new ArrayList<ModelObject>();
		for(ModelObject oldObject : systemModel.getModelObjectList())
		{
			if(!newModel.getModelObjectList().contains(oldObject))
			{
				toBeRemoved.add(oldObject);
			}
		}
		for(ModelObject object : toBeRemoved)
		{
			systemModel.getModelObjectList().remove(object);
		}
		
		
		//Modify existing object with new details
			//HouseObjects
			for(ModelObject oldObject : systemModel.getModelObjectList())
			{
				for(ModelObject newObject : newModel.getModelObjectList())
				{
					if(oldObject.equals(newObject))
					{
						try 
						{
							oldObject.edit(newObject.getParameters(), newObject.getValues());
						}
						catch (Exception e) 
						{
							System.err.println("Problem editting existing object");
							e.printStackTrace();
						}
					}
				}
			}
		
		if(GenericController.VERBOSE)
		{System.out.println("NEW: " + systemModel.toString());}
		
		notifyModelSubscribers();
	}
		
	
	//MODIFY MODEL***********************************************************
	
	public synchronized void modifyObject(ModelObject object, String [] parameters, Object [] values)
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
	
	
	private void updateStaticNode(StaticNode staticNode, int lightingValue)
	{
		staticNode.setLightingValue(lightingValue);
		
		//Send lighting command
		System.out.println("SEND XBEE COMMAND TO:");
		System.out.println("STATIC NODE: " + staticNode.getMACAddress());
		System.out.println("LIGHTING VALUE: " + lightingValue);
		
			//This timing seems to work for configuring Xbee address
			//10ms guard times
			//10ms command mode timeout, setting sleep to 10 seems to fast to recognize
			
			try
			{
				System.out.println("Sent: +++");
	            serial.write("+++");
	            
	            Thread.sleep(25);
	  
	            System.out.println("Sent: ATDH" + staticNode.getMACAddress().substring(0, 7));
				serial.write("ATDH" + staticNode.getMACAddress().substring(0, 7) + '\r');
				
				Thread.sleep(25);
				
	            System.out.println("Sent: ATDL" + staticNode.getMACAddress().substring(8, 15));
				serial.write("ATDL" + staticNode.getMACAddress().substring(8, 15) + '\r');
				
				Thread.sleep(25);
				
				System.out.println("Sent: ATCN");
				serial.write("ATCN" + '\r');
					
				Thread.sleep(25);
				
				Thread.sleep(250);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	
			
		serial.write("!" + String.valueOf(lightingValue));
	}
	
	
	public ArrayList<ModelObject> getModelObjects()
	{
		return systemModel.getModelObjectList();
	}
	
	public User getUser(String MACAddress)
	{
		 for(ModelObject object : getModelObjects())
		 {
			 if(object instanceof User)
			 {
				 User user = (User) object;
				 if(user.getMACAddress().equals(MACAddress))
				 {
					 return user;
				 }
			 }
		 }
		 return null;
	}
	
	public StaticNode getSensor(String MACAddress)
	{
		 for(ModelObject object : getModelObjects())
		 {
			 if(object instanceof StaticNode)
			 {
				 StaticNode sensor = (StaticNode) object;
				 if(sensor.getMACAddress().equals(MACAddress))
				 {
					 return sensor;
				 }
			 }
		 }
		 return null;
	}
	

	//SUBSCRIBERS/OBSERVERS****************************************************************

	//houseModel subscribers
	public void addModelSubscriber(ModelSubscriber subscriber) 
	{
		modelSubscriberList.add(subscriber);		
	}

	public void removeModelSubscriber(ModelSubscriber subscriber) 
	{
		modelSubscriberList.remove(subscriber);
	}

	public void notifyModelSubscribers() 
	{
		for(ModelSubscriber subscriber : modelSubscriberList)
		{
			subscriber.modelChanged();
		}
	}
}
