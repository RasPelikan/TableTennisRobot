package com.pi4j.component.lcd.luma;

import java.io.IOException;

public interface SerialInterface {

	void command(final byte... cmds) throws IOException;
	
	void data(final byte[] data) throws IOException;
	
}
