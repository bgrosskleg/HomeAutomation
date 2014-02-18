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
			String input1;
			String input2;
			
			for(;;)
			{
				System.out.println("INPUT1:");
				input1 = br.readLine();
				
				System.out.println("INPUT2:");
				input2 = br.readLine();
				
				serial.write(input1);
				Thread.sleep(10);
				serial.write(input2);
			}	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
}
