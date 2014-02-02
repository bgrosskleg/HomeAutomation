// LCD Header for Operating a NewHaven LCD Screen
// Author: Brian Grosskleg - 11079251
// Date: Nov 14, 2013

#ifndef LCD_HEADER		//Macro gaurds to prevent redefining
#define LCD_HEADER

///*proccessor header file*/
#include <xc.h>

#include <string.h>

//Declare functions here for use later - prototypes

void print_to_screen_button(unsigned char const word[]);
void print_to_screen(unsigned char const word[]);
void Print_command(unsigned char const command);

void LCD_Init(void);
void LCD_Command(unsigned int command);
void LCD_Write(unsigned char data);
void LCD_Clear_Display(void);
void Goto_Second_Line(void);

#include "..\C Source Code\LCD_Display_C_Code_MASTER.c"


#endif


