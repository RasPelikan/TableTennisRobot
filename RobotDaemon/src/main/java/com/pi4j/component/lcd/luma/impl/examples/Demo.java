package com.pi4j.component.lcd.luma.impl.examples;

import com.lexicalscope.jewel.cli.CliFactory;
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

public abstract class Demo<O extends DemoOptions> {

	protected O options;

	protected Demo(final String[] arguments) {

		this.options = CliFactory.parseArguments(getOptionsInterface(),
				arguments);

	}

	protected abstract Class<O> getOptionsInterface();

	protected abstract void demo(final MatrixLCD lcd) throws Exception;

	public void run() throws Exception {

		final SerialInterface serialInterface = buildSerialInterface();

		final MatrixLCD.Rotation rotation = MatrixLCD.Rotation.valueOf("CW"
				+ this.options.getDisplayRotation());

		final MatrixLCD lcd;
		switch (this.options.getDisplayType()) {
		case "SH1106":
			lcd = new SH1106Device(serialInterface,
					this.options.getDisplayWidth(),
					this.options.getDisplayHeight(), rotation);
			break;

		default:
			throw new UnsupportedOperationException("Display type '"
					+ this.options.getDisplayType() + "' is not implemented!");
		}

		demo(lcd);

		lcd.shutdown();

		final GpioController gpioController = GpioFactory.getInstance();
		gpioController.shutdown();

	}

	private SerialInterface buildSerialInterface() throws Exception {

		if (this.options.getDisplayInterface().equals(DemoOptions.INTERFACE_SPI)) {
			return buildSpiSerialInterface();
		} else {
			return buildI2CSerialInterface();
		}

	}

	private SpiSerialInterface buildSpiSerialInterface() throws Exception {

		final SpiChannel spiChannel = SpiChannel.getByNumber(
				this.options.getSpiChannel());
		final SpiDevice displaySpiDevice = SpiFactory.getInstance(spiChannel,
				this.options.getSpiSpeed());

		final GpioController gpioController = GpioFactory.getInstance();
		final GpioPinDigitalOutput resetPin = gpioController
				.provisionDigitalOutputPin(RaspiPin
						.getPinByAddress(this.options.getResetGpioPinNumber()));
		final GpioPinDigitalOutput dcPin = gpioController
				.provisionDigitalOutputPin(RaspiPin
						.getPinByAddress(this.options.getDCGpioPinNumber()));

		final SpiSerialInterface serialInterface = new SpiSerialInterface(
				displaySpiDevice, resetPin, dcPin,
				SpiSerialInterface.DEFAULT_TRANSFER_SIZE);

		return serialInterface;

	}

	private SerialInterface buildI2CSerialInterface() {
		throw new UnsupportedOperationException("I2C not yet implemented!");
	}

}
