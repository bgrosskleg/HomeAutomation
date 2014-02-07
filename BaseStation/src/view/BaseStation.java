package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.Timer;

import controller.BaseStationController;
import model.HouseObject;
import model.User;

class BaseStation 
{	
	private static BaseStationController controller;
	
    public static synchronized void main(String[] args) 
    {
    	//Create base station controller
    	controller = new BaseStationController();           
        
        
        //Creates a timer to simulate moving user position 
        
        int delay2 = 500; //milliseconds
        
        if(controller.getUser("ABCDEDGH12345678") == null)
        {
        	User user = new User("Brian", "ABCDEDGH12345678", new Point2D.Double(25/2, 25/2), HouseObject.randomColor());
        	controller.addUser(user);
    	}
    	       
        ActionListener taskPerformer2 = new ActionListener() 
        {
        	boolean moveRight = true;
        	int stepSize = 25;
        	User temp = BaseStation.getController().getUser("ABCDEDGH12345678");
        	
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
        		
        		controller.notifyUsersModelSubscribers();
        	}	
        };
        
        new Timer(delay2, taskPerformer2).start(); 
    }
    
    public static BaseStationController getController()
    {
    	return controller;
    }
}
