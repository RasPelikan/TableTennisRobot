package com.pi4j.component.lcd.luma.impl.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.pi4j.component.lcd.MatrixLCD;
import com.pi4j.component.lcd.luma.SerialInterface;
import com.pi4j.component.lcd.luma.impl.core.constants.Common;

public abstract class LumaDevice implements MatrixLCD {

	private final Mode mode;
	
	private final int width;
	
	private final int height;

	protected final SerialInterface serialInterface;

	protected BufferedImage image;
	
	protected Graphics2D graphics2D;
	
	protected LumaDevice(final SerialInterface serialInterface,
			final Mode mode, final int width, final int height) {

		this.serialInterface = serialInterface;
		this.mode = mode;
		this.width = width;
		this.height = height;
		
		createBufferedImage();

	}

	@Override
	public int getHeight() {
		
		return height;
		
	}
	
	@Override
	public int getWidth() {
		
		return width;
		
	}
	
	@Override
	public Mode getMode() {
		
		return mode;
		
	}
	
	protected abstract String getDeviceString();

	public void show() throws IOException {

		this.serialInterface.command(Common.DISPLAYON);

	}

	public void hide() throws IOException {

		this.serialInterface.command(Common.DISPLAYOFF);

	}

	/**
	 * Switches the display contrast to the desired level, in the range 0-255.
	 * Note that setting the level to a low (or zero) value will not necessarily
	 * dim the display to nearly off. In other words, this method is **NOT**
	 * suitable for fade-in/out animation.
	 * 
	 * @param level
	 *            Desired contrast level in the range of 0-255.
	 */
	public void contrast(final int level) throws IOException {

		assert 0 <= level;
		assert level <= 255;

		this.serialInterface.command(Common.SETCONTRAST, (byte) level);

	}

	public void clear() throws IOException {

		this.createBufferedImage();
		this.display();

	}
	
	public BufferedImage createBufferedImage() {
		
		this.image = buildImage();
		if (this.graphics2D != null) {
			this.graphics2D.dispose();
		}
		this.graphics2D = null;
		return this.image;
		
	}
	
	@Override
	public BufferedImage getBufferedImage() {
		
		return this.image;
		
	}
	
	@Override
	public Graphics2D getGraphics2D() {
		
		if (this.graphics2D == null) {
			this.graphics2D = this.image.createGraphics();
		}
		return this.graphics2D;
		
	}
	
	protected abstract BufferedImage buildImage();

	public abstract void display() throws IOException;

	public void shutdown() throws IOException {

		this.hide();
		this.clear();

	}

}
