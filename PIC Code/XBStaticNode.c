#include <stdio.h>
#include <stdlib.h>
#include <xc.h>
#include "..\Header Files\USART_16F_MASTER.h"
#include "..\Header Files\ZIGBEE_16F_MASTER.h"

/*xtal freq for ms and us delays, this is PIC frequency*/
#ifndef _XTAL_FREQ
#define _XTAL_FREQ 4000000
#endif
/*
 *
 */
// CONFIG1
#pragma config FOSC = INTRC_NOCLKOUT// Oscillator Selection bits (INTOSCIO oscillator: I/O function on RA6/OSC2/CLKOUT pin, I/O function on RA7/OSC1/CLKIN)
#pragma config WDTE = OFF       // Watchdog Timer Enable bit (WDT disabled and can be enabled by SWDTEN bit of the WDTCON register)
#pragma config PWRTE = OFF      // Power-up Timer Enable bit (PWRT disabled)
#pragma config MCLRE = ON       // RE3/MCLR pin function select bit (RE3/MCLR pin function is MCLR)
#pragma config CP = OFF         // Code Protection bit (Program memory code protection is disabled)
#pragma config CPD = OFF        // Data Code Protection bit (Data memory code protection is disabled)
#pragma config BOREN = OFF      // Brown Out Reset Selection bits (BOR disabled)
#pragma config IESO = ON        // Internal External Switchover bit (Internal/External Switchover mode is enabled)
#pragma config FCMEN = OFF      // Fail-Safe Clock Monitor Enabled bit (Fail-Safe Clock Monitor is disabled)
#pragma config LVP = OFF        // Low Voltage Programming Enable bit (RB3 pin has digital I/O, HV on MCLR must be used for programming)

// CONFIG2
#pragma config BOR4V = BOR21V   // Brown-out Reset Selection bit (Brown-out Reset set to 2.1V)
#pragma config WRT = OFF        // Flash Program Memory Self Write Enable bits (Write protection off)

unsigned int ZeroCrossLength = 0;
unsigned short long ZeroCrossSamples=0;
unsigned char ZeroCount=0;

unsigned long CycleLength = 8333;			
unsigned short long CycleLengthSample=0;
unsigned char CycleCount=0;
unsigned long Delay=0;
char sensor[16+1];  	//sensor MAC address

void PIC_Initialization(void);
void SetPower(char Level);
void RSSI_Request(void);


void main(){
    PIC_Initialization();
    USART_Init();
    __delay_ms(250);
    enterCommandMode();
    char sensor[16+1];
    getIdentification(sensor); //Get sensor MAC address
    setDestination("0000000000000000");
    exitCommandMode();
    __delay_ms(250);
    char Level = 50;
    SetPower((100-Level)*255/100);
    
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
            char temp[3];
            memcpy(temp,&RSSI[2],2);
            temp[2]='\0';

            send_USART_char('#');
            send_USART_string(broadcastNumber);
            send_USART_char(':');
            send_USART_string(mobile);
            send_USART_char(':');
            send_USART_string(sensor);
            send_USART_char(':');
            send_USART_string(temp);
            send_USART_char('?');
            __delay_ms(250);
        }
        else if(command == '!')		//Read in lighting command, convert ASCII to decimal and set power level. 
        {
            char PowerString[4];
            PowerString[0]=get_USART_char_timeout();
            PowerString[1]=get_USART_char_timeout();
            PowerString[2]=get_USART_char_timeout();
            PowerString[3]='\0';
            unsigned int Level = (int) strtol (PowerString, NULL, 10);
            Level=(100-Level)*255/100;		// convert 0-100% power level (time on) to corresponding 0-255 phase delay where 255 delay is fully off.
            SetPower(Level);
          
        }
    }
}
void SetPower(unsigned char Level){
    Delay=CycleLength*Level;		//Calculate phase delay based on Cycle length (aprox .5*(1/60hz) seconds) and width of the zero crossing pulse
    Delay = Delay/256   +(ZeroCrossLength/2);
    return;
    }
interrupt void isr(void){

    if (TMR1IF && TMR1IE){
        //Error, Timer 1 should never overflow
	TMR1IF=0; 	//Clear Flag
	}

    if (RBIF && RBIE && RB5 && (TMR1H>2)){		//rising edge of zero crossing signal ( Time 1 Hight > 2 in case of noise durring falling edge)
        if(RB4==1) RB4=0;						//Flip GPIO B4 for debug monitoring
        else if(RB4==0) RB4=1;				
        TMR1ON=0;		//Pause timer for read, store length of last cycle
	CycleLengthSample+= TMR1H*256 + TMR1L;
	TMR1H=0;		//Clear Timer and restart
	TMR1L=0;
 	TMR1ON=1;
        CCP2CON=0b00000000;		//Reset CCP2 compare module
        CCP2CON=0b00001000;     //Timer Mode trigger output on match
        CCPR2H=Delay/256;		//Set phase delay to CCP2 period register
        CCPR2L=Delay-CCPR2H*256;

        CycleCount++;
	if (CycleCount>59){			//Cycle Length Averaging
            CycleLength=CycleLengthSample/60;
            CycleCount=0;
            CycleLengthSample=0;
            }
	}

    if (RBIF && RBIE){RBIF=0;}	// Clear flag 

    if (CCP1IF && CCP1IE){
	//Timing of Zero Crossing Pulse Width
	ZeroCrossSamples+=CCPR1H*256+CCPR1L;
	ZeroCount++;
	if (ZeroCount>59) {
		ZeroCrossLength=ZeroCrossSamples/60;
		ZeroCount=0;
                ZeroCrossSamples=0;
		}
	CCP1IF=0;		//clear flag
	}
    }
void PIC_Initialization()
{
	GIE = 0;
	WDTCON = 0;		//Watchdog timer off
	OPTION_REG = 0b11000000;   //Portb pull up off True, External Interrupt on rising edge true, Timer 0 on external pin false, Timer 0 high to low transition false
								//Prescaler on WDR (True) or Timer0 (flase), Prescaler bits [3]
	ANSEL = 0;		//Analog select false on all IO pins
	ANSELH = 0;
	TRISB5=1;		//Zero cross sensor pin input
	RBIE=1;			//Port B Interrupt on Change
	IOCB5=1;		//B5 Interrput on change True
	TRISC2=1;		//CCP1 (Zero cross) pin input
	TRISC1=0;		//CCP2 (trigger) pin output
	TRISC6=0;		//TX pin Output
	TRISC7=1;		//RX pin Input
	TRISB4=0;       //Debug pin output
	TRISB3=0;       //Debug pin output
	RB4=0;

	//CCP2M=0b1000;	//CCP2 output high when Period register equals Timer1 - implemented below
	//CCP1M=0b0101;	//CCP1 Capture on falling edge - implemented below
	CCP2CON=0b00001000;
	CCP1CON=0b00000100;
	CCP1IE=1;		//Enable CCP1 interrupt for zero crossing timing
	TMR1CS=0;		//Timer 1 clock source internal fosc/4
	T1CKPS0=0;      //Timer 1 Clock Prescale = 1:1
	T1CKPS1=0;
	TMR1GE=0;		//Timer 1 Gate enable false ==> Always count, not on certain conditions
	TMR1ON=1;		//Timer 1 On
	TMR1IE=1;		//Timer 1 Interrupt Enable True
	RBIF=0;			//Clear all flags for enabled interrupts
	TMR1IF=0;
	CCP1IF=0;
	PEIE=1;			//Peripheral Interrupt Enable
	GIE = 1;		//Global Interrupt Enable

}