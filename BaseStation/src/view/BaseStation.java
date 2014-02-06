package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import controller.BaseStationController;
import model.User;


/* 
 * Java(TM) SE 6
 * Code is the same as 1.0.
 */


class BaseStation 
{	
	private static BaseStationController controller;
	
    public static void main(String[] args) 
    {
    	//Create base station controller
    	controller = new BaseStationController();           
        
        
        //Creates a timer to simulate moving user position 
        
        int delay2 = 500; //milliseconds
        
        User user = new User("Brian", "ABCDEDGH12345678", new Point2D.Double(25/2, 25/2));
    	BaseStation.getController().getSystemModel().addUser(user);
    	       
        ActionListener taskPerformer2 = new ActionListener() 
        {
        	boolean moveRight = true;
        	int stepSize = 25;
        	User temp = BaseStation.getController().getSystemModel().getUser("ABCDEDGH12345678");
        	
        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{        		
        		double LocX = temp.getLocation().getX();
        		double LocY = temp.getLocation().getY();
        		//Move userA
        		if(temp != null)
        		{	
	        		if(moveRight && LocX < 175)
	        		{
	        			temp.setLocation(new Point2D.Double(LocX += stepSize, LocY));
	        		}
	        		else if(moveRight && LocX >= 175)
	        		{
	        			moveRight = false;
	        			temp.setLocation(new Point2D.Double(LocX -= stepSize, LocY));
	        		}
	        		else if(!moveRight && LocX > 25)
	        		{
	        			temp.setLocation(new Point2D.Double(LocX -= stepSize, LocY));
	        		}
	        		else if(!moveRight && LocX <= 25)
	        		{
	        			moveRight = true;
	        			temp.setLocation(new Point2D.Double(LocX += stepSize, LocY));
	        		}	
        		}
        		
        		controller.getSystemModel().notifyUsersModelSubscribers();
        	}	
        };
        
        new Timer(delay2, taskPerformer2).start(); 
    }
    
    public static BaseStationController getController()
    {
    	return controller;
    }
}
