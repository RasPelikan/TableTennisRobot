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

#define BUTTON_1_ID 1
#define BUTTON_2_ID 2
#define BUTTON_3_ID 3
#define BUTTON_4_ID 4
#define BUTTON_UP_ID 5
#define BUTTON_DOWN_ID 6
#define BUTTON_LEFT_ID 7
#define BUTTON_RIGHT_ID 8
#define BUTTON_MAIN_ID 9

void buttons_init();

bool check_buttons();
