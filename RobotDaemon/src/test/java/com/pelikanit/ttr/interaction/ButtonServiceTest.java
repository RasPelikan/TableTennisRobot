package com.pelikanit.ttr.interaction;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.pelikanit.ttr.RobotDaemon;
import com.pelikanit.ttr.support.ButtonListener;
import com.pelikanit.ttr.support.ButtonService;
import com.pelikanit.ttr.support.ButtonService.Button;
import com.pelikanit.ttr.utils.ConfigurationUtils;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

@RunWith(MockitoJUnitRunner.class)
public class ButtonServiceTest {

	/*
	private static final boolean HIGH = true;
	private static final boolean LOW = false;
	
	private static enum ButtonAddress {
		BUTTON1(1, Button.ONE),
		BUTTON2(2, Button.TWO),
		BUTTON3(3, Button.THREE),
		BUTTON4(4, Button.FOUR),
		UP(5, Button.UP),
		DOWN(6, Button.DOWN),
		LEFT(7, Button.LEFT),
		RIGHT(8, Button.RIGHT),
		MAIN(9, Button.MAIN);
		
		private int address;
		private Button button;
		
		private ButtonAddress(final int address, final Button button) {
			this.button = button;
			this.address = address;
		}
		
		public int getAddress() {
			return address;
		}
		
		public Button getButton() {
			return button;
		}
		
		public static ButtonAddress byAddress(final int address) {
			for (final ButtonAddress candidate : values()) {
				if (candidate.getAddress() == address) {
					return candidate;
				}
			}
			return null;
		}
	}
	
	@Mock
	private RobotDaemon daemon;
	
	@Mock
	private ConfigurationUtils configurationUtils;
	
	@Mock
	private GpioController gpioController;
	
	private ButtonService buttonService;

	private HashMap<GpioPinDigitalInput, Button> buttons = new HashMap<>();
	private HashMap<Button, GpioPinListenerDigital> listeners = new HashMap<>();
	
	@Before
	public void setUp() throws Exception {
		
		when(configurationUtils.getIn^()).thenReturn(
				ButtonAddress.BUTTON1.getAddress());
		when(configurationUtils.getButton2Gpio()).thenReturn(
				ButtonAddress.BUTTON2.getAddress());
		when(configurationUtils.getButton3Gpio()).thenReturn(
				ButtonAddress.BUTTON3.getAddress());
		when(configurationUtils.getButton4Gpio()).thenReturn(
				ButtonAddress.BUTTON4.getAddress());
		when(configurationUtils.getButtonUpGpio()).thenReturn(
				ButtonAddress.UP.getAddress());
		when(configurationUtils.getButtonDownGpio()).thenReturn(
				ButtonAddress.DOWN.getAddress());
		when(configurationUtils.getButtonLeftGpio()).thenReturn(
				ButtonAddress.LEFT.getAddress());
		when(configurationUtils.getButtonRightGpio()).thenReturn(
				ButtonAddress.RIGHT.getAddress());
		when(configurationUtils.getButtonMainGpio()).thenReturn(
				ButtonAddress.MAIN.getAddress());
		
		when(daemon.getGpioController()).thenReturn(gpioController);
		
		when(gpioController.provisionDigitalInputPin(any(Pin.class),
				eq(PinPullResistance.PULL_DOWN))).thenAnswer(new Answer<GpioPinDigitalInput>() {
					@Override
					public GpioPinDigitalInput answer(
							final InvocationOnMock invocation) throws Throwable {
						final int address = ((Pin) invocation.getArguments()[0]).getAddress();
						final ButtonAddress buttonAddress = ButtonAddress.byAddress(address);

						final GpioPinDigitalInput button = mock(GpioPinDigitalInput.class);
						buttons.put(button, buttonAddress.getButton());
						
						doAnswer(new Answer<Void>() {
							@Override
							public Void answer(final InvocationOnMock invocation) throws Throwable {
								final GpioPinDigitalInput buttonInput = (GpioPinDigitalInput) invocation.getMock();
								final Button button = buttons.get(buttonInput);
								listeners.put(button, (GpioPinListenerDigital) invocation.getArguments()[0]);
								return null;
							}
						}).when(button).addListener(any(GpioPinListenerDigital.class));
						
						return button;
					}
				});
		
		buttonService = new ButtonService(daemon, configurationUtils); 
		
		// wait for fire-thread to become active
		Thread.sleep(new Random(System.currentTimeMillis()).nextInt(1000));
		
	}
	
	@After
	public void tearDown() {
		
		buttonService.shutdown();
		
	}
	
	@Test
	public void testButton1() throws Exception {
		
		testButton(Button.ONE);
		
	}

	@Test
	public void testButton2() throws Exception {
		
		testButton(Button.TWO);
		
	}

	@Test
	public void testButton3() throws Exception {
		
		testButton(Button.THREE);
		
	}

	@Test
	public void testButton4() throws Exception {
		
		testButton(Button.FOUR);
		
	}
	
	@Test
	public void testUp() throws Exception {
		
		testButton(Button.UP);
		
	}

	@Test
	public void testDown() throws Exception {
		
		testButton(Button.DOWN);
		
	}

	@Test
	public void testLeft() throws Exception {
		
		testButton(Button.LEFT);
		
	}

	@Test
	public void testRight() throws Exception {
		
		testButton(Button.RIGHT);
		
	}
	
	@Test
	public void testMain() throws Exception {
		
		testButton(Button.MAIN);
		
	}
	
	private void testButton(final Button button) throws Exception {

		final ButtonListener listener = getMockedListener(button);
		testButtonPressedShort(button, listener);
		testButtonPressedLong(button, listener);
		
	}
	
	private ButtonListener getMockedListener(final Button button) throws Exception {
		
		final ButtonListener listener = mock(ButtonListener.class);
		buttonService.registerListener(listener);
		
		return listener;
		
	}
	
	private void testButtonPressedShort(final Button button, final ButtonListener listener) throws Exception {

		final GpioPinListenerDigital gpioPinListener = listeners.get(button);
		
		gpioPinListener.handleGpioPinDigitalStateChangeEvent(getEvent(HIGH));
		
		Thread.sleep(100);
		verify(listener, times(1)).buttonEvent(eq(button));
		
		gpioPinListener.handleGpioPinDigitalStateChangeEvent(getEvent(LOW));

		Thread.sleep(ButtonService.FIRE_INTERVAL_MS);
		verify(listener, times(1)).buttonEvent(eq(button));
		
	}
	
	private void testButtonPressedLong(final Button button, final ButtonListener listener) throws Exception {
		
		reset(listener);
		
		final GpioPinListenerDigital gpioPinListener = listeners.get(button);
		
		gpioPinListener.handleGpioPinDigitalStateChangeEvent(getEvent(HIGH));
		
		Thread.sleep(ButtonService.FIRE_INTERVAL_MS * 3 + 100);
		verify(listener, times(4)).buttonEvent(eq(button));
		
		gpioPinListener.handleGpioPinDigitalStateChangeEvent(getEvent(LOW));

		Thread.sleep(ButtonService.FIRE_INTERVAL_MS);
		verify(listener, times(4)).buttonEvent(eq(button));
		
	}
		
	private GpioPinDigitalStateChangeEvent getEvent(final boolean high) {
		
		return new GpioPinDigitalStateChangeEvent(this, null,
				high ? PinState.HIGH : PinState.LOW);
		
	}
	*/
	
}
