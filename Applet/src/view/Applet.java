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

import controller.AppletCommunicationThread;
import controller.AppletController;

import java.awt.*;

public class Applet extends JApplet
{

	private static final long serialVersionUID = 1L;
	
	private static AppletController controller;
	private static AppletCommunicationThread appComThread;
 
    public void init() 
    {
    	//Create applet controller and canvas
    	controller = new AppletController(this, new Canvas());
    	
    	//Start communication thread
    	try 
    	{
    		appComThread = new AppletCommunicationThread(controller);
    		appComThread.start();
		}
    	catch (Exception e1) 
    	{
    		System.err.println("Error creating AppletCommunicationThread!");
			System.err.println("Running locally!");
			e1.printStackTrace();
		}  	
    	
        //Set up the user interface.
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try 
        {
            SwingUtilities.invokeAndWait(new Runnable() 
            {
                public void run() 
                {
                    createGUI();
                }
            });
        }
        catch (Exception e) 
        {
            System.err.println("createGUI didn't successfully complete");
            e.printStackTrace();
        } 		
    }

    private void createGUI() 
    {    	
    	//Add instructions pane
    	add(new Instructions(), BorderLayout.NORTH);
    
    	//Add canvas
    	add(controller.getCanvas(), BorderLayout.CENTER);
    	
    	//Add toolbar
    	add(new CanvasToolbar(), BorderLayout.EAST);
    	
    	setPreferredSize(new Dimension(1000,950));
    	setMinimumSize(getPreferredSize());
    	setMaximumSize(getPreferredSize());
    	setSize(getPreferredSize());
    } 
    
        
    public static AppletController getController()
    {
    	return controller;
    }
    
    public static AppletCommunicationThread getComThread()
    {
    	return appComThread;
    }
}


