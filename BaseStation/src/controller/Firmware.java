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
    public static void main(String[] args) 
    {
    	//Start server thread
        new BaseStationCommunicationThread().start();
        
        //Load model from file
        BaseStationController.setCM(BaseStationController.loadModelfromFile());
        
        //Send model to applet
		BaseStationCommunicationThread.sendModel();
        
		
		
		
		
		
		
      //Creates an autosave timer to save model to file every 60 seconds
        int delay = 60000; //milliseconds

        ActionListener taskPerformer = new ActionListener() {

        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{
        		//Save CurrentModel to RPi card
        		BaseStationController.saveModeltoFile();
        	}
        };
        new Timer(delay, taskPerformer).start();    
        
        
        
        //Creates a timer to triangulate user position 
        
        int delay2 = 2000; //milliseconds

        ActionListener taskPerformer2 = new ActionListener() 
        {
        	boolean moveRight = true;
                	
        	@Override
        	public void actionPerformed(ActionEvent e) 
        	{        		
        		
        		User user = BaseStationController.getCM().getUsers().get(0);

        		//Move userA
        		int stepSize = BaseStationController.getCM().gridSize;

        		if(moveRight && user.getLocation().x < 175)
        		{
        			user.setLocation(new Point2D.Double(user.getLocation().x += stepSize, user.getLocation().y));
        			BaseStationController.getCM().currentModelChanged();
        		}
        		else if(moveRight && user.getLocation().x >= 175)
        		{
        			moveRight = false;
        			user.setLocation(new Point2D.Double(user.getLocation().x -= stepSize, user.getLocation().y));
        			BaseStationController.getCM().currentModelChanged();
        		}
        		else if(!moveRight && user.getLocation().x > 25)
        		{
        			user.setLocation(new Point2D.Double(user.getLocation().x -= stepSize, user.getLocation().y));
        			BaseStationController.getCM().currentModelChanged();
        		}
        		else if(!moveRight && user.getLocation().x <= 25)
        		{
        			moveRight = true;
        			user.setLocation(new Point2D.Double(user.getLocation().x += stepSize, user.getLocation().y));
        			BaseStationController.getCM().currentModelChanged();
        		}	
        	}
        };
        
        new Timer(delay2, taskPerformer2).start(); 
    }
}
