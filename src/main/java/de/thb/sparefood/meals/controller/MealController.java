package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.exception.MealNotFoundException;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.model.Property;
import de.thb.sparefood.meals.service.MealService;
import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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

  @GET
  public Response getAllMeals(@Context UriInfo info) {
    MultivaluedMap<String, String> queryParameters = info.getQueryParameters();

    List<Property> filterCriteria;
    try {
      filterCriteria = extractFilterQueryParameter(queryParameters);
    } catch (IllegalArgumentException e) {
      logger.debug("Failed to extract query filter parameter!", e);
      return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
    }

    List<Meal> availableMeals = mealService.getAllMeals(filterCriteria);
    return Response.ok().entity(availableMeals).build();
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
  public Response addMeal(Meal meal, @Context SecurityContext ctx) {
    String email = ctx.getUserPrincipal().getName();
    Optional<User> user = userService.getUserByEmail(email);

    if (user.isEmpty()) {
      logger.error("Couldn't find user for user principal name of {}", email);
      return Response.serverError().build();
    }

    meal.setCreator(user.get());

    Meal createdMeal;
    try {
      createdMeal = mealService.addMeal(meal);
    } catch (InvalidParameterException e) {
      return Response.status(BAD_REQUEST).entity(e.getMessage()).build();
    } catch (Exception e) {
      logger.error("Failed to add meal!", e);
      return Response.serverError().build();
    }

    return Response.ok().entity(createdMeal).build();
  }

  @PUT
  @Path("/{id}")
  @Consumes(APPLICATION_JSON)
  public Response updateMeal(@PathParam("id") long id, Meal meal, @Context SecurityContext ctx) {
    String email = ctx.getUserPrincipal().getName();
    Optional<User> user = userService.getUserByEmail(email);

    if (user.isEmpty()) {
      logger.error("Couldn't find user for user principal name of {}", email);
      return Response.serverError().build();
    }

    try {
      Meal updatedMeal = mealService.updateMeal(id, meal, user.get());
      return Response.ok().entity(updatedMeal).build();
    } catch (MealNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(NOT_FOUND).build();
    }
  }

  @DELETE
  @Path("/{id}")
  @Produces
  public Response deleteMeal(@PathParam("id") long id) {
    mealService.removeMeal(id);
    return Response.noContent().build();
  }

  private List<Property> extractFilterQueryParameter(
      MultivaluedMap<String, String> queryParameters) {
    List<String> filterProperties = queryParameters.get("filter.property");

    if (filterProperties == null) {
      return new ArrayList<>();
    }

    List<Property> filterCriteria = new ArrayList<>();
    for (String propertyName : filterProperties) {
      Property property = Property.valueOf(propertyName.toUpperCase());
      filterCriteria.add(property);
    }

    return filterCriteria;
  }
}
