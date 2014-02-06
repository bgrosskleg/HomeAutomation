package tracker;

/**
 * A packet that has been received from the XBee network.  Contains the mac
 * addresses of the mobile node and static node pair that sent the message,
 * a signal strength, and broadcast number.
 * @author wyatt
 *
 */
public class ReceivePacket 
{
	public String mobileMac;
	public String staticMac;
	public int signalStrength;
	public int broadcastNumber;
	
	public ReceivePacket()
	{
	
	}
	
	public ReceivePacket(String mobileMac, String staticMac, int signalStrength, int broadcastNumber)
	{
		this.mobileMac = mobileMac;
		this.staticMac = staticMac;
		this.signalStrength = signalStrength;
		this.broadcastNumber = broadcastNumber;	
	}
}
