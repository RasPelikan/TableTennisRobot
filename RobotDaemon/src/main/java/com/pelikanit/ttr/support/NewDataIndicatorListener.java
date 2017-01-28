package com.pelikanit.ttr.support;

import java.io.IOException;

import com.pelikanit.ttr.RobotDaemon;
import com.pelikanit.ttr.utils.ConfigurationUtils;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class NewDataIndicatorListener {

	public static final int ADDRESS_STATUS = 0x00;
	public static final int STATUS_BUTTONS_CHANGED = 0x01;
	
	private final RobotDaemon owner;
	
	public NewDataIndicatorListener(final RobotDaemon owner,
			final ConfigurationUtils config) throws IOException {

		this.owner = owner;
		
		final GpioController gpioController = owner.getGpioController();
		
		final Pin newDataPin = RaspiPin
				.getPinByAddress(config.getSupportNewDataIndicatorGpio());
		GpioPinDigitalInput newData = gpioController.provisionDigitalInputPin(newDataPin,
				PinPullResistance.PULL_DOWN);
		newData.setShutdownOptions(true);
		newData.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(event);
			}
		});

	}
	
	public void shutdown() {
		
	}
	
	private void handlePinEvent(final GpioPinDigitalStateChangeEvent event) {

		// pin change indicates action
		if ((event.getEdge() == PinEdge.RISING) 
				|| (event.getEdge() == PinEdge.FALLING)) {
			try {
				
				final int status = owner.getSupportDevice().read(ADDRESS_STATUS);
				switch (status) {
					case STATUS_BUTTONS_CHANGED:
						owner.getButtonService().processStatusChangedEvent();
						break;
						
					default:
						if (status < 0) {
							System.err.println(
									"Could not read support-device status byte! Got: " + status);
						}
				}
				
			} catch (IOException e) {
				System.err.println("Could not read support-device status!");
				e.printStackTrace();
			}
			
		}
		
	}
	
}
