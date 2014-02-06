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
    private String host;
    private InetAddress address;   	
    	
	public AppletCommunicationThread(AppletController cntrl) 
    {
		super(cntrl, "AppletCommunicationThread");
			
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
		activePort = 65002;
		passivePort = 65001;
    }
	
	public void initializeConnection() throws IOException
	{ 
		connected = false;

		System.out.println("Initializing network connection...");

		//Grab codebase and host.
		//This finds the codebase and host name that the application was loaded from (ie. the server)
		codebase = ((AppletController) controller).getApplication().getCodeBase();
		host = ((AppletController) controller).getApplication().getCodeBase().getHost();
		//System.out.println("Codebase: " + codebase);
		//System.out.println("Host: " + host);

		//Get IP address of host server
		address = InetAddress.getByName(host);
		//System.out.println("Address: " + address);

		//TCP Socket Setup
		//http://lycog.com/java/tcp-object-transmission-java/

		//socket = new Socket(host, serverPort);
		objectSocket = new Socket(InetAddress.getByName(host), objectPort);
		activeSocket = new Socket(InetAddress.getByName(host), activePort);
		passiveSocket = new Socket(InetAddress.getByName(host), passivePort);

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
		System.out.println("Network connection initialized!");	
	}
	

	//NETWORKING PARAMETERS*******************************************************
  		
	//Get codebase
	public URL getCodebase() {
		return codebase;
	}
	
	//Get host
	public String getHost() {
		return host;
	}
	
	//Get address
	public InetAddress getAddress() {
		return address;
	}
}