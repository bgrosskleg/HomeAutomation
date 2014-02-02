package controller;


/* 
 * Java(TM) SE 6
 * Code is the same as 1.0.
 */


class Firmware 
{
    public static void main(String[] args) 
    {
    	//Start server thread
        new CommunicationThread().start();
    }
}
