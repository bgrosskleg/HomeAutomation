package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class AppletCommunicationThread extends GenericCommunicationThread
{	
	private static final long serialVersionUID = 1L;
	  	
	private URL codebase;
    	
	public AppletCommunicationThread(AppletController cntrl) 
    {
		super("AppletCommunicationThread", cntrl);
		
		//Grab codebase to support loading files from host
		//This finds the codebase and host name that the application was loaded from (ie. the server)
		codebase = cntrl.getClientApplet().getCodeBase();
		System.out.println("Codebase: " + codebase);		
			
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

		activeObjectPort = 65001;
		passiveObjectPort = 65000;
		
		activeCommandPort = 65003;
		passiveCommandPort = 65002;
    }
	
	public void initializeConnection() throws IOException
	{ 
		connected = false;

		System.out.println("Initializing network connection...");

		//TCP Socket Setup
		//http://lycog.com/java/tcp-object-transmission-java/

		//socket = new Socket(host, serverPort);
		activeObjectSocket = new Socket(InetAddress.getByName(getCodebase().getHost()), activeObjectPort);
		passiveObjectSocket = new Socket(InetAddress.getByName(getCodebase().getHost()), passiveObjectPort);
		activeCommandSocket = new Socket(InetAddress.getByName(getCodebase().getHost()), activeCommandPort);
		passiveCommandSocket = new Socket(InetAddress.getByName(getCodebase().getHost()), passiveCommandPort);

		
		//Opening streams, CAREFUL, ObjectInputStream() blocks, create output first, flush then create input
		//http://stackoverflow.com/questions/5658089/java-creating-a-new-objectinputstream-blocks
		//http://stackoverflow.com/questions/14110986/new-objectinputstream-blocks
		//http://www.velocityreviews.com/forums/t138056-why-does-objectinputstream-constructor-block-reading-a-header.html
		
		
		//Initialize the passive object streams
		passiveObjectStreamOut = new ObjectOutputStream(passiveObjectSocket.getOutputStream());
		passiveObjectStreamOut.flush();
		passiveObjectStreamIn = new ObjectInputStream(passiveObjectSocket.getInputStream());

		//Initialize the active object streams
		activeObjectStreamOut = new ObjectOutputStream(activeObjectSocket.getOutputStream());
		activeObjectStreamOut.flush();
		activeObjectStreamIn = new ObjectInputStream(activeObjectSocket.getInputStream());

		
		//Initialize the active stream writer and reader
		activeCommandStreamOut = new PrintWriter(activeCommandSocket.getOutputStream(), true);
		activeCommandStreamIn = new BufferedReader(new InputStreamReader(activeCommandSocket.getInputStream()));

		//Initialize the passive stream writer and reader
		passiveCommandStreamOut = new PrintWriter(passiveCommandSocket.getOutputStream(), true);
		passiveCommandStreamIn = new BufferedReader(new InputStreamReader(passiveCommandSocket.getInputStream()));

		connected = true;
		System.out.println("Network connection initialized!");	
	}
	

	//NETWORKING PARAMETERS*******************************************************
  		
	//Get codebase
	public URL getCodebase() 
	{
		return codebase;
	}
}