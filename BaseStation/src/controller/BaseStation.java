package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import model.User;

/**
 * BaseStation application that runs as the firmware
 * @author Brian Grosskleg
 *
 */
class BaseStation 
{	
	private static BaseStationController controller;
	
	/**
	 * Entry point for program
	 * @param args not used
	 */
    public static synchronized void main(String[] args) 
    {
    	//Create base station controller
    	controller = new BaseStationController();           
        
        
        //Creates a timer to simulate moving user position 
        
        int delay = 2000; //milliseconds
    	       
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
	        		if(moveRight && LocX < 400)
	        		{
	        			newLocation = new Point2D.Double(LocX += stepSize, LocY);
	        		}
	        		else if(moveRight && LocX >= 400)
	        		{
	        			moveRight = false;
	        			newLocation = new Point2D.Double(LocX -= stepSize, LocY);
	        		}
	        		else if(!moveRight && LocX > 50)
	        		{
	        			newLocation = new Point2D.Double(LocX -= stepSize, LocY);
	        		}
	        		else if(!moveRight && LocX <= 50)
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
        			//System.err.println("User ABCDEFGH12345678 is null!");
        		}
        	}	
        };
        
        new Timer(delay, taskPerformer).start(); 
    }
    
    /**
     * Reference to get controller, static can be accessed anywhere
     * @return the baseStation controller
     */
    public static BaseStationController getController()
    {
    	return controller;
    }
}
