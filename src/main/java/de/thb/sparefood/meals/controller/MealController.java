package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.service.MealService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.security.InvalidParameterException;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

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
    Meal meal = mealService.findMealById(id);
    return Response.ok().entity(meal).build();
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
    Meal updatedMeal = mealService.updateMeal(id, meal);
    return Response.ok().entity(updatedMeal).build();
  }

  @DELETE
  @Path("/{id}")
  @Produces
  public Response deleteMeal(@PathParam("id") long id) {
    mealService.removeMeal(id);
    return Response.noContent().build();
  }
}
