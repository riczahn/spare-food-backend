package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.exception.MealNotFoundException;
import de.thb.sparefood.meals.model.*;
import de.thb.sparefood.meals.service.ContextService;
import de.thb.sparefood.meals.service.MealService;
import de.thb.sparefood.meals.service.StorageService;
import de.thb.sparefood.user.exception.MealCantBeReservedException;
import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.service.UserService;
import io.smallrye.common.annotation.Blocking;
import lombok.AllArgsConstructor;
import org.jboss.resteasy.reactive.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Path("/meals")
@AllArgsConstructor
@Produces(APPLICATION_JSON)
@RolesAllowed("User")
public class MealController {

  private static final Logger logger = LoggerFactory.getLogger(MealController.class);

  @Inject MealService mealService;
  @Inject UserService userService;
  @Inject ContextService contextService;
  @Inject StorageService storageService;

  @GET
  public Response getAllMeals(@Context UriInfo info, @Context SecurityContext context) {
    MultivaluedMap<String, String> queryParameters = info.getQueryParameters();

    FilterCriteria filterCriteria;
    try {
      filterCriteria = extractFilterCriteria(queryParameters);
    } catch (IllegalArgumentException e) {
      logger.debug("Failed to extract query parameters!", e);
      return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
    }

    try {
      User user = contextService.getCurrentUser(context);
      List<Meal> availableMeals = mealService.getAllMeals(filterCriteria, user);
      return Response.ok().entity(availableMeals).build();
    } catch (UserPrincipalNotFoundException e) {
      logger.error("Failed to get all meals!", e);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/{id}")
  public Response getMealById(@PathParam("id") long id, @Context SecurityContext ctx) {
    Optional<Meal> optionalMeal = mealService.findMealById(id);

    if (optionalMeal.isEmpty()) {
      return Response.status(NOT_FOUND).build();
    }

    Meal meal = optionalMeal.get();
    if (!meal.getCreator().getEmail().equals(ctx.getUserPrincipal().getName())) {
      return Response.status(FORBIDDEN).build();
    }

    return Response.ok().entity(meal).build();
  }

  @POST
  @Consumes(APPLICATION_JSON)
  public Response addMeal(Meal meal, @Context SecurityContext context) {
    try {
      User user = contextService.getCurrentUser(context);
      meal.setCreator(user);

      Meal createdMeal = mealService.persist(meal);
      return Response.ok().entity(createdMeal).build();
    } catch (InvalidParameterException e) {
      return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
    } catch (Exception e) {
      logger.error("Failed to add meal!", e);
      return Response.serverError().build();
    }
  }

  @PUT
  @Path("/{id}")
  @Consumes(APPLICATION_JSON)
  @Transactional
  public Response updateMeal(
      @PathParam("id") long id, Meal meal, @Context SecurityContext context) {
    try {
      User user = contextService.getCurrentUser(context);
      Meal updatedMeal = mealService.updateMeal(id, meal, user);
      return Response.ok().entity(updatedMeal).build();
    } catch (MealNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(NOT_FOUND).build();
    } catch (Exception e) {
      logger.error("Failed to update meal! %s", e);
      return Response.status(INTERNAL_SERVER_ERROR).build();
    }
  }

  @DELETE
  @Path("/{id}")
  @Produces
  public Response deleteMeal(@PathParam("id") long id) {
    mealService.removeMeal(id);
    return Response.noContent().build();
  }

  @POST
  @Path("/{id}/reserve")
  public Response reserveMeal(@PathParam("id") long id, @Context SecurityContext context) {
    try {
      User user = contextService.getCurrentUser(context);
      mealService.reserveMeal(id, user);
      return Response.ok().build();
    } catch (MealNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(NOT_FOUND).build();
    } catch (MealCantBeReservedException e) {
      logger.error(e.getMessage());
      return Response.status(CONFLICT).build();
    } catch (Exception e) {
      logger.error("Failed to reserve meal! %s", e);
      return Response.status(INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("/{id}/release")
  public Response releaseMeal(@PathParam("id") long id, @Context SecurityContext context) {
    try {
      User user = contextService.getCurrentUser(context);
      mealService.releaseMeal(id, user);
      return Response.ok().build();
    } catch (MealNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(NOT_FOUND).build();
    } catch (SecurityException e) {
      logger.error(e.getMessage());
      return Response.status(FORBIDDEN).build();
    } catch (Exception e) {
      logger.error("Failed to release meal! %s", e);
      return Response.status(INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("/{id}/image")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  @Blocking
  @Transactional
  public Response uploadImage(
      @PathParam("id") long mealId,
      @MultipartForm FileUploadForm data,
      @Context SecurityContext context) {

    Optional<Meal> optionalMeal = mealService.findMealById(mealId);
    if (optionalMeal.isEmpty()) {
      return Response.status(NOT_FOUND).build();
    }
    Meal meal = optionalMeal.get();

    try {
      User user = contextService.getCurrentUser(context);
      if (!meal.getCreator().getEmail().equals(user.getEmail())) {
        return Response.status(FORBIDDEN).build();
      }

      File file = data.getData();
      String key = storageService.saveFile(file, String.valueOf(mealId));
      meal.setPicturePath(key);
      mealService.persist(meal);
      return Response.ok().build();
    } catch (Exception e) {
      logger.error("Failed", e);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/{id}/image")
  @Produces("image/png")
  public Response getImage(@PathParam("id") long mealId, @Context SecurityContext context) {
    Optional<Meal> optionalMeal = mealService.findMealById(mealId);
    if (optionalMeal.isEmpty()) {
      return Response.status(NOT_FOUND).build();
    }
    Meal meal = optionalMeal.get();

    if (meal.getPicturePath() == null) {
      return Response.noContent().build();
    }

    try {
      byte[] file = storageService.getFileAsBytes(meal.getPicturePath());
      return Response.ok().entity(file).build();
    } catch (Exception e) {
      logger.error("Failed", e);
      return Response.serverError().build();
    }
  }

  private FilterCriteria extractFilterCriteria(MultivaluedMap<String, String> queryParameters) {
    FilterCriteria filterCriteria = new FilterCriteria();
    List<Property> properties = extractPropertiesFromQueryParameters(queryParameters);
    filterCriteria.setProperties(properties);

    Location location = extractLocationFromQueryParameters(queryParameters);
    filterCriteria.setUserLocation(location);

    Double searchRadius = extractSearchRadiusFromQueryParameters(queryParameters);
    filterCriteria.setSearchRadius(searchRadius);

    return filterCriteria;
  }

  private List<Property> extractPropertiesFromQueryParameters(
      MultivaluedMap<String, String> queryParameters) {
    List<String> filterProperties = queryParameters.get("filter.property");

    if (filterProperties == null) {
      return new ArrayList<>();
    }

    List<Property> properties = new ArrayList<>();
    for (String propertyName : filterProperties) {
      Property property = Property.valueOf(propertyName.toUpperCase());
      properties.add(property);
    }

    return properties;
  }

  private Location extractLocationFromQueryParameters(
      MultivaluedMap<String, String> queryParameters) {
    String queryLongitude = queryParameters.getFirst("longitude");
    String queryLatitude = queryParameters.getFirst("latitude");

    if (queryLongitude == null || queryLatitude == null) {
      throw new InvalidParameterException("No Location details were specified.");
    }

    Double longitude = Double.parseDouble(queryLongitude);
    Double latitude = Double.parseDouble(queryLatitude);

    validateLocationParameters(longitude, latitude);

    return new Location(longitude, latitude);
  }

  private Double extractSearchRadiusFromQueryParameters(
      MultivaluedMap<String, String> queryParameters) {
    String queryRadius = queryParameters.getFirst("filter.radius");

    if (queryRadius == null) {
      throw new InvalidParameterException("No Location details were specified.");
    } else if (Double.parseDouble(queryRadius) < 0.0) {
      throw new InvalidParameterException("The search radius must be positive");
    }

    return Double.valueOf(queryRadius);
  }

  private void validateLocationParameters(Double longitude, Double latitude) {
    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180");
    }

    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180");
    }
  }
}
