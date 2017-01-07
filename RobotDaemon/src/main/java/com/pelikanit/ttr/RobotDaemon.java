package com.pelikanit.ttr;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pelikanit.ttr.admin.HttpsAdmin;
import com.pelikanit.ttr.utils.ConfigurationUtils;

public class RobotDaemon implements Shutdownable {

	private static final Logger logger = Logger.getLogger(
			RobotDaemon.class.getCanonicalName());
	
	private static Thread shutdownHook;
	
	private volatile boolean shutdown = false;
	
	private HttpsAdmin httpsAdmin;
	
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
			
			// stop admin-httpserver
			daemon.stop();
			
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
		
		initializeAdminHttpServer(config);
		
		logger.info("Init complete");
		
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
	
	private void stop() {
		
		stopAdminHttpServer();
		
		stopShutdownHook();

		logger.info("Shutdown complete");
		
	}

	private void stopAdminHttpServer() {
		
		if (httpsAdmin != null) {
			httpsAdmin.stop();
		}
		
	}

	private void stopShutdownHook() {
		
		Runtime.getRuntime().removeShutdownHook(shutdownHook);
		
	}
	
}
