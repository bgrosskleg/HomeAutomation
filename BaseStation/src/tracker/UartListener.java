package tracker;

import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;

public class UartListener implements SerialDataListener {

	/**
	 * The XBee object to push received data into.
	 */
	public XBee xbee;
	
	/**
	 * Unfortunately, the way we have set up communication and the way the pi4j library
	 * works, we end up with a weird state machine here.  Packets look like this:
	 *  ###\n 
	 *  Mobile node identifier\n 
	 *  Static node identifier\n 
	 *  Signal strength\n
	 *  Broadcast number\n 
	 *  $$$\n
	 *  
	 *  State 0: Waiting for ###\n, move to state 1
	 *  State 1: Waiting for mobile node identifier, store, move to state 2
	 *  State 2: Waiting for static node identifier, store, move to state 3
	 *  State 3: Waiting for signal strength, store, move to state 4
	 *  State 4: Waiting for broadcast number, store, move to state 5
	 *  State 5: Waiting for $$$\n, Send packet to xbee, move to state 0	 *  
	 */
	private int state = 0;
	
	/**
	 * Store the current packet we are building.
	 */
	ReceivePacket packet;
	
	@Override
	public void dataReceived(SerialDataEvent event) {
		String data = event.getData();
		
		switch (data) 
		{
		case "###\n":
			if(state == 0)
			{
				packet = new ReceivePacket();
				state = 1;
			}
			else
			{
				throw new IllegalStateException("Current state: " + state + ". Received ###.");
			}
			break;
		case "$$$\n":
			if(state == 5)
			{
				xbee.AddPacket(packet);
				state = 0;
			}
			else
			{
				throw new IllegalStateException("Current state: " + state + ". Received $$$.");
			}
			break;
		default:
			switch (state) 
			{
			case 1:
				packet.mobileMac = data.substring(0, data.length() - 2);
				break;
			case 2:
				packet.staticMac = data.substring(0, data.length() - 2);
				break;
			case 3:
				packet.signalStrength = Integer.parseInt(data.substring(0, data.length() - 2));
				break;
			case 4:
				packet.broadcastNumber = Integer.parseInt(data.substring(0, data.length() - 2));
				break;
			default:
				throw new IllegalStateException("Current state: " + state + ". Received: " + data + ".");
			}
			break;
		}

	}

}
