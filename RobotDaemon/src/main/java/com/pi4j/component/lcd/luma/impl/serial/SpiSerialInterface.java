package com.pi4j.component.lcd.luma.impl.serial;

import java.io.IOException;

import com.pi4j.component.lcd.luma.SerialInterface;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.spi.SpiDevice;

public class SpiSerialInterface implements SerialInterface {

	public static int DEFAULT_SPEED = 8000000;
	public static int DEFAULT_TRANSFER_SIZE = 4096;
	private static PinState CMD_PIN_STATE = PinState.LOW;
	private static PinState DATA_PIN_STATE = PinState.HIGH;
	
	private final SpiDevice spiDevice;
	private final GpioPinDigitalOutput dcPin;
	private final GpioPinDigitalOutput resetPin;
	private final int transferSize;
	
	public SpiSerialInterface(final SpiDevice spiDevice,
			final GpioPinDigitalOutput dcPin,
			final GpioPinDigitalOutput resetPin,
			final int transferSize) {
		
		this.spiDevice = spiDevice;
		this.dcPin = dcPin;
		this.resetPin = resetPin;
		this.transferSize = transferSize;
		
		resetDevice();
		
	}
	
	private void resetDevice() {
		
		this.resetPin.setState(PinState.LOW);  // reset
		this.resetPin.setState(PinState.HIGH); // Keep RESET pulled high
		
	}
	
	@Override
	public void command(byte... cmds) throws IOException {
		
		this.dcPin.setState(SpiSerialInterface.CMD_PIN_STATE);
		this.spiDevice.write(cmds);
		
	}
	
	@Override
	public void data(byte[] data) throws IOException {
		
		this.dcPin.setState(SpiSerialInterface.DATA_PIN_STATE);
		
		for (int offset = 0; offset < data.length; offset += transferSize) {
			
			int remaining = data.length - offset;
			if (remaining < transferSize) {
				
				this.spiDevice.write(data, offset, remaining);
				
			} else {
				
				this.spiDevice.write(data, offset, transferSize);
				
			}
			
		}
		
	}
	
}
