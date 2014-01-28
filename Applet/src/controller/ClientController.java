package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.event.EventListenerList;



//import communication.RequestModelWorker;
//import communication.SendModelWorker;
import subscribers.MainControllerSubscriber;
import model.CurrentModel;

public class ClientController 
{
	private static EventListenerList subscriberList = new EventListenerList();
	
	private static JApplet application;
	
	private static CurrentModel CM;
	
	private static String currentTool;
	
	private static URL codebase;
	private static Socket commandSocket;
	private static Socket objectSocket;
    private static String host;
    private static InetAddress address;
    private static final int serverPort = 65000;
    private static PrintWriter outToServer;
    private static BufferedReader inFromServer;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    
    //private static RequestModelWorker requestModel = new RequestModelWorker();
    //private static SendModelWorker	sendModel = new SendModelWorker();
   	
    public static void initializeNetworkConnection(JApplet application)
    { 
    	try 
    	{
	    	System.out.println("Initializing network connection...");
	    	ClientController.application = application;
			
		    //Initialize networking stuff.
			codebase = application.getCodeBase();
		    host = application.getCodeBase().getHost();
		    
		    System.out.println("Codebase: " + codebase);
		    System.out.println("Model path: " + codebase.getPath());
		    System.out.println("Host: " + host);
		
		    address = InetAddress.getByName(host);
		    System.out.println("Address: " + address);
		   
		    
		    //TCP Socket Setup
		    //http://lycog.com/java/tcp-object-transmission-java/
	    
		    commandSocket = new Socket(InetAddress.getByName(host), serverPort);
		    objectSocket = new Socket(InetAddress.getByName(host), serverPort);
			//socket = new Socket(host, serverPort);
			outToServer = new PrintWriter(commandSocket.getOutputStream(), true);
			inFromServer = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
			oos = new ObjectOutputStream(objectSocket.getOutputStream());
			ois = new ObjectInputStream(objectSocket.getInputStream());
			
	        System.out.println("Network connection initialized!");
	        
	    } catch (Exception e1) {
			System.out.println("Failure initializing TCP socket and streams!");
			//e1.printStackTrace();
		}
    }
		
    //REFERENCE TO APPLICATION***********************************
	public static JApplet getApplication() {
		return application;
	}

	public static void setApplication(JApplet application) {
		ClientController.application = application;
	}
	
	
	//CURRENT MODEL**********************************************
	public static CurrentModel getCM() {
		return CM;
	}

	public static void setCM(CurrentModel cM) 
	{
		CM = cM;
	}	
	
	public static void sendModel()
	{
		//Submit model to Pi
		try 
		{
			System.out.println("Sending model to server...");
			ClientController.getOutToServer().println("SENDINGMODEL");
			if(ClientController.getInFromServer().readLine().equals("OKAY"))
			{
				//ClientController.getOOS().reset();
				ClientController.getOOS().writeObject(ClientController.getCM());
				ClientController.getOOS().flush();
				ClientController.getOOS().reset();
				if(ClientController.getInFromServer().readLine().equals("OKAY"))
				{
					System.out.println("Model save complete.");
				}
				else
				{
					System.out.println("Model did not save correctly.");
				}
			}
		} 
		catch (Exception e1) 
		{
			System.out.println("Failure sending model to server");
			//e1.printStackTrace();
		}
	}
	
	
	public static void requestModel()
	{
		//Get model from Pi
		try {
			System.out.println("Requesting model...");
			ClientController.getOutToServer().println("REQUESTMODEL");
				
			if(ClientController.getInFromServer().readLine().equals("SENDINGMODEL"))
			{
				System.out.println("Model incoming...");
						
				ClientController.setCM((CurrentModel) ClientController.getOIS().readObject());
							
				System.out.println("Get's here 1");
				ClientController.getOutToServer().println("OKAY");
				
				System.out.println("Model recieved.");
			}
			
		} catch (Exception e1) {
			ClientController.getOutToServer().println("FAIL");
			System.out.println("Failure requesting model from server!");
			e1.printStackTrace();
		}
	}
	
	//CURRENT TOOL***********************************************
	public static String getCurrentTool() {
		return currentTool;
	}

	public static void setCurrentTool(String currentTool) {
		ClientController.currentTool = currentTool;
		clientControllerChanged();
	}
	
	
	
	//NETWORKING PARAMETERS*******************************************************
	
	//Codebase
	public static URL getCodebase() {
		return codebase;
	}

	public static void setCodebase(URL url) {
		ClientController.codebase = url;
	}

	//Server port number
	public static int getServerPort() {
		return serverPort;
	}

	//Command socket
	public static Socket getCommandSocket() {
		return commandSocket;
	}

	public static void setCommandSocket(Socket socket) {
		ClientController.commandSocket = socket;
		clientControllerChanged();
	}
	
	//Object socket
	public static Socket getObjectSocket() {
		return objectSocket;
	}

	public static void setObjectSocket(Socket socket) {
		ClientController.objectSocket = socket;
		clientControllerChanged();
	}

	//Host name
	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		ClientController.host = host;
		clientControllerChanged();
	}

	//Internet address
	public static InetAddress getAddress() {
		return address;
	}

	public static void setAddress(InetAddress address) {
		ClientController.address = address;
		clientControllerChanged();
	}
		
	//Input/output TCP Streams
	public static PrintWriter getOutToServer() {
		return outToServer;
	}

	public static BufferedReader getInFromServer() {
		return inFromServer;
	}
	
	public static ObjectOutputStream getOOS() {
		return oos;
	}

	public static ObjectInputStream getOIS() {
		return ois;
	}
	
	/*public static RequestModelWorker getRequestWorker()
	{
		return requestModel;
	}
	
	public static SendModelWorker getSendWorker()
	{
		return sendModel;
	}*/
		
	
	//SUBSCRIBERS************************************************


	//Add subscribers
	public static void addMainControllerSubscriber(MainControllerSubscriber subscriber)
	{
		subscriberList.add(MainControllerSubscriber.class, subscriber);
	}

	//Remove subscriber
	public static void removeMainControllerSubscriber(MainControllerSubscriber subscriber)
	{
		subscriberList.remove(MainControllerSubscriber.class, subscriber);
	}

	public static void clientControllerChanged()
	{
		//Notify Listeners
		Object[] subscribers = subscriberList.getListenerList();
		for (int i = 0; i < subscribers.length; i = i+2) {
			if (subscribers[i] == MainControllerSubscriber.class) {
				((MainControllerSubscriber) subscribers[i+1]).mainControllerChanged();
			}
		}
	}	
}
