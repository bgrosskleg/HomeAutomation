package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import controller.BaseStationController;
import model.User;

class BaseStation 
{	
	private static BaseStationController controller;
	
    public static synchronized void main(String[] args) 
    {
    	//Create base station controller
    	controller = new BaseStationController();           
        
        
        //Creates a timer to simulate moving user position 
        
        int delay = 500; //milliseconds
    	       
        ActionListener taskPerformer = new ActionListener() 
        {
        	boolean moveRight = true;
        	int stepSize = 25;
        	
        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{        	
        		Point2D.Double newLocation = null;
        		User temp = controller.getUser("ABCDEFGH12345678");
        		
        		if(temp != null)
        		{
        			double LocX = temp.getLocation().getX();
        		
	        		double LocY = temp.getLocation().getY();
	        		
	        		//Move user
	        		if(moveRight && LocX < 175)
	        		{
	        			newLocation = new Point2D.Double(LocX += stepSize, LocY);
	        		}
	        		else if(moveRight && LocX >= 175)
	        		{
	        			moveRight = false;
	        			newLocation = new Point2D.Double(LocX -= stepSize, LocY);
	        		}
	        		else if(!moveRight && LocX > 25)
	        		{
	        			newLocation = new Point2D.Double(LocX -= stepSize, LocY);
	        		}
	        		else if(!moveRight && LocX <= 25)
	        		{
	        			moveRight = true;
	        			newLocation = new Point2D.Double(LocX += stepSize, LocY);
	        		}
	        		
	        		String [] parameters = new String [] {"location"};
	        		Object [] values = new Object [] {newLocation};
	        		
	        		controller.modifyObject(temp, parameters, values);
        		}
        		else
        		{
        			System.err.println("User ABCDEFGH12345678 is null!");
        		}
        	}	
        };
        
        new Timer(delay, taskPerformer).start(); 
    }
    
    public static BaseStationController getController()
    {
    	return controller;
    }
}
