package com.pelikanit.ttr.admin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.pelikanit.ttr.RobotDaemon;
import com.pelikanit.ttr.support.ButtonListener;
import com.pelikanit.ttr.support.ButtonService.Button;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioPinPwmOutput;

@Path("/ttr")
@Produces(MediaType.APPLICATION_JSON)
public class MainAdminService {

	@Context
	private RobotDaemon daemon;

	@GET
	@Path("/shutdown")
	public String shutdown() {

		daemon.shutdown();

		return "SHUTDOWN now...";

	}

	@GET
	@Path("/status")
	public String status() {

		return "Up and running!";

	}

	static GpioPinPwmOutput pin1;
	static GpioPinPwmOutput pin2;
	
	@GET
	@Path("/test")
	public String test() throws Exception {

		if (pin1 == null) {
			System.err.println("Init 1");
			pin1 = daemon.getGpioController()
				.provisionPwmOutputPin(daemon.getServoProvider(),
						PCA9685Pin.PWM_00);
			System.err.println("Init 1 done");
		}
		if (pin2 == null) {
			System.err.println("Init 2");
			pin2 = daemon.getGpioController()
				.provisionPwmOutputPin(daemon.getServoProvider(),
						PCA9685Pin.PWM_01);
			System.err.println("Init 2 done");
		}
		
		try {

			final GpioPinPwmOutput[] pins = new GpioPinPwmOutput[2];
			pins[0] = pin1;
			pins[1] = pin2;
			final int[] pulseWidth = new int[2];
			pulseWidth[0] = 1750;
			pulseWidth[1] = 1750;
			pin1.setPwm(pulseWidth[0]);
			pin2.setPwm(pulseWidth[1]);
			final int current[] = new int[] { 0 };

			final ButtonListener listener = new ButtonListener() {
				@Override
				public void buttonEvent(final Button button) {
					final int c = current[0];
					if (button == Button.ONE) {
						current[0] = 0;
					} else if (button == Button.TWO) {
						current[0] = 1;
					} else if (button == Button.UP) {
						pulseWidth[c] -= 1;
					} else if (button == Button.DOWN) {
						pulseWidth[c] += 1;
					} else if (button == Button.LEFT) {
						pulseWidth[c] += 200;
					} else if (button == Button.RIGHT) {
						pulseWidth[c] -= 200;
					}
					if (pulseWidth[c] < 700) {
						pulseWidth[c] = 700;
					}
					if (pulseWidth[c] > 2800) {
						pulseWidth[c] = 2800;
					}
					System.err.println(pulseWidth[c]);
					pins[c].setPwm(pulseWidth[c]);
				}
			};
			System.err.println("Start");
			daemon.getButtonService().registerListener(listener);
			Thread.sleep(60000);
			daemon.getButtonService().unregisterListener(listener);
			System.err.println("Stop");

		} finally {
			pin1.unexport();
			pin2.unexport();
		}

		return "OK";

	}

}
