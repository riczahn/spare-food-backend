package de.thb.sparefood.meals.service;

import de.thb.sparefood.meals.model.Meal;
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
    Meal createdMeal = mealService.addMeal(new Meal("any meal"));

    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    assertThat(availableMeals).hasSize(1).containsExactly(createdMeal);
  }

  @Test
  @TestTransaction
  void removingAMealDeletesIt() {
    Meal createdMeal = mealService.addMeal(new Meal("any meal"));

    mealService.removeMeal(createdMeal.getId());

    List<Meal> availableMeals = mealService.getAllAvailableMeals();
    assertThat(availableMeals).isEmpty();
  }
}
