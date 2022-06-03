package de.thb.sparefood.meals.controller;

import de.thb.sparefood.meals.model.Meal;
import de.thb.sparefood.meals.service.MealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MealControllerTest {

  MealService mealService;
  MealController mealController;

  @BeforeEach
  void setUp() {
    mealService = mock(MealService.class);
    mealController = new MealController(mealService);
  }

  @Test
  void givenOneMealGettingAllMealsReturnsListWithOneElement() {
    Meal anyMeal = new Meal("any meal");
    when(mealService.getAllAvailableMeals()).thenReturn(List.of(anyMeal));

    List<Meal> allMeals = mealController.getAllMeals();

    assertThat(allMeals).hasSize(1).containsExactly(anyMeal);
  }
}
