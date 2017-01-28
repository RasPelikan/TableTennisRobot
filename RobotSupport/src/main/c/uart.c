#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdio.h>
#include <stdbool.h>

#ifndef F_CPU
#define F_CPU 8000000UL
#endif

#ifndef BAUD
#define BAUD 9600
#endif
#include <util/setbaud.h>
#include "util.h"

/* http://www.cs.mun.ca/~rod/Winter2007/4723/notes/serial/serial.html */

#define DETECTION_PORT PORTD
#define DETECTION_PIN PIND
#define DETECTION_DDR DDRD
#define DETECTION_PINNAME PD2
#define DETECTION_INTERRUPT_REGISTER 3
#define DETECTION_INTERRUPT_MASK CONCAT(PCMSK, DETECTION_INTERRUPT_REGISTER)
#define DETECTION_INTERRUPT CONCAT(PCINT, CONCAT(DETECTION_INTERRUPT_REGISTER, _vect))
#define DETECTION_INTERRUPT_ID PCINT26
#define DETECTION_INTERRUPT_ENABLE CONCAT(PCIE, DETECTION_INTERRUPT_REGISTER)

static bool UART_ACTIVE = false;

uint8_t backup_H;
uint8_t backup_L;
uint8_t backup_A;
uint8_t backup_B;
uint8_t backup_C;

void uart_disable() {

	if (!UART_ACTIVE) {
		return;
	}

	UBRR0H = backup_H;
	UBRR0L = backup_L;
	UCSR0A = backup_A;
	UCSR0B = backup_B;
	UCSR0C = backup_C;

	UART_ACTIVE = false;

}

void uart_enable() {

	if (UART_ACTIVE) {
		return;
	}

	backup_H = UBRR0H;
	backup_L = UBRR0L;
	backup_A = UCSR0A;
	backup_B = UCSR0B;
	backup_C = UCSR0C;

    UBRR0H = UBRRH_VALUE;
    UBRR0L = UBRRL_VALUE;

#if USE_2X
    UCSR0A |= _BV(U2X0);
#else
    UCSR0A &= ~(_BV(U2X0));
#endif

    UCSR0C = _BV(UCSZ01) | _BV(UCSZ00); /* 8-bit data */
    UCSR0B = _BV(RXEN0) | _BV(TXEN0);   /* Enable RX and TX */

	UART_ACTIVE = true;

}

void uart_putchar(char c, FILE *stream) {

	if (!UART_ACTIVE) {
		return;
	}

    if (c == '\n') {
        uart_putchar('\r', stream);
    }
    loop_until_bit_is_set(UCSR0A, UDRE0);
    UDR0 = c;

}

char uart_getchar(FILE *stream) {

	if (!UART_ACTIVE) {
		return 0x00;
	}

    loop_until_bit_is_set(UCSR0A, RXC0);
    return UDR0;

}

void uart_init() {

	// enable uart-detector (a line which is connected to VCC of
	// communication partner
	clear_bit(DETECTION_DDR, DETECTION_PINNAME);     // set for input
	set_bit(DETECTION_PORT, DETECTION_PINNAME);    // pull-up

	set_bit(DETECTION_INTERRUPT_MASK, DETECTION_INTERRUPT_ID);
	set_bit(PCICR, DETECTION_INTERRUPT_ENABLE);

	if (is_bit_low(DETECTION_PIN, DETECTION_PINNAME)) {
		uart_enable();
	}

}

ISR(DETECTION_INTERRUPT) {

	if (is_bit_low(DETECTION_PIN, DETECTION_PINNAME)) {
		uart_enable();
	} else {
		uart_disable();
	}

}
