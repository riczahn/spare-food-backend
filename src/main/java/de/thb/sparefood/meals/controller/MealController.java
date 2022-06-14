package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.exception.MealNotFoundException;
import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.service.MealService;
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
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/meals")
@AllArgsConstructor
@Produces(APPLICATION_JSON)
public class MealController {

  private static final Logger logger = LoggerFactory.getLogger(MealController.class);

  @Inject MealService mealService;

  @GET
  public Response getAllMeals() {
    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    return Response.ok().entity(availableMeals).build();
  }

  @GET
  @Path("/{id}")
  public Response getMealById(@PathParam("id") long id) {
    Optional<Meal> meal = mealService.findMealById(id);

    if (meal.isPresent()) {
      return Response.ok().entity(meal.get()).build();
    } else {
      return Response.status(NOT_FOUND).build();
    }
  }

  @POST
  @Consumes(APPLICATION_JSON)
  public Response addMeal(Meal meal) {
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
