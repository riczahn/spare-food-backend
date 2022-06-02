package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.service.MealService;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class MealServiceIT {

  @Inject MealService mealService;

  @Test
  @TestTransaction
  void addingAMealPersistsIt() {
    Meal anyMeal = new Meal("any meal");

    mealService.addMeal(anyMeal);

    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    assertThat(availableMeals).hasSize(1).containsExactly(anyMeal);
  }

  @Test
  @TestTransaction
  void removingAMealDeletesIt() {
    Meal anyMeal = new Meal("any meal");
    mealService.addMeal(anyMeal);

    mealService.removeMeal(anyMeal);

    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    assertThat(availableMeals).isEmpty();
  }
}
