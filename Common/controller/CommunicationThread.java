package controller;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import model.CanvasObject;
import model.CanvasObjectList;
import model.CurrentModel;
import model.User;


public abstract class CommunicationThread extends Thread
{
	protected GenericController controller;
	
	protected Socket objectSocket;
	protected Socket activeSocket;
	protected Socket passiveSocket;
	
    protected int objectPort;
    protected int activePort;
    protected int passivePort;
    
    protected ObjectOutputStream objectStreamOut;
    protected ObjectInputStream objectStreamIn;

    protected PrintWriter activeStreamOut;
    protected BufferedReader activeStreamIn;
    
    protected PrintWriter passiveStreamOut;
    protected BufferedReader passiveStreamIn;	
    
    protected boolean connected;
    
    public CommunicationThread(GenericController cntrl, String name)
    {
    	super(name);
    	controller = cntrl;
    }
    
    public abstract void initializeConnection();
    
    public void run() 
	{
    	//Create connection
    	initializeConnection();
    			
		//Ensure sockets are initialized
        if (objectSocket == null || activeSocket == null || passiveSocket == null || !connected)
        {
        	System.err.println("One or more sockets not created, server may be down or off.");
        	
        	//Initialize the next connection
			initializeConnection();
        }
        
        //Ensure streams are initialized
        if(objectStreamIn == null || objectStreamOut == null || 
        		activeStreamIn == null || activeStreamOut == null ||
        		passiveStreamIn == null || passiveStreamOut == null || !connected)
		{
			System.err.println("One or more streams not initialized, server may be down or off.");
			
			//Initialize the next connection
			initializeConnection();
		}
                
        //Infinite loop, receive requests on passive stream
        while (true) 
        {
        	// Receive request on passive stream
        	String recieved = null;
			try 
			{		
				recieved = passiveStreamIn.readLine();
				
				if(recieved == null)
				{
					//EOF Stream is closed, likely remote closed socket, trigger catch statement
					System.err.println("Socket closed, throw exception...");
					throw new Exception();
				}
				
				System.out.println("Received: |" + recieved + "|");
				
				//Process command
	        	switch(recieved)
		        {	
	        		case "REQUESTMODEL":						sendModel();
	        													break;
	        													
		        	case "SENDINGMODEL":						controller.setCM(receiveModel());
		        												break;
      	
		        	case "ADDOBJECT":							controller.getCM().addCanvasObject(receiveObject());
		        												break;
		        	
		        	case "REMOVEOBJECT":						controller.getCM().removeCanvasObject(receiveObject());
		        												break;
		        																				
		        	case "SENDUSERS":							controller.getCM().setUsers(receiveUsers());
		        												break;
		        												
		        	default:									System.out.println("DEFAULT BEHAVIOUR");
		        												break;
		        }
			} 
			catch (Exception e)
			{				
				//Socket error, close all streams and sockets
				closeStreams();
				
				//Wait to let server close as not to re-initializeConnection too soon
				try 
				{
					Thread.sleep(1000);
				} catch (Exception e1) {
					System.out.println("Failure in waiting to re-connect");
					e1.printStackTrace();
				}
				
				//Initialize the next connection
				initializeConnection();
			}
        }	
	}	
    
    //MODEL TRANSMISSION*****************************************************************
    
    public void sendModel()
	{
		//Ensure streams have been initialized
		if(activeStreamOut == null || activeStreamIn == null || objectStreamOut == null || !connected )
		{
			System.err.println("Active or object stream not initialized, server may be off...");
			return;
		}
		
		//Send model
		try 
		{
			System.out.println("Sending model...");
			
			//Notify applet on command stream
			activeStreamOut.println("SENDINGMODEL");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(controller.getCM());

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("Model sent successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("Model did not send successfully.");
				}
				else
				{
					System.err.println("Impossible response!");
				}
			}
		} 
		catch (Exception e1) 
		{
			//Must check if null because closing streams might have been what caused the initial exception
			if(activeStreamOut != null)
			{activeStreamOut.println("FAIL");}
			System.err.println("Failure sending model.");
			//e1.printStackTrace();
		}
	}
    
    public CurrentModel receiveModel()
	{
		//Ensure streams have been initialized
		if(passiveStreamOut == null || passiveStreamIn == null || objectStreamIn == null || !connected)
		{
			System.err.println("Command or object stream not initialized, server may be off...");
			return null;
		}
		
		//Receive model
		try 
		{
			//Notify applet ready
			passiveStreamOut.println("OKAY");

			System.out.println("Recieving model...");
									
			//Read model off object stream
			CurrentModel received = (CurrentModel) objectStreamIn.readObject();
				
			//Notify applet result
			passiveStreamOut.println("OKAY");
				
			System.out.println("Model recieved.");
			return received;
		} 
		catch (Exception e1) 
		{
			passiveStreamOut.println("FAIL");
			System.err.println("Failure receiving model.");
			//e1.printStackTrace();
			return null;
		}
	} 
    
    
    
    //OBJECT TRANSMISSION*****************************************************************
	
	public void addObject(CanvasObject object)
	{
		//Ensure streams have been initialized
		if(activeStreamOut == null || activeStreamIn == null || objectStreamOut == null || !connected)
		{
			System.err.println("Active or object stream not initialized, server may be off...");
			return;
		}
		
		//Send object to be added
		try 
		{
			System.out.println("Sending object to be added...");
			
			//Notify server on command stream
			activeStreamOut.println("ADDOBJECT");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(object);

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("Object sent successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("Object did not send successfully.");
				}
				else
				{
					System.err.println("Impossible response!");
				}
			}
		} 
		catch (Exception e1) 
		{
			//Must check if null because closing streams might have been what caused the initial exception
			if(activeStreamOut != null)
			{activeStreamOut.println("FAIL");}
			System.err.println("Failure sending object.");
			//e1.printStackTrace();
		}
	}
	
	public void removeObject(CanvasObject object)
	{
		//Ensure streams have been initialized
		if(activeStreamOut == null || activeStreamIn == null || objectStreamOut == null || !connected)
		{
			System.err.println("Active or object stream not initialized, server may be off...");
			return;
		}
		
		//Send object to be removes
		try 
		{
			System.out.println("Sending object to be removed...");
			
			//Notify server on command stream
			activeStreamOut.println("REMOVEOBJECT");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(object);

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("Object sent successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("Object did not send successfully.");
				}
				else
				{
					System.err.println("Impossible response!");
				}
			}
		} 
		catch (Exception e1) 
		{
			//Must check if null because closing streams might have been what caused the initial exception
			if(activeStreamOut != null)
			{activeStreamOut.println("FAIL");}
			System.err.println("Failure sending object.");
			//e1.printStackTrace();
		}
	}
	
	
	public CanvasObject receiveObject()
	{
		//Ensure streams have been initialized
		if(passiveStreamOut == null || passiveStreamIn == null || objectStreamIn == null || !connected)
		{
			System.err.println("Command or object stream not initialized, server may be off...");
			return null;
		}

		//Get model from applet
		try 
		{
			//Notify applet ready
			passiveStreamOut.println("OKAY");

			System.out.println("Recieving object...");

			//Read model off object stream
			CanvasObject received = (CanvasObject) objectStreamIn.readObject();

			//Notify applet result
			passiveStreamOut.println("OKAY");

			System.out.println("Object recieved successfully.");

			return received;

		} 
		catch (Exception e1) 
		{
			passiveStreamOut.println("FAIL");
			System.err.println("Failure receiving object.");
			//e1.printStackTrace();
			return null;
		}
	} 
	
	
	
	//USERS TRANSMISSION******************************************************************************
	
	public void sendUsers()
	{
		//Ensure streams have been initialized
		if(activeStreamOut == null || activeStreamIn == null || objectStreamOut == null || !connected)
		{
			System.err.println("Active or object stream not initialized, server may be off...");
			return;
		}
		
		//Send users
		try 
		{
			System.out.println("Sending object to be removed...");
			
			//Notify server on command stream
			activeStreamOut.println("SENDUSERS");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(controller.getCM().getUsers());

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("Users sent successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("Users did not send successfully.");
				}
				else
				{
					System.err.println("Impossible response!");
				}
			}
		} 
		catch (Exception e1) 
		{
			//Must check if null because closing streams might have been what caused the initial exception
			if(activeStreamOut != null)
			{activeStreamOut.println("FAIL");}
			System.err.println("Failure sending users.");
			//e1.printStackTrace();
		}
	}
	
	public CanvasObjectList<User> receiveUsers()
	{
		//Ensure streams have been initialized
		if(passiveStreamOut == null || passiveStreamIn == null || objectStreamIn == null || !connected)
		{
			System.err.println("Command or object stream not initialized, server may be off...");
			return null;
		}

		//Get users
		try 
		{
			//Notify applet ready
			passiveStreamOut.println("OKAY");

			System.out.println("Recieving users...");

			//Read model off object stream
			@SuppressWarnings("unchecked")
			CanvasObjectList<User> received = (CanvasObjectList<User>) objectStreamIn.readObject();

			//Notify applet result
			passiveStreamOut.println("OKAY");

			System.out.println("Users recieved successfully.");

			return received;
		} 
		catch (Exception e1) 
		{
			passiveStreamOut.println("FAIL");
			System.err.println("Failure receiving object.");
			//e1.printStackTrace();
			return null;
		}
	} 
	
	
	
	//CLOSE STREAMS************************************************************************************
	
	public void closeStreams()
    {
    	//Socket error, close all streams and sockets
		
		System.out.println("Closing existing streams and sockets...");

		//CLOSE STREAMS********************************************
		
		//Close object streams
		try 
		{
			if(objectStreamIn != null)
			{objectStreamIn.close();}
		}
		
		catch(Exception e)
		{
			System.err.println("Failure closing objectStreamIn (object input stream).");
			//e.printStackTrace();
		}
		
		try
		{
			if(objectStreamOut != null)
			{objectStreamOut.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing objectStreamOut (object output stream).");
			//e.printStackTrace();
		}
		
		
		//Close active streams
		try
		{
			if(activeStreamOut != null)
			{activeStreamOut.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		try
		{
			if(activeStreamIn != null)
			{activeStreamIn.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		
		//Close passive streams
		try
		{
			if(passiveStreamOut != null)
			{passiveStreamOut.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		try
		{
			if(passiveStreamIn != null)
			{passiveStreamIn.close();}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		
		//CLOSE SOCKETS****************************************
		//Close object socket
		try
		{
			if(objectSocket != null)
			{objectSocket.close();}
		} 
		catch (Exception e) 
		{
			System.err.println("Failure closing objectSocket.");
			//e.printStackTrace();
		}
		
		//Close active socket
		try
		{
			if(activeSocket != null)
			{activeSocket.close();}
		}
		catch (Exception e) 
		{
			System.err.println("Failure closing activeSocket.");
			//e.printStackTrace();
		}
		
		//Close passive socket
		try
		{
			if(passiveSocket != null)
			{passiveSocket.close();}
		}
		catch (Exception e) 
		{
			System.err.println("Failure closing passiveSocket.");
			//e.printStackTrace();
		}
		
		System.out.println("Existing streams and sockets closed.");
		connected = false;
    }
	
	

	//NETWORKING PARAMETERS*******************************************************
	
	//Connected status
    public boolean isConnected() {
    	return connected;
    }
	
	
  	//PORT NUMBERS**************************************************
    
    //Object port number
	public int getObjectPort() {
		return objectPort;
	}
	
	//Active port number
	public int getActivePort() {
		return activePort;
	}
		
	//Passive port number
	public int getPassivePort() {
		return passivePort;
	}
    	
  	
  	//SOCKETS*****************************************************
  	
  	//Object socket
  	public Socket getObjectSocket() {
  		return objectSocket;
  	}  	
  	
  	//Active socket
  	public Socket getActiveSocket() {
  		return activeSocket;
  	}
  	
  	//Passive socket
  	public Socket getPassiveSocket() {
  		return passiveSocket;
  	}
  	
  	
  	//TCP STREAMS*************************************************
  	
  	//Object stream to client
  	public ObjectOutputStream getObjectStreamOut() {
  		return objectStreamOut;
  	}

  	//Object stream from client
  	public ObjectInputStream getObjectStreamIn() {
  		return objectStreamIn;
  	}
  	
  	//Active stream out
  	public PrintWriter getActiveStreamOut() {
  		return activeStreamOut;
  	}

  	//Active stream in
  	public BufferedReader getActiveStreamIn() {
  		return activeStreamIn;
  	}
  	
  	//Passive stream out
  	public PrintWriter getPassiveStreamOut() {
  		return passiveStreamOut;
  	}

  	//Passive steam in
  	public BufferedReader getPassiveStreamIn() {
  		return passiveStreamIn;
  	}
}
