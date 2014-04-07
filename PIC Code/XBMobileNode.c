/*
 * File:   XB1Button.c
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

void main()
{
    PIC_Initialization();

    USART_Init();

    SPEN = 1;

    __delay_ms(250);

    //Enter command mode
    enterCommandMode();

    //Set destination address
    setDestination("000000000000FFFF");

    //Get xbee MAC address
    char mobile[16+1];
    getIdentification(mobile);

    //Exit command mode
    exitCommandMode();

    int broadcastNumber = 0;
    char broadcastNumberBuf[10];
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);

        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
        
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);

        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);

    for(;;)
    {
        __delay_ms(250);

        //Mark as RSSI broadcast ping
        send_USART_char('*');

        if(broadcastNumber == 999)
        {
            broadcastNumber = 1;
        }

        if(broadcastNumber < 10)
        {
            broadcastNumberBuf[0] = '0';
            broadcastNumberBuf[1] = '0';
            sprintf(broadcastNumberBuf+2 , "%d", broadcastNumber);
            send_USART_string(broadcastNumberBuf);
        }
        else if(broadcastNumber < 100)
        {
            broadcastNumberBuf[0] = '0';
            sprintf(broadcastNumberBuf+1 , "%d", broadcastNumber);
            send_USART_string(broadcastNumberBuf);
        }
        else
        {
            sprintf(broadcastNumberBuf , "%d", broadcastNumber);
            send_USART_string(broadcastNumberBuf);
        }

        broadcastNumber++;

        //Send mobile MAC address
        send_USART_string(mobile);
        send_USART_char('@');

        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);

        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
        __delay_ms(250);
    }
}
void PIC_Initialization()
{
	GIE = 0;
	WDTCON = 0;		//Watchdog timer off
	OPTION_REG = 0b11000000;//Portb pull up off True, External Interrupt on rising edge true, Timer 0 on external pin false, Timer 0 high to low transition false
								//Prescaler on WDR (True) or Timer0 (flase), Prescaler bits [3]
	ANSEL = 0;		//Analog select false on all IO pins
	ANSELH = 0;
	TRISC6=0;		//TX pin Output
	TRISC7=1;		//RX pin Input
}