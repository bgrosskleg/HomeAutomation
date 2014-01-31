package firmware;
/*
 * Extended from Oracle's Network Client Applet Example to ensure network operation
 * Written by: Brian Grosskleg
 * Date: Jan. 4 2014
 */ 

/**
 * Java(TM) SE 6 version.
 */

//Must omit package in order to compile nicely on RPI command line

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.net.ServerSocket;

import javax.swing.Timer;

import model.CurrentModel;
import model.Point;
import model.Sensor;
import controller.ServerController;

class ServerThread extends Thread 
{   	
    ServerThread() 
    {
        super("TrackingGUIServer");
        
        //Create one serverSocket for each command and object steams
        //Recommended to seperate commands (Strings) from object (CurrentModel) to avoid framing screw ups
        //http://stackoverflow.com/questions/11199471/multiple-object-streams-over-a-single-socket
        try 
    	{
			ServerController.setCommandServerSocket(new ServerSocket(ServerController.getCommandPort()));
			ServerController.setObjectServerSocket(new ServerSocket(ServerController.getObjectPort()));
		} 
    	catch (Exception e1) 
    	{
			System.err.println("Failure creating server sockets!");
			e1.printStackTrace();
			return;
		}
    	
        System.out.println("Server command stream listening on port: " + ServerController.getCommandPort());
        System.out.println("Server object stream listening on port: " + ServerController.getObjectPort());
        
        
        ServerController.initializeSocket();
        
        
        //Creates a timer to save model to file every 60 seconds
        int delay = 60000; //milliseconds

        ActionListener taskPerformer = new ActionListener() {

        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{
        		//Save CurrentModel to RPi card
        		ServerController.saveModeltoDisk();
        	}
        };
        new Timer(delay, taskPerformer).start();
        
        //Fetch RSSI numbers from hardware
        //...
        
        
        //Creates a timer to triangulate user position 
      
        int delay2 = 100; //milliseconds

        ActionListener taskPerformer2 = new ActionListener() {
        	//boolean moveRight = true;
                	
        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{        		
        		/*
    					User user = ServerController.getCM().users.get(0);
    					
		        		//Move userA
		        		int stepSize = ServerController.getCM().gridSize;
		    			
		    			if(moveRight && user.location.x < 175)
		    			{
		    				user.location.x += stepSize;
		    			}
		    			else if(moveRight && user.location.x >= 175)
		    			{
		    				moveRight = false;
		    				user.location.x -= stepSize;
		    			}
		    			else if(!moveRight && user.location.x > 25)
		    			{
		    				user.location.x -= stepSize;
		    			}
		    			else if(!moveRight && user.location.x <= 25)
		    			{
		    				moveRight = true;
		    				user.location.x += stepSize;
		    			}
        		 */
        		
        		
        		
        		for(Point point : ServerController.getCM().points)
        		{
        			point.weight = 0;
        		}
        		Point max = new Point(new Point2D.Double(0,0));
            	double tolerance = 0.10;
        		
        		for(Sensor sensor : ServerController.getCM().sensors)
        		{
        				
                	for(Point point : ServerController.getCM().points)
                	{
                		if(point.location.distance(sensor.location) > sensor.RSSI*(1-tolerance) && point.location.distance(sensor.location) < sensor.RSSI*(1+tolerance))
                		{
                			point.weight += 5;
                		}
                	}
        		}
        		
        		for(Point point : ServerController.getCM().points)
        		{
        			if(point.weight > max.weight)
        			{
        				max = point;
        			}
        		}
        		
        		ServerController.getCM().users.get(0).setLocation(max.location);
        		
        	}
        };
        new Timer(delay2, taskPerformer2).start();
    }

    public void run() 
    {
        if (ServerController.getCommandSocket() == null || ServerController.getObjectSocket() == null)
        {
        	System.err.println("Sockets not created...");
            return;
        }
        
        if(ServerController.getInFromClient() == null)
		{
			System.out.println("Input stream not initialized...");
			return;
		}
                
        while (true) 
        {
        	// Receive request
        	String received = null;
			try {
								
				received = ServerController.getInFromClient().readLine();
				
				if(received == null)
				{
					//EOF Stream is closed, likely remote closed socket, trigger catch statement
					System.out.println("Client closed socket, throw exception...");
					throw new Exception();
				}
				
				System.out.println("Received: |" + received + "|");
				
	        	switch(received)
		        	{
		        	case "REQUESTMODEL":					try {
																	System.out.println("Sending model to client...");
																	ServerController.getOutToClient().println("SENDINGMODEL");
																	
																	ServerController.getOOS().reset();
																	ServerController.getOOS().writeObject(ServerController.getCM());
																	//ServerController.getOOS().writeUnshared(ServerController.getCM());
																										
																	if(ServerController.getInFromClient().readLine().equals("OKAY"))
																	{
																		System.out.println("Model sent complete.");
																	}
																	else if(ServerController.getInFromClient().readLine().equals("FAIL"))
																	{
																		System.out.println("Model did not send correctly.");
																	}
																	else
																	{
																		System.err.println("Impossible response: " + ServerController.getInFromClient().readLine());
																	}
																	
																} catch (Exception e) {
																	System.out.println("Failure sending model to client!");
																	e.printStackTrace();
																}
		        	
		        												break;
		        												
		
		        	case "SENDINGMODEL":					try {
																	System.out.println("Receiving model...");
																	ServerController.getOutToClient().println("OKAY");
																	ServerController.setCM((CurrentModel) ServerController.getOIS().readObject());
																	//ServerController.setCM((CurrentModel) ServerController.getOIS().readUnshared());
																	ServerController.getOutToClient().println("OKAY");
													    			System.out.println("Model recieved.");
													    			
			        												//Save CurrentModel to RPi card
			        								        		ServerController.saveModeltoDisk();
			        								        		
																} catch (Exception e) {
																	ServerController.getOutToClient().println("FAIL");
																	System.out.println("Failure getting model from client!");
																	e.printStackTrace();
																} 
		        												break;
		        	
		        	default:	
		        												break;
		
		        	}
			} 
			catch (Exception e2)
			{
				System.out.println("Socket Exception, closing existing socket...");

				try 
				{
					if(ServerController.getOIS() != null)
					{ServerController.getOIS().close();}
				}
				
				catch(Exception e)
				{
					System.out.println("Failure closing OIS (object input stream).");
					e.printStackTrace();
				}
				
				try
				{
					if(ServerController.getOOS() != null)
					{ServerController.getOOS().close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing OOS (object output stream).");
					e.printStackTrace();
				}
				
				try
				{
					if(ServerController.getOutToClient() != null)
					{ServerController.getOutToClient().close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing OutToClient (print writer).");
					e.printStackTrace();
				}
				
				try
				{
					if(ServerController.getInFromClient() != null)
					{ServerController.getInFromClient().close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing InFromClient (buffered reader).");
					e.printStackTrace();
				}
				
				try
				{
					if(ServerController.getCommandSocket() != null)
					{ServerController.getCommandSocket().close();}
					System.out.println("Existing commandSocket closed.");
				}
					catch (Exception e) 
					{
						System.out.println("Failure closing commandSocket.");
						e.printStackTrace();
					}
					
				try
				{
					if(ServerController.getObjectSocket() != null)
					{ServerController.getObjectSocket().close();}
					System.out.println("Existing objectSocket closed.");	
				} 
				catch (Exception e) 
				{
					System.out.println("Failure closing objectSocket.");
					e.printStackTrace();
				}
			
				//Initialize the next connection
				ServerController.initializeSocket();
			}
			
			
        }
    }




	
	
}

		
