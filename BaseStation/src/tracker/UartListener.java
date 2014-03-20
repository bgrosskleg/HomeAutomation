package tracker;

import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;

public class UartListener implements SerialDataListener 
{	
	private static final String BROADCAST_START_DELIM = "*";
	private static final String BROADCAST_END_DELIM = "@";
	private static final String STRENGTH_START_DELIM = "#";
	private static final String STRENGTH_END_DELIM = "?";

	/**
	 * The XBee object to push received data into.
	 */
	public XBee xbee;

	/**
	 * A buffer that is the character data that has been received so far.
	 */
	private StringBuffer buffer = new StringBuffer();

	@Override
	public void dataReceived(SerialDataEvent event) {
		String data = event.getData();
		synchronized (System.out) {

			System.out.println(data.replace("\r", "\\r"));
			System.out.println();
		}
		data = data.replaceAll(XBee.OK, "");
		buffer.append(data);

		// Indices of delimiters
		int bStartDelim;
		int bEndDelim;
		int sStartDelim;
		int sEndDelim;
		do
		{
			bStartDelim = buffer.indexOf(BROADCAST_START_DELIM);
			bEndDelim = buffer.indexOf(BROADCAST_END_DELIM);
			sStartDelim = buffer.indexOf(STRENGTH_START_DELIM);
			sEndDelim = buffer.indexOf(STRENGTH_END_DELIM);
			if(bStartDelim == 0)
			{
				if(bEndDelim != -1)
				{
					buffer.replace(bStartDelim, bEndDelim + 1, "");
				}
			}
			else if(sStartDelim == 0)
			{
				if(sEndDelim != -1)
				{
					String sigStren = buffer.substring(sStartDelim + 1, sEndDelim);
					buffer.replace(sStartDelim, sEndDelim + 1, "");
					ReceivePacket temp = new ReceivePacket(sigStren.substring(4, 20), 
							 sigStren.substring(21, 37), 
							 sigStren.substring(38, 40), 
							 Integer.parseInt(sigStren.substring(0, 3)));
					xbee.AddPacket(new ReceivePacket(sigStren.substring(4, 20), 
													 sigStren.substring(21, 37), 
													 sigStren.substring(38, 40), 
													 Integer.parseInt(sigStren.substring(0, 3))));
				}
			}
		}while(bEndDelim != -1 || sEndDelim != -1);
	}

}
