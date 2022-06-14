package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.exception.MealNotFoundException;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.service.MealService;
import de.thb.sparefood.user.model.User;
import de.thb.sparefood.user.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Path("/meals")
@AllArgsConstructor
@Produces(APPLICATION_JSON)
public class MealController {

  private static final Logger logger = LoggerFactory.getLogger(MealController.class);

  @Inject MealService mealService;
  @Inject UserService userService;

  @GET
  public Response getAllMeals() {
    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    return Response.ok().entity(availableMeals).build();
  }

  @GET
  @Path("/{id}")
  @RolesAllowed("User")
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
  @RolesAllowed("User")
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
  public Response updateMeal(@PathParam("id") long id, Meal meal) {
    try {
      Meal updatedMeal = mealService.updateMeal(id, meal);
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
}
