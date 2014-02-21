package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

public class TestXbee 
{	
	public static Serial serial;
	
	public static void main(String[] args) 
	{
		try 
        {
			serial = SerialFactory.createInstance();
			
			serial.addListener(new SerialDataListener() 
			{
				@Override
				public void dataReceived(SerialDataEvent event) 
				{
					System.out.println("!");
					System.out.println(event.getData());
				}
			});
			
		    // wait 1 second before opening
			Thread.sleep(1000);
			
	
	        // open the default serial port provided on the GPIO header
	        serial.open(Serial.DEFAULT_COM_PORT, 9600);
	        
	        // wait 1 second before continuing
			Thread.sleep(1000);
        
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String waitTime;
			String input1;
			
			for(;;)
			{
				System.out.println("Wait time ms (typ 50ms):");
				waitTime = br.readLine();
				
				if(waitTime.equals("exit"))
				{
					break;
				}
				
				System.out.println("CONFIG:");
				input1 = br.readLine();
				
				if(input1.equals("exit"))
				{
					break;
				}
				
				System.out.println("SENT: +++");
				serial.write("+++");
				Thread.sleep(Integer.valueOf(waitTime));
				System.out.println("SENT: " + input1 + '\r');
				serial.write(input1 + '\r');
			}	
			
			for(;;)
			{
				System.out.println("MESSAGE:");
				input1 = br.readLine();
				serial.write(input1);
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
}