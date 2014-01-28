package controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JApplet;
import javax.swing.event.EventListenerList;

import subscribers.MainControllerSubscriber;
import model.CurrentModel;

public class ServerController 
{
	private static EventListenerList subscriberList = new EventListenerList();
	
	private static JApplet application;
	
	private static CurrentModel CM;
	
	private static String currentTool;
	
	private static ServerSocket commandServerSocket;
	private static ServerSocket objectServerSocket;
	private static Socket commandSocket;
	private static Socket objectSocket;
    private static int commandPort = 65000;
    private static int objectPort = 65001;

    private static PrintWriter outToClient;
    private static BufferedReader inFromClient;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    
    private static String modelPath;
   	
    public static void initializeSocket()
    {
	    try 
	    {	    	
	    	System.out.println("Initializing socket...");
	    	System.out.println("Waiting for request from applet...");   	
	       
	    	commandSocket = commandServerSocket.accept();
	    	objectSocket = objectServerSocket.accept();
	        
	        if(commandSocket.getLocalAddress().toString().equals("/127.0.0.1"))
	        {
	        	System.out.println("modelPath = C:/Users/Brian Grosskleg/Desktop/model.ser");
	        	modelPath = "C:/Users/Brian Grosskleg/Desktop/model.ser";
	        }
	        else if(commandSocket.getLocalAddress().toString().equals("/172.16.1.85"))
	        {
	        	System.out.println("modelPath = var/www/model.ser");
	        	modelPath = "/var/www/model.ser";
	        }
	        else
	        {
	        	System.err.println("Could not set model path.");
	        	throw new Exception();
	        }
	        CM = loadModelfromFile();
	        
	        
	        outToClient = new PrintWriter(commandSocket.getOutputStream(), true);
	        inFromClient = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
	        oos = new ObjectOutputStream(objectSocket.getOutputStream());
	        ois = new ObjectInputStream(objectSocket.getInputStream());
	        System.out.println("Initializing socket complete.");
	    } 
	    catch (Exception e) 
	    {
	        System.err.println("Could not initialize socket.");
	    }   
    }
    
		
    //REFERENCE TO APPLICATION***********************************
	public static JApplet getApplication() {
		return application;
	}

	public static void setApplication(JApplet application) {
		ServerController.application = application;
	}
	
	
	//CURRENT MODEL**********************************************
	public static CurrentModel getCM() {
		return CM;
	}

	public static void setCM(CurrentModel cM) 
	{
		CM = cM;
		//CM.currentModelChanged();
	}
	
	
	
	//CURRENT TOOL***********************************************
	public static String getCurrentTool() {
		return currentTool;
	}

	public static void setCurrentTool(String currentTool) {
		ServerController.currentTool = currentTool;
		serverControllerChanged();
	}
	
	
	
	//NETWORKING PARAMETERS*******************************************************
	
	//Command port number
	public static int getCommandPort() {
		return commandPort;
	}
	
	//Command port number
		public static int getObjectPort() {
			return objectPort;
		}
		
	//ServerSocket
	public static ServerSocket getCommandServerSocket() {
		return commandServerSocket;
	}
	
	public static void setCommandServerSocket(ServerSocket socket) {
		ServerController.commandServerSocket = socket;
		serverControllerChanged();
	}
	
	public static ServerSocket getObjectServerSocket() {
		return objectServerSocket;
	}
	
	public static void setObjectServerSocket(ServerSocket socket) {
		ServerController.objectServerSocket = socket;
		serverControllerChanged();
	}	


	//Sockets
	public static Socket getCommandSocket() {
		return commandSocket;
	}
	
	public static void setCommandSocket(Socket socket) {
		ServerController.commandSocket = socket;
		serverControllerChanged();
	}
	
	public static Socket getObjectSocket() {
		return objectSocket;
	}
	
	public static void setObjectSocket(Socket socket) {
		ServerController.objectSocket = socket;
		serverControllerChanged();
	}


				
	
	//Input/output TCP Streams
	public static PrintWriter getOutToClient() {
		return outToClient;
	}

	public static BufferedReader getInFromClient() {
		return inFromClient;
	}
	
	public static ObjectOutputStream getOOS() {
		return oos;
	}

	public static ObjectInputStream getOIS() {
		return ois;
	}
	
	//Model path
	public static String getModelPath() {
		return ServerController.modelPath;
	}
	
	
	
	//MODEL HANDLING*********************************************
	
	public static void saveModeltoDisk()
    {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
    	try 
    	{
    		fos = new FileOutputStream(modelPath);
    		oos = new ObjectOutputStream(fos);
    		oos.writeObject(ServerController.getCM());
    		oos.close();
    		fos.close();
    		System.out.println("Model saved to file!");
    	} 
    	catch (Exception e1) 
    	{
    		try
    		{
	    		if(fos != null)
	    		{
	    			fos.close();
	    		}
	    		if(oos != null)
	    		{
	    			oos.close();
	    		}
	    		System.out.println("Existing file stream closed.");
				} catch (Exception e) {
				System.out.println("Failure closeing file stream");
				e.printStackTrace();
		}
    		System.out.println("Model not saved to file!");
    		e1.printStackTrace();
    	}	
    }
	
    private static CurrentModel loadModelfromFile() 
    {
        //De-serialize the model
        try 
        {
        	InputStream fileStream = new FileInputStream(modelPath);
            InputStream buffer = new BufferedInputStream(fileStream);
            ObjectInput input = new ObjectInputStream (buffer);
			CurrentModel result = (CurrentModel)input.readObject();
			input.close();
			System.out.println("Model loaded from file!");
			return result;
		} 
        catch (Exception e1) 
        {
        	System.out.println("Model not loaded from file!");
			System.out.println("New model created.");
			return new CurrentModel();
		}
	}
    
    
   

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

	public static void serverControllerChanged()
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
