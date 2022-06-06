package de.thb.sparefood.user.controller;

import de.thb.sparefood.user.exception.UnknownUserException;
import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/users")
@Produces(APPLICATION_JSON)
public class UserController {

  @Inject UserService userService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @GET
  @Path("/{email}")
  public Response findUserByEmail(@PathParam("email") String email) {
    Optional<User> user = userService.getUserByEmail(email);

    if (user.isEmpty()) {
      return Response.status(NOT_FOUND).build();
    }

    return Response.ok().entity(user.get()).build();
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
    try {
      userService.removeUserWithEmail(email);
    } catch (UnknownUserException e) {
      logger.error("Couldn't delete user. Reason: {}", e.getMessage());
      return Response.status(NOT_FOUND).build();
    }
    return Response.noContent().build();
  }
}
