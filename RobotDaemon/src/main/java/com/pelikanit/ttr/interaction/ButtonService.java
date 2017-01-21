package com.pelikanit.ttr.interaction;

import java.util.HashSet;

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

/**
 * Handles buttons via GPIO pins.
 * <p>
 * In my situation there a six lines for nine buttons:
 * <ul>
 * <li>ONE:
 */
public class ButtonService {
	
	public static final long FIRE_INTERVAL_MS = 250;

	public static enum Button {
		ONE, TWO, THREE, FOUR, LEFT, RIGHT, UP, DOWN, MAIN;
	};

	private volatile boolean shutdown;
	private HashSet<ButtonListener> listener;
	private volatile Button activeButton;
	private Thread fireButtonEventsThread;

	private GpioPinDigitalInput button1;
	private GpioPinDigitalInput button2;
	private GpioPinDigitalInput button3;
	private GpioPinDigitalInput button4;
	private GpioPinDigitalInput up;
	private GpioPinDigitalInput down;
	private GpioPinDigitalInput left;
	private GpioPinDigitalInput right;
	private GpioPinDigitalInput main;

	public ButtonService(final RobotDaemon owner,
			final ConfigurationUtils config) {

		shutdown = false;
		activeButton = null;
		listener = new HashSet<ButtonListener>();
		
		final GpioController gpioController = owner.getGpioController();
		
		fireButtonEventsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				fireButtonEvents();
			}
		});
		fireButtonEventsThread.start();

		final Pin button1Pin = RaspiPin
				.getPinByAddress(config.getButton1Gpio());
		button1 = gpioController.provisionDigitalInputPin(button1Pin,
				PinPullResistance.PULL_DOWN);
		button1.setShutdownOptions(true);
		button1.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.ONE, event);
			}
		});

		final Pin button2Pin = RaspiPin
				.getPinByAddress(config.getButton2Gpio());
		button2 = gpioController.provisionDigitalInputPin(button2Pin,
				PinPullResistance.PULL_DOWN);
		button2.setShutdownOptions(true);
		button2.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.TWO, event);
			}
		});

		final Pin button3Pin = RaspiPin
				.getPinByAddress(config.getButton3Gpio());
		button3 = gpioController.provisionDigitalInputPin(button3Pin,
				PinPullResistance.PULL_DOWN);
		button3.setShutdownOptions(true);
		button3.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.THREE, event);
			}
		});

		final Pin button4Pin = RaspiPin
				.getPinByAddress(config.getButton4Gpio());
		button4 = gpioController.provisionDigitalInputPin(button4Pin,
				PinPullResistance.PULL_DOWN);
		button4.setShutdownOptions(true);
		button4.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.FOUR, event);
			}
		});

		final Pin upPin = RaspiPin.getPinByAddress(config.getButtonUpGpio());
		up = gpioController.provisionDigitalInputPin(upPin,
				PinPullResistance.PULL_DOWN);
		up.setShutdownOptions(true);
		up.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.UP, event);
			}
		});

		final Pin downPin = RaspiPin
				.getPinByAddress(config.getButtonDownGpio());
		down = gpioController.provisionDigitalInputPin(downPin,
				PinPullResistance.PULL_DOWN);
		down.setShutdownOptions(true);
		down.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.DOWN, event);
			}
		});

		final Pin leftPin = RaspiPin
				.getPinByAddress(config.getButtonLeftGpio());
		left = gpioController.provisionDigitalInputPin(leftPin,
				PinPullResistance.PULL_DOWN);
		left.setShutdownOptions(true);
		left.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.LEFT, event);
			}
		});

		final Pin rightPin = RaspiPin.getPinByAddress(config
				.getButtonRightGpio());
		right = gpioController.provisionDigitalInputPin(rightPin,
				PinPullResistance.PULL_DOWN);
		right.setShutdownOptions(true);
		right.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.RIGHT, event);
			}
		});

		final Pin mainPin = RaspiPin
				.getPinByAddress(config.getButtonMainGpio());
		main = gpioController.provisionDigitalInputPin(mainPin,
				PinPullResistance.PULL_DOWN);
		main.setShutdownOptions(true);
		main.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					final GpioPinDigitalStateChangeEvent event) {
				handlePinEvent(Button.MAIN, event);
			}
		});

	}

	public void registerListener(final ButtonListener listener) {

		synchronized (this) {
			this.listener.add(listener);
		}

	}

	public void unregisterListener(final ButtonListener listener) {

		synchronized (this) {
			this.listener.remove(listener);
		}

	}

	private void fireButtonEvents() {
		
		synchronized (fireButtonEventsThread) {

			while (!shutdown) {
				
				final long sleepInterval = activeButton != null ? FIRE_INTERVAL_MS : 10000;
				try {
					fireButtonEventsThread.wait(sleepInterval);
				} catch (InterruptedException e) {
					// expected
				}
				
				if (!shutdown) {
					
					if (activeButton != null) {
						
						synchronized (this) {
							for (final ButtonListener listener : this.listener) {
								listener.buttonEvent(activeButton);
							}
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public void shutdown() {
		
		shutdown = true;
		synchronized (fireButtonEventsThread) {
			fireButtonEventsThread.notify();
		}
		
	}
	
	private void handlePinEvent(final Button button,
			final GpioPinDigitalStateChangeEvent event) {
		
		if (event.getEdge() == PinEdge.RISING) {
			activeButton = button;
		} else if (event.getEdge() == PinEdge.FALLING) {
			activeButton = null;
		}
		
		synchronized (fireButtonEventsThread) {
			fireButtonEventsThread.notify();
		}

	}

}
