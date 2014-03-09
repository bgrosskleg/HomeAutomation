package tracker;

import java.awt.geom.Point2D;

import model.User;
import controller.BaseStationController;

public class LocationThread extends Thread 
{
	/**
	 * The xbee module we use for receiving and sending messages.
	 */
	private XBee xbee;
	
	/**
	 * A storage system for the mobile nodes
	 */
	private MobileNodes nodes;
	
	/**
	 * The controller for the model.  Use this to get a hold of model information
	 */
	private BaseStationController controller;
	
	/**
	 * Used to try find the location of a mobile node.
	 */
	LocationEstimator estimator;
	
	public LocationThread(BaseStationController controller)
	{
		// Initialize the xbee module we use for communication
		xbee = new XBee();
		
		// Initialize the mobile nodes storage
		nodes = new MobileNodes();
		
		// A handle to the controller so we can get info about the current home model
		this.controller = controller;
		
		// Initialize estimation
		estimator = new LocationEstimator(800, 800);
	}
	
	public void run() 
	{		
    	for(;;)
    	{
    		// Get a the received packet
    		ReceivePacket packet = xbee.NextPacket();
    		
    		// Get a reference to this node
    		MobileNode node = nodes.GetMobileNode(packet.mobileMac);
    		
    		// Add the signal strength
    		SignalStrength strength = new SignalStrength(packet.signalStrength, packet.broadcastNumber);
    		node.AddSignalStrength(strength, packet.staticMac);    		
    		
    		// Calculate location of mobile node
    		Location location = estimator.CalculateLocation(controller, node, packet.broadcastNumber);
    		
    		if(location != null)
    		{
	    		// Store the current location
	    		node.SetLocation(location);
	    		
	    		// Send the new location to the model, it will decide if we need to send lighting commands and do so if necessary
	    		User user = controller.getUser(node.mac);
	    		
	    		String [] params = new String[] {"location"};
	    		Object[] values = new Object[] { new Point2D.Double(location.x, location.y) };
	    		controller.modifyObject(user, params, values);
    		}
    		
    	}
	}
}
