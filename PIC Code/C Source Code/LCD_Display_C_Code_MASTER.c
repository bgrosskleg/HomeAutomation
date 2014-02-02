// LCD C Code for Operating a NewHaven LCD Screen
// Author: Brian Grosskleg - 11079251
// Date: Nov 14, 2013

#ifndef LCD_CODE		//Macro gaurds to prevent redefining
#define LCD_CODE

    //////////////////////////////////////////////////////////////////////////
    //DISPLAY FUNCTIONS

  void print_to_screen_button(unsigned char const word[])
    {
        while(print_button == 0)
        {}
        print_to_screen(word);
        while(print_button == 1)
        {}
        return;
    }

    void print_to_screen(unsigned char const word[])
    {
        for(unsigned char iter = 0 ; iter < strlen(word) ; iter++)
            {
            LCD_Write(word[iter]);
            }
        // Takes arguement of function, creates array word
        // prints each character of the word out to the LCD screen

        LCD_Write(' ');

        return;
    }

    ///////////////////////////////////////////////////////////////////////
    // LCD FUNCTIONS

    void LCD_Init(void)
    {
    LCD_Command(0b00000001);		// Clear display
    LCD_Command(0b00111000);		// Set function
    LCD_Command(0b00000110);		// Set Entry
    LCD_Command(0b00001100);		// Set display
    LCD_Command(0b00000001);		// Clear display
    LCD_Command(0b10000000);		// Clear display

    __delay_ms(1);
    return;
    }

    void LCD_Command(unsigned int command)
    {
            LCD_DATA = command;			// Places command on data port to LCD
            RW = 0;						// Clears R/W to write instruction (not read)
            RS = 0;  					// Clears RS to recieve instruction
            E = 1;						// Sets E enable (symbolizes clock pulse, needs to be longer than 300ns, falling edge triggered)
            __delay_ms(2);				// Delay must be longer than 300us
            E = 0;						// Falling edge of E, intiates action
            __delay_ms(10);				// Delay to stabilize
    return;
    }

    void LCD_Write(unsigned char data)
    {
            LCD_DATA = data;			// Places data on data port to LCD
            RW = 0;						// Clears R/W to write instruction (not read)
            RS = 1;  					// Sets RS to recieve data
            E = 1;						// Sets E enable (symbolizes clock pulse, needs to be longer than 300ns, falling edge triggered)
            __delay_us(200);			// Delay must be longer than 300us
            __delay_us(200);
            E = 0;						// Falling edge of E, intiates action
            __delay_us(100);			// Delay to stabilize
    return;
    }

    void LCD_Clear_Display(void)
    {
            LCD_Command(0b00000001);	// Command for clearing display
            return;
    }

    void Goto_Second_Line(void)
    {
            __delay_ms(1);
            LCD_Command(0b11000000);	// Move cursor to second line
            return;
    }

#endif