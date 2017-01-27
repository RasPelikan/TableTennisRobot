package com.pelikanit.ttr.support;

import java.io.IOException;
import java.util.HashSet;

import com.pelikanit.ttr.RobotDaemon;
import com.pelikanit.ttr.utils.ConfigurationUtils;

/**
 * Handles buttons via GPIO pins.
 * <p>
 * In my situation there a six lines for nine buttons:
 * <ul>
 * <li>ONE:
 */
public class ButtonService {
	
	private static final int ADDRESS_PRESSED_BUTTON = 0x01;
	
	public static final long FIRE_INTERVAL_MS = 250;

	public static enum Button {
		ONE(1), TWO(2), THREE(3), FOUR(4),
		LEFT(5), RIGHT(6), UP(7), DOWN(8), MAIN(9);
		
		private int statusCode;
		
		private Button(final int statusCode) {
			this.statusCode = statusCode;
		}
		
		public int getStatusCode() {
			return statusCode;
		}
		
		public static Button byStatusCode(final int statusCode) {
			for (final Button button : values()) {
				if (button.getStatusCode() == statusCode) {
					return button;
				}
			}
			return null;
		}
	};

	private RobotDaemon owner;
	
	private volatile boolean shutdown;
	private HashSet<ButtonListener> listener;
	private volatile Button activeButton;
	private Thread fireButtonEventsThread;

	public ButtonService(final RobotDaemon owner,
			final ConfigurationUtils config) {

		this.owner = owner;
		
		shutdown = false;
		activeButton = null;
		listener = new HashSet<ButtonListener>();
		
		fireButtonEventsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				fireButtonEvents();
			}
		});
		fireButtonEventsThread.start();

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
	
	public void processStatusChangedEvent() {
		
		try {
			
			final int statusCode = owner.getSupportDevice().read(ADDRESS_PRESSED_BUTTON);
			activeButton = Button.byStatusCode(statusCode);

			synchronized (fireButtonEventsThread) {
				fireButtonEventsThread.notify();
			}
			
		} catch (IOException e) {
			System.err.println("Could not read button status code!");
			e.printStackTrace();
		}
		
	}
	
}
