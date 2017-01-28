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

	txbuffer[BUTTONS] = 0x00;

}

bool check_buttons() {

	unsigned char pressedButton = 0x00;

	// BUTTON_3 (RED-BLACK)
	if (is_bit_low(RED_PIN, RED_PINNAME)) {
		pressedButton = BUTTON_3_ID;
	} else {
		set_bit(RED_DDR, RED_PINNAME);    // set RED for output
		set_bit(RED_PORT, RED_PINNAME);   // RED to high (see manual chapter 14.2.3)
		clear_bit(RED_PORT, RED_PINNAME); // RED to low
		nop();                            // sync for reading (see manual Figure 14-4)
		nop();
		nop();

		// DOWN (RED-BROWN)
		if (is_bit_low(BROWN_PIN, BROWN_PINNAME)) {
			pressedButton = BUTTON_DOWN_ID;
		}

		// BUTTON_2 (RED-GREEN)
		if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
			pressedButton = BUTTON_2_ID;
		}

		if (is_bit_low(YELLOW_PIN, YELLOW_PINNAME)) {
			pressedButton = BUTTON_LEFT_ID;
		}

		// reset RED for next cycle
		clear_bit(RED_DDR, RED_PINNAME);    // set RED for input
		clear_bit(RED_PORT, RED_PINNAME);     // pull-up for RED
		set_bit(RED_PORT, RED_PINNAME);     // pull-up for RED
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

	}

	if (pressedButton == 0x00) {

		set_bit(ORANGE_DDR, ORANGE_PINNAME);    // set ORANGE for output
		set_bit(ORANGE_PORT, ORANGE_PINNAME);   // ORANGE to high (see manual chapter 14.2.3)
		clear_bit(ORANGE_PORT, ORANGE_PINNAME); // ORANGE to low
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

		// (BROWN-ORANGE)
		if (is_bit_low(BROWN_PIN, BROWN_PINNAME)) {
			pressedButton = BUTTON_UP_ID;
		}

		// (YELLOW-ORANGE)
		if (is_bit_low(YELLOW_PIN, YELLOW_PINNAME)) {
			pressedButton = BUTTON_RIGHT_ID;
		}

		// reset ORANGE for next cycle
		clear_bit(ORANGE_DDR, ORANGE_PINNAME);  // set ORANGE for input
		clear_bit(ORANGE_PORT, ORANGE_PINNAME);   // pull-up for ORANGE
		set_bit(ORANGE_PORT, ORANGE_PINNAME);   // pull-up for ORANGE
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

	}

	if (pressedButton == 0x00) {

		set_bit(YELLOW_DDR, YELLOW_PINNAME);    // set YELLOW for output
		set_bit(YELLOW_PORT, YELLOW_PINNAME);   // YELLOW to high (see manual chapter 14.2.3)
		clear_bit(YELLOW_PORT, YELLOW_PINNAME); // YELLOW to low
		nop();                                  // sync for reading (see manual Figure 14-4)
		nop();
		nop();

		// BUTTON_1 (GREEN-YELLOW)
		if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
			pressedButton = BUTTON_1_ID;
		}

		// reset YELLOW for next cycle
		clear_bit(YELLOW_DDR, YELLOW_PINNAME);  // set YELLOW for input
		clear_bit(YELLOW_PORT, YELLOW_PINNAME);   // pull-up for YELLOW
		set_bit(YELLOW_PORT, YELLOW_PINNAME);   // pull-up for YELLOW
		nop();
		nop();
		nop();

	}

	if ((pressedButton == 0x00)
			|| (pressedButton == BUTTON_1_ID)
			|| (pressedButton == BUTTON_2_ID)) {

		// MAIN (GREEN-BLACK)
		if (is_bit_low(GREEN_PIN, GREEN_PINNAME)) {
			pressedButton = BUTTON_MAIN_ID;
		}

	}

	if (pressedButton == 0x00) {

		// BUTTON_4
		if (is_bit_low(BLUE_PIN, BLUE_PINNAME)) {
			pressedButton = BUTTON_4_ID;
		}

	}

	if (pressedButton != txbuffer[BUTTONS]) {

		txbuffer[BUTTONS] = pressedButton;
		return true;

	} else {
		return false;
	}

}
