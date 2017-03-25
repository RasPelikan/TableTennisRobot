package com.pi4j.component.lcd.luma.impl.oled;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;

import com.pi4j.component.lcd.luma.SerialInterface;
import com.pi4j.component.lcd.luma.impl.core.LumaDevice;

public class SH1106Device extends LumaDevice {
	
	private final int pages;
	
	public SH1106Device(final SerialInterface serialInterface,
			final int width, final int height) throws IOException {
		
		super(serialInterface, Mode.BLACK_WHITE, width, height);
		
		this.pages = height / SH1106Constants.PAGE_HEIGHT;
		
		super.serialInterface.command(
				SH1106Constants.DISPLAYOFF,
				SH1106Constants.MEMORYMODE,
				SH1106Constants.SETHIGHCOLUMN, (byte) 0xB0, (byte) 0xC8,
				SH1106Constants.SETLOWCOLUMN, (byte) 0x10, (byte) 0x40,
				SH1106Constants.SETSEGMENTREMAP,
				SH1106Constants.NORMALDISPLAY,
				SH1106Constants.SETMULTIPLEX, (byte) 0x3F,
				SH1106Constants.DISPLAYALLON_RESUME,
				SH1106Constants.SETDISPLAYOFFSET, (byte) 0x00,
				SH1106Constants.SETDISPLAYCLOCKDIV, (byte) 0xF0,
				SH1106Constants.SETPRECHARGE, (byte) 0x22,
				SH1106Constants.SETCOMPINS, (byte) 0x12,
				SH1106Constants.SETVCOMDETECT, (byte) 0x20,
				SH1106Constants.CHARGEPUMP, (byte) 0x14);
		
		contrast(0x7F);
		clear();
		show();
		
	}
	
	@Override
	protected String getDeviceString() {
		
		return "SH1106";
		
	}

	@Override
	protected BufferedImage buildImage() {
		
		return new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		
	}

	@Override
	public void display() throws IOException {
		
		assert this.image.getType() == BufferedImage.TYPE_BYTE_BINARY;
		assert this.image.getWidth() == getWidth();
		assert this.image.getHeight() == getHeight();

		byte setPageAddress = (byte) 0xB0;
		byte[] buffer = new byte[this.getWidth()];
		
		final WritableRaster imageData = this.image.getRaster();

		for (int page = 0; page < this.pages; ++page) {
			
			this.serialInterface.command(setPageAddress, (byte) 0x02, (byte) 0x10);
			
			for (int x = 0; x < this.getWidth(); ++x) {
				
				byte value = 0;

				int y = page * SH1106Constants.PAGE_HEIGHT;;
				for (int i = 1; i != 256 /* after 8 bits */; i <<= 1) {
					
					if (imageData.getSample(x, y, 0) == 1) {
						value |= i;
					}
					++y;
					
				}
				
				buffer[x] = value;
						
			}

			this.serialInterface.data(buffer);

			setPageAddress += 1;
			
		}
		
	}
	
}
