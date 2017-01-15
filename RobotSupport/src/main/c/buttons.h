#include <stdio.h>
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

#define BUTTON_1_PORTLETTER D
#define BUTTON_1_PINNUMBER 5
#define BUTTON_2_PORTLETTER D
#define BUTTON_2_PINNUMBER 4
#define BUTTON_3_PORTLETTER D
#define BUTTON_3_PINNUMBER 3
#define BUTTON_4_PORTLETTER D
#define BUTTON_4_PINNUMBER 2
#define UP_PORTLETTER D
#define UP_PINNUMBER 6
#define DOWN_PORTLETTER D
#define DOWN_PINNUMBER 7
#define LEFT_PORTLETTER C
#define LEFT_PINNUMBER 2
#define RIGHT_PORTLETTER C
#define RIGHT_PINNUMBER 3
#define MAIN_PORTLETTER C
#define MAIN_PINNUMBER 4

void buttons_init();

void check_buttons();
