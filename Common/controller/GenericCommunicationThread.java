package controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import model.SystemModel;


public abstract class GenericCommunicationThread extends Thread implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private GenericController controller;
	
	
	//ActiveObject stream
	protected Socket activeObjectSocket;
	protected ObjectOutputStream activeObjectStreamOut;
    protected ObjectInputStream activeObjectStreamIn;
    protected int activeObjectPort;
    
    
    //PassiveObject stream
	protected Socket passiveObjectSocket;
	protected ObjectOutputStream passiveObjectStreamOut;
    protected ObjectInputStream passiveObjectStreamIn;
    protected int passiveObjectPort;
	
    
    //ActiveCommand stream
	protected Socket activeCommandSocket;
	protected PrintWriter activeCommandStreamOut;
    protected BufferedReader activeCommandStreamIn;
    protected int activeCommandPort;
    
    
    //PassiveCommand stream
	protected Socket passiveCommandSocket;
    protected PrintWriter passiveCommandStreamOut;
    protected BufferedReader passiveCommandStreamIn;
    protected int passiveCommandPort;
    
    
    protected boolean connected;
    
    public GenericCommunicationThread(String name, GenericController cntrl)
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
    			{
    				initializeConnection();
    			}
    			while(!connected);
 
    			//Ensure sockets are initialized
    			if (activeObjectSocket == null || activeCommandSocket == null || passiveObjectSocket == null || passiveCommandSocket == null || !connected)
    			{
    				System.err.println("One or more sockets not created, server may be down or off.");
    				throw new Exception();
    			}

    			//Ensure streams are initialized
    			if(activeObjectStreamOut == null || activeObjectStreamIn == null || 
    					activeCommandStreamOut == null || activeCommandStreamIn == null ||
    					passiveObjectStreamOut == null || passiveObjectStreamIn == null ||
    					passiveCommandStreamOut == null || passiveCommandStreamIn == null || !connected)
    			{
    				System.err.println("One or more streams not initialized, server may be down or off.");
    				throw new Exception();
    			}

    			//Infinite loop, receive requests on passive stream
    			while (true) 
    			{
    				// Receive request on passive stream
    				String recieved = null;

    				recieved = passiveCommandStreamIn.readLine();

    				if(recieved == null)
    				{
    					//EOF Stream is closed, likely remote closed socket, trigger catch statement
    					throw new Exception();
    				}

    				if(GenericController.VERBOSE)
    				{System.out.println("Received: |" + recieved + "|");}
    				
    				//Process command
    				switch(recieved)
    				{	
	    				case "MODEL":			//Update just the house objects
	    										controller.updateSystemModel(receiveModel());
												break;
	
	    				default:				System.err.println("DEFAULT BEHAVIOUR");
	    										break;
	    			}
    			} 
    		}
    		catch (Exception e)
    		{				
    			//Socket error, close all streams and sockets
    			closeStreams();
    			//e.printStackTrace();
    		}
    	}
	}	
    
    
    
  //OBJECT TRANSMISSION*****************************************************************

  	public void sendModel()
  	{		
  		//Send object
  		try 
  		{
  			if(GenericController.VERBOSE)
  			{System.out.println("Sending model...");}

  			//Notify on command stream
  			activeCommandStreamOut.println("MODEL");

  			//Analyze command stream response
  			if(activeCommandStreamIn.readLine().equals("OKAY"))
  			{
  				//Reset the stream so next write, writes a new complete object, not a reference to the first one
  				activeObjectStreamOut.reset();

  				//Write object to object stream
  				activeObjectStreamOut.writeObject(controller.systemModel);

  				//Analyze command stream response
  				if(activeCommandStreamIn.readLine().equals("OKAY"))
  				{
  					if(GenericController.VERBOSE)
  					{System.out.println("Model sent successfully.");}
  				}
  				else if(activeCommandStreamIn.readLine().equals("FAIL"))
  				{
  					System.err.println("Model did not send successfully.");
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
  			System.err.println("Failure sending model.");
  			//e.printStackTrace();
  		}
  	}


  	public SystemModel receiveModel()
  	{
  		//Get objects
  		try 
  		{
  			//Notify ready
  			passiveCommandStreamOut.println("OKAY");

  			if(GenericController.VERBOSE)
  			{System.out.println("Recieving model...");}

  			//Read object off object stream
  			SystemModel received = (SystemModel) passiveObjectStreamIn.readObject();
  			
  			//Notify result
  			passiveCommandStreamOut.println("OKAY");

  			if(GenericController.VERBOSE)
  			{System.out.println("Model recieved successfully.");}

  			return received;
  		} 
  		catch (Exception e) 
  		{
  			passiveCommandStreamOut.println("FAIL");
  			System.err.println("Failure receiving model.");
  			//e.printStackTrace();
  			return null;
  		}
  	} 
    
 	
	
	//CLOSE STREAMS************************************************************************************
	
	public void closeStreams()
    {
    	//Socket error, close all streams and sockets
		
		System.out.println("Closing existing streams and sockets...");

		//CLOSE STREAMS********************************************
		
		//Close active object streams
		try 
		{
			if(activeObjectStreamIn != null)
			{
				System.out.println("Closing activeObjectStreamIn...");
				activeObjectStreamIn.close();
				activeObjectStreamIn = null;
			}
		}
		
		catch(Exception e)
		{
			System.err.println("Failure closing activeObjectStreamIn (object input stream).");
			e.printStackTrace();
		}
		
		try
		{
			if(activeObjectStreamOut != null)
			{
				System.out.println("Closing activeObjectStreamOut...");
				activeObjectStreamOut.close();
				activeObjectStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeObjectStreamOut (object output stream).");
			//e.printStackTrace();
		}
		
		//Close passive object streams
		try 
		{
			if(passiveObjectStreamIn != null)
			{
				System.out.println("Closing passiveObjectStreamIn...");
				passiveObjectStreamIn.close();
				passiveObjectStreamIn = null;
			}
		}
		
		catch(Exception e)
		{
			System.err.println("Failure closing passiveObjectStreamIn (object input stream).");
			//e.printStackTrace();
		}
		
		try
		{
			if(passiveObjectStreamOut != null)
			{
				System.out.println("Closing passiveObjectStreamOut...");
				passiveObjectStreamOut.close();
				passiveObjectStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveObjectStreamOut (object output stream).");
			//e.printStackTrace();
		}
		
		
		//Close active command streams
		try
		{
			if(activeCommandStreamIn != null)
			{
				System.out.println("Closing activeCommandStreamIn...");
				activeCommandStreamIn.close();
				activeCommandStreamIn = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeCommandStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		try
		{
			if(activeCommandStreamOut != null)
			{
				System.out.println("Closing activeCommandStreamOut...");
				activeCommandStreamOut.close();
				activeCommandStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing activeCommandStreamOut (print writer).");
			//e.printStackTrace();
		}
			
		
		//Close passive streams command
		try
		{
			if(passiveCommandStreamIn != null)
			{
				System.out.println("Closing passiveCommandStreamIn...");
				passiveCommandStreamIn.close();
				passiveCommandStreamIn = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveCommandStreamIn (buffered reader).");
			//e.printStackTrace();
		}
		
		try
		{
			if(passiveCommandStreamOut != null)
			{
				System.out.println("Closing passiveCommandStreamOut...");
				passiveCommandStreamOut.close();
				passiveCommandStreamOut = null;
			}
		}
		catch(Exception e)
		{
			System.err.println("Failure closing passiveCommandStreamOut (print writer).");
			//e.printStackTrace();
		}
		
		
		//CLOSE SOCKETS****************************************
		//Close active object socket
		try
		{
			if(activeObjectSocket != null)
			{
				System.out.println("Closing activeObjectSocket...");
				activeObjectSocket.close();
				activeObjectSocket = null;
			}
		} 
		catch (Exception e) 
		{
			System.err.println("Failure closing activeObjectSocket.");
			//e.printStackTrace();
		}
		
		//Close passive object socket
		try
		{
			if(passiveObjectSocket != null)
			{
				System.out.println("Closing passiveObjectSocket...");
				passiveObjectSocket.close();
				passiveObjectSocket = null;
			}
		} 
		catch (Exception e) 
		{
			System.err.println("Failure closing passiveObjectSocket.");
			//e.printStackTrace();
		}

		//Close active command socket
		try
		{
			if(activeCommandSocket != null)
			{
				System.out.println("Closing activeCommandSocket...");
				activeCommandSocket.close();
				activeCommandSocket = null;
			}
		}
		catch (Exception e) 
		{
			System.err.println("Failure closing activeCommandSocket.");
			//e.printStackTrace();
		}
		
		//Close passive command socket
		try
		{
			if(passiveCommandSocket != null)
			{
				System.out.println("Closing passiveCommandSocket...");
				passiveCommandSocket.close();
				passiveCommandSocket = null;
			}
		}
		catch (Exception e) 
		{
			System.err.println("Failure closing passiveCommandSocket.");
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
}
