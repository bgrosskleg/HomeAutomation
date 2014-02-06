package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class BaseStationCommunicationThread extends GenericCommunicationThread
{    
	private static final long serialVersionUID = 1L;
	
	private static ServerSocket activeObjectServerSocket;
	private static ServerSocket passiveObjectServerSocket;
	private static ServerSocket activeCommandServerSocket;
	private static ServerSocket passiveCommandServerSocket;
    	
	public BaseStationCommunicationThread(BaseStationController cntrl)
    {
		super("BaseStationCommunicationThread", cntrl);
			
		//Set right port numbers
		//Server
		//activeObjectPort = 65000
		//passiveObjectPort 65001
		//activePort = 65002
		//passivePort = 65003

		//Applet
		//activeObjectPort = 65001
		//passiveObjectPort = 65000
		//activeCommandPort = 65003
		//passiveCommandPort = 65002
		
		//Active stream of one must be paired to the passive stream of the other to avoid readLine() collisions
		
		activeObjectPort = 65000;
		passiveObjectPort = 65001;
		
		activeCommandPort = 65002;
		passiveCommandPort = 65003;
    }
	
	public void initializeConnection() throws IOException
	{ 
		connected = false;

		//Create serverSockets
		if(activeObjectServerSocket == null)
		{activeObjectServerSocket = new ServerSocket(activeObjectPort);}

		if(passiveObjectServerSocket == null)
		{passiveObjectServerSocket = new ServerSocket(passiveObjectPort);}	

		if(activeCommandServerSocket == null)
		{activeCommandServerSocket = new ServerSocket(activeCommandPort);}

		if(passiveCommandServerSocket == null)
		{passiveCommandServerSocket = new ServerSocket(passiveCommandPort);}

		System.out.println("Server active object stream listening on port: " + activeObjectPort);
		System.out.println("Server passive object stream listening on port: " + passiveObjectPort);
		System.out.println("Server active command stream listening on port: " + activeCommandPort);
		System.out.println("Server passive command stream listening on port: " + passiveCommandPort);



		//Wait for applet connection
		System.out.println("Initializing socket...");
		System.out.println("Waiting for request from applet...");   	

		//.accept() sits and waits for connection request on serverSocket then returns 
		//the requesting socket to establish connection
		activeObjectSocket = activeObjectServerSocket.accept();
		System.out.println("ActiveObject connection made!");

		passiveObjectSocket = passiveObjectServerSocket.accept();
		System.out.println("passiveObject connection made!");

		activeCommandSocket = activeCommandServerSocket.accept();
		System.out.println("activeCommand connection made!");

		passiveCommandSocket = passiveCommandServerSocket.accept();
		System.out.println("passiveCommand connection made!");



		//Opening streams, CAREFUL, ObjectInputStream() blocks, create output first, flush then create input
		//http://stackoverflow.com/questions/5658089/java-creating-a-new-objectinputstream-blocks
		//http://stackoverflow.com/questions/14110986/new-objectinputstream-blocks
		//http://www.velocityreviews.com/forums/t138056-why-does-objectinputstream-constructor-block-reading-a-header.html
		//ORDER IS IMPORTANT TO PREVENT DEADLOCK
		
		//MUST INITIALIZE ACTIVE-PASSIVE PAIRS TOGETHER TO AVOID DEADLOCK WITH BLOCKING INPUTOUTPUTSTREAM()
		
		//Initialize the active object streams
		activeObjectStreamOut = new ObjectOutputStream(activeObjectSocket.getOutputStream());
		activeObjectStreamOut.flush();
		activeObjectStreamIn = new ObjectInputStream(activeObjectSocket.getInputStream());
		System.out.println("ActiveObjectStream ready!");
		
		//Initialize the passive object streams
		passiveObjectStreamOut = new ObjectOutputStream(passiveObjectSocket.getOutputStream());
		passiveObjectStreamOut.flush();
		passiveObjectStreamIn = new ObjectInputStream(passiveObjectSocket.getInputStream());
		System.out.println("PassiveObjectStream ready!");

		//Initialize the active stream writer and reader
		activeCommandStreamOut = new PrintWriter(activeCommandSocket.getOutputStream(), true);
		activeCommandStreamIn = new BufferedReader(new InputStreamReader(activeCommandSocket.getInputStream()));
		System.out.println("ActiveCommandStream ready!");

		//Initialize the passive stream writer and reader
		passiveCommandStreamOut = new PrintWriter(passiveCommandSocket.getOutputStream(), true);
		passiveCommandStreamIn = new BufferedReader(new InputStreamReader(passiveCommandSocket.getInputStream()));
		System.out.println("PassiveCommandStream ready!");

		connected = true;
		System.out.println("Initializing sockets complete.");
		
		//Send model to ClientApplet
		sendModel(GenericCommunicationThread.HOUSEOBJECTS);
	}
	
	//NETWORKING PARAMETERS*******************************************************
  	
	//SERVER SOCKETS************************************************
  	
  	//Active object serverSocket
  	public static ServerSocket getActiveObjectServerSocket() {
  	  	return activeObjectServerSocket;
  	}
  	
  	//Passive object serverSocket
  	public static ServerSocket getPassiveObjectServerSocket() {
  	  	return passiveObjectServerSocket;
  	}
  	
  	//Active serverSocket
  	public static ServerSocket getActiveCommandServerSocket() {
  		return activeCommandServerSocket;
  	}
  	
  	//Passive serverSocket
  	public static ServerSocket getPassiveCommandServerSocket() {
  		return passiveCommandServerSocket;
  	}
}