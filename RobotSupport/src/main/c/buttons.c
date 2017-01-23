#include <stdbool.h>
#include <stdint.h>

#include "I2C_slave.h"
#include "buttons.h"
#include "util.h"

#define BROWN_PORT CONCAT(PORT, BROWN_PORTLETTER)
#define BROWN_DDR CONCAT(DDR, BROWN_PORTLETTER)
#define BROWN_PIN CONCAT(PIN, BROWN_PORTLETTER)
#define BROWN_PINNAME CONCAT(P, CONCAT(BROWN_PORTLETTER, BROWN_PINNUMBER))
#define BROWN_VAR CONCAT(pins, BROWN_PORTLETTER)

#define RED_PORT CONCAT(PORT, RED_PORTLETTER)
#define RED_DDR CONCAT(DDR, RED_PORTLETTER)
#define RED_PIN CONCAT(PIN, RED_PORTLETTER)
#define RED_PINNAME CONCAT(P, CONCAT(RED_PORTLETTER, RED_PINNUMBER))
#define RED_VAR CONCAT(pins, RED_PORTLETTER)

#define ORANGE_PORT CONCAT(PORT, ORANGE_PORTLETTER)
#define ORANGE_DDR CONCAT(DDR, ORANGE_PORTLETTER)
#define ORANGE_PIN CONCAT(PIN, ORANGE_PORTLETTER)
#define ORANGE_PINNAME CONCAT(P, CONCAT(ORANGE_PORTLETTER, ORANGE_PINNUMBER))
#define ORANGE_VAR CONCAT(pins, ORANGE_PORTLETTER)

#define YELLOW_PORT CONCAT(PORT, YELLOW_PORTLETTER)
#define YELLOW_DDR CONCAT(DDR, YELLOW_PORTLETTER)
#define YELLOW_PIN CONCAT(PIN, YELLOW_PORTLETTER)
#define YELLOW_PINNAME CONCAT(P, CONCAT(YELLOW_PORTLETTER, YELLOW_PINNUMBER))
#define YELLOW_VAR CONCAT(pins, YELLOW_PORTLETTER)

#define GREEN_PORT CONCAT(PORT, GREEN_PORTLETTER)
#define GREEN_DDR CONCAT(DDR, GREEN_PORTLETTER)
#define GREEN_PIN CONCAT(PIN, GREEN_PORTLETTER)
#define GREEN_PINNAME CONCAT(P, CONCAT(GREEN_PORTLETTER, GREEN_PINNUMBER))
#define GREEN_VAR CONCAT(pins, GREEN_PORTLETTER)

#define BLUE_PORT CONCAT(PORT, BLUE_PORTLETTER)
#define BLUE_DDR CONCAT(DDR, BLUE_PORTLETTER)
#define BLUE_PIN CONCAT(PIN, BLUE_PORTLETTER)
#define BLUE_PINNAME CONCAT(P, CONCAT(BLUE_PORTLETTER, BLUE_PINNUMBER))
#define BLUE_VAR CONCAT(pins, BLUE_PORTLETTER)

#define BUTTON_1_VAR CONCAT(buttons, BUTTON_1_BYTE)
#define BUTTON_2_VAR CONCAT(buttons, BUTTON_2_BYTE)
#define BUTTON_3_VAR CONCAT(buttons, BUTTON_3_BYTE)
#define BUTTON_4_VAR CONCAT(buttons, BUTTON_4_BYTE)
#define BUTTON_UP_VAR CONCAT(buttons, BUTTON_UP_BYTE)
#define BUTTON_DOWN_VAR CONCAT(buttons, BUTTON_DOWN_BYTE)
#define BUTTON_LEFT_VAR CONCAT(buttons, BUTTON_LEFT_BYTE)
#define BUTTON_RIGHT_VAR CONCAT(buttons, BUTTON_RIGHT_BYTE)
#define BUTTON_MAIN_VAR CONCAT(buttons, BUTTON_MAIN_BYTE)

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

	txbuffer[BUTTONS0] = 0x00;
	txbuffer[BUTTONS1] = 0x00;

}

bool check_buttons() {

	bool furtherGreenChecks = true;
	bool furtherBrownChecks = true;
	bool furtherYellowChecks = true;

	bool setButton1 = false;
	bool setButton2 = false;

	unsigned char buttons0 = txbuffer[BUTTONS0];
	unsigned char buttons1 = txbuffer[BUTTONS1];

	// BUTTON_3 (RED-BLACK)
	if (is_bit_low(RED_PIN, RED_PINNAME)) {
		set_bit(BUTTON_3_VAR, BUTTON_3_BIT);
	} else {
		clear_bit(BUTTON_3_VAR, BUTTON_3_BIT);

		set_bit(RED_DDR, RED_PINNAME);    // set RED for output
		set_bit(RED_PORT, RED_PINNAME);   // RED to high (see manual chapter 14.2.3)
		clear_bit(RED_PORT, RED_PINNAME); // RED to low
		nop();                            // sync for reading (see manual Figure 14-4)
		nop();
		nop();

		// DOWN (RED-BROWN)
		if (is_bit_low(BROWN_PIN, BROWN_PINNAME)) {
			set_bit(BUTTON_DOWN_VAR, BUTTON_DOWN_BIT);
			furtherBrownChecks = false;
		} else {
			clear_bit(BUTTON_DOWN_VAR, BUTTON_DOWN_BIT);
		}

		// BUTTON_2 (RED-GREEN)
		if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
			setButton2 = true;
			furtherGreenChecks = false;
		}

		if (is_bit_low(YELLOW_PIN, YELLOW_PINNAME)) {
			set_bit(BUTTON_LEFT_VAR, BUTTON_LEFT_BIT);
			furtherYellowChecks = false;
		} else {
			clear_bit(BUTTON_LEFT_VAR, BUTTON_LEFT_BIT);
		}

		// reset RED for next cycle
		clear_bit(RED_DDR, RED_PINNAME);    // set RED for input
		clear_bit(RED_PORT, RED_PINNAME);     // pull-up for RED
		set_bit(RED_PORT, RED_PINNAME);     // pull-up for RED
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

	}

	if (furtherBrownChecks || furtherYellowChecks) {

		set_bit(ORANGE_DDR, ORANGE_PINNAME);    // set ORANGE for output
		set_bit(ORANGE_PORT, ORANGE_PINNAME);   // ORANGE to high (see manual chapter 14.2.3)
		clear_bit(ORANGE_PORT, ORANGE_PINNAME); // ORANGE to low
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

		if (furtherBrownChecks) {

			// (BROWN-ORANGE)
			if (is_bit_low(BROWN_PIN, BROWN_PINNAME)) {
				set_bit(BUTTON_UP_VAR, BUTTON_UP_BIT);
			} else {
				clear_bit(BUTTON_UP_VAR, BUTTON_UP_BIT);
			}

		}

		if (furtherYellowChecks) {

			// (YELLOW-ORANGE)
			if (is_bit_low(YELLOW_PIN, YELLOW_PINNAME)) {
				set_bit(BUTTON_RIGHT_VAR, BUTTON_RIGHT_BIT);
				furtherYellowChecks = false;
			} else {
				clear_bit(BUTTON_RIGHT_VAR, BUTTON_RIGHT_BIT);
			}

		}

		// reset ORANGE for next cycle
		clear_bit(ORANGE_DDR, ORANGE_PINNAME);  // set ORANGE for input
		clear_bit(ORANGE_PORT, ORANGE_PINNAME);   // pull-up for ORANGE
		set_bit(ORANGE_PORT, ORANGE_PINNAME);   // pull-up for ORANGE
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

	}

	if (furtherGreenChecks && furtherYellowChecks) {

		set_bit(YELLOW_DDR, YELLOW_PINNAME);    // set YELLOW for output
		set_bit(YELLOW_PORT, YELLOW_PINNAME);   // YELLOW to high (see manual chapter 14.2.3)
		clear_bit(YELLOW_PORT, YELLOW_PINNAME); // YELLOW to low
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

		// BUTTON_1 (GREEN-YELLOW)
		if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
			setButton1 = true;
		}

		// reset YELLOW for next cycle
		clear_bit(YELLOW_DDR, YELLOW_PINNAME);  // set YELLOW for input
		clear_bit(YELLOW_PORT, YELLOW_PINNAME);   // pull-up for YELLOW
		set_bit(YELLOW_PORT, YELLOW_PINNAME);   // pull-up for YELLOW
		nop();
		nop();
		nop();

	}

	// MAIN (GREEN-BLACK)
	if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
		set_bit(BUTTON_MAIN_VAR, BUTTON_MAIN_BIT);
	} else {
		clear_bit(BUTTON_MAIN_VAR, BUTTON_MAIN_BIT);

		if (setButton1) {
			set_bit(BUTTON_1_VAR, BUTTON_1_BIT);
		} else {
			clear_bit(BUTTON_1_VAR, BUTTON_1_BIT);
		}
		if (setButton2) {
			set_bit(BUTTON_2_VAR, BUTTON_2_BIT);
		} else {
			clear_bit(BUTTON_2_VAR, BUTTON_2_BIT);
		}

	}

	// BUTTON_4
	if (is_bit_low(BLUE_PIN, BLUE_PINNAME)) {
		set_bit(BUTTON_4_VAR, BUTTON_4_BIT);
	} else {
		clear_bit(BUTTON_4_VAR, BUTTON_4_BIT);
	}

	bool result = false;
	if (buttons0 != txbuffer[BUTTONS0]) {
		result = true;
		txbuffer[BUTTONS0] = buttons0;
		printf("BUTTONS0 %d %d %d\n", buttons0, buttons1, furtherGreenChecks ? 1 : 0);
	}
	if (buttons1 != txbuffer[BUTTONS1]) {
		result = true;
		txbuffer[BUTTONS1] = buttons1;
		printf("BUTTONS1 %d %d %d\n", buttons0, buttons1, furtherGreenChecks ? 1 : 0);
	}
	return result;

}
