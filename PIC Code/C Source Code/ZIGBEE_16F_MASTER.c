// C Code for Operating a XBee Zigbee Port in PIC16F family
// Author: Brian Grosskleg - 11079251
// Date: January 15, 2012

#ifndef ZIGBEE_CODE		//Macro gaurds to prevent redefining
#define ZIGBEE_CODE

#include <string.h>
#include "..\Header Files\USART_16F_MASTER.h"

void getRSSI(unsigned char RSSI[], unsigned char dBm[], unsigned char percentage[])
{
    //Write RSSI
    RSSI[0] = '0';
    RSSI[1] = 'x';
    
    flush_RCREG();
    send_USART_string("ATDB\r");

        for(char iter = 0; iter < 2; iter++)
        {RSSI[iter+2] = get_USART_char();}	// Save bytes from USART

    RSSI[4] = '\0';

    char RSSInum[2+1];
    RSSInum[0] = RSSI[2];
    RSSInum[1] = RSSI[3];
    RSSInum[2] = RSSI[4];

    //Write dBm
    //Convert recieved string to integer dBm value
    unsigned int dBmNum = (int) strtol (RSSInum, NULL, 16);
    char dBmbuf[2];
    sprintf(dBmbuf , "%d", dBmNum);
    dBm[0] = '-';
    dBm[1] = dBmbuf[0];
    dBm[2] = dBmbuf[1];
    dBm[3] = 'd';
    dBm[4] = 'B';
    dBm[5] = 'm';
    dBm[6] = '\0';

   
    //Write percentage
    //26dBM = 100%, 92dBm = 0%
    if(dBmNum > 25 && dBmNum <= 35)
    {
        percentage[0] = '('; percentage[1] = '1'; percentage[2] = '0'; percentage[3] = '0'; percentage[4] = '%'; percentage[5] = ')'; percentage[6] = '\0';
    }
    else if(dBmNum > 35 && dBmNum <= 45)
    {
        percentage[0] = '('; percentage[1] = '8'; percentage[2] = '5'; percentage[3] = '%'; percentage[4] = ')'; percentage[5] = '\0'; percentage[6] = '\0';
    }
    else if(dBmNum > 45 && dBmNum <= 55)
    {
        percentage[0] = '('; percentage[1] = '6'; percentage[2] = '0'; percentage[3] = '%'; percentage[4] = ')'; percentage[5] = '\0'; percentage[6] = '\0';
    }
    else if(dBmNum > 55 && dBmNum <= 65)
    {
        percentage[0] = '('; percentage[1] = '4'; percentage[2] = '5'; percentage[3] = '%'; percentage[4] = ')'; percentage[5] = '\0'; percentage[6] = '\0';
    }
    else if(dBmNum > 65 && dBmNum <= 75)
    {
        percentage[0] = '('; percentage[1] = '3'; percentage[2] = '0'; percentage[3] = '%'; percentage[4] = ')'; percentage[5] = '\0'; percentage[6] = '\0';
    }
    else if(dBmNum > 75 && dBmNum <= 85)
    {
        percentage[0] = '('; percentage[1] = '1'; percentage[2] = '5'; percentage[3] = '%'; percentage[4] = ')'; percentage[5] = '\0'; percentage[6] = '\0';
    }
    else if(dBmNum > 85 && dBmNum <= 95)
    {
        percentage[0] = '('; percentage[1] = '0'; percentage[2] = '%'; percentage[3] = ')'; percentage[4] = '\0'; percentage[5] = '\0'; percentage[6] = '\0';
    }
    else
    {
        //percentage = (unsigned char *) "ERROR";
    }
}

void getIdentification(unsigned char result[])
{
    //Request XBEE module 64-bit ID (16 hex values)
    //Populate result with all zero's as the Xbee module will shorten response if leading zeros,
    //This shortening means can't simply wait for 8 bytes to come in, must use timeout

    //Populate result with 0's
    for(char iter = 0; iter < 16; iter++)
    {result[iter] = '0';}

    //Add end of string character
    result[16] = '\0';

    //Request upper 8 hex portion (may not recieve all 8 values)
    flush_RCREG();
    send_USART_string("ATSH\r");

        char temp[8];
        char offset = 0;

        //Recieve 8 hex values (may not recieve all 8 values)
        //Ex: temp = [ 1 3 A 2 0 0 \0 \0]
        for(char iter = 0; iter < 8; iter++)
        {temp[iter] = get_USART_char_timeout();}	// Save bytes from USART

        //Find offset
        //Ex: offset = 2
        for(signed char iter = 7; iter >= 0; iter--)
        {
            if(temp[iter] == '\0' || temp[iter] == '\r')
            {offset++;}
        }

        //Populate result in correct position based on offset
        for(char iter = 0; iter < 8; iter++)
        {
            result[iter+offset] = temp[iter];
        }

    //Request lower 8 hex portion (may not recieve all 8 values)
    flush_RCREG();
    send_USART_string("ATSL\r");

        offset = 0;

        //Recieve 8 hex values (may not recieve all 8 values)
        //Ex: temp = [ 1 3 A 2 0 0 \0 \0]
        for(char iter = 0; iter < 8; iter++)
        {temp[iter] = get_USART_char_timeout();}	// Save bytes from USART

        //Find offset
        //Ex: offset = 2
        for(signed char iter = 7; iter >= 0; iter--)
        {
            if(temp[iter] == '\0' || temp[iter] == '\r')
            {offset++;}
        }

        //Populate result in correct position based on offset
        for(char iter = 0; iter < 8; iter++)
        {
            result[iter+8+offset] = temp[iter];
        }
}

void setDestination(unsigned char const destination[])
{
    //Set destination address
    //send_USART_string("ATDH0013A200\r");
    //send_USART_string("ATDL408B4278\r");

    //Set high address
    flush_RCREG();
    send_USART_string("ATDH");

    //http://stackoverflow.com/questions/4214314/get-a-substring-of-a-char
    char subbuff[9];
    memcpy( subbuff, &destination[0], 8 );
    subbuff[8] = '\0';
    send_USART_string(subbuff);

    send_USART_string("\r");

        //Recieve 'OK'
        for(char iter = 0; iter < 2; iter++)
        {BYTES_BUFFER[iter] = get_USART_char();}	// Save bytes from USART


    //Set low address
    flush_RCREG();
    send_USART_string("ATDL");

    //http://stackoverflow.com/questions/4214314/get-a-substring-of-a-char
    char subbuff2[9];
    memcpy( subbuff2, &destination[8], 8 );
    subbuff2[8] = '\0';
    send_USART_string(subbuff2);

    send_USART_string("\r");

        //Recieve 'OK'
        for(char iter =  0; iter < 2; iter++)
        {BYTES_BUFFER[iter] = get_USART_char();}	// Save bytes from USART

    __delay_ms(10);
}


void enterCommandMode(void)
{
    /*
    Page 31, ZB User Manual
    To Enter AT Command Mode:
    Send the 3-character command sequence ?+++? and observe guard times before and after the command
    characters. [Refer to the ?Default AT Command Mode Sequence? below.]
    Default AT Command Mode Sequence (for transition to Command Mode):
    ?No characters sent for one second [GT (Guard Times) parameter = 0x3E8]
    ?Input three plus characters (?+++?) within one second [CC (Command Sequence Character) parameter
    = 0x2B.]
    ?No characters sent for one second [GT (Guard Times) parameter = 0x3E8]
    Once the AT command mode sequence has been issued, the module sends an "OK\r" out the DOUT pin. The
    "OK\r" characters can be delayed if the module has not finished transmitting received serial data.
    When command mode has been entered, the command mode timer is started (CT command), and the
    module is able to receive AT commands on the DIN pin.
    All of the parameter values in the sequence can be modified to reflect user preferences.
    */

    flush_RCREG();
    
    //Gaurd time - Delay for at least 10ms
    __delay_ms(15);

    //Send three '+' within one second
    send_USART_string("+++");

    //Gaurd time - Delay for at least 10ms
    __delay_ms(15);

    //Recieve 'OK'
    for(char iter = 0; iter < 2; iter++)
    {BYTES_BUFFER[iter] = get_USART_char();}	// Save bytes from USART
}

void exitCommandMode(void)
{
     flush_RCREG();
     send_USART_string("ATCN\r");

     //Recieve 'OK'
     for(char iter = 0; iter < 2; iter++)
     {BYTES_BUFFER[iter] = get_USART_char();}	// Save bytes from USART
}



#endif