package io.muserver.examples;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("users")
public class User {

	@GET
	public String go() {
		return "Hi";
	}

}
