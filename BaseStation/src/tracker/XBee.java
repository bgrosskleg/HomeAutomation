package tracker;

import java.util.concurrent.Semaphore;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
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
	public final static String OK = "OK";
	private final static String COMMAND_MODE = "+++";
	private final static String DESTINATION_HIGH = "ATDH";
	private final static String DESTINATION_LOW = "ATDL";
	private final static String WRITE_CHANGES = "ATWR";
	private final static String APPLY_CHANGES = "ATAC";
	private final static String EXIT_COMMAND_MODE = "ATCN";	
	
	/**
	 * Stores all the received packets that have not yet been read.
	 */
	private Packets packets;
	
	/**
	 * Serial communications object that handles UART communication.
	 */
	private final Serial serial;
	
	/**
	 * A UartListener that collects messages sent to us over UART.
	 */
	private UartListener listener;
	
	/**
	 * The current mode of the attached XBEE module.
	 */
	private Mode mode;
	
	/**
	 * The number of OK's we have received so far while sending a message.
	 */
	Semaphore okSem;
	
	/**
	 * Current MAC address to send to.
	 */
	private String mac;
	
	public XBee()
	{
		// Initialize the serial communications object and register UartListener
		// to listen for messages
		serial = SerialFactory.createInstance();
		listener = new UartListener();
		listener.xbee = this;
		serial.addListener(listener);
		
		// The XBee starts in normal mode to start
		mode = Mode.NORMAL;
		
		// Originally no mac to send to
		mac = "";
		
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
		if(nodeIdentifier != mac)
		{
			// A semaphore that allows us to wait for the "OK" response from the XBee
			okSem = new Semaphore(0);
			
			// Add a listener that handle's the OK's
			SerialDataListener tempListener = new SerialDataListener() {
				
				@Override
				public void dataReceived(SerialDataEvent event) {
					if(event.getData() == OK)
					{
						// When an OK is received we increase the number of permits available for the main send
						// thread to handle
						okSem.release();
					}				
				}
			};		
			serial.addListener(tempListener);	
			
			// Switch to command mode if necessary
			if(mode != Mode.COMMAND)
			{
				serial.write(COMMAND_MODE);
				mode = Mode.COMMAND;
				
				// Wait for OK before continuing
				okSem.acquireUninterruptibly();
			}
			
			//Set high and low bytes
			serial.write(DESTINATION_HIGH + nodeIdentifier.substring(0, 7));
			okSem.acquireUninterruptibly();
			serial.write(DESTINATION_LOW + nodeIdentifier.substring(8, 15));
			okSem.acquireUninterruptibly();
			mac = nodeIdentifier;
			
			// Write and apply changes
			serial.write(WRITE_CHANGES);
			okSem.acquireUninterruptibly();
			serial.write(APPLY_CHANGES);
			okSem.acquireUninterruptibly();
			
			// Exit command mode
			serial.write(EXIT_COMMAND_MODE);
			okSem.acquireUninterruptibly();
			mode = Mode.NORMAL;
			
			serial.removeListener(tempListener);
		}
		
		// Send the data, we are sending to the correct node
		serial.write(message);
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

enum Mode
{
	COMMAND, NORMAL
}

