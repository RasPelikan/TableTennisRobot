#include <stdio.h>
#include <stdbool.h>
#include <avr/io.h>

#define BROWN_PORTLETTER B
#define BROWN_PINNUMBER 4
#define RED_PORTLETTER B
#define RED_PINNUMBER 3
#define ORANGE_PORTLETTER B
#define ORANGE_PINNUMBER 2
#define YELLOW_PORTLETTER B
#define YELLOW_PINNUMBER 1
#define GREEN_PORTLETTER B
#define GREEN_PINNUMBER 0
#define BLUE_PORTLETTER A
#define BLUE_PINNUMBER 0

#define BUTTON_1_BIT 0
#define BUTTON_1_BYTE 0
#define BUTTON_2_BIT 1
#define BUTTON_2_BYTE 0
#define BUTTON_3_BIT 2
#define BUTTON_3_BYTE 0
#define BUTTON_4_BIT 3
#define BUTTON_4_BYTE 0
#define BUTTON_UP_BIT 4
#define BUTTON_UP_BYTE 0
#define BUTTON_DOWN_BIT 5
#define BUTTON_DOWN_BYTE 0
#define BUTTON_LEFT_BIT 6
#define BUTTON_LEFT_BYTE 0
#define BUTTON_RIGHT_BIT 7
#define BUTTON_RIGHT_BYTE 0
#define BUTTON_MAIN_BIT 0
#define BUTTON_MAIN_BYTE 1

void buttons_init();

bool check_buttons();
