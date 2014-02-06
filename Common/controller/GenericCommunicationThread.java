package controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import model.HouseObject;
import model.User;


public abstract class GenericCommunicationThread extends Thread implements Serializable
{
	private static final long serialVersionUID = 1L;

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
    
    public GenericCommunicationThread(GenericController cntrl, String name)
    {
    	super(name);
    	controller = cntrl;
    }
    
    protected abstract void initializeConnection() throws IOException;
    
    
    
    public void run() 
	{
    	for(;;)
    	{
    		try 
    		{	   			
    			//Await connection
    			do
    			{initializeConnection();}
    			while(!connected);

    			//Ensure sockets are initialized
    			if (objectSocket == null || activeSocket == null || passiveSocket == null || !connected)
    			{
    				System.err.println("One or more sockets not created, server may be down or off.");
    				throw new Exception();
    			}

    			//Ensure streams are initialized
    			if(objectStreamIn == null || objectStreamOut == null || 
    					activeStreamIn == null || activeStreamOut == null ||
    					passiveStreamIn == null || passiveStreamOut == null || !connected)
    			{
    				System.err.println("One or more streams not initialized, server may be down or off.");
    				throw new Exception();
    			}

    			//Infinite loop, receive requests on passive stream
    			while (true) 
    			{
    				// Receive request on passive stream
    				String recieved = null;

    				recieved = passiveStreamIn.readLine();

    				if(recieved == null)
    				{
    					//EOF Stream is closed, likely remote closed socket, trigger catch statement
    					throw new Exception();
    				}

    				System.out.println("Received: |" + recieved + "|");

    				//Process command
    				switch(recieved)
    				{	
	    				case "UPDATEHOUSEOBJECTS":					ArrayList<HouseObject> temp1 = receiveHouseObjectList();
												    				if(temp1 != null)
												    				{
												    					//Update just the house objects
												    					controller.getSystemModel().setHouseObjectList(temp1);
												    				}
												    				break;
	
	    				case "UPDATEUSERS":							ArrayList<User> temp2 = receiveUserList();
												    				if(temp2 != null)
												    				{
												    					//Update just the users
												    					controller.getSystemModel().setUserList(temp2);
												    				}
												    				break;
	
	    				default:									System.out.println("DEFAULT BEHAVIOUR");
	    															break;
	    			}
    			} 
    		}
    		catch (Exception e)
    		{				
    			//Socket error, close all streams and sockets
    			closeStreams();
    		}
    	}
	}	
    
    //OBJECT TRANSMISSION*****************************************************************
    
    public void sendHouseObjectList()
	{		
		//Send object
		try 
		{
			System.out.println("Sending house objects...");
			
			//Notify on command stream
			activeStreamOut.println("UPDATEHOUSEOBJECTS");
			
			//Analyze command stream response
			if(activeStreamIn.readLine().equals("OKAY"))
			{
				//Reset the stream so next write, writes a new complete object, not a reference to the first one
				objectStreamOut.reset();
				
				//Write object to object stream
				objectStreamOut.writeObject(controller.getSystemModel().getHouseObjectList());

				//Analyze command stream response
				if(activeStreamIn.readLine().equals("OKAY"))
				{
					System.out.println("House objects sent successfully.");
				}
				else if(activeStreamIn.readLine().equals("FAIL"))
				{
					System.err.println("House objects did not send successfully.");
				}
				else
				{
					System.err.println("Impossible response!");
				}
			}
			else
			{
				System.err.println("Remote application not ready!");
			}
		} 
		catch (Exception e) 
		{
			System.err.println("Failure sending house objects.");
			e.printStackTrace();
		}
	}
    
    public void sendUserList()
   	{		
   		//Send object
   		try 
   		{
   			System.out.println("Sending users...");
   			
   			//Notify on command stream
   			activeStreamOut.println("UPDATEUSERS");
   			
   			//Analyze command stream response
   			if(activeStreamIn.readLine().equals("OKAY"))
   			{
   				//Reset the stream so next write, writes a new complete object, not a reference to the first one
   				objectStreamOut.reset();
   				
   				//Write object to object stream
   				objectStreamOut.writeObject(controller.getSystemModel().getUserList());

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
   			else
   			{
   				System.err.println("Remote application not ready!");
   			}
   		} 
   		catch (Exception e) 
   		{
   			System.err.println("Failure sending users.");
   			//e.printStackTrace();
   		}
   	}
    
   
	@SuppressWarnings("unchecked")
	public ArrayList<HouseObject> receiveHouseObjectList()
	{
		//Get objects
		try 
		{
			//Notify ready
			passiveStreamOut.println("OKAY");

			System.out.println("Recieving house object list...");

			//Read object off object stream
			Object received = objectStreamIn.readObject();
			
			//Check to ensure it is a model
			if(received instanceof ArrayList)
			{
				//Notify result
				passiveStreamOut.println("OKAY");
	
				System.out.println("House object list recieved successfully.");
	
				return (ArrayList<HouseObject>) received;
			}
			else
			{
				System.err.println("Object not a ArrayList object.");
				throw new Exception();
			}

		} 
		catch (Exception e) 
		{
			passiveStreamOut.println("FAIL");
			System.err.println("Failure receiving house object list.");
			//e.printStackTrace();
			return null;
		}
	} 
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<User> receiveUserList()
	{
		//Get objects
		try 
		{
			//Notify ready
			passiveStreamOut.println("OKAY");

			System.out.println("Recieving user list...");

			//Read object off object stream
			Object received = objectStreamIn.readObject();
			
			//Check to ensure it is a model
			if(received instanceof ArrayList)
			{
				//Notify result
				passiveStreamOut.println("OKAY");
	
				System.out.println("User list recieved successfully.");
	
				return (ArrayList<User>) received;
			}
			else
			{
				System.err.println("Object not a ArrayList object.");
				throw new Exception();
			}

		} 
		catch (Exception e) 
		{
			passiveStreamOut.println("FAIL");
			System.err.println("Failure receiving user list.");
			e.printStackTrace();
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
			{
				System.out.println("Closing objectStreamIn...");
				objectStreamIn.close();
				objectStreamIn = null;
			}
		}
		
		catch(Exception e)
		{
			System.err.println("Failure closing objectStreamIn (object input stream).");
			//e.printStackTrace();
		}
		
		try
		{
			if(objectStreamOut != null)
			{
				System.out.println("Closing objectStreamOut...");
				objectStreamOut.close();
				objectStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing objectStreamOut (object output stream).");
			//e.printStackTrace();
		}
		
		
		//Close active streams
		try
		{
			if(activeStreamIn != null)
			{
				System.out.println("Closing activeStreamIn...");
				activeStreamIn.close();
				activeStreamIn = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		try
		{
			if(activeStreamOut != null)
			{
				System.out.println("Closing activeStreamOut...");
				activeStreamOut.close();
				activeStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeStreamOut (print writer).");
			//e.printStackTrace();
		}
			
		
		//Close passive streams
		try
		{
			if(passiveStreamIn != null)
			{
				System.out.println("Closing passiveStreamIn...");
				passiveStreamIn.close();
				passiveStreamIn = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		try
		{
			if(passiveStreamOut != null)
			{
				System.out.println("Closing passiveStreamOut...");
				passiveStreamOut.close();
				passiveStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		
		//CLOSE SOCKETS****************************************
		//Close object socket
		try
		{
			if(objectSocket != null)
			{
				System.out.println("Closing objectSocket...");
				objectSocket.close();
				objectSocket = null;
			}
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
			{
				System.out.println("Closing activeSocket...");
				activeSocket.close();
				activeSocket = null;
			}
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
			{
				System.out.println("Closing passiveSocket...");
				passiveSocket.close();
				passiveSocket = null;
			}
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
