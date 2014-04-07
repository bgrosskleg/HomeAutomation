// Header for Operating an XBee Zigbee module in PIC16F family
// Author: Brian Grosskleg - 11079251
// Date: Jan 15, 2014

#ifndef ZIGBEE_HEADER		//Macro gaurds to prevent redefining
#define ZIGBEE_HEADER

///*proccessor header file*/
#include <htc.h> //loads right header file for the chip in use

#include <pic.h> //needed for some compiler library functions


// Define Equivalent (equ) Variables (DO NOT PUT ;)


//Declare some variables


//Declare functions here for use later - prototypes

void enterCommandMode(void);
void exitCommandMode(void);
void getRSSI(unsigned char RSSI[], unsigned char dBm[], unsigned char percentage[]);
void setDestination(unsigned char const destination[]);
void getIdentification(unsigned char result[]);

long strtol(const char * s, char ** endptr, int base);


//Includes c code containing USART functions
#include "..\C Source Code\ZIGBEE_16F_MASTER.c"
// <> used for system headers, "" used for user headers

#endif