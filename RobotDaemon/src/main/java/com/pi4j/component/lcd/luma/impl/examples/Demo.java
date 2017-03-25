package com.pi4j.component.lcd.luma.impl.examples;

import com.pi4j.component.lcd.MatrixLCD;
import com.pi4j.component.lcd.luma.SerialInterface;
import com.pi4j.component.lcd.luma.impl.oled.SH1106Device;
import com.pi4j.component.lcd.luma.impl.serial.SpiSerialInterface;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

public abstract class Demo extends DemoOpts {

	protected abstract void demo(final MatrixLCD lcd) throws Exception;
	
	public void run() throws Exception {
		
		final SerialInterface serialInterface = buildSerialInterface();
		
		final MatrixLCD lcd;
		switch (this.display) {
			case "SH1106":
				lcd = new SH1106Device(serialInterface, super.width, super.height);
				break;
				
			default:
				throw new UnsupportedOperationException("Display type '"
						+ super.display + "' is not implemented!");
		}
		
		demo(lcd);
		
		lcd.shutdown();
		
		final GpioController gpioController = GpioFactory.getInstance();
		gpioController.shutdown();
		
	}
	
	private SerialInterface buildSerialInterface() throws Exception {
		
		if (super.spi) {
			return buildSpiSerialInterface();
		} else {
			return buildI2CSerialInterface();
		}
		
	}
	
	private SpiSerialInterface buildSpiSerialInterface() throws Exception {
		
		final SpiChannel spiChannel = SpiChannel.getByNumber(super.csChannel);
		final SpiDevice displaySpiDevice = SpiFactory.getInstance(
				spiChannel, super.speed);
		
		final GpioController gpioController = GpioFactory.getInstance();
		final GpioPinDigitalOutput resetPin = gpioController.provisionDigitalOutputPin(
				RaspiPin.getPinByAddress(super.resetGpioPin));
		final GpioPinDigitalOutput dcPin = gpioController.provisionDigitalOutputPin(
				RaspiPin.getPinByAddress(super.dcGpioPin));
		
		final SpiSerialInterface serialInterface = new SpiSerialInterface(displaySpiDevice,
				resetPin,
				dcPin,
				SpiSerialInterface.DEFAULT_TRANSFER_SIZE);
		
		return serialInterface;
		
	}

	private SerialInterface buildI2CSerialInterface() {
		throw new UnsupportedOperationException("I2C not yet implemented!");
	}
	
}
