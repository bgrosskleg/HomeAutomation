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

    int lightingValue;
    char lightingValueBuf[10];

    for(;;)
    {
        if (RB0 == 1)
        {
            //Mark as RSSI lighting control
            send_USART_char('!');

            lightingValue = 147;

            if(lightingValue < 10)
            {
                lightingValueBuf[0] = '0';
                lightingValueBuf[1] = '0';
                sprintf(lightingValueBuf+2 , "%d", lightingValue);
                send_USART_string(lightingValueBuf);
            }
            else if(lightingValue < 100)
            {
                lightingValueBuf[0] = '0';
                sprintf(lightingValueBuf+1 , "%d", lightingValue);
                send_USART_string(lightingValueBuf);
            }
            else
            {
                sprintf(lightingValueBuf , "%d", lightingValue);
                send_USART_string(lightingValueBuf);
            }
            __delay_ms(250);
        }


        if (RB1 == 1)
        {
            //Broadcast...
            RA7 = 1;
            __delay_ms(250);

            //while(RB1 == 0)
            //{
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

                __delay_ms(250);
                __delay_ms(250);
                __delay_ms(250);
                __delay_ms(250);

                __delay_ms(250);
                __delay_ms(250);
                __delay_ms(250);
                __delay_ms(250);
            //}

            RA7 = 0;
            __delay_ms(250);
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
	TRISB = 0b11111111;
	TRISC = 0b00000000;
	PORTA = 0;
	PORTB = 0;
	PORTC = 0;
	GIE = 1;
}