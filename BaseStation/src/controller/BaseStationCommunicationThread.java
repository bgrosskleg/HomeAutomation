package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class BaseStationCommunicationThread extends CommunicationThread
{    
    private static ServerSocket objectServerSocket;
	private static ServerSocket activeServerSocket;
	private static ServerSocket passiveServerSocket;
    	
	public BaseStationCommunicationThread(BaseStationController cntrl) throws Exception 
    {
		super(cntrl, "ServerCommunicationThread");
			
		//Set right port numbers
		//Server
		//objectPort = 65000
		//activePort = 65001
		//passivePort = 65002

		//Applet
		//objectPort = 65000
		//activePort = 65002
		//passivePort = 65001
		
		//Active stream of one must be paired to the passive stream of the other to avoid readLine() collisions
		
		objectPort = 65000;
		activePort = 65001;
		passivePort = 65002;

		//Create connection to applet
		initializeConnection();
    }
	
	public void initializeConnection()
	{ 
		try 
		{
			connected = false;
			
			if(objectServerSocket == null)
			{objectServerSocket = new ServerSocket(objectPort);}
			if(activeServerSocket == null)
			{activeServerSocket = new ServerSocket(activePort);}		
			if(passiveServerSocket == null)
			{passiveServerSocket = new ServerSocket(passivePort);}

			System.out.println("Server object stream listening on port: " + objectPort);
			System.out.println("Server active stream listening on port: " + activePort);
			System.out.println("Server passive stream listening on port: " + passivePort);

			//Wait for applet connection
			System.out.println("Initializing socket...");
			System.out.println("Waiting for request from applet...");   	

			//.accept() sits and waits for connection request on serverSocket then returns 
			//the requesting socket to establish connection
			objectSocket = objectServerSocket.accept();
			activeSocket = activeServerSocket.accept();
			passiveSocket = passiveServerSocket.accept();

			//Initialize the object streams
			objectStreamOut = new ObjectOutputStream(objectSocket.getOutputStream());
			objectStreamIn = new ObjectInputStream(objectSocket.getInputStream());

			//Initialize the active stream writer and reader
			activeStreamOut = new PrintWriter(activeSocket.getOutputStream(), true);
			activeStreamIn = new BufferedReader(new InputStreamReader(activeSocket.getInputStream()));

			//Initialize the passive stream writer and reader
			passiveStreamOut = new PrintWriter(passiveSocket.getOutputStream(), true);
			passiveStreamIn = new BufferedReader(new InputStreamReader(passiveSocket.getInputStream()));


			connected = true;
			System.out.println("Initializing sockets complete.");
		} 
		catch (Exception e) 
		{
			System.err.println("Failure initializing TCP socket and streams!");
			closeStreams();
			//e.printStackTrace();
		}
	}
	
	//NETWORKING PARAMETERS*******************************************************
  	
	//SERVER SOCKETS************************************************
  	
  	//Object serverSocket
  	public static ServerSocket getObjectServerSocket() {
  	  	return objectServerSocket;
  	}
  	
  	//Active serverSocket
  	public static ServerSocket getActiveServerSocket() {
  		return activeServerSocket;
  	}
  	
  	//Passive serverSocket
  	public static ServerSocket getPassiveServerSocket() {
  		return passiveServerSocket;
  	}
}