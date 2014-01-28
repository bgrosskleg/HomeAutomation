package view;
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

import model.CanvasObject;
import model.CurrentModel;
import model.Point;
import model.Sensor;
import controller.ServerController;

class ServerThread extends Thread 
{   	
    ServerThread() 
    {
        super("TrackingGUIServer");
        
        //Create one serverSocket
        try 
    	{
			ServerController.setServerSocket(new ServerSocket(ServerController.getServerPort()));
		} 
    	catch (Exception e1) 
    	{
			System.err.println("Failure creating server socket!");
			e1.printStackTrace();
			return;
		}
    	
        System.out.println("TrackingGUIServer listening on port: " + ServerController.getServerPort());
        
        
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
      
        int delay2 = 2000; //milliseconds

        ActionListener taskPerformer2 = new ActionListener() {
        	//boolean moveRight = true;
                	
        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{        		
        		/*
    					User user = ServerController.getCM().getUser();
    					
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
        		
        		for(Point point : ServerController.getCM().getPoints())
        		{
        			point.weight = 0;
        		}
        		Point max = new Point(new Point2D.Double(0,0));
            	double tolerance = 0.10;
        		
        		for(CanvasObject object : ServerController.getCM().getObjects())
        		{
        			if(object instanceof Sensor)
        			{
        				Sensor sensor = (Sensor) object;
        				
                		for(Point point : ServerController.getCM().getPoints())
                		{
                			if(point.location.distance(sensor.location) > sensor.RSSI*(1-tolerance) && point.location.distance(sensor.location) < sensor.RSSI*(1+tolerance))
                			{
                				point.weight += 5;
                			}
                		}
        			}
        		}
        		
        		for(Point point : ServerController.getCM().getPoints())
        		{
        			if(point.weight > max.weight)
        			{
        				max = point;
        			}
        		}
        		
        		ServerController.getCM().getUser().setLocation(max.location);
        	}
        };
        new Timer(delay2, taskPerformer2).start();
    }

    public void run() 
    {
        if (ServerController.getSocket() == null)
        {
        	System.err.println("Socket not created...");
            return;
        }
                
        while (true) 
        {
        	// Receive request
        	String received = null;
			try {
				if(ServerController.getInFromClient() == null)
				{
					System.out.println("Input stream not initialized...");
					throw new Exception();
				}
				
				received = ServerController.getInFromClient().readLine();
				
				if(received == null)
				{
					//EOF Stream is closed, likely remote closed socket, trigger catch statement
					System.out.println("Client closed socket, throw exception...");
					throw new Exception();
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
					if(ServerController.getSocket() != null)
					{ServerController.getSocket().close();}
					System.out.println("Existing socket closed.");
					
					//Initialize the next connection
					ServerController.initializeSocket();
				} 
				catch (Exception e) 
				{
					System.out.println("Failure closeing socket.");
					e.printStackTrace();
				}
			}
			
			System.out.println("Received: |" + received + "|");
        	if(received != null)
          	{
        	switch(received)
	        	{
	        	case "REQUESTMODEL":					try {
																System.out.println("Sending model to client...");
																ServerController.getOutToClient().println("SENDINGMODEL");
																//ServerController.getOOS().reset();
																ServerController.getOOS().flush();
																System.out.println("Get's here 1");
																
																ServerController.getOOS().writeObject(ServerController.getCM());
																
																System.out.println("Get's here 2");
																//ServerController.getOOS().flush();
																System.out.println("Get's here 3");
																//ServerController.getOOS().reset();
																System.out.println("Get's here 4");
									
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
																	System.err.println("Impossible response.");
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
																ServerController.getOutToClient().println("OKAY");
												    			System.out.println("Model recieved.");
												    			
		        												//Save CurrentModel to RPi card
		        								        		ServerController.saveModeltoDisk();
		        								        		
															} catch (Exception e) {
																System.out.println("Failure getting model from client!");
																e.printStackTrace();
															} 
	        												break;
	        	
	        	default:	
	        												break;
	
	        	}
          	}
        }
    }




	
	
}

		
