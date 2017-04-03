package com.pelikanit.ttr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationUtils {
	
	private static final Logger logger = Logger.getLogger(
			ConfigurationUtils.class.getCanonicalName());
	
	private static final String PROPS_HTTPSADMIN_KEYSTORE = "httpsadmin.keystore";
	private static final String PROPS_HTTPSADMIN_KEYSTOREPASSWORD = "httpsadmin.keystore.password";
	private static final String PROPS_HTTPSADMIN_HOST = "httpsadmin.host";
	private static final String PROPS_HTTPSADMIN_PORT = "httpsadmin.port";
	
	private static final String PROPS_SUPPORT_NDI_GPIO = "support.newdataindicator.gpio";
	private static final String PROPS_SUPPORT_I2C_ADDRESS = "support.i2c.address";
	
	private static final String PROPS_I2C_BUS = "i2c.bus";

	private static final String PROPS_SERVOCONTROLLER_ADDRESS = "servocontroller.i2c.address";
	private static final String PROPS_SERVO_SPEED_SLOT = "servocontroller.servo.speed.slot";
	private static final String PROPS_SERVO_SPIN_SLOT = "servocontroller.servo.spin.slot";
	
	private static final String PROPS_DISPLAY_SPICHANNEL = "display.spi.channel";
	private static final String PROPS_DISPLAY_SPISPEED = "display.spi.speed";
	private static final String PROPS_DISPLAY_GPIORESET = "display.gpio.reset";
	private static final String PROPS_DISPLAY_GPIODC = "display.gpio.dc";
	private static final String PROPS_DISPLAY_CLASS = "display.class";
	private static final String PROPS_DISPLAY_WIDTH = "display.width";
	private static final String PROPS_DISPLAY_HEIGHT = "display.height";
	private static final String PROPS_DISPLAY_ROTATION = "display.rotation";
	
	private Properties props;
	
	public ConfigurationUtils(final String[] args) {
		
		if (args.length < 1) {
			System.err.println("Expecting configuration-file as argument!");
		}
		if (args.length > 1) {
			System.err.println("Expecting configuration-file as argument!");
		}
		
		InputStream in = null;
		try {
			
			final File propsFile = new File(args[0]);
			in = new FileInputStream(propsFile);
			
			props = new Properties();
			props.load(in);
			
		} catch (Exception e) {
			
			logger.log(Level.SEVERE,
					"Could not parse properties '"
					+ args[0]
					+ "'",
					e);
			Runtime.getRuntime().exit(1);
			
		} finally {
			
			if (in != null) {
				
				try {
					in.close();
				} catch (Throwable e) {
					logger.log(Level.WARNING,
							"Could not close input-stream of properties '"
							+ args[1]
							+ "'",
							e);
				}
				
			}
			
		}
		
	}
	
	public String getHttpsAdminKeystore() {
		
		final String keystore = props.getProperty(PROPS_HTTPSADMIN_KEYSTORE);
		
		if (keystore == null) {
			throw new RuntimeException("No property '"
					+ PROPS_HTTPSADMIN_KEYSTORE
					+ "' given. Use 'keytool -genkey -keyalg RSA -alias selfsigned -keystore test.jks -storepass any_password_you_like -keysize 2048' to build it");
		}
		
		return keystore;
		
	}
	
	public String getHttpsAdminKeystorePassword() {
		return props.getProperty(PROPS_HTTPSADMIN_KEYSTOREPASSWORD);
	}

	public int getHttpsAdminPort() {
		return getIntPropertyAvailable(PROPS_HTTPSADMIN_PORT);
	}
	
	public String getHttpsAdminHost() {
		return props.getProperty(PROPS_HTTPSADMIN_HOST);
	}
	
	public int getSupportNewDataIndicatorGpio() {
		return getIntPropertyAvailable(PROPS_SUPPORT_NDI_GPIO);
	}

	public int getSupportI2CAddress() {
		return getIntPropertyAvailable(PROPS_SUPPORT_I2C_ADDRESS);
	}

	public int getI2CBus() {
		return getIntPropertyAvailable(PROPS_I2C_BUS);
	}
	
	public int getSpeedServoSlot() {
		return getIntPropertyAvailable(PROPS_SERVO_SPEED_SLOT);
	}
	
	public int getSpinServoSlot() {
		return getIntPropertyAvailable(PROPS_SERVO_SPIN_SLOT);
	}
	
	public int getServocontrollerAddress() {
		return getIntPropertyAvailable(PROPS_SERVOCONTROLLER_ADDRESS);
	}

	public int getDisplaySpiChannel() {
		return getIntPropertyAvailable(PROPS_DISPLAY_SPICHANNEL);
	}

	public int getDisplaySpiSpeed() {
		return getIntPropertyAvailable(PROPS_DISPLAY_SPISPEED);
	}

	public int getDisplayGpioReset() {
		return getIntPropertyAvailable(PROPS_DISPLAY_GPIORESET);
	}

	public int getDisplayGpioDc() {
		return getIntPropertyAvailable(PROPS_DISPLAY_GPIODC);
	}

	public String getDisplayClass() {
		return props.getProperty(PROPS_DISPLAY_CLASS);
	}

	public int getDisplayWidth() {
		return getIntPropertyAvailable(PROPS_DISPLAY_WIDTH);
	}

	public int getDisplayHeight() {
		return getIntPropertyAvailable(PROPS_DISPLAY_HEIGHT);
	}

	public int getDisplayRotation() {
		return getIntPropertyAvailable(PROPS_DISPLAY_ROTATION);
	}
	
	@SuppressWarnings("unused")
	private float getFloatPropertyAvailable(final String propsName) {
		
		final String value = props.getProperty(propsName);
		if (value == null) {
			throw new NoSuchElementException(propsName);
		}
		try {
			
			return Float.parseFloat(value);
			
		} catch(Exception e) {
			
			throw new RuntimeException(
					"Could not read property '"
					+ propsName
					+ "' as float. Given value is '"
					+ value
					+ "'", e);
			
		}
		
	}
		
	private int getIntPropertyAvailable(final String propsName) {
		
		final String value = props.getProperty(propsName);
		if (value == null) {
			throw new NoSuchElementException(propsName);
		}
		try {
			
			if (value.startsWith("0x")) {
				return Integer.parseInt(value.substring(2), 16);
			} else {
				return Integer.parseInt(value);
			}
			
		} catch(Exception e) {
			
			throw new RuntimeException(
					"Could not read property '"
					+ propsName
					+ "' as int. Given value is '"
					+ value
					+ "'", e);
			
		}
		
	}

}
