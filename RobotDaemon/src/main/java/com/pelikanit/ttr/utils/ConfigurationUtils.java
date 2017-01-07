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
			
			return Integer.parseInt(value);
			
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
