package view;
/*
 * Copyright (c) 1995, 2009, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Java(TM) SE 6 version.
 * SwingWorker can be downloaded at:
 * https://swingworker.dev.java.net/
 * SwingWorker is included in Java(TM) SE 6.
 */

import javax.swing.*;

import model.CurrentModel;
import controller.CanvasMouseAdapter;
import controller.ClientController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Applet extends JApplet
{

	private static final long serialVersionUID = 1L;
	
 
    public void init() 
    {
    	//Set's up socket to server
    	ClientController.initializeNetworkConnection(this);
    	
    	//Creates canvas and adds subscribers
    	Canvas canvas = new Canvas();
    	ClientController.setCanvas(canvas);
    	CanvasMouseAdapter.addTempObjectSubscriber(canvas);
    	 
    	//Get model from server
    	ClientController.requestModel();
    	
    	//If server did not send model, create blank model
    	if(ClientController.getCM() == null)
    	{
        	System.out.println("Server not sending model!");
        	ClientController.setCM(new CurrentModel());
        }
    	
        //Set up the user interface.
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't successfully complete");
            e.printStackTrace();
        }
        
        
        //Start automatic update
      		int delay = 100; //milliseconds
      		ActionListener taskPerformer = new ActionListener() 
      		{
      			@Override
      			public void actionPerformed(ActionEvent e) 
      			{
      				//Request model and notify canvas to repaint
      				ClientController.requestModel();
      				ClientController.getCanvas().currentModelChanged();
      			}
      		};

      		new Timer(delay, taskPerformer).start();
      		
    }

    private void createGUI() 
    {    	
    	//Add instructions pane
    	add(new Instructions(), BorderLayout.NORTH);
    
    	//Add canvas
    	add(ClientController.getCanvas(), BorderLayout.CENTER);
    	
    	//Add toolbar
    	add(new CanvasToolbar(), BorderLayout.EAST);
    	
    	setPreferredSize(new Dimension(1000,950));
    	setMinimumSize(getPreferredSize());
    	setMaximumSize(getPreferredSize());
    	setSize(getPreferredSize());
    } 
    
    
    @Override
    public void destroy()
    {
    	/*
    	http://docs.oracle.com/javase/7/docs/api/java/applet/Applet.html#destroy()
    	public void destroy()
    	Called by the browser or applet viewer to inform this applet that it is being reclaimed and that it should destroy any resources that it has allocated. The stop method will always be called before destroy.
    	A subclass of Applet should override this method if it has any operation that it wants to perform before it is destroyed. For example, an applet with threads would use the init method to create the threads and the destroy method to kill them.

    	The implementation of this method provided by the Applet class does nothing.

    	See Also:
    	init(), start(), stop()
    	*/
    	
    	//Close streams and sockets opened by applet, MUST BE DONE, ie default() does nothing if not defined!
    	
    	System.out.println("Closing applet streams and socket...");
    	
    	try 
    	{
 			if(ClientController.getOIS() != null)
			{ClientController.getOIS().close();}
    	} 
    	catch (Exception e) 
    	{
			System.out.println("Failure closing OIS (Object Input Stream)");
			e.printStackTrace();
		}   
				
    	try
    	{
    		if(ClientController.getOOS() != null)
			{ClientController.getOOS().close();}
    	}
    	catch (Exception e) 
    	{
			System.out.println("Failure closing OOS (Object Output Stream)");
			e.printStackTrace();
		}
				
    	try
    	{
			if(ClientController.getOutToServer() != null)
			{ClientController.getOutToServer().close();}
    	}
    	catch (Exception e) 
	    	{
				System.out.println("Failure closing OutToServer (Print Writer)");
				e.printStackTrace();
			}
				
    	try
    	{
			if(ClientController.getInFromServer() != null)
			{ClientController.getInFromServer().close();}
    	}
    	catch (Exception e) 
    	{
			System.out.println("Failure closing InFromServer (Buffered Reader)");
			e.printStackTrace();
		}
    	
    	try
    	{
			if(ClientController.getCommandSocket() != null)
			{ClientController.getCommandSocket().close();}
			System.out.println("Command socket closed.");
		} 
    	catch (Exception e) 
    	{
			System.out.println("Failure closing command socket!");
			e.printStackTrace();
		}    	
    	
    	try
    	{
			if(ClientController.getObjectSocket() != null)
			{ClientController.getObjectSocket().close();}
			System.out.println("Object socket closed.");
		} 
    	catch (Exception e) 
    	{
			System.out.println("Failure closing object socket!");
			e.printStackTrace();
		}    	
    }
}


