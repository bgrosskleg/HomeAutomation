package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import model.User;


/* 
 * Java(TM) SE 6
 * Code is the same as 1.0.
 */


class Firmware 
{	
	private static BaseStationController controller;
	private static BaseStationCommunicationThread baseComThread;
	
    public static void main(String[] args) 
    {
    	//Create base station controller
    	controller = new BaseStationController();
    	    	
    	//Start communication thread
        try 
        {
        	baseComThread = new BaseStationCommunicationThread(controller);
        	baseComThread.start();
		} 
        catch (Exception e1) 
        {
			System.err.println("Error creating BaseStationCommunicationThread!");
			System.err.println("Running locally!");
			e1.printStackTrace();
		}              
        
        
        //Creates a timer to triangulate user position 
        
        int delay2 = 500; //milliseconds

        ActionListener taskPerformer2 = new ActionListener() 
        {
        	boolean moveRight = true;
                	
        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{        		
        		
        		User user = controller.getCM().getUsers().get(0);

        		//Move userA
        		int stepSize = 25;

        		if(moveRight && user.getLocation().x < 175)
        		{
        			user.setLocation(new Point2D.Double(user.getLocation().x += stepSize, user.getLocation().y));
        			controller.getCM().currentModelChanged();
        		}
        		else if(moveRight && user.getLocation().x >= 175)
        		{
        			moveRight = false;
        			user.setLocation(new Point2D.Double(user.getLocation().x -= stepSize, user.getLocation().y));
        			controller.getCM().currentModelChanged();
        		}
        		else if(!moveRight && user.getLocation().x > 25)
        		{
        			user.setLocation(new Point2D.Double(user.getLocation().x -= stepSize, user.getLocation().y));
        			controller.getCM().currentModelChanged();
        		}
        		else if(!moveRight && user.getLocation().x <= 25)
        		{
        			moveRight = true;
        			user.setLocation(new Point2D.Double(user.getLocation().x += stepSize, user.getLocation().y));
        			controller.getCM().currentModelChanged();
        		}	
        	}
        };
        
        new Timer(delay2, taskPerformer2).start(); 
    }
    
    public static BaseStationController getController()
    {
    	return controller;
    }
    
    public static BaseStationCommunicationThread getComThread()
    {
    	return baseComThread;
    }
}
