package de.thb.sparefood.user.controller;

import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.service.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/users")
@Produces(APPLICATION_JSON)
public class UserController {

  @Inject UserService userService;

  @GET
  @Path("/{email}")
  public Response findUserByEmail(@PathParam("email") String email) {
    User user = userService.getUserByEmail(email);

    if (user == null) {
      return Response.status(NOT_FOUND).build();
    }

    return Response.ok().entity(user).build();
  }

  @POST
  @Consumes(APPLICATION_JSON)
  public Response createUser(User user) {
    userService.addUser(user);
    return Response.ok().entity(user).build();
  }

  @DELETE
  @Path("/{email}")
  public Response deleteUserByEmail(@PathParam("email") String email) {
    userService.removeUserWithEmail(email);
    return Response.noContent().build();
  }
}
