#ifndef UTIL_H
#define UTIL_H

#define _CONCAT(a,b) a##b
#define CONCAT(a,b) _CONCAT(a,b)

#define clear_bit(WHERE, WHICH) WHERE &= ~(_BV(WHICH))

#define set_bit(WHERE, WHICH) WHERE |= _BV(WHICH)

#define is_bit_high(WHERE, WHICH) WHERE & _BV(WHICH)

#define is_bit_low(WHERE, WHICH) !(is_bit_high(WHERE, WHICH))

#define nop() asm volatile("nop")

#endif // UTIL_H
