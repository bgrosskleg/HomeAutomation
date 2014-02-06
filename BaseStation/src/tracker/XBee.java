package tracker;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialFactory;

/**
 * Represents an XBee module connected to the Pi via UART.  This class
 * assumes two operations exist.  Receiving and sending.  It has functions for
 * each.  Will register itself to listen for the UART module automatically.
 * @author wyatt
 *
 */
public class XBee 
{
	/**
	 * Stores all the received packets that have not yet been read.
	 */
	private Packets packets;
	
	/**
	 * Serial communications object that handles UART communication.
	 */
	final Serial serial;
	
	/**
	 * A UartListener that collects messages sent to us over UART.
	 */
	UartListener listener;
	public XBee()
	{
		// Initialize the serial communications object and register UartListener
		// to listen for messages
		serial = SerialFactory.createInstance();
		listener = new UartListener();
		listener.xbee = this;
		serial.addListener(listener);
		
		// Initialize the packet storage
		packets = new Packets();		
	}
	
	/**
	 * Send a message out to a node on the XBee network.
	 * @param nodeIdentifier - The mac address of the node to send to.
	 * @param message - The message to send.
	 */
	public void SendMessage(String nodeIdentifier, String message)
	{
		// TODO: Write this.
	}
	
	/**
	 * Get the oldest packet that has been received over the XBee device.
	 * @return - The oldest ReceivePacket from the XBee network.
	 */
	public ReceivePacket NextPacket()
	{
		return packets.pop();
	}
	
	/**
	 * Add a packet to the end of the packets.
	 * @param packet - The packet to add.
	 */
	public void AddPacket(ReceivePacket packet)
	{
		packets.push(packet);
	}
}
