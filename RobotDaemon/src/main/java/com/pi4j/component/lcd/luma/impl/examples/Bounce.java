package com.pi4j.component.lcd.luma.impl.examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import org.apache.commons.cli.Options;

import com.pi4j.component.lcd.MatrixLCD;
import com.pi4j.wiringpi.GpioUtil;

public class Bounce extends Demo {

	public static class Ball {
		
		private static final Random random = new Random(System.currentTimeMillis());
		private int w;
		private int h;
		private int r;
		private int d;
		public int x;
		public int y;
		public int xSpeed;
		public int ySpeed;
		
		public Ball(int w, int h, int r) {
			this.w = w;
			this.h = h;
			this.r = r;
			this.d = r * 2;
			this.x = w / 2;
			this.y = h / 2;
			this.xSpeed = random.nextInt(10) - 5;
			this.ySpeed = random.nextInt(10) - 5;
		}
		
		public void updatePos() {
			if ((x + r > w) || (x - r < 0)) {
				xSpeed *= -1;
			}
			if ((y + r > h) || (y - r < 0)) {
				ySpeed *= -1;
			}
			x += xSpeed;
			y += ySpeed;
		}
		
		public void draw(final Graphics2D graphics2D) {
			
			graphics2D.fillOval(x - r, y - r, d, d);
			
		}
	};
	
	public static void main(final String[] args) throws Exception {

		GpioUtil.enableNonPrivilegedAccess();
		
		final Bounce bounce = new Bounce();
		
		final Options options = bounce.prepareOptions();
		if (!bounce.parseOptions(args, options)) {
			return;
		}
		
		bounce.run();
		
	}
	
	private int fps = 0;
	private String fpsString = "";
	
	@Override
	protected String getDemoName() {
		return "java " + getClass().getName();
	}
	
	@Override
	public void demo(final MatrixLCD lcd) throws Exception {
		
		final Ball[] balls = new Ball[10];
		for (int i = 0; i < 10; ++i) {
			balls[i] = new Ball(lcd.getWidth(), lcd.getHeight(), (int) (i * 1.5));
		}
		
		long before = System.currentTimeMillis();
		while (true) {

			lcd.createBufferedImage(); // new image
			
			final Graphics2D graphics = lcd.getGraphics2D();
			graphics.setColor(Color.WHITE);
			
			graphics.drawRect(0, 0, lcd.getWidth() - 1, lcd.getHeight() - 1);

			for (int i = 0; i < 10; ++i) {
				balls[i].draw(graphics);
				balls[i].updatePos();
			}

			final long now = System.currentTimeMillis();
			final long elapsed = now - before;
			before = displayFps(graphics, before, elapsed);
			
			lcd.display();

			++fps;
			
		}
		
	}
	
	private long displayFps(final Graphics2D graphics2D, final long before, final long time) {

		graphics2D.drawString(fpsString, 1, 11);
		
		if (time < 250) {
			return before;
		}

		final double calcFps = ((double) this.fps) / time * 1000;
		fpsString = String.format("FPS %.2f", calcFps);
		
		this.fps = 0;
		return System.currentTimeMillis();
		
	}
	
}
