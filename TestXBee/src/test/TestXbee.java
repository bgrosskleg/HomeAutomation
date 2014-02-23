package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

public class TestXbee 
{	
	//Serial object
	public static Serial serial;
	
	static Semaphore full = new Semaphore(0);
	//static Semaphore empty = new Semaphore(0);
	static Semaphore lock = new Semaphore(1);
	static String temp = "";
	
	public static void main(String[] args) 
	{
		try 
        {
			//Create serial object
			serial = SerialFactory.createInstance();
			
			//Add data listener
			serial.addListener(new SerialDataListener() 
			{
				//Semaphore sem = new Semaphore(1);
				@Override
				public void dataReceived(SerialDataEvent event) 
				{
					//System.out.println("Hello world.");
					lock.acquireUninterruptibly();
					temp += event.getData();
					if(temp.length() > 100)
					{
						full.release();
						/*System.out.println(temp);
						System.out.println();
						System.out.println();
						temp = "";*/
					}
					//System.out.println(event.getData());
					lock.release();
				}
				
			});
			
		    // wait 1 second before opening
			Thread.sleep(1000);
			
	
	        // open the default serial port provided on the GPIO header
	        serial.open(Serial.DEFAULT_COM_PORT, 1200);
	        
	        // wait 1 second before continuing
			Thread.sleep(1000);
        
			
			//Create input reader from console in
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String waitTime;
			String input1;
			
			for(;;)
			{
				//Ask for delay between +++ and command (used in debugging)
				System.out.println("Wait time ms (typ. 50ms):");
				waitTime = br.readLine();
				
				//If blank, use 50ms
				if((waitTime.isEmpty()))
				{waitTime = "50";}
				
				//If exit, break to sending messages
				if(waitTime.equals("exit"))
				{
					break;
				}
				
				//Ask the user what they want to configure
				System.out.println("CONFIG:");
				input1 = br.readLine();
				
				//If exit, break to sending messages
				if(input1.equals("exit"))
				{
					break;
				}
				
				//Send enter command mode
				System.out.println("SENT: +++");
				serial.write("+++");
				
				//Wait set amount
				Thread.sleep(Integer.valueOf(waitTime));
				
				//Send CONFIG
				System.out.println("SENT: " + input1 + '\r');
				serial.write(input1 + '\r');
				
				//REPEAT
			}	
			
			for(;;)
			{				
				//Type message to sent out serial
				System.out.println("MESSAGE:");
				//input1 = br.readLine();
				
				//Send message
				//serial.write(input1);
				
				full.acquireUninterruptibly();
				lock.acquireUninterruptibly();
				System.out.println("Hello world");
				System.out.println(temp.length());
				String temp2 = temp;
				System.out.println(temp2.getBytes());
				System.out.println();
				temp = "";
				lock.release();
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
}