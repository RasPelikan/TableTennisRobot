package com.pelikanit.ttr;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.pelikanit.ttr.admin.HttpsAdmin;
import com.pelikanit.ttr.support.ButtonService;
import com.pelikanit.ttr.support.NewDataIndicatorListener;
import com.pelikanit.ttr.utils.ConfigurationUtils;
import com.pi4j.component.lcd.MatrixLCD;
import com.pi4j.component.lcd.MatrixLCD.Rotation;
import com.pi4j.component.lcd.luma.SerialInterface;
import com.pi4j.component.lcd.luma.impl.serial.SpiSerialInterface;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.impl.I2CFactoryProviderRaspberryPi;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.wiringpi.GpioUtil;

public class RobotDaemon implements Shutdownable {

	private static final Logger logger = Logger.getLogger(
			RobotDaemon.class.getCanonicalName());
	
	private static Thread shutdownHook;
	
	private volatile boolean shutdown = false;
	
	private HttpsAdmin httpsAdmin;
	
	private GpioController gpioController;
	
	private I2CDevice supportDevice;
	
	private NewDataIndicatorListener newDataIndicatorListener;
	
	private ButtonService buttonService;
	
	private I2CBus i2cBus;
	
	private PCA9685GpioProvider pwmGpioProvider;
	
	private MatrixLCD display;
	
	public static void main(String[] args) throws Exception {

		// must be done as soon after program start as possible
		correctPCA9685MaxFrequency();
		
		// process arguments
		final ConfigurationUtils config = new ConfigurationUtils(args);

		// build central instance
		RobotDaemon daemon = new RobotDaemon();
		try {
			
			// initialize admin-httpserver, sensors,...
			daemon.initialize(config);
		
			// wait until shutdown
			while (!daemon.isShutdown()) {
				
				// go asleep
				synchronized (daemon) {
					
					try {
						daemon.wait();
					} catch (InterruptedException e) {
						// expected
					}
					
				}
				
			}
			
		}
		catch (Throwable e) {
			
			logger.log(Level.SEVERE, "Error running application", e);
			
		}
		// shutdown
		finally {
			
			// stop services
			try {
				daemon.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private static void correctPCA9685MaxFrequency() throws Exception {

		// see https://cdn-shop.adafruit.com/datasheets/PCA9685.pdf chapter 7.3.5
		final BigDecimal REAL_MAX_FREQUENCY = new BigDecimal("1526");
		
		final Field field = PCA9685GpioProvider.class.getDeclaredField("MAX_FREQUENCY");
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		int modifiers = modifiersField.getInt(field);
		modifiers &= ~Modifier.FINAL;
		modifiersField.setInt(field, modifiers);
		field.set(null, REAL_MAX_FREQUENCY);

	}
	
	@Override
	public void shutdown() {
		
		logger.info("Shutdown...");
		
		// wake-up main-method to shutdown all services immediately
		synchronized (this) {
			
			shutdown = true;
			this.notify();
			
		}

		logger.info("...in progress!");
		
	}
	
	public boolean isShutdown() {
		
		return shutdown;
		
	}
	
	private void initialize(final ConfigurationUtils config) throws Exception {
		
		initializeShutdownHook();
		initializeProperties(config);
		initializeGpio(config);
		initializeDisplay(config);
		initializeAdminHttpServer(config);
		
		logger.info("Init complete");
		
	}

	private void stop() throws Exception {
		
		stopAdminHttpServer();
		stopDisplay();
		stopGpio();
		stopShutdownHook();

		logger.info("Shutdown complete");
		
	}

	private void stopDisplay() throws Exception {
		
		display.shutdown();
		
	}
	
	private void stopGpio() throws Exception {
		
		buttonService.shutdown();
		
		newDataIndicatorListener.shutdown();
		
		pwmGpioProvider.shutdown();
		
		i2cBus.close();
		
		if (gpioController != null) {
			try {
				gpioController.shutdown();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
	}

	private void initializeGpio(final ConfigurationUtils config) throws Exception {
		
		GpioUtil.enableNonPrivilegedAccess();
		
		gpioController = GpioFactory.getInstance();
		
		i2cBus = new I2CFactoryProviderRaspberryPi()
			.getBus(config.getI2CBus(), 1, TimeUnit.SECONDS);
		
		supportDevice = i2cBus.getDevice(config.getSupportI2CAddress());
		
		pwmGpioProvider = new PCA9685GpioProvider(
				i2cBus, config.getServocontrollerAddress());
		
		newDataIndicatorListener = new NewDataIndicatorListener(this, config);
		
		buttonService = new ButtonService(this, config);
		
	}
	
	private void initializeDisplay(final ConfigurationUtils config) throws Exception {
		
		final SpiChannel spiChannel = SpiChannel.getByNumber(
				config.getDisplaySpiChannel());
		final SpiDevice displaySpiDevice = SpiFactory.getInstance(spiChannel,
				config.getDisplaySpiSpeed());
		
		final GpioPinDigitalOutput resetPin = gpioController
				.provisionDigitalOutputPin(RaspiPin
						.getPinByAddress(config.getDisplayGpioReset()));
		final GpioPinDigitalOutput dcPin = gpioController
				.provisionDigitalOutputPin(RaspiPin
						.getPinByAddress(config.getDisplayGpioDc()));
		
		final SpiSerialInterface serialInterface = new SpiSerialInterface(
				displaySpiDevice, resetPin, dcPin,
				SpiSerialInterface.DEFAULT_TRANSFER_SIZE);
		
		final Rotation rotation = MatrixLCD.Rotation.valueOf(
				"CW" + config.getDisplayRotation());
		
		@SuppressWarnings("unchecked")
		final Class<MatrixLCD> displayClass = (Class<MatrixLCD>) Class.forName(
				config.getDisplayClass());
		final Constructor<MatrixLCD> constructor = displayClass.getConstructor(
				SerialInterface.class, int.class, int.class,
				MatrixLCD.Rotation.class);
		display = constructor.newInstance(serialInterface,
				config.getDisplayWidth(),
				config.getDisplayHeight(), rotation);
		
		display.createBufferedImage();
		
		final InputStream logoStream = getClass().getClassLoader().getResourceAsStream("logo.png");
		try {
			final BufferedImage logo = ImageIO.read(logoStream);
			display.getGraphics2D().drawImage(logo,
					(display.getWidth() - logo.getWidth()) / 2, 
					(display.getHeight() - logo.getHeight()) / 2, null);
			display.display();
		} finally {
			logoStream.close();
		}
		
	}
	
	private void initializeShutdownHook() {
		
		// capture "kill" commands and shutdown regularly
		shutdownHook = new Thread() {
					
					@Override
					public void run() {
						
						// shutdown
						shutdown();
						
					}
					
				};
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		
	}

	private void initializeProperties(final ConfigurationUtils config) {
		
		
	}
	
	/**
	 * Initialize the Admin-HttpServer
	 * 
	 * @throws Exception
	 */
	private void initializeAdminHttpServer(
			final ConfigurationUtils config) throws Exception {

		httpsAdmin = new HttpsAdmin(config, this);
		
		httpsAdmin.start();
		
	}

	private void stopAdminHttpServer() {
		
		if (httpsAdmin != null) {
			try {
				httpsAdmin.stop();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
	}

	private void stopShutdownHook() {
		
		Runtime.getRuntime().removeShutdownHook(shutdownHook);
		
	}
	
	public GpioController getGpioController() {
		return gpioController;
	}
	
	public ButtonService getButtonService() {
		return buttonService;
	}
	
	public I2CBus getI2cBus() {
		return i2cBus;
	}
	
	public PCA9685GpioProvider getPwmGpioProvider() {
		return pwmGpioProvider;
	}

	public I2CDevice getSupportDevice() {
		return supportDevice;
	}
	
}
