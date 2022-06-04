package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.service.MealService;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/meals")
@AllArgsConstructor
@Produces(APPLICATION_JSON)
public class MealController {

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
    Meal createdMeal = mealService.addMeal(meal);
    return Response.ok().entity(createdMeal).build();
  }

  @DELETE
  @Path("/{id}")
  @Produces
  public Response deleteMeal(@PathParam("id") long id) {
    mealService.removeMeal(id);
    return Response.noContent().build();
  }
}
