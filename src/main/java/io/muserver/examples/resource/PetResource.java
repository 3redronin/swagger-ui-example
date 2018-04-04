/**
 *  Copyright 2016 SmartBear Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.muserver.examples.resource;

import io.muserver.Description;
import io.muserver.rest.ApiResponse;
import io.muserver.rest.ApiResponses;
import io.muserver.examples.data.PetData;
import io.muserver.examples.model.Pet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

@Path("/pet")
@Description(value = "pet", details = "Operations about pets")
//@Api(
// authorizations = {
//  @Authorization(value = "petstore_auth",
//  scopes = {
//    @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
//    @AuthorizationScope(scope = "read:pets", description = "read your pets")
//  })
//}, tags = "pet")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class PetResource {
	static PetData petData = new PetData();

	@GET
	@Path("/{petId}")
	@Description(value = "Find pet by ID", details = "Returns a pet when ID <= 10.  ID > 10 or nonintegers will simulate API error conditions")
//  @ApiOperation(value = "",
//    response = Pet.class,
//    authorizations = @Authorization(value = "api_key")
//  )
	@ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Pet not found")})
	public Response getPetById(
//      @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,10]", required = true)
			@PathParam("petId") Long petId)
			throws NotFoundException {
		Pet pet = petData.getPetById(petId);
		if (pet != null) {
			return Response.ok().entity(pet).build();
		} else {
			throw new NotFoundException("Pet not found");
		}
	}

	@GET
	@Path("/{petId}/download")
	@Description(value = "Find pet by ID", details = "Returns a pet when ID <= 10.  ID > 10 or nonintegers will simulate API error conditions")
//    response = Pet.class,
//    authorizations = @Authorization(value = "api_key")
//  )
	@ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Pet not found")})
	public Response downloadFile(
//      @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,10]", required = true)
			@PathParam("petId") Long petId)
			throws NotFoundException {
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				try {
					// TODO: write file content to output;
					output.write("hello, world".getBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		return Response.ok(stream, "application/force-download")
				.header("Content-Disposition", "attachment; filename = foo.bar")
				.build();
	}

	@DELETE
	@Path("/{petId}")
	@Description("Deletes a pet")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Pet not found")})
	public Response deletePet(
			@HeaderParam("api_key")
					String apiKey,
//    @ApiParam(value = "Pet id to delete", required = true)
			@PathParam("petId") Long petId) {
		if (petData.deletePet(petId)) {
			return Response.ok().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Description(value = "Add a new pet to the store")
	@ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input", response = Pet.class)})
	public Response addPet(
//      @ApiParam(value = "Pet object that needs to be added to the store", required = true)
			Pet pet) {
		Pet updatedPet = petData.addPet(pet);
		return Response.ok().entity(updatedPet).build();
	}

	@PUT
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Description("Update an existing pet")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Pet not found"),
			@ApiResponse(code = 405, message = "Validation exception")})
	public Response updatePet(
//      @ApiParam(value = "Pet object that needs to be added to the store", required = true)
			Pet pet) {
		Pet updatedPet = petData.addPet(pet);
		return Response.ok().entity(updatedPet).build();
	}

	@GET
	@Path("/findByStatus")
	@Description(value = "Finds Pets by status", details = "Multiple status values can be provided with comma separated strings")
//    response = Pet.class,
//    responseContainer = "List")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid status value")})
	public Response findPetsByStatus(
//      @ApiParam(value = "Status values that need to be considered for filter", required = true, defaultValue = "available", allowableValues = "available,pending,sold", allowMultiple = true)
			@QueryParam("status") String status) {
		return Response.ok(petData.findPetByStatus(status)).build();
	}

	@GET
	@Path("/findByTags")
//  @ApiOperation(value = "Finds Pets by tags",
//    notes = "Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.",
//    response = Pet.class,
//    responseContainer = "List")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid tag value")})
	@Deprecated
	public Response findPetsByTags(
			@HeaderParam("api_key") String api_key,
//      @ApiParam(value = "Tags to filter by", required = true, allowMultiple = true)
			@QueryParam("tags") String tags) {
		return Response.ok(petData.findPetByTags(tags)).build();
	}

	@POST
	@Path("/{petId}")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED})
	@Description("Updates a pet in the store with form data")
	@ApiResponses(value = {
			@ApiResponse(code = 405, message = "Invalid input")})
	public Response updatePetWithForm(
//   @ApiParam(value = "ID of pet that needs to be updated", required = true)
			@PathParam("petId") Long petId,
//   @ApiParam(value = "Updated name of the pet", required = false)
			@FormParam("name") String name,
//   @ApiParam(value = "Updated status of the pet", required = false)
			@FormParam("status") String status) {
		Pet pet = petData.getPetById(petId);
		if (pet != null) {
			if (name != null && !"".equals(name))
				pet.setName(name);
			if (status != null && !"".equals(status))
				pet.setStatus(status);
			petData.addPet(pet);
			return Response.ok().build();
		} else
			return Response.status(404).build();
	}
}
