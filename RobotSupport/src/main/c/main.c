#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdbool.h>

#include "uart.h"
#include "buttons.h"
#include "util.h"

static volatile bool do_it = false;

ISR(TIMER0_OVF_vect) {

	do_it = true;

}

void init_timer() {

	TIMSK0 = 0x00;			// Disable timer0 (if not yet disabled)
	TCCR0A = 0x00;          // Mode = Normal
	set_bit(TCCR0B, CS00);  // Prescaler to 64
	set_bit(TCCR0B, CS10);  // Prescaler to 64
	clear_bit(TCCR0B, CS02);  // Prescaler to 64
	TCNT0 = 0x00;			// Clear timer-counter
	set_bit(TIMSK0, TOIE0); // enable timer0 overflow interrupt

	printf("timer initialized\n");

}

/*
 * Boot the device (set start-configuration)
 */
void boot() {

	//uart_init();
	stdout = &uart_output;
	stdin  = &uart_input;
	sei();                      // enable global interrupts

	printf("boot\n");

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
 * Main-routine
 */
int main() {

	// configure MCU and start timers
	boot();

	while(1) {

		if (do_it) {

			do_it = false;

			check_buttons();

		}

	}

	return 0;

}
