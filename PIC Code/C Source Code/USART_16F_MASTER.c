// C Code for Operating a USART Port in PIC16F family
// Author: Brian Grosskleg - 11079251
// Date: March 3, 2012

#ifndef USART_CODE		//Macro gaurds to prevent redefining
#define USART_CODE

#include <string.h>

#define Fosc 4000000	//Fosc

unsigned char BYTES_BUFFER[16];

//////////////////////////////////////////////////////////////////////////////
//USART INITIALIZATION

void USART_Init(void)
{
        /*
        PAGE 162, PIC16F886 DATASHEET
        
        12.1.2.8 Asynchronous Reception Setup:
        1. Initialize the SPBRGH, SPBRG register pair and
        the BRGH and BRG16 bits to achieve the
        desired baud rate (see Section 12.3 ?EUSART
        Baud Rate Generator (BRG)?).

        2. Enable the serial port by setting the SPEN bit.
        The SYNC bit must be clear for asynchronous
        operation.

        3. If interrupts are desired, set the RCIE bit of the
        PIE1 register and the GIE and PEIE bits of the
        INTCON register.

        4. If 9-bit reception is desired, set the RX9 bit.

        5. Enable reception by setting the CREN bit.

        6. The RCIF interrupt flag bit will be set when a
        character is transferred from the RSR to the
        receive buffer. An interrupt will be generated if
        the RCIE interrupt enable bit was also set.

        7. Read the RCSTA register to get the error flags
        and, if 9-bit data reception is enabled, the ninth
        data bit.

        8. Get the received 8 Least Significant data bits
        from the receive buffer by reading the RCREG
        register.
        
        9. If an overrun occurred, clear the OERR flag by
        clearing the CREN receiver enable bit.
        */

        //Step #1 - BAUD CONFIGURATION
            Setup_Baud_Gen(9600);   //Sets BRG, BRGH, BRG16, SPBRGH and SPBRG

	//Step #2) - SERIAL PORT ENABLE
            //TXSTA register
            SYNC = 0;           //Set serial port to asynchronous mode
            //RCSTA register
            SPEN = 1;           //Turn on serial port, sync must be 0 for async mode

        //Step #3 - INTERRUPT CONFIGURATION
            TXIE = 0;		//TX interrupt disabled
            RCIE = 0;		//RX interrupt enabled
	
        //Step #4 - 9 BIT RECEPTION CONFIGRATION
            //RCSTA register
            RX9=0;		//9-bit reception disabled
            //TXSTA register
            TX9=0;		//9-bit transmission disabled
            
	//Step #5 - ENABLE RECIEVER AND TRANSMITTER
            //RCSTA register
            CREN = 1;           //Enable reciever
            //TXSTA register
            TXEN = 1;           //Enable transmitter
}


void Setup_Baud_Gen(int BAUD)
{
        if(Fosc == 4000000)
	{
                //Pg 168-170 PIC16F886 data sheet, using tables to minimize baud error
                BRGH = 1;       //Configure BAUD generator to high speed for 9600 baud w/ 4Mhz
                BRG16 = 0;      //No not use 16-bit BAUD generator
                SPBRGH = 0;     //Not using high 8 bits of BAUD generator, set to 0's

		switch(BAUD)    //set lower 8 bits of BAUD generator
		{
                    case 1200:  SPBRG = 207;    break;
                    case 2400:  SPBRG = 103;    break;
                    case 9600:	SPBRG = 25;     break;
                    case 10417: SPBRG = 23;     break;
                    case 19200: SPBRG = 12;     break;
                    
                    //DEFAULT  BAUD OF 9600
                    default:    SPBRG = 25;     break;
                }
	}

        //MORE Fosc's TO BE EXPANDED
}


//////////////////////////////////////////////////////////////////////////////
//USART CHARACTER COMMANDS

//RECIEVE COMMANDS
unsigned char get_USART_char(void)
{
	do 
	{clear_usart_errors();}
	while(RCIF == 0);  //Continously reset errors until full byte is recieved

	return RCREG;	//Upon reading RCREG, RCIF flag is reset
}


unsigned char get_USART_char_timeout(void)
{
	//Recieve one byte with a timeout in microseconds (approx)
	for( int timeout_int = 1000000; timeout_int > 0; timeout_int--)
	{	
		if (RCIF == 1)	//Continuously check if byte was recieved
		{return RCREG;}
		clear_usart_errors();
                __delay_us(1);
	}
	
	return '\0';	//If no byte was recieved within time limit, return end of string character.
}


//TRANSMIT COMMANDS
void send_USART_char(unsigned char character)
{
	do                      //Clear any errors while the TXREG is full
	{clear_usart_errors();}
	while(TXIF == 0);	

	TXREG = character;	//Write character out to USART
	__delay_ms(1);          //Delay to transmit
}

void send_USART_string(unsigned char const string[])
{
	for(unsigned char iter = 0 ; iter < strlen(string) ; iter++)
	{send_USART_char(string[iter]);}
}



////////////////////////////////////////////////////////////////////
//USART ERROR CLEARING
void clear_usart_errors(void)	
{
        //pg. 161 PIC16F886 DATASHEET
	if (OERR == 1)													
	{																	
			
                        TXEN=0;		//Reset transmitter
			TXEN=1;													
			CREN=0;		//Reset continous recieve									
			CREN=1;
	}
        
	if (FERR == 1)													
	{
			dummy=RCREG;    //Flush RCREG
			TXEN=0;		//Reset transmitter									
			TXEN=1;
        }
}

void flush_RCREG(void)
{
    //To be done after long period of inactivity
    dummy=RCREG;    //Flush RCREG
    dummy=RCREG;    //Flush RCREG
    dummy=RCREG;    //Flush RCREG
}

#endif