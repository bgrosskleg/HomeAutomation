/*
 * File:   XB2Button.c
 * Author: Jason
 *
 * Created on November 15, 2013, 6:24 PM
 */
#include <stdio.h>
#include <stdlib.h>
#include <pic16f886.h>
#include <pic.h>

/*xtal freq for ms and us delays, this is PIC frequency*/
#ifndef _XTAL_FREQ
#define _XTAL_FREQ 4000000
#endif
/*
 *
 */
__CONFIG(BOREN_OFF & INTIO & WDTDIS & PWRTDIS & MCLRDIS & CP_OFF & DUNPROTECT & IESODIS & FCMDIS & LVP_OFF & DEBUG_OFF);

#include "..\Header Files\USART_16F_MASTER.h"
#include "..\Header Files\ZIGBEE_16F_MASTER.h"

void PIC_Initialization(void);

void main(){
    PIC_Initialization();

    USART_Init();

    SPEN = 1;

    __delay_ms(250);

    enterCommandMode();

    //Get sensor MAC address
    char sensor[16+1];
    getIdentification(sensor);

    setDestination("0000000000000000");

    exitCommandMode();

    __delay_ms(250);
    
    for(;;)
    {
        flush_RCREG();
        
        char command = get_USART_char();

        if(command == '*')
        {
            //RSSI request

            //Get broadcast number
            char broadcastNumber[3+1];
            for(char iter = 0; iter < 3; iter++)
            {broadcastNumber[iter] = get_USART_char();}	// Save bytes from USART
            broadcastNumber[3] = '\0';

            //Get mobile node address
            char mobile[16+1];
            for(char iter = 0; iter < 16; iter++)
            {mobile[iter] = get_USART_char();}	// Save bytes from USART
            mobile[16] = '\0';

            enterCommandMode();

            //Get received RSSI
            char RSSI[4+1];
            char dBm[6+1];
            char percentage[6+1];
            getRSSI(RSSI, dBm, percentage);

            exitCommandMode();

            //Transmit RSSI to computer attached node
            send_USART_string("\rBroadcast #: ");
            send_USART_string(broadcastNumber);  //Send mobile information to computer

            send_USART_string("\rMobile: ");
            send_USART_string(mobile);  //Send mobile information to computer

            send_USART_string("\rSensor: ");
            send_USART_string(sensor);  //Send sensor information to computer

            send_USART_string("\rRSSI: ");
            send_USART_string(RSSI);            //Send RSSI information to computer
            send_USART_char(' ');
            send_USART_string(dBm);            //Send dBm information to computer
            send_USART_char(' ');
            send_USART_string(percentage);      //Send % information to computer
        }
        else if(command == '!')
        {
            //Lighting control
            //...
        }
    }
}
void PIC_Initialization()
{
	GIE = 0;
	//Shut off watchdog timer
	WDTCON = 0;
	OPTION_REG = 0b01011000;
	//Make all pins digital pins
	ANSEL = 0;
	ANSELH = 0;
	PSTRCON = 0;
	TRISA = 0b00000000;
	TRISB = 0b11111111; // 1 is input, 0 is output. so all tris B is now input. 
	TRISC = 0b00000000;
	PORTA = 0;
	PORTB = 0;
	PORTC = 0;
	GIE = 1;
        TXIE = 0;
}