package tracker;

import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;

public class UartListener implements SerialDataListener 
{
	private final static String BROADCAST_NUMBER = "Broadcast #: ";
	private final static String MOBILE_NODE = "Mobile: ";
	private final static String STATIC_NODE = "Sensor: ";
	private final static String RSSI = "RSSI: ";
	
	/**
	 * The XBee object to push received data into.
	 */
	public XBee xbee;
	
	/**
	 * Unfortunately, the way we have set up communication and the way the pi4j library
	 * works, we end up with a weird state machine here.  Packets look like this:
	 *  ###\n 
	 *  Broadcast number\n 
	 *  Mobile: Mobile node identifier\n 
	 *  Sensor: Static node identifier\n 
	 *  RSSI: Signal strength\n
	 *  $$$\n
	 *  
	 *  State 0: Waiting for "Broadcast #: <number>", move to state 1
	 *  State 1: Waiting for "Mobile: <mobile node identifier>", store, move to state 2
	 *  State 2: Waiting for "Sensor: <static node identifier>, store, move to state 3
	 *  State 3: Waiting for "RSSI: <signal strength>", store, move to state 4 
	 */
	private int state = 0;
	
	/**
	 * Store the current packet we are building.
	 */
	ReceivePacket packet;

	@Override
	public void dataReceived(SerialDataEvent event) {
		String data = event.getData();
		
		if(data == XBee.OK || data.substring(0, 1) == "0x")
		{
			// Ignore OK responses as they mean nothing to us
			// Ignore db readings as we don't handle them
		}
		// This is a broadcast from a mobile node so pass it to the XBee module to build the packet
		else if(data.charAt(0) == '*')
		{
			int broadcastNumber = Integer.parseInt(data.substring(1, 3));
			String mac = data.substring(4);
			xbee.GetReceiveSignalPacket(broadcastNumber, mac);
		}
		// TODO: Add a case in here that handles adding mobile/static nodes to network
		else if(false)
		{			
		}
		else
		{
			switch (state) 
			{
			case 0:
				packet.broadcastNumber = Integer.parseInt(data.replaceFirst(BROADCAST_NUMBER, ""));
				break;
			case 1:
				packet.mobileMac = data.replaceFirst(MOBILE_NODE, "");
				break;
			case 2:
				packet.staticMac = data.replaceFirst(STATIC_NODE, "");
				break;
			case 3:
				packet.signalStrength = data.replaceFirst(RSSI, "");
				break;
			default:
				throw new IllegalStateException("Current state: " + state + ". Received: " + data + ".");
			}
		}
	}

}
