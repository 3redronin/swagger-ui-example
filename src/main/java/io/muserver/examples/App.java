package io.muserver.examples;

import io.muserver.MuServer;
import io.muserver.examples.resource.PetResource;
import io.muserver.examples.resource.PetStoreResource;
import io.muserver.examples.resource.UserResource;
import io.muserver.examples.resource.VehicleResource;
import io.muserver.handlers.ResourceHandler;
import io.muserver.rest.RestHandlerBuilder;

import static io.muserver.MuServerBuilder.httpServer;

public class App {

	public static void main(String[] args) {
		MuServer server = httpServer()
				.withHttpConnection(10100)
				.addHandler(RestHandlerBuilder.restHandler(
						new PetResource(), new PetStoreResource(), new UserResource(), new VehicleResource()
				))

				// Use the index.html from our own resource folder
				.addHandler(ResourceHandler.fileOrClasspath("src/main/resources/swagger-overrides", "/swagger-overrides")
						.withPathToServeFrom("/docs"))

				// ...but use all the other swagger resources from the swagger-ui dependency
				.addHandler(ResourceHandler.classpathHandler("/META-INF/resources/webjars/swagger-ui/3.13.0")
						.withPathToServeFrom("/docs"))
				.start();

		Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

		System.out.println("Started server at " + server.uri() + " - browse docs at " + server.uri().resolve("/docs"));
	}
}
