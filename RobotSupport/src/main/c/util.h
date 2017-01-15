#define clear_bit(WHERE, WHICH) WHERE &= ~(_BV(WHICH))

#define set_bit(WHERE, WHICH) WHERE |= _BV(WHICH)

#define is_bit_high(WHERE, WHICH) WHERE & _BV(WHICH)

#define is_bit_low(WHERE, WHICH) !(is_bit_high(WHERE, WHICH))

#define nop() asm volatile("nop")
