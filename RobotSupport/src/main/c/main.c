#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdbool.h>
#include <stdint.h>
#include <util/twi.h>

#include "util.h"
#include "uart.h"
//#include "I2C_slave.h"
#include "buttons.h"

#define NEWDATA_PORT
#define I2C_ADDRESS 0x50

#define INDICATOR_PORTLETTER D
#define INDICATOR_PINNUMBER 2

#define INDICATOR_PORT CONCAT(PORT, INDICATOR_PORTLETTER)
#define INDICATOR_DDR CONCAT(DDR, INDICATOR_PORTLETTER)
#define INDICATOR_PIN CONCAT(PIN, INDICATOR_PORTLETTER)
#define INDICATOR_PINNAME CONCAT(P, CONCAT(INDICATOR_PORTLETTER, INDICATOR_PINNUMBER))

#define ACKN 0
#define CHECK_BUTTONS 1

static volatile int do_it = ACKN;

ISR(TIMER0_OVF_vect) {

	do_it = CHECK_BUTTONS;

}

void init_timer() {

	TIMSK0 = 0x00;			// Disable timer0 (if not yet disabled)
	TCCR0A = 0x00;          // Mode = Normal
	clear_bit(TCCR0B, CS00);  // Prescaler to 256
	clear_bit(TCCR0B, CS10);  // Prescaler to 256
	set_bit(TCCR0B, CS02);  // Prescaler to 256
	TCNT0 = 0x00;			// Clear timer-counter
	set_bit(TIMSK0, TOIE0); // enable timer0 overflow interrupt

	printf("timer initialized\n");

}

/*
 * Boot the device (set start-configuration)
 */
void boot() {

	uart_init();
	stdout = &uart_output;
	stdin  = &uart_input;
	sei();                      // enable global interrupts

	printf("boot\n");

	/*
	 * new data indicator
	 */
	clear_bit(INDICATOR_PORT, INDICATOR_PINNAME); // INDICATOR low
	set_bit(INDICATOR_DDR, INDICATOR_PINNAME); // set INDICATOR for output

	/*
	 * I2C slave
	 */
	//I2C_init(I2C_ADDRESS);

	/*
	 * configure MCU
	 */
	buttons_init();

	/*
	 * init ADC
	 */
	ADMUX = _BV(REFS0);        // AREF = AVcc
	// Enable ADC and set prescaler to 64 = 4MHz / 64 = 64kHz
	ADCSRA = _BV(ADEN) | _BV(ADPS2) | _BV(ADPS1);

	init_timer();

}

/*
 * Shutdown
 */
void shutdown() {

	//I2C_stop();

}

void indicate_new_data() {

	set_bit(INDICATOR_PORT, INDICATOR_PINNAME); // INDICATOR high
	nop();
	clear_bit(INDICATOR_PORT, INDICATOR_PINNAME); // INDICATOR low

}

/*
 * Main-routine
 */
int main() {

	// configure MCU and start timers
	boot();

	while(1) {

		switch (do_it) {

			case CHECK_BUTTONS:
				do_it = ACKN;
				if (check_buttons()) {
					indicate_new_data();
				}
				break;

		}

	}

	shutdown();

	return 0;

}
