package com.pi4j.component.lcd.luma.impl.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;

import com.pi4j.component.lcd.MatrixLCD;
import com.pi4j.component.lcd.luma.SerialInterface;
import com.pi4j.component.lcd.luma.impl.core.constants.Common;

public abstract class LumaDevice implements MatrixLCD {

	private final Mode mode;
	
	private final int width;
	
	private final int height;
	
	private Rotation rotation = Rotation.CW0;
	
	private final SerialInterface serialInterface;

	private BufferedImage image;
	
	private Graphics2D graphics2D;
	
	protected LumaDevice(final SerialInterface serialInterface,
			final Mode mode, final int width, final int height,
			final Rotation rotation) {

		this.serialInterface = serialInterface;
		this.mode = mode;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		
		createBufferedImage();

	}

	@Override
	public int getHeight() {
		
		return this.height;
		
	}
	
	public int getRotatedHeight() {
		
		switch (this.rotation) {
		case CW90:
		case CW270:
			return getWidth();
		default:
			return getHeight();
		}
		
	}
	
	public int getRotatedWidth() {

		switch (this.rotation) {
		case CW90:
		case CW270:
			return getHeight();
		default:
			return getWidth();
		}

	}
	
	@Override
	public int getWidth() {
		
		return this.width;
		
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

	public Rotation getRotation() {
		return rotation;
	}

	protected SerialInterface getSerialInterface() {
		return serialInterface;
	};
	
	protected BufferedImage getImage() {
		return image;
	}
	
	protected int getSample(final int x, final int y, final int b) {
		
		final int xTransformed;
		final int yTransformed;
		switch (this.rotation) {
		case CW0:
			xTransformed = x;
			yTransformed = y;
			break;
		case CW90:
			xTransformed = this.height - y - 1;
			yTransformed = x;
			break;
		case CW180:
			xTransformed = this.width - x - 1;
			yTransformed = this.height - y - 1;
			break;
		default:
			xTransformed = y;
			yTransformed = this.width - x - 1;
			break;
		}
		
		final WritableRaster raster = this.image.getRaster();
		
		return raster.getSample(xTransformed, yTransformed, b);
		
	}
	
}
