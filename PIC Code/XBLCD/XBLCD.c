/*
 * File:   XBLCD.c
 * Author: Jason
 *
 * Created on November 15, 2013, 6:24 PM
 */

/*proccessor header file*/
#include <xc.h>
#include <stdio.h>

/*xtal freq for ms and us delays, this is PIC frequency*/
#ifndef _XTAL_FREQ
#define _XTAL_FREQ 4000000
#endif


/*Configuration bits*/
// the header file pic16f887.h explains the function of these
// Config Reg 1 and 2. data sheet page 210
__CONFIG(INTIO & WDTDIS & PWRTDIS & MCLRDIS & UNPROTECT & DUNPROTECT & BORDIS & IESODIS & FCMDIS & LVPDIS & DEBUGDIS);


//Define equivalent variable, MUST DEFINE PORTS, PINS ETC BEFORE INCLUDING HEADER FILE B/C OF #IFNDEF
#ifndef LCD_DATA, LCD_CTRL, E, RW, RS, print_button
#define LCD_DATA PORTB
#define LCD_CTRL PORTA
#define RS RA1
#define RW RA2
#define E RA3
#define print_button RA4
#define LED RA5
#endif


//Includes header file containing proper LCD display functions
#include "..\Header Files\LCD_Display_C_Code_MASTER.h"

// <> used for system headers, "" used for user headers
#include "..\Header Files\USART_16F_MASTER.h"

// <> used for system headers, "" used for user headers
#include "..\Header Files\ZIGBEE_16F_MASTER.h"


//Define function prototypes here
void PIC_Initialization(void);

long strtol(const char * s, char ** endptr, int base);
char * intToString(int number);
int sprintf ( char * s, const char * format, ... );


///////////////////////////////////////////////////////////////////////////////////////////////////////
//MAIN FUNCTION
void main()
{
	PIC_Initialization();

	RC5 = 0;

	LCD_Init();

	print_to_screen("Initialized...");

	USART_Init();

        __delay_ms(250);

        enterCommandMode();

        //Get sensor MAC address
        char sensor[16+2];
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


                //Display results on screen
                LCD_Clear_Display();
                print_to_screen("RSSI:");
                print_to_screen(RSSI);

                Goto_Second_Line();
                print_to_screen(dBm);
                print_to_screen(percentage);
            }
            else if(command == '!')
            {
                //Get broadcast number
                char lightingValue[3+1];
                for(char iter = 0; iter < 3; iter++)
                {lightingValue[iter] = get_USART_char();}	// Save bytes from USART
                lightingValue[3] = '\0';

                //Display results on screen
                LCD_Clear_Display();
                print_to_screen("Lighting Value:");
                Goto_Second_Line();
                print_to_screen(lightingValue);
            }
    }
}



////////////////////////////////////////////////////////////////////////////////////////////////////
//INITIALIZATION
void PIC_Initialization(void)
{
	GIE = 0;

	//Shut off watchdog timer
	WDTCON = 0;

	OPTION_REG = 0b01011000;

	//Make all pins digital pins
	ANSEL = 0;
	ANSELH = 0;

	PSTRCON = 0;

	TRISA = 0b00010001;
	TRISB = 0;
	TRISC = 0b10000000;

	PORTA = 0;
	PORTB = 0;
	PORTC = 0;

	GIE = 1;

return;
}





