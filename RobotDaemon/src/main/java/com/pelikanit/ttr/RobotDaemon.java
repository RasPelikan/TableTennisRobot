package com.pelikanit.ttr;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pelikanit.ttr.admin.HttpsAdmin;
import com.pelikanit.ttr.interaction.ButtonService;
import com.pelikanit.ttr.utils.ConfigurationUtils;
import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.PCA9685GpioServoProvider;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.impl.I2CFactoryProviderRaspberryPi;
import com.pi4j.wiringpi.GpioUtil;

public class RobotDaemon implements Shutdownable {

	private static final Logger logger = Logger.getLogger(
			RobotDaemon.class.getCanonicalName());
	
	private static Thread shutdownHook;
	
	private volatile boolean shutdown = false;
	
	private HttpsAdmin httpsAdmin;
	
	private GpioController gpioController;
	
	private ButtonService buttonService;
	
	private I2CBus i2cBus;
	
	private PCA9685GpioProvider servoGpioProvider;
	
	private ServoProvider servoProvider;
	
	public static void main(String[] args) {

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
		initializeAdminHttpServer(config);
		
		logger.info("Init complete");
		
	}

	private void stop() throws Exception {
		
		stopAdminHttpServer();
		stopGpio();
		stopShutdownHook();

		logger.info("Shutdown complete");
		
	}

	private void stopGpio() throws Exception {
		
		buttonService.shutdown();
		
		servoGpioProvider.shutdown();
		
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
		
		servoGpioProvider = new PCA9685GpioProvider(i2cBus, config.getServocontrollerAddress());
		
		servoProvider = new PCA9685GpioServoProvider(servoGpioProvider);
		
		buttonService = new ButtonService(this, config);
		
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
	
	public ServoProvider getServoProvider() {
		return servoProvider;
	}
	
}
