package firmware;
/*
 * Extended from Oracle's Network Client Applet Example to ensure network operation
 * Written by: Brian Grosskleg
 * Date: Jan. 4 2014
 */  

/* 
 * Java(TM) SE 6
 * Code is the same as 1.0.
 */


class Firmware 
{
    public static void main(String[] args) 
    {
    	//Start server thread
        new ServerThread().start();
    }
}
