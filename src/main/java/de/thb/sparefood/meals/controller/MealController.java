package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.service.MealService;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/meals")
@AllArgsConstructor
public class MealController {

  @Inject MealService mealService;

  @GET
  @Produces(APPLICATION_JSON)
  public List<Meal> getAllMeals() {
    return mealService.getAllAvailableMeals();
  }
}
