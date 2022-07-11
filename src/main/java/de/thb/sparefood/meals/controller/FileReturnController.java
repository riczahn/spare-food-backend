package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.service.StorageService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/admin/images")
@AllArgsConstructor
@RolesAllowed("User")
public class FileReturnController {

  private static final Logger logger = LoggerFactory.getLogger(FileReturnController.class);

  @Inject StorageService storageService;

  @GET
  @Path("/{key}")
  @Produces("image/png")
  public Response getImage(@PathParam("key") String key) {
    byte[] file = storageService.getFileAsBytes(key);
    return Response.ok().entity(file).build();
  }

  @DELETE
  @Path("/{key}")
  public Response deleteImage(@PathParam("key") String key) {
    logger.info("Delete File with key {} was called.", key);
    storageService.deleteFile(key);
    return Response.ok().build();
  }
}
