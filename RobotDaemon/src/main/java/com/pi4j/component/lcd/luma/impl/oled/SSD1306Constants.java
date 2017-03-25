package com.pi4j.component.lcd.luma.impl.oled;

import com.pi4j.component.lcd.luma.impl.core.constants.Common;

public class SSD1306Constants extends Common {

	public static final byte CHARGEPUMP = (byte) 0x8D;
    public static final byte COLUMNADDR = (byte) 0x21;
    public static final byte COMSCANDEC = (byte) 0xC8;
    public static final byte COMSCANINC = (byte) 0xC0;
    public static final byte EXTERNALVCC = (byte) 0x1;
    public static final byte MEMORYMODE = (byte) 0x20;
    public static final byte PAGEADDR = (byte) 0x22;
    public static final byte SETCOMPINS = (byte) 0xDA;
    public static final byte SETDISPLAYCLOCKDIV = (byte) 0xD5;
    public static final byte SETDISPLAYOFFSET = (byte) 0xD3;
    public static final byte SETHIGHCOLUMN = (byte) 0x10;
    public static final byte SETLOWCOLUMN = (byte) 0x00;
    public static final byte SETPRECHARGE = (byte) 0xD9;
    public static final byte SETSEGMENTREMAP = (byte) 0xA1;
    public static final byte SETSTARTLINE = (byte) 0x40;
    public static final byte SETVCOMDETECT = (byte) 0xDB;
    public static final byte SWITCHCAPVCC = (byte) 0x2;
        
}
