#ifndef I2C_SLAVE_H
#define I2C_SLAVE_H

#include <stdint.h>
#include <avr/interrupt.h>

#define STATUS 0x00
#define BUTTONS0 0x01
#define BUTTONS1 0x02

volatile unsigned char buffer_address;
volatile unsigned char txbuffer[0xFF];
volatile unsigned char rxbuffer[0xFF];

void I2C_init(unsigned char address);
void I2C_stop(void);
ISR(TWI_vect);

#endif // I2C_SLAVE_H
