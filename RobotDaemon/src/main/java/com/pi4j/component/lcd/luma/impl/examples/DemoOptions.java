package com.pi4j.component.lcd.luma.impl.examples;

import com.lexicalscope.jewel.cli.Option;

public interface DemoOptions {

	@Option(shortName = "h", longName = "help",
			helpRequest = true, description = "Print this output")
	boolean isHelp();

	String DISPLAY_SH1106 = "SH1106";

	@Option(shortName = "d", longName = "display",
			description = "The display type to be used. Possible values: '"
			+ DISPLAY_SH1106 + "'", defaultValue = DISPLAY_SH1106) 
	String getDisplayType();
	
	@Option(longName = "width", description = "Width of the display in pixels. Default: "
			+ "128", defaultValue = "128")
	int getDisplayWidth();
	
	@Option(longName = "height", description = "Height of the display in pixels: Default: '"
			+ "64", defaultValue = "64")
	int getDisplayHeight();
	
	String CW0 = "0";
	String CW90 = "90";
	String CW180 = "180";
	String CW270 = "270";
	
	@Option(longName = "rotation", description = "Possible values: " + CW0
			+ ", " + CW90 + ", " + CW180 + ", " + CW270, defaultValue = CW0)
	int getDisplayRotation();
	
	String INTERFACE_I2C = "i2c";
	String INTERFACE_SPI = "spi";
	
	@Option(shortName = "i", longName = "interface",
			description = "Serial interface type. Possible values: '"
			+ INTERFACE_SPI + "', '" + INTERFACE_I2C + "'. Default: '" + INTERFACE_SPI
			+ "'", defaultValue = INTERFACE_SPI)
	String getDisplayInterface();
	
	@Option(longName = "spi-port", description = "SPI port. Possible values: 0, 1. Default: 0",
			defaultValue = "0")
	int getSpiChannel();
	
	@Option(longName = "spi-speed", description = "SPI max bus speed (Hz). Default: 8000000"
			, defaultValue = "8000000")
	int getSpiSpeed();
	
	@Option(longName = "gpio-reset", description = "GPIO pin for RESET (SPI devices only). "
			+ "See http://pi4j.com -> PIN NUMBERING. Default: 5", defaultValue = "5")
	int getResetGpioPinNumber();
	
	@Option(longName = "gpio-data-command", description = "GPIO pin for D/C (SPI devices only). "
			+ "See http://pi4j.com -> PIN NUMBERING. Default: 6", defaultValue = "6")
	int getDCGpioPinNumber();

}
