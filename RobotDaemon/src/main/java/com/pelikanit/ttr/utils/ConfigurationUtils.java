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
	
	private static final String PROPS_BUTTON_1_GPIO = "button.1.gpio";
	private static final String PROPS_BUTTON_2_GPIO = "button.2.gpio";
	private static final String PROPS_BUTTON_3_GPIO = "button.3.gpio";
	private static final String PROPS_BUTTON_4_GPIO = "button.4.gpio";
	private static final String PROPS_BUTTON_UP_GPIO = "button.up.gpio";
	private static final String PROPS_BUTTON_DOWN_GPIO = "button.down.gpio";
	private static final String PROPS_BUTTON_LEFT_GPIO = "button.left.gpio";
	private static final String PROPS_BUTTON_RIGHT_GPIO = "button.right.gpio";
	private static final String PROPS_BUTTON_MAIN_GPIO = "button.main.gpio";
	
	private static final String PROPS_I2C_BUS = "i2c.bus";

	private static final String PROPS_SERVOCONTROLLER_ADDRESS = "servocontroller.address";
	private static final String PROPS_SERVO_SPEED_SLOT = "servocontroller.servo.speed.slot";
	private static final String PROPS_SERVO_SPIN_SLOT = "servocontroller.servo.spin.slot";
	
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
	
	public int getButton1Gpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_1_GPIO);
	}

	public int getButton2Gpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_2_GPIO);
	}

	public int getButton3Gpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_3_GPIO);
	}

	public int getButton4Gpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_4_GPIO);
	}

	public int getButtonUpGpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_UP_GPIO);
	}

	public int getButtonDownGpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_DOWN_GPIO);
	}

	public int getButtonLeftGpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_LEFT_GPIO);
	}

	public int getButtonRightGpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_RIGHT_GPIO);
	}

	public int getButtonMainGpio() {
		return getIntPropertyAvailable(PROPS_BUTTON_MAIN_GPIO);
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
