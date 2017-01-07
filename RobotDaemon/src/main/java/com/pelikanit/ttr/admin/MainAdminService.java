package com.pelikanit.ttr.admin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.pelikanit.ttr.RobotDaemon;

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
	
}
