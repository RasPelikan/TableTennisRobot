package com.pi4j.component.lcd;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface MatrixLCD {

	enum Mode {
		BLACK_WHITE, RGB, RGBA
	}
	
	enum Rotation {
		CW0, CW90, CW180, CW270
	}
	
	void clear() throws IOException;
	
	int getWidth();
	
	int getRotatedWidth();
	
	int getHeight();
	
	int getRotatedHeight();
	
	Mode getMode();
	
	BufferedImage createBufferedImage();

	BufferedImage getBufferedImage();
	
	Graphics2D getGraphics2D();
	
	void display() throws IOException;
	
	void shutdown() throws IOException;
	
	Rotation getRotation();
	
}
