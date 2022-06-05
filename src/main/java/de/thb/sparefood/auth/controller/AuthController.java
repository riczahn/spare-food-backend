package de.thb.sparefood.auth.controller;

import de.thb.sparefood.auth.token.TokenUtils;
import de.thb.sparefood.auth.model.BasicAuthDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path("/auth")
@RequestScoped
public class AuthController {
  @Inject TokenUtils tokenUtils;

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @GET
  @Path("/some-secured-endpoint")
  @RolesAllowed({"User"})
  @Produces(TEXT_PLAIN)
  public String userSpecificOutput(@Context SecurityContext ctx) {
    if (ctx.getUserPrincipal().getName().equals("zahn@th-brandenburg.de")) {
      return "Hi Richard";
    } else {
      return "You are not me";
    }
  }

  @POST
  @Path("/generate-token")
  @PermitAll
  @Consumes(APPLICATION_JSON)
  @Produces(TEXT_PLAIN)
  public Response generateToken(BasicAuthDTO basicAuthDTO) {
    try {
      String token = tokenUtils.generateToken(basicAuthDTO);
      return Response.ok().entity(token).build();
    } catch (SecurityException e) {
      logger.info(e.getMessage());
      return Response.status(UNAUTHORIZED).build();
    }
  }
}
