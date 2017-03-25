package com.pi4j.component.lcd.luma.impl.examples;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.pi4j.component.lcd.luma.impl.serial.SpiSerialInterface;

public abstract class DemoOpts {

	protected static final String DEFAULT_DISPLAY = "SH1106";
	private Option displayOption;
	protected String display;
	protected static final String DEFAULT_WIDTH = "128";
	private Option widthOption;
	protected int width;
	protected static final String DEFAULT_HEIGHT = "64";
	private Option heightOption;
	protected int height;
	protected static final String DEFAULT_INTERFACE = "spi";
	private Option interfaceOption;
	protected boolean spi;
	private Option helpOption;
	protected static final String DEFAULT_CSCHANNEL = "0";
	private Option csChannelOption;
	protected int csChannel;
	protected static final String DEFAULT_RESETGPIOPIN = "5";
	private Option resetGpioPinOption;
	protected int resetGpioPin;
	protected static final String DEFAULT_DCGPIOPIN = "6";
	private Option dcGpioPinOption;
	protected int dcGpioPin;
	private Option speedOption;
	protected int speed;
	
	protected Options prepareOptions() {
		
		final Options options = new Options();

		this.helpOption = Option.builder("h")
				.longOpt("help")
				.build();
		options.addOption(helpOption);
		
		this.displayOption = Option.builder("d")
				.longOpt("display")
				.hasArg()
				.argName("name of the display")
				.desc("Display type, supports real devices or emulators. Possible values: 'SH1106'. Default: '"
						+ DemoOpts.DEFAULT_DISPLAY + "'")
				.type(String.class)
				.build();
		options.addOption(displayOption);

		this.widthOption = Option.builder()
				.longOpt("width")
				.hasArg()
				.argName("width in pixels")
				.desc("Width of the device in pixels. Default: '"
						+ DemoOpts.DEFAULT_WIDTH + "'")
				.type(Integer.class)
				.build();
		options.addOption(widthOption);

		this.heightOption = Option.builder()
				.longOpt("height")
				.hasArg()
				.argName("height in pixels")
				.desc("Height of the device in pixels. Default: '"
						+ DemoOpts.DEFAULT_HEIGHT + "'")
				.type(Integer.class)
				.build();
		options.addOption(heightOption);

		this.interfaceOption = Option.builder("i")
				.longOpt("interface")
				.hasArg()
				.argName("type")
				.desc("Serial interface type. Possible values: 'spi', 'i2c'. Default: '"
						+ DemoOpts.DEFAULT_INTERFACE + "'")
				.type(String.class)
				.build();
		options.addOption(interfaceOption);
		
		final OptionGroup spiGroup = new OptionGroup();
		
		this.csChannelOption = Option.builder()
				.longOpt("spi-port")
				.hasArg()
				.argName("cs port")
				.desc("SPI port. Possible values: '0', '1'. Default: '"
						+ DemoOpts.DEFAULT_CSCHANNEL + "'")
				.type(Integer.class)
				.build();
		spiGroup.addOption(this.csChannelOption);

		this.speedOption = Option.builder()
				.longOpt("spi-bus-speed")
				.hasArg()
				.argName("bus speed")
				.desc("SPI max bus speed (Hz)")
				.type(Integer.class)
				.build();
		spiGroup.addOption(this.speedOption);
		
		options.addOptionGroup(spiGroup);
		
		final OptionGroup gpioGroup = new OptionGroup();
		
		this.dcGpioPinOption = Option.builder()
				.longOpt("gpio-data-command")
				.hasArg()
				.argName("gpio pin")
				.desc("GPIO pin for D/C RESET (SPI devices only). See http://pi4j.com -> PIN NUMBERING. Default: '"
						+ DemoOpts.DEFAULT_DCGPIOPIN + "'")
				.type(Integer.class)
				.build();
		gpioGroup.addOption(this.dcGpioPinOption);

		this.resetGpioPinOption = Option.builder()
				.longOpt("gpio-reset")
				.hasArg()
				.argName("gpio pin")
				.desc("GPIO pin for RESET (SPI devices only). See http://pi4j.com -> PIN NUMBERING. Default: '"
						+ DemoOpts.DEFAULT_RESETGPIOPIN + "'")
				.type(Integer.class)
				.build();
		gpioGroup.addOption(this.resetGpioPinOption);
		
		options.addOptionGroup(gpioGroup);

		return options;
		
	}
	
	protected abstract String getDemoName();
	
	protected boolean parseOptions(final String[] args, final Options options) throws Exception {
		
		CommandLineParser parser = new DefaultParser();
	    try {
	        // parse the command line arguments
	        final CommandLine line = parser.parse(options, args);
	        
	        if (line.hasOption('h')) {
	        	
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp(getDemoName(), options);
	        	return false;
	        	
	        }
	        
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	    
	    this.display = this.displayOption.getValue(DemoOpts.DEFAULT_DISPLAY);
	    this.width = Integer.parseInt(this.widthOption.getValue(DemoOpts.DEFAULT_WIDTH));
	    this.height = Integer.parseInt(this.heightOption.getValue(DemoOpts.DEFAULT_HEIGHT));
	    
	    final String serialInterface = this.interfaceOption.getValue(DemoOpts.DEFAULT_INTERFACE);
	    if (serialInterface.equals("spi")) {
	    	spi = true;
	    } else if (serialInterface.equals("i2c")) {
	    	spi = false;
	    } else {
	    	throw new Exception("Unsupported argument value '" + serialInterface + "'");
	    }
	    
	    this.resetGpioPin = Integer.parseInt(this.resetGpioPinOption.getValue(DemoOpts.DEFAULT_RESETGPIOPIN));
	    this.dcGpioPin = Integer.parseInt(this.dcGpioPinOption.getValue(DemoOpts.DEFAULT_DCGPIOPIN));
	    this.speed = Integer.parseInt(this.speedOption.getValue(
	    		Integer.toString(SpiSerialInterface.DEFAULT_SPEED)));
	    this.csChannel = Integer.parseInt(this.csChannelOption.getValue(DemoOpts.DEFAULT_CSCHANNEL));
	    
	    return true;

	}
	
}
