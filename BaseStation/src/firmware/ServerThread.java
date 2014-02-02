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
import controller.Controller;

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
			Controller.setCommandServerSocket(new ServerSocket(Controller.getCommandPort()));
			Controller.setObjectServerSocket(new ServerSocket(Controller.getObjectPort()));
		} 
    	catch (Exception e1) 
    	{
			System.err.println("Failure creating server sockets!");
			e1.printStackTrace();
			return;
		}
    	
        System.out.println("Server command stream listening on port: " + Controller.getCommandPort());
        System.out.println("Server object stream listening on port: " + Controller.getObjectPort());
        
        //Wait for applet connection
        Controller.initializeSocket();
        
        
        //Creates a timer to save model to file every 60 seconds
        int delay = 60000; //milliseconds

        ActionListener taskPerformer = new ActionListener() {

        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{
        		//Save CurrentModel to RPi card
        		Controller.saveModeltoDisk();
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
        		
        		
        		
        		for(Point point : Controller.getCM().points)
        		{
        			point.weight = 0;
        		}
        		Point max = new Point(new Point2D.Double(0,0));
            	double tolerance = 0.10;
        		
        		for(Sensor sensor : Controller.getCM().sensors)
        		{
        				
                	for(Point point : Controller.getCM().points)
                	{
                		if(point.location.distance(sensor.location) > sensor.RSSI*(1-tolerance) && point.location.distance(sensor.location) < sensor.RSSI*(1+tolerance))
                		{
                			point.weight += 5;
                		}
                	}
        		}
        		
        		for(Point point : Controller.getCM().points)
        		{
        			if(point.weight > max.weight)
        			{
        				max = point;
        			}
        		}
        		
        		Controller.getCM().users.get(0).setLocation(max.location);
        		
        	}
        };
        new Timer(delay2, taskPerformer2).start();
    }

    public void run() 
    {
    	//Ensure sockets are initialized
        if (Controller.getCommandSocket() == null || Controller.getObjectSocket() == null)
        {
        	System.err.println("Sockets not created...");
            return;
        }
        
        //Ensure streams are initialized
        if(Controller.getInFromClient() == null)
		{
			System.out.println("Input stream not initialized...");
			return;
		}
                
        //Infinite loop
        while (true) 
        {
        	// Receive request on command stream
        	String received = null;
			try {
								
				received = Controller.getInFromClient().readLine();
				
				if(received == null)
				{
					//EOF Stream is closed, likely remote closed socket, trigger catch statement
					System.out.println("Client closed socket, throw exception...");
					throw new Exception();
				}
				
				System.out.println("Received: |" + received + "|");
				
				//Process command
	        	switch(received)
		        	{
		        	case "REQUESTMODEL":					try {
																	System.out.println("Sending model to client...");
																	
																	//Respond to client
																	Controller.getOutToClient().println("SENDINGMODEL");
																	
																	//Reset object stream and write model to stream
																	Controller.getOOS().reset();
																	Controller.getOOS().writeObject(Controller.getCM());
																	
																	//Analyze response
																	if(Controller.getInFromClient().readLine().equals("OKAY"))
																	{
																		System.out.println("Model sent complete.");
																	}
																	else if(Controller.getInFromClient().readLine().equals("FAIL"))
																	{
																		System.out.println("Model did not send correctly.");
																	}
																	else
																	{
																		System.err.println("Impossible response: " + Controller.getInFromClient().readLine());
																	}
																	
																} catch (Exception e) {
																	System.out.println("Failure sending model to client!");
																	e.printStackTrace();
																}
		        	
		        												break;
		        												
		
		        	case "SENDINGMODEL":					try {
																	System.out.println("Receiving model...");
																	
																	//Send response to client
																	Controller.getOutToClient().println("OKAY");
																	
																	//Read object off object stream
																	Controller.setCM((CurrentModel) Controller.getOIS().readObject());
																	
																	//Send response to client
																	Controller.getOutToClient().println("OKAY");
													    			System.out.println("Model recieved.");
													    			
			        												//Save CurrentModel to RPi card
			        								        		Controller.saveModeltoDisk();
			        								        		
																} catch (Exception e) {
																	Controller.getOutToClient().println("FAIL");
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
				//Socket error, close all streams and sockets
				
				System.out.println("Socket Exception, closing existing socket...");

				try 
				{
					if(Controller.getOIS() != null)
					{Controller.getOIS().close();}
				}
				
				catch(Exception e)
				{
					System.out.println("Failure closing OIS (object input stream).");
					e.printStackTrace();
				}
				
				try
				{
					if(Controller.getOOS() != null)
					{Controller.getOOS().close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing OOS (object output stream).");
					e.printStackTrace();
				}
				
				try
				{
					if(Controller.getOutToClient() != null)
					{Controller.getOutToClient().close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing OutToClient (print writer).");
					e.printStackTrace();
				}
				
				try
				{
					if(Controller.getInFromClient() != null)
					{Controller.getInFromClient().close();}
				}
				catch(Exception e)
				{
					System.out.println("Failure closing InFromClient (buffered reader).");
					e.printStackTrace();
				}
				
				try
				{
					if(Controller.getCommandSocket() != null)
					{Controller.getCommandSocket().close();}
					System.out.println("Existing commandSocket closed.");
				}
					catch (Exception e) 
					{
						System.out.println("Failure closing commandSocket.");
						e.printStackTrace();
					}
					
				try
				{
					if(Controller.getObjectSocket() != null)
					{Controller.getObjectSocket().close();}
					System.out.println("Existing objectSocket closed.");	
				} 
				catch (Exception e) 
				{
					System.out.println("Failure closing objectSocket.");
					e.printStackTrace();
				}
			
				//Initialize the next connection
				Controller.initializeSocket();
			}
			
			
        }
    }




	
	
}

		
