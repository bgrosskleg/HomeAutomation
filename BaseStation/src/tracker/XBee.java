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
	public final static String OK = "OK\r";
	private final static String COMMAND_MODE = "+++";
	private final static String DESTINATION_HIGH = "ATDH";
	private final static String DESTINATION_LOW = "ATDL";
	private final static String WRITE_CHANGES = "ATWR\r";
	private final static String APPLY_CHANGES = "ATAC\r";
	private final static String EXIT_COMMAND_MODE = "ATCN\r";
	
	@SuppressWarnings("unused")
	private final static String SIGNAL_STRENGTH = "ATDB\r";
	@SuppressWarnings("unused")
	private final static String LOCAL_MAC_HIGH = "ATSH\r";
	@SuppressWarnings("unused")
	private final static String LOCAL_MAC_LOW = "ATSL\r";
	
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
	 * Locks the thread until the dbm value has been received.
	 */
	Semaphore dbmSem;
	
	/**
	 * A received dbm value.
	 */
	String dbm;
	
	/**
	 * Current MAC address to send to.
	 */
	private String currentMac;
	
	/**
	 * The XBee modules mac address.
	 */
	@SuppressWarnings("unused")
	private String myMac;
	
	public XBee()
	{
		// The serial instance for communication
		serial = SerialFactory.createInstance();
		
		/* This section is for getting received signal strengths.  Commented out for now.
		// A semaphore that allows us to wait for the "OK" response from the XBee
		okSem = new Semaphore(0);
		
		// Add a listener that handle's the OK's and get's back the mac address
		SerialDataListener tempListener = new SerialDataListener() {
			
			@Override
			public void dataReceived(SerialDataEvent event) {
				if(event.getData() == OK)
				{
					// When an OK is received we increase the number of permits available for the main send
					// thread to handle
					okSem.release();
				}
				else
				{
					String data = event.getData();
					String padded = "00000000".substring(data.length()) + data;
					myMac += padded;
				}
			}
		};		
		serial.addListener(tempListener);
		
		CommandMode();
		
		//Get high and low bytes
		serial.write(LOCAL_MAC_HIGH);
		okSem.acquireUninterruptibly();
		
		serial.write(LOCAL_MAC_LOW);
		okSem.acquireUninterruptibly();
		
		ExitCommandMode();
		serial.removeListener(tempListener);		
		*/
		
		// The XBee starts in normal mode to start
		mode = Mode.NORMAL;
				
		// Initialize the serial communications object and register UartListener
		// to listen for messages
		try 
		{
			listener = new UartListener();
			listener.xbee = this;
			serial.addListener(listener);	
			// wait 1 second before opening
			Thread.sleep(1000);
	        // open the default serial port provided on the GPIO header
	        serial.open(Serial.DEFAULT_COM_PORT, 9600);        
	        // wait 1 second before continuing		
			Thread.sleep(1000);
			
		} catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Originally no mac to send to
		currentMac = "";
		
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
		// Change the mac address we send to if necessary
		if(nodeIdentifier != currentMac)
		{
			// A semaphore that allows us to wait for the "OK" response from the XBee
			okSem = new Semaphore(0);
			System.out.println("Get's here 1 - " + System.currentTimeMillis());
			// Add a listener that handle's the OK's
			SerialDataListener tempListener = new SerialDataListener() {
				
				@Override
				public void dataReceived(SerialDataEvent event) {
					if(event.getData().contains(OK))
					{
						// When an OK is received we increase the number of permits available for the main send
						// thread to handle
						System.out.println("Get's here 2 - " + System.currentTimeMillis());
						okSem.release();
					}				
				}
			};		
			
			//Wyatt's code - not working, hangs after first transmit
			/*serial.addListener(tempListener);	
			
			System.out.println("Get's here 3 - " + System.currentTimeMillis());
			CommandMode();
			System.out.println("Get's here 4 - " + System.currentTimeMillis());
			//Set high and low bytes
			serial.write(DESTINATION_HIGH + nodeIdentifier.substring(0, 7) + '\r');
			System.out.println("Get's here 5 - " + System.currentTimeMillis());
			okSem.acquireUninterruptibly();
			System.out.println("Get's here 6 - " + System.currentTimeMillis());
			serial.write(DESTINATION_LOW + nodeIdentifier.substring(8, 15) + '\r');
			System.out.println("Get's here 7 - " + System.currentTimeMillis());
			okSem.acquireUninterruptibly();
			System.out.println("Get's here 8 - " + System.currentTimeMillis());
			currentMac = nodeIdentifier;
			
			// Write and apply changes
			System.out.println("Get's here 9 - " + System.currentTimeMillis());
			serial.write(WRITE_CHANGES);
			System.out.println("Get's here 10 - " + System.currentTimeMillis());
			okSem.acquireUninterruptibly();
			System.out.println("Get's here 11 - " + System.currentTimeMillis());
			serial.write(APPLY_CHANGES);
			System.out.println("Get's here 12 - " + System.currentTimeMillis());
			okSem.acquireUninterruptibly();
			System.out.println("Get's here 13 - " + System.currentTimeMillis());
			
			ExitCommandMode();
			
			serial.removeListener(tempListener);
			*/
			
			//Brian's old code - usable fallback
			try
			{
				Thread.sleep(75);

				System.out.println("Sent: +++");
				Thread.sleep(10);
				serial.write("+++");
				Thread.sleep(10);

				Thread.sleep(75);

				System.out.println("Sent: ATDH" + nodeIdentifier.substring(0, 8));
				serial.write("ATDH" + nodeIdentifier.substring(0, 8) + '\r');

				Thread.sleep(75);

				System.out.println("Sent: ATDL" + nodeIdentifier.substring(8, 16));
				serial.write("ATDL" + nodeIdentifier.substring(8, 16) + '\r');

				Thread.sleep(75);

				System.out.println("Sent: ATCN");
				serial.write("ATCN" + '\r');

				Thread.sleep(75);

				serial.write(message);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		// Send the data, we are sending to the correct node
		serial.write(message);
	}
	
	/**
	 * Build a receive signal packet from the most recent receive signal strength.
	 */
	/* This section is for getting received signal strengths.  Commented out for now.
	public void GetReceiveSignalPacket(int broadcastNumber, String mobileMac)
	{
		// A semaphore that allows us to wait for the "OK" response from the XBee
		okSem = new Semaphore(0);
		dbmSem = new Semaphore(0);
		
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
				else if(event.getData().substring(0, 1) == "0x")
				{
					dbm = event.getData();
					dbmSem.release();
				}
			}
		};		
		serial.addListener(tempListener);
		
		CommandMode();
		
		//Set high and low bytes
		serial.write(SIGNAL_STRENGTH);
		dbmSem.acquireUninterruptibly();
		
		ExitCommandMode();
		serial.removeListener(tempListener);
		
		// Build signal strength, first put in the raw db
		String signalStrength = dbm;
		
		// Add the int db value
		Integer intDbm = -Integer.parseInt(dbm.substring(2), 16);
		signalStrength += " " + intDbm + "dBm ";
		
		// Add percentage
		Integer percent = intDbm + 92 / 66;
		signalStrength += "(" + percent + "%)";
		
		ReceivePacket packet = new ReceivePacket(mobileMac, myMac, signalStrength, broadcastNumber);
		AddPacket(packet);
	}
	*/
	
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
	
	/**
	 * Put the XBee module into command mode.
	 */
	private void CommandMode()
	{
		// Switch to command mode if necessary
		if(mode != Mode.COMMAND)
		{
			serial.write(COMMAND_MODE);
			mode = Mode.COMMAND;
			
			// Wait for OK before continuing
			okSem.acquireUninterruptibly();
		}
	}
	
	/**
	 * Put the XBee module into normal mode.
	 */
	private void ExitCommandMode()
	{
		// Exit command mode
		if(mode == Mode.COMMAND)
		{
			serial.write(EXIT_COMMAND_MODE);
			okSem.acquireUninterruptibly();
			mode = Mode.NORMAL;
		}
	}
}

enum Mode
{
	COMMAND, NORMAL
}

