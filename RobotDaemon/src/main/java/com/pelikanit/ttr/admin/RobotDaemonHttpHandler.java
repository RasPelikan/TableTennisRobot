package com.pelikanit.ttr.admin;

public class RobotDaemonHttpHandler extends CGIHttpHandler {

	public static final String PATH = "/ttr";
	
	private static final String RESOURCE_PACKAGE = "com/pelikanit";
	
	@Override
	protected String getResourcePackage() {
		
		return RESOURCE_PACKAGE;
		
	}

}
