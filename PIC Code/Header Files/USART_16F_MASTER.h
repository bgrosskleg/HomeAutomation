// Header for Operating a USART Port in PIC16F family
// Author: Brian Grosskleg - 11079251
// Date: March 3, 2012

#ifndef USART_HEADER		//Macro gaurds to prevent redefining
#define USART_HEADER

///*proccessor header file*/
#include <htc.h> //loads right header file for the chip in use

#include <pic.h> //needed for some compiler library functions


// Define Equivalent (equ) Variables (DO NOT PUT ;)


//Declare some variables

unsigned char dummy;


//Declare functions here for use later - prototypes

void USART_Init(void);

void clear_usart_errors(void);
unsigned char get_USART_char(void);
unsigned char get_USART_char_timeout(void);
void send_USART_char(unsigned char character);
void send_USART_string(unsigned char const string[]);
void flush_RCREG(void);

void Setup_Baud_Gen(int BAUD);



//Includes c code containing USART functions
#include "..\C Source Code\USART_16F_MASTER.c"

// <> used for system headers, "" used for user headers

#endif