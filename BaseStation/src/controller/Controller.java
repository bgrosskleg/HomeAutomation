package controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JApplet;
import javax.swing.event.EventListenerList;

import subscribers.CurrentModelSubscriber;
import model.CurrentModel;

public class Controller 
{	
	private static EventListenerList currentModelSubscriberList = new EventListenerList();
	
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
	       
	    	//.accept() sits and waits for connection request on serverSocket then returns 
	    	//the requesting socket to establish connection
	    	commandSocket = commandServerSocket.accept();
	    	objectSocket = objectServerSocket.accept();
	        
	    	//If being run locally by eclipse, load the model from local path
	    	//If being run on webserver on Pi, load model from the /var/www path
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
	        
	        //Initialize the command stream writer and reader
	        outToClient = new PrintWriter(commandSocket.getOutputStream(), true);
	        inFromClient = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
	        
	        //Initialize the object stream
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
		Controller.application = application;
	}
	
	
	//CURRENT MODEL**********************************************
	public static CurrentModel getCM()
	{
		return CM;
	}

	public static void setCM(CurrentModel cM) 
	{
		CM = cM;
		currentModelChanged();
	}
	
	
	
	//CURRENT TOOL***********************************************
	public static String getCurrentTool() {
		return currentTool;
	}

	public static void setCurrentTool(String currentTool) {
		Controller.currentTool = currentTool;
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
		Controller.commandServerSocket = socket;
	}
	
	public static ServerSocket getObjectServerSocket() {
		return objectServerSocket;
	}
	
	public static void setObjectServerSocket(ServerSocket socket) {
		Controller.objectServerSocket = socket;
	}	


	//Sockets
	public static Socket getCommandSocket() {
		return commandSocket;
	}
	
	public static void setCommandSocket(Socket socket) {
		Controller.commandSocket = socket;
	}
	
	public static Socket getObjectSocket() {
		return objectSocket;
	}
	
	public static void setObjectSocket(Socket socket) {
		Controller.objectSocket = socket;
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
		return Controller.modelPath;
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
    		oos.writeObject(Controller.getCM());
    		oos.close();
    		fos.close();
    		System.out.println("Model saved to file!");
    	} 
    	catch (Exception e1) 
    	{
    		//Close any open streams
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
			} 
    		catch (Exception e) 
    		{
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
    	
    	FileInputStream fis = null;
		ObjectInputStream ois = null;
		
        try 
        {
        	fis = new FileInputStream(modelPath);
            ois = new ObjectInputStream (fis);
			CurrentModel result = (CurrentModel)ois.readObject();
			ois.close();
			fis.close();
			System.out.println("Model loaded from file!");
			return result;
		} 
        catch (Exception e1) 
        {
        	//Close any open streams
        	try
        	{
        		if(ois != null)
        			{
        		ois.close();
        			}
    		
        		if(fis != null)
        		{
        				fis.close();
        		}
        		System.out.println("Existing file stream closed.");
			} 
    		catch (Exception e) 
    		{
				System.out.println("Failure closeing file stream");
				e.printStackTrace();
    		}
    		
        	System.out.println("Model not loaded from file!");
			System.out.println("New model created.");
			return new CurrentModel();
		}
	}
    
    
    
  //SUBSCRIBERS************************************************


  	//Add subscribers
  	public static void addCurrentModelSubscriber(CurrentModelSubscriber subscriber)
  	{
  		currentModelSubscriberList.add(CurrentModelSubscriber.class, subscriber);
  	}

  	//Remove subscriber
  	public static void removeCanvasSubscriber(CurrentModelSubscriber subscriber)
  	{
  		currentModelSubscriberList.remove(CurrentModelSubscriber.class, subscriber);
  	}

  	public static void currentModelChanged()
  	{
  		//Notify Listeners
  		Object[] subscribers = currentModelSubscriberList.getListenerList();
  		for (int i = 0; i < subscribers.length; i = i+2) {
  			if (subscribers[i] == CurrentModelSubscriber.class) {
  				((CurrentModelSubscriber) subscribers[i+1]).currentModelChanged();
  			}
  		}
  	}
}