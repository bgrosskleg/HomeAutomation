package tracker;

public class ReceivePacket 
{
	public String mobileMac;
	public String staticMac;
	public int signalStrength;
	public int broadcastNumber;
	
	public ReceivePacket(String mobileMac, String staticMac, int signalStrength, int broadcastNumber)
	{
		this.mobileMac = mobileMac;
		this.staticMac = staticMac;
		this.signalStrength = signalStrength;
		this.broadcastNumber = broadcastNumber;	
	}
}
