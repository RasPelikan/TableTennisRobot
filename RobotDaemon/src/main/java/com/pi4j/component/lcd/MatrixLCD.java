package com.pi4j.component.lcd;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface MatrixLCD {

	enum Mode {
		BLACK_WHITE, RGB, RGBA
	}
	
	void clear() throws IOException;
	
	int getWidth();
	
	int getHeight();
	
	Mode getMode();
	
	BufferedImage createBufferedImage();

	BufferedImage getBufferedImage();
	
	Graphics2D getGraphics2D();
	
	void display() throws IOException;
	
	void shutdown() throws IOException;
	
}
