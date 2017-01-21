#include <stdbool.h>

#include "buttons.h"
#include "util.h"

#define _CONCAT(a,b) a##b
#define CONCAT(a,b) _CONCAT(a,b)

#define BROWN_PORT CONCAT(PORT, BROWN_PORTLETTER)
#define BROWN_DDR CONCAT(DDR, BROWN_PORTLETTER)
#define BROWN_PIN CONCAT(PIN, BROWN_PORTLETTER)
#define BROWN_PINNAME CONCAT(P, CONCAT(BROWN_PORTLETTER, BROWN_PINNUMBER))

#define RED_PORT CONCAT(PORT, RED_PORTLETTER)
#define RED_DDR CONCAT(DDR, RED_PORTLETTER)
#define RED_PIN CONCAT(PIN, RED_PORTLETTER)
#define RED_PINNAME CONCAT(P, CONCAT(RED_PORTLETTER, RED_PINNUMBER))

#define ORANGE_PORT CONCAT(PORT, ORANGE_PORTLETTER)
#define ORANGE_DDR CONCAT(DDR, ORANGE_PORTLETTER)
#define ORANGE_PIN CONCAT(PIN, ORANGE_PORTLETTER)
#define ORANGE_PINNAME CONCAT(P, CONCAT(ORANGE_PORTLETTER, ORANGE_PINNUMBER))

#define YELLOW_PORT CONCAT(PORT, YELLOW_PORTLETTER)
#define YELLOW_DDR CONCAT(DDR, YELLOW_PORTLETTER)
#define YELLOW_PIN CONCAT(PIN, YELLOW_PORTLETTER)
#define YELLOW_PINNAME CONCAT(P, CONCAT(YELLOW_PORTLETTER, YELLOW_PINNUMBER))

#define GREEN_PORT CONCAT(PORT, GREEN_PORTLETTER)
#define GREEN_DDR CONCAT(DDR, GREEN_PORTLETTER)
#define GREEN_PIN CONCAT(PIN, GREEN_PORTLETTER)
#define GREEN_PINNAME CONCAT(P, CONCAT(GREEN_PORTLETTER, GREEN_PINNUMBER))

#define BLUE_PORT CONCAT(PORT, BLUE_PORTLETTER)
#define BLUE_DDR CONCAT(DDR, BLUE_PORTLETTER)
#define BLUE_PIN CONCAT(PIN, BLUE_PORTLETTER)
#define BLUE_PINNAME CONCAT(P, CONCAT(BLUE_PORTLETTER, BLUE_PINNUMBER))

#define BUTTON_1_PORT CONCAT(PORT, BUTTON_1_PORTLETTER)
#define BUTTON_1_DDR CONCAT(DDR, BUTTON_1_PORTLETTER)
#define BUTTON_1_PIN CONCAT(PIN, BUTTON_1_PORTLETTER)
#define BUTTON_1_PINNAME CONCAT(P, CONCAT(BUTTON_1_PORTLETTER, BUTTON_1_PINNUMBER))

#define BUTTON_2_PORT CONCAT(PORT, BUTTON_2_PORTLETTER)
#define BUTTON_2_DDR CONCAT(DDR, BUTTON_2_PORTLETTER)
#define BUTTON_2_PIN CONCAT(PIN, BUTTON_2_PORTLETTER)
#define BUTTON_2_PINNAME CONCAT(P, CONCAT(BUTTON_2_PORTLETTER, BUTTON_2_PINNUMBER))

#define BUTTON_3_PORT CONCAT(PORT, BUTTON_3_PORTLETTER)
#define BUTTON_3_DDR CONCAT(DDR, BUTTON_3_PORTLETTER)
#define BUTTON_3_PIN CONCAT(PIN, BUTTON_3_PORTLETTER)
#define BUTTON_3_PINNAME CONCAT(P, CONCAT(BUTTON_3_PORTLETTER, BUTTON_3_PINNUMBER))

#define BUTTON_4_PORT CONCAT(PORT, BUTTON_4_PORTLETTER)
#define BUTTON_4_DDR CONCAT(DDR, BUTTON_4_PORTLETTER)
#define BUTTON_4_PIN CONCAT(PIN, BUTTON_4_PORTLETTER)
#define BUTTON_4_PINNAME CONCAT(P, CONCAT(BUTTON_4_PORTLETTER, BUTTON_4_PINNUMBER))

#define UP_PORT CONCAT(PORT, UP_PORTLETTER)
#define UP_DDR CONCAT(DDR, UP_PORTLETTER)
#define UP_PIN CONCAT(PIN, UP_PORTLETTER)
#define UP_PINNAME CONCAT(P, CONCAT(UP_PORTLETTER, UP_PINNUMBER))

#define DOWN_PORT CONCAT(PORT, DOWN_PORTLETTER)
#define DOWN_DDR CONCAT(DDR, DOWN_PORTLETTER)
#define DOWN_PIN CONCAT(PIN, DOWN_PORTLETTER)
#define DOWN_PINNAME CONCAT(P, CONCAT(DOWN_PORTLETTER, DOWN_PINNUMBER))

#define LEFT_PORT CONCAT(PORT, LEFT_PORTLETTER)
#define LEFT_DDR CONCAT(DDR, LEFT_PORTLETTER)
#define LEFT_PIN CONCAT(PIN, LEFT_PORTLETTER)
#define LEFT_PINNAME CONCAT(P, CONCAT(LEFT_PORTLETTER, LEFT_PINNUMBER))

#define RIGHT_PORT CONCAT(PORT, RIGHT_PORTLETTER)
#define RIGHT_DDR CONCAT(DDR, RIGHT_PORTLETTER)
#define RIGHT_PIN CONCAT(PIN, RIGHT_PORTLETTER)
#define RIGHT_PINNAME CONCAT(P, CONCAT(RIGHT_PORTLETTER, RIGHT_PINNUMBER))

#define MAIN_PORT CONCAT(PORT, MAIN_PORTLETTER)
#define MAIN_DDR CONCAT(DDR, MAIN_PORTLETTER)
#define MAIN_PIN CONCAT(PIN, MAIN_PORTLETTER)
#define MAIN_PINNAME CONCAT(P, CONCAT(MAIN_PORTLETTER, MAIN_PINNUMBER))

void buttons_init() {

	clear_bit(BLUE_DDR, BLUE_PINNAME);     // set BLUE for input
	set_bit(BLUE_PORT, BLUE_PINNAME);      // pull-up for BLUE
	clear_bit(RED_DDR, RED_PINNAME);       // set RED for input
	set_bit(RED_PORT, RED_PINNAME);        // pull-up for RED
	clear_bit(BROWN_DDR, BROWN_PINNAME);   // set BROWN to input
	set_bit(BROWN_PORT, BROWN_PINNAME);    // pull-up for BROWN
	clear_bit(GREEN_DDR, GREEN_PINNAME);   // set GREEN for input
	set_bit(GREEN_PORT, GREEN_PINNAME);    // pull-up for GREEN
	clear_bit(YELLOW_DDR, YELLOW_PINNAME); // set YELLOW for input
	set_bit(YELLOW_PORT, YELLOW_PINNAME);  // pull-up for YELLOW
	clear_bit(ORANGE_DDR, ORANGE_PINNAME); // set ORANGE for input
	set_bit(ORANGE_DDR, ORANGE_PINNAME);   // pull-up for ORANGE

	clear_bit(BUTTON_1_PORT, BUTTON_1_PINNAME); // BUTTON_1 not pressed
	set_bit(BUTTON_1_DDR, BUTTON_1_PINNAME); // set BUTTON_1 for output
	clear_bit(BUTTON_2_PORT, BUTTON_2_PINNAME); // BUTTON_2 not pressed
	set_bit(BUTTON_2_DDR, BUTTON_2_PINNAME); // set BUTTON_2 for output
	clear_bit(BUTTON_3_PORT, BUTTON_3_PINNAME); // BUTTON_3 not pressed
	set_bit(BUTTON_3_DDR, BUTTON_3_PINNAME); // set BUTTON_3 for output
	clear_bit(BUTTON_4_PORT, BUTTON_4_PINNAME); // BUTTON_4 not pressed
	set_bit(BUTTON_4_DDR, BUTTON_4_PINNAME); // set BUTTON_4 for output
	clear_bit(UP_PORT, UP_PINNAME);          // UP not pressed
	set_bit(UP_DDR, UP_PINNAME);             // set UP for output
	clear_bit(DOWN_PORT, DOWN_PINNAME);      // DOWN not pressed
	set_bit(DOWN_DDR, DOWN_PINNAME);         // set DOWN for output
	clear_bit(LEFT_PORT, LEFT_PINNAME);      // LEFT not pressed
	set_bit(LEFT_DDR, LEFT_PINNAME);         // set LEFT for output
	clear_bit(RIGHT_PORT, RIGHT_PINNAME);    // RIGHT not pressed
	set_bit(RIGHT_DDR, RIGHT_PINNAME);       // set RIGHT for output
	clear_bit(MAIN_PORT, MAIN_PINNAME);      // MAIN not pressed
	set_bit(MAIN_DDR, MAIN_PINNAME);         // set MAIN for output

}

void check_buttons() {

	bool furtherGreenChecks = true;
	bool furtherBrownChecks = true;
	bool furtherYellowChecks = true;

	// MAIN (GREEN-BLACK)
	if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
		set_bit(MAIN_PORT, MAIN_PINNAME);
		furtherGreenChecks = false;
	} else {
		clear_bit(MAIN_PORT, MAIN_PINNAME);
	}

	// BUTTON_3 (RED-BLACK)
	if (is_bit_low(RED_PIN, RED_PINNAME)) {
		set_bit(BUTTON_3_PORT, BUTTON_3_PINNAME);
	} else {
		clear_bit(BUTTON_3_PORT, BUTTON_3_PINNAME);

		set_bit(RED_DDR, RED_PINNAME);    // set RED for output
		set_bit(RED_PORT, RED_PINNAME);   // RED to high (see manual chapter 14.2.3)
		clear_bit(RED_PORT, RED_PINNAME); // RED to low
		nop();                            // sync for reading (see manual Figure 14-4)

		// DOWN (RED-BROWN)
		if (is_bit_low(BROWN_PIN, BROWN_PINNAME)) {
			set_bit(DOWN_PORT, DOWN_PINNAME);
			furtherBrownChecks = false;
		} else {
			clear_bit(DOWN_PORT, DOWN_PINNAME);
		}

		// BUTTON_2 (RED-GREEN)
		if (furtherGreenChecks) {

			if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
				set_bit(BUTTON_2_PORT, BUTTON_2_PINNAME);
				furtherGreenChecks = false;
			} else {
				clear_bit(BUTTON_2_PORT, BUTTON_2_PINNAME);
			}

		}

		if (is_bit_low(YELLOW_PIN, YELLOW_PINNAME)) {
			set_bit(LEFT_PORT, LEFT_PINNAME);
			furtherYellowChecks = false;
		} else {
			clear_bit(LEFT_PORT, LEFT_PINNAME);
		}

		// reset RED for next cycle
		clear_bit(RED_DDR, RED_PINNAME);    // set RED for input
		set_bit(RED_PORT, RED_PINNAME);     // pull-up for RED

	}

	if (furtherBrownChecks || furtherYellowChecks) {

		set_bit(ORANGE_DDR, ORANGE_PINNAME);    // set ORANGE for output
		set_bit(ORANGE_PORT, ORANGE_PINNAME);   // ORANGE to high (see manual chapter 14.2.3)
		clear_bit(ORANGE_PORT, ORANGE_PINNAME); // ORANGE to low
		nop();                                  // sync for reading (see manual Figure 14-4)

		if (furtherBrownChecks) {

			// (BROWN-ORANGE)
			if (is_bit_low(BROWN_PIN, BROWN_PINNAME)) {
				set_bit(UP_PORT, UP_PINNAME);
			} else {
				clear_bit(UP_PORT, UP_PINNAME);
			}

		}

		if (furtherYellowChecks) {

			// (YELLOW-ORANGE)
			if (is_bit_low(YELLOW_PIN, YELLOW_PINNAME)) {
				set_bit(RIGHT_PORT, RIGHT_PINNAME);
				furtherYellowChecks = false;
			} else {
				clear_bit(RIGHT_PORT, RIGHT_PINNAME);
			}

		}

		// reset ORANGE for next cycle
		clear_bit(ORANGE_DDR, ORANGE_PINNAME);  // set ORANGE for input
		set_bit(ORANGE_PORT, ORANGE_PINNAME);   // pull-up for ORANGE

	}

	if (furtherGreenChecks && furtherYellowChecks) {

		set_bit(YELLOW_DDR, YELLOW_PINNAME);    // set YELLOW for output
		set_bit(YELLOW_PORT, YELLOW_PINNAME);   // YELLOW to high (see manual chapter 14.2.3)
		clear_bit(YELLOW_PORT, YELLOW_PINNAME); // YELLOW to low
		nop();                                  // sync for reading (see manual Figure 14-4)

		// BUTTON_1 (GREEN-YELLOW)
		if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
			set_bit(BUTTON_1_PORT, BUTTON_1_PINNAME);
		} else {
			clear_bit(BUTTON_1_PORT, BUTTON_1_PINNAME);
		}

		// reset YELLOW for next cycle
		clear_bit(YELLOW_DDR, YELLOW_PINNAME);  // set YELLOW for input
		set_bit(YELLOW_PORT, YELLOW_PINNAME);   // pull-up for YELLOW

	}

	// BUTTON_4
	if (is_bit_low(BLUE_PIN, BLUE_PINNAME)) {
		set_bit(BUTTON_4_PORT, BUTTON_4_PINNAME);
	} else {
		clear_bit(BUTTON_4_PORT, BUTTON_4_PINNAME);
	}

}
